/*
 * Copyright  1999-2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.cms.repository.Node;

/**
 * A publication.
 * @version $Id$
 */
public class PublicationImpl extends AbstractLogEnabled implements Publication {

    private static final String[] areas = { AUTHORING_AREA, DAV_AREA, STAGING_AREA, LIVE_AREA, ADMIN_AREA,
            ARCHIVE_AREA, TRASH_AREA };

    private String id;
    private File servletContext;
    private DocumentIdToPathMapper mapper = null;
    private ArrayList languages = new ArrayList();
    private String defaultLanguage = null;
    private String breadcrumbprefix = null;
    private String instantiatorHint = null;

    private static final String ELEMENT_PROXY = "proxy";
    private static final String ATTRIBUTE_AREA = "area";
    private static final String ATTRIBUTE_URL = "url";
    private static final String ATTRIBUTE_SSL = "ssl";

    private boolean isConfigLoaded = false;

    /**
     * <code>CONFIGURATION_FILE</code> The publication configuration file
     */
    public static final String CONFIGURATION_FILE = CONFIGURATION_PATH + File.separator
            + "publication.xconf";

    private static final String ELEMENT_TEMPLATES = "templates";
    private static final String ELEMENT_TEMPLATE = "template";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ELEMENT_PATH_MAPPER = "path-mapper";
    private static final String ELEMENT_DOCUMENT_BUILDER = "document-builder";
    private static final String ELEMENT_SITE_MANAGER = "site-manager";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ELEMENT_TEMPLATE_INSTANTIATOR = "template-instantiator";
    private static final String LANGUAGES = "languages";
    private static final String DEFAULT_LANGUAGE_ATTR = "default";
    private static final String BREADCRUMB_PREFIX = "breadcrumb-prefix";
    private static final String ELEMENT_RESOURCE_TYPE = "resource-type";
    private static final String ATTRIBUTE_WORKFLOW = "workflow";

    /**
     * Creates a new instance of Publication
     * @param _id the publication id
     * @param servletContextPath the servlet context of this publication
     * @throws PublicationException if there was a problem reading the config file
     */
    protected PublicationImpl(String _id, String servletContextPath) throws PublicationException {
        this.id = _id;
        this.servletContext = new File(servletContextPath);
    }

    /**
     * Loads the configuration.
     */
    protected void loadConfiguration() {

        if (isConfigLoaded) {
            return;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Loading configuration for publication [" + getId() + "]");
        }

        File configFile = getConfigurationFile();

        if (!configFile.exists()) {
            getLogger().error("Config file [" + configFile.getAbsolutePath() + "] does not exist: ",
                    new RuntimeException());
            throw new RuntimeException("The configuration file [" + configFile
                    + "] does not exist!");
        } else {
            getLogger().debug("Configuration file [" + configFile + "] exists.");
        }

        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

        Configuration config;

        String pathMapperClassName = null;

        try {
            config = builder.buildFromFile(configFile);

            try {
                pathMapperClassName = config.getChild(ELEMENT_PATH_MAPPER).getValue();
                Class pathMapperClass = Class.forName(pathMapperClassName);
                this.mapper = (DocumentIdToPathMapper) pathMapperClass.newInstance();
            } catch (final ClassNotFoundException e) {
                throw new PublicationException("Cannot instantiate documentToPathMapper: ["
                        + pathMapperClassName + "]", e);
            }

            Configuration documentBuilderConfiguration = config.getChild(ELEMENT_DOCUMENT_BUILDER,
                    false);
            if (documentBuilderConfiguration != null) {
                this.documentBuilderHint = documentBuilderConfiguration.getAttribute(ATTRIBUTE_NAME);
            }

            Configuration[] _languages = config.getChild(LANGUAGES).getChildren();
            for (int i = 0; i < _languages.length; i++) {
                Configuration languageConfig = _languages[i];
                String language = languageConfig.getValue();
                this.languages.add(language);
                if (languageConfig.getAttribute(DEFAULT_LANGUAGE_ATTR, null) != null) {
                    this.defaultLanguage = language;
                }
            }

            Configuration siteManagerConfiguration = config.getChild(ELEMENT_SITE_MANAGER, false);
            if (siteManagerConfiguration != null) {
                this.siteManagerName = siteManagerConfiguration.getAttribute(ATTRIBUTE_NAME);
            }

            Configuration[] proxyConfigs = config.getChildren(ELEMENT_PROXY);
            for (int i = 0; i < proxyConfigs.length; i++) {
                String url = proxyConfigs[i].getAttribute(ATTRIBUTE_URL);
                String ssl = proxyConfigs[i].getAttribute(ATTRIBUTE_SSL);
                String area = proxyConfigs[i].getAttribute(ATTRIBUTE_AREA);

                Proxy proxy = new Proxy();
                proxy.setUrl(url);

                Object key = getProxyKey(area, Boolean.valueOf(ssl).booleanValue());
                this.areaSsl2proxy.put(key, proxy);
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Adding proxy: [" + proxy + "] for area=[" + area + "] SSL=["
                            + ssl + "]");
                }
            }

            Configuration templatesConfig = config.getChild(ELEMENT_TEMPLATES);
            if (templatesConfig != null) {
                Configuration[] templateConfigs = templatesConfig.getChildren(ELEMENT_TEMPLATE);
                this.templates = new String[templateConfigs.length];
                for (int i = 0; i < templateConfigs.length; i++) {
                    String templateId = templateConfigs[i].getAttribute(ATTRIBUTE_ID);
                    this.templates[i] = templateId;
                }
            }

            Configuration templateInstantiatorConfig = config.getChild(ELEMENT_TEMPLATE_INSTANTIATOR,
                    false);
            if (templateInstantiatorConfig != null) {
                this.instantiatorHint = templateInstantiatorConfig.getAttribute(PublicationImpl.ATTRIBUTE_NAME);
            }

            Configuration[] resourceTypeConfigs = config.getChildren(ELEMENT_RESOURCE_TYPE);
            for (int i = 0; i < resourceTypeConfigs.length; i++) {
                String name = resourceTypeConfigs[i].getAttribute(ATTRIBUTE_NAME);
                this.resourceTypes.add(name);

                String workflow = resourceTypeConfigs[i].getAttribute(ATTRIBUTE_WORKFLOW, null);
                if (workflow != null) {
                    this.resourceType2workflow.put(name, workflow);
                }
            }

        } catch (final Exception e) {
            throw new RuntimeException("Problem with config file: " + configFile.getAbsolutePath(),
                    e);
        }

        this.breadcrumbprefix = config.getChild(BREADCRUMB_PREFIX).getValue("");

        isConfigLoaded = true;
    }

    /**
     * @return The configuration file (publication.xconf).
     */
    protected File getConfigurationFile() {
        File configFile = new File(getDirectory(), CONFIGURATION_FILE);
        return configFile;
    }

    /**
     * Returns the publication ID.
     * @return A string value.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the servlet context this publication belongs to (usually, the
     * <code>webapps/lenya</code> directory).
     * @return A <code>File</code> object.
     */
    public File getServletContext() {
        return this.servletContext;
    }

    /**
     * Returns the publication directory.
     * @return A <code>File</code> object.
     */
    public File getDirectory() {
        return new File(getServletContext(), PUBLICATION_PREFIX + File.separator + getId());
    }

    /**
     * @see org.apache.lenya.cms.publication.Publication#getContentDirectory(String)
     */
    public File getContentDirectory(String area) {
        return new File(getDirectory(), CONTENT_PATH + File.separator + area);
    }

    /**
     * Set the path mapper
     * @param _mapper The path mapper
     */
    public void setPathMapper(DefaultDocumentIdToPathMapper _mapper) {
        assert _mapper != null;
        this.mapper = _mapper;
    }

    /**
     * Returns the path mapper.
     * @return a <code>DocumentIdToPathMapper</code>
     */
    public DocumentIdToPathMapper getPathMapper() {
        if (this.mapper == null) {
            loadConfiguration();
        }
        return this.mapper;
    }

    /**
     * Returns if a given string is a valid area name.
     * @param area The area string to test.
     * @return A boolean value.
     */
    public static boolean isValidArea(String area) {
        return area != null && Arrays.asList(areas).contains(area);
    }

    /**
     * Get the default language
     * @return the default language
     */
    public String getDefaultLanguage() {
        if (this.defaultLanguage == null) {
            loadConfiguration();
        }
        return this.defaultLanguage;
    }

    /**
     * Set the default language
     * @param language the default language
     */
    public void setDefaultLanguage(String language) {
        this.defaultLanguage = language;
    }

    /**
     * Get all available languages for this publication
     * @return an <code>Array</code> of languages
     */
    public String[] getLanguages() {
        loadConfiguration();
        return (String[]) this.languages.toArray(new String[this.languages.size()]);
    }

    /**
     * Get the breadcrumb prefix. It can be used as a prefix if a publication is part of a larger
     * site
     * @return the breadcrumb prefix
     */
    public String getBreadcrumbPrefix() {
        loadConfiguration();
        return this.breadcrumbprefix;
    }

    private String documentBuilderHint;

    /**
     * Returns the document builder of this instance.
     * @return A document builder.
     */
    public String getDocumentBuilderHint() {
        loadConfiguration();
        return this.documentBuilderHint;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        boolean equals = false;

        if (getClass().isInstance(object)) {
            Publication publication = (Publication) object;
            equals = getId().equals(publication.getId())
                    && getServletContext().equals(publication.getServletContext());
        }

        return equals;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String key = getServletContext() + ":" + getId();
        return key.hashCode();
    }

    private Map areaSsl2proxy = new HashMap();

    /**
     * Generates a hash key for a area-SSL combination.
     * @param area The area.
     * @param isSslProtected If the proxy is assigned for SSL-protected pages.
     * @return An object.
     */
    protected Object getProxyKey(String area, boolean isSslProtected) {
        return area + ":" + isSslProtected;
    }

    /**
     * @see org.apache.lenya.cms.publication.Publication#getProxy(org.apache.lenya.cms.publication.Document,
     *      boolean)
     */
    public Proxy getProxy(Document document, boolean isSslProtected) {
        loadConfiguration();
        Object key = getProxyKey(document.getArea(), isSslProtected);
        Proxy proxy = (Proxy) this.areaSsl2proxy.get(key);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Resolving proxy for [" + document + "] SSL=[" + isSslProtected + "]");
            getLogger().debug("Resolved proxy: [" + proxy + "]");
        }

        return proxy;
    }

    private String siteManagerName;

    /**
     * @see org.apache.lenya.cms.publication.Publication#exists()
     */
    public boolean exists() {
        return getConfigurationFile().exists();
    }

    private String[] templates;

    /**
     * @see org.apache.lenya.cms.publication.Publication#getTemplateIds()
     */
    public String[] getTemplateIds() {
        loadConfiguration();
        List list = Arrays.asList(this.templates);
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * @see org.apache.lenya.cms.publication.Publication#getSiteManagerHint()
     */
    public String getSiteManagerHint() {
        loadConfiguration();
        return this.siteManagerName;
    }

    /**
     * @see org.apache.lenya.cms.publication.Publication#getInstantiatorHint()
     */
    public String getInstantiatorHint() {
        loadConfiguration();
        return this.instantiatorHint;
    }

    /**
     * @see org.apache.lenya.cms.publication.Publication#getSourceURI()
     */
    public String getSourceURI() {
        return Node.LENYA_PROTOCOL + PUBLICATION_PREFIX_URI + "/" + this.id;
    }

    /**
     * @see org.apache.lenya.cms.publication.Publication#getContentURI(java.lang.String)
     */
    public String getContentURI(String area) {
        return getSourceURI() + "/" + CONTENT_PATH + "/" + area;
    }

    private Map resourceType2workflow = new HashMap();

    /**
     * @see org.apache.lenya.cms.publication.Publication#getWorkflowSchema(org.apache.lenya.cms.publication.ResourceType)
     */
    public String getWorkflowSchema(ResourceType resourceType) {
        String workflow = (String) this.resourceType2workflow.get(resourceType.getName());
        return workflow;
    }

    private List resourceTypes = new ArrayList();

    /**
     * @see org.apache.lenya.cms.publication.Publication#getResourceTypeNames()
     */
    public String[] getResourceTypeNames() {
        return (String[]) resourceTypes.toArray(new String[resourceTypes.size()]);
    }

}