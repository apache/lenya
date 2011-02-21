/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.lang.Validate;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;

/**
 * A publication's configuration. Keep in sync with src/resources/build/publication.rng!
 */
public class PublicationConfiguration extends AbstractLogEnabled implements Publication {

    private String id;
    private String name;
    private String description;
    private String version;
    private String lenyaVersion;
    private String lenyaRevision;
    private String cocoonVersion;

    private String pubBaseUri;
    private DocumentIdToPathMapper mapper = null;
    private SortedSet languages = new TreeSet();
    private String defaultLanguage = null;
    private String breadcrumbprefix = null;
    private String instantiatorHint = null;
    private String contentUri = null;
    private SortedSet modules = new TreeSet();
    private String contextPath;
    private SourceResolver sourceResolver;

    private boolean isConfigLoaded = false;

    /**
     * <code>CONFIGURATION_FILE</code> The publication configuration file
     */
    public static final String CONFIGURATION_URI = CONFIGURATION_PATH + "/" + "publication.xml";

    private static final String CONFIGURATION_NAMESPACE = "http://apache.org/cocoon/lenya/publication/1.1";

    // properties marked with "*" are currently not parsed by this class.
    private static final String ELEMENT_NAME = "name";
    private static final String ELEMENT_DESCRIPTION = "description"; // *
    private static final String ELEMENT_VERSION = "version"; // *
    private static final String ELEMENT_LENYA_VERSION = "lenya-version"; // *
    private static final String ELEMENT_LENYA_REVISION = "lenya-revision"; // *
    private static final String ELEMENT_COCOON_VERSION = "cocoon-version"; // *
    private static final String ELEMENT_LANGUAGES = "languages";
    private static final String ELEMENT_LANGUAGE = "language";
    private static final String ATTRIBUTE_DEFAULT_LANGUAGE = "default";
    private static final String ELEMENT_TEMPLATE = "template";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ELEMENT_TEMPLATE_INSTANTIATOR = "template-instantiator";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ELEMENT_PATH_MAPPER = "path-mapper";
    private static final String ELEMENT_DOCUMENT_BUILDER = "document-builder";
    private static final String ELEMENT_SITE_MANAGER = "site-manager";
    private static final String ELEMENT_RESOURCE_TYPES = "resource-types";// *
    private static final String ELEMENT_RESOURCE_TYPE = "resource-type";// *
    private static final String ATTRIBUTE_WORKFLOW = "workflow";
    private static final String ELEMENT_MODULES = "modules";// *
    private static final String ELEMENT_MODULE = "module";// *
    private static final String ELEMENT_BREADCRUMB_PREFIX = "breadcrumb-prefix";
    private static final String ELEMENT_CONTENT_DIR = "content-dir";
    private static final String ATTRIBUTE_SRC = "src";
    private static final String ELEMENT_PROXIES = "proxies";
    private static final String ELEMENT_PROXY = "proxy";
    private static final String ATTRIBUTE_AREA = "area";
    private static final String ATTRIBUTE_URL = "url";
    private static final String ATTRIBUTE_SSL = "ssl";

    protected static final String NAMESPACE = "http://apache.org/cocoon/lenya/publication/1.1";
    protected static final String NS_PREFIX = "";

    /**
     * Creates a new instance of Publication
     * @param _id the publication id
     * @param pubBaseUri The base filesystem path where publications are located.
     * @param servletContextUrlPath The servlet context path (URL snippet).
     * @throws PublicationException if there was a problem reading the config file
     */
    protected PublicationConfiguration(String _id, String pubBaseUri, String servletContextUrlPath)
            throws PublicationException {
        this.id = _id;
        this.pubBaseUri = pubBaseUri;
        this.contextPath = servletContextUrlPath;
    }

    public void setSourceResolver(SourceResolver resolver) {
        this.sourceResolver = resolver;
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

        String configUri = getConfigurationUri();
        Configuration config;
        Source source = null;
        try {
            source = this.sourceResolver.resolveURI(configUri);
            if (!source.exists()) {
                throw new RuntimeException("The configuration file [" + configUri
                        + "] does not exist!");
            } else {
                getLogger().debug("Configuration file [" + configUri + "] exists.");
            }
            final boolean ENABLE_XML_NAMESPACES = true;
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder(
                    ENABLE_XML_NAMESPACES);
            config = builder.build(source.getInputStream());
        } catch (final Exception e) {
            throw new RuntimeException("Problem with config file: " + configUri, e);
        } finally {
            if (source != null) {
                this.sourceResolver.release(source);
            }
        }

        String pathMapperClassName = null;

        try {

            String pubName = config.getChild(ELEMENT_NAME).getValue(null);
            if (pubName == null) {
                getLogger().warn(
                        "No publication name set for publication [" + getId()
                                + "], using default name.");
                this.name = getId();
            } else {
                this.name = pubName;
            }

            this.description = config.getChild(ELEMENT_DESCRIPTION).getValue("");
            this.version = config.getChild(ELEMENT_VERSION).getValue("");
            this.lenyaVersion = config.getChild(ELEMENT_LENYA_VERSION).getValue("");
            this.lenyaRevision = config.getChild(ELEMENT_LENYA_REVISION).getValue("");
            this.cocoonVersion = config.getChild(ELEMENT_COCOON_VERSION).getValue("");

            try {
                // one sanity check for the proper namespace. we should really
                // do that for every element,
                // but since ELEMENT_PATH_MAPPER is mandatory, this should catch
                // most cases of forgotten namespace.
                if (config.getChild(ELEMENT_PATH_MAPPER).getNamespace() != CONFIGURATION_NAMESPACE) {
                    getLogger().warn(
                            "Deprecated configuration: the publication configuration elements in "
                                    + configUri + " must be in the " + CONFIGURATION_NAMESPACE
                                    + " namespace."
                                    + " See webapp/lenya/resources/schemas/publication.xml.");
                }
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
                this.documentBuilderHint = documentBuilderConfiguration
                        .getAttribute(ATTRIBUTE_NAME);
            }

            Configuration[] _languages = config.getChild(ELEMENT_LANGUAGES).getChildren(
                    ELEMENT_LANGUAGE);
            for (int i = 0; i < _languages.length; i++) {
                Configuration languageConfig = _languages[i];
                String language = languageConfig.getValue();
                this.languages.add(language);
                if (languageConfig.getAttribute(ATTRIBUTE_DEFAULT_LANGUAGE, null) != null) {
                    this.defaultLanguage = language;
                }
            }

            Configuration siteManagerConfiguration = config.getChild(ELEMENT_SITE_MANAGER, false);
            if (siteManagerConfiguration != null) {
                this.siteManagerName = siteManagerConfiguration.getAttribute(ATTRIBUTE_NAME);
            }

            Configuration proxiesConfig = config.getChild(ELEMENT_PROXIES);
            Configuration[] proxyConfigs = proxiesConfig.getChildren(ELEMENT_PROXY);
            for (int i = 0; i < proxyConfigs.length; i++) {
                String url = proxyConfigs[i].getAttribute(ATTRIBUTE_URL);
                String ssl = proxyConfigs[i].getAttribute(ATTRIBUTE_SSL);
                String area = proxyConfigs[i].getAttribute(ATTRIBUTE_AREA);

                Object key = getProxyKey(area, Boolean.valueOf(ssl).booleanValue());
                Proxy proxy = new ProxyImpl(getDefaultProxyUrl(area));
                proxy.setUrl(url);
                this.areaSsl2proxy.put(key, proxy);
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(
                            "Adding proxy URL: [" + url + "] for area=[" + area + "] SSL=[" + ssl
                                    + "]");
                }
            }

            Configuration templateConfig = config.getChild(ELEMENT_TEMPLATE, false);
            if (templateConfig != null) {
                this.template = templateConfig.getAttribute(ATTRIBUTE_ID);
            }

            Configuration templateInstantiatorConfig = config.getChild(
                    ELEMENT_TEMPLATE_INSTANTIATOR, false);
            if (templateInstantiatorConfig != null) {
                this.instantiatorHint = templateInstantiatorConfig
                        .getAttribute(PublicationConfiguration.ATTRIBUTE_NAME);
            }

            Configuration contentDirConfig = config.getChild(ELEMENT_CONTENT_DIR, false);
            if (contentDirConfig != null) {
                this.contentUri = contentDirConfig.getAttribute(ATTRIBUTE_SRC);
                getLogger().info(
                        "Content directory loaded from pub configuration: " + this.contentUri);
            } else {
                getLogger().info("No content directory specified within pub configuration!");
            }

            Configuration resourceTypeConfig = config.getChild(ELEMENT_RESOURCE_TYPES);
            if (resourceTypeConfig != null) {
                Configuration[] resourceTypeConfigs = resourceTypeConfig
                        .getChildren(ELEMENT_RESOURCE_TYPE);
                for (int i = 0; i < resourceTypeConfigs.length; i++) {
                    String name = resourceTypeConfigs[i].getAttribute(ATTRIBUTE_NAME);
                    this.resourceTypes.add(name);

                    String workflow = resourceTypeConfigs[i].getAttribute(ATTRIBUTE_WORKFLOW, null);
                    if (workflow != null) {
                        this.resourceType2workflow.put(name, workflow);
                    }
                }
            }

            Configuration modulesConf = config.getChild(ELEMENT_MODULES);
            if (modulesConf != null) {
                Configuration[] modulesConfigs = modulesConf.getChildren(ELEMENT_MODULE);
                for (int i = 0; i < modulesConfigs.length; i++) {
                    String name = modulesConfigs[i].getAttribute(ATTRIBUTE_NAME);
                    this.modules.add(name);
                }
            }

        } catch (final Exception e) {
            throw new RuntimeException("Problem with config file: " + configUri, e);
        }

        this.breadcrumbprefix = config.getChild(ELEMENT_BREADCRUMB_PREFIX).getValue("");

        isConfigLoaded = true;
    }

    protected String getDefaultProxyUrl(String area) {
        return this.contextPath + "/" + this.id + "/" + area;
    }

    /**
     * @return The configuration file ({@link #CONFIGURATION_URI}).
     */
    protected String getConfigurationUri() {
        return getPubBaseUri() + "/" + getId() + "/" + CONFIGURATION_URI;
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
    public String getPubBaseUri() {
        return this.pubBaseUri;
    }

    /**
     * Set the path mapper
     * @param _mapper The path mapper
     */
    //florent : use interface instead of impl
    //public void setPathMapper(DefaultDocumentIdToPathMapper _mapper) {
    public void setPathMapper(DocumentIdToPathMapper _mapper) {
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
        Validate.notNull(language);
        if (!Arrays.asList(getLanguages()).contains(language)) {
            throw new IllegalArgumentException("The publication [" + this
                    + "] doesn't contain the language [" + language + "]!");
        }
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
                    && getPubBaseUri().equals(publication.getPubBaseUri());
        }

        return equals;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String key = getPubBaseUri() + ":" + getId();
        return key.hashCode();
    }

    private Map areaSsl2proxy = new HashMap();

    /**
     * Generates a hash key for a area-SSL combination.
     * @param area The area.
     * @param isSslProtected If the proxy is assigned for SSL-protected pages.
     * @return An object.
     */
    protected String getProxyKey(String area, boolean isSslProtected) {
        return area + ":" + isSslProtected;
    }

    protected String getProxyUrl(String area, boolean isSslProtected) {
        loadConfiguration();
        Object key = getProxyKey(area, isSslProtected);
        return (String) this.areaSsl2proxy.get(key);
    }

    public Proxy getProxy(Document document, boolean isSslProtected) {
        return getProxy(document.getArea(), isSslProtected);
    }

    public Proxy getProxy(String area, boolean isSslProtected) {
        String key = getProxyKey(area, isSslProtected);
        Proxy proxy = (Proxy) this.areaSsl2proxy.get(key);
        if (proxy == null) {
            proxy = new ProxyImpl(getDefaultProxyUrl(area));
            this.areaSsl2proxy.put(key, proxy);
        }
        return proxy;
    }

    private String siteManagerName;

    /**
     * @see org.apache.lenya.cms.publication.Publication#exists()
     */
    public boolean exists() {
        Source source = null;
        try {
            source = this.sourceResolver.resolveURI(getConfigurationUri());
            return source.exists();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (source != null) {
                this.sourceResolver.release(source);
            }
        }
    }

    private String template;

    /**
     * @see org.apache.lenya.cms.publication.Publication#getTemplateId()
     */
    public String getTemplateId() {
        loadConfiguration();
        return template;
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

    public String getContentUri() {
        loadConfiguration();
        if (this.contentUri == null) {
            this.contentUri = getDefaultContentUri();
        }
        return this.contentUri;
    }

    protected String getDefaultContentUri() {
        return getSourceUri() + "/" + CONTENT_PATH;
    }

    public String getSourceUri() {
        return getPubBaseUri() + "/" + this.id;
    }

    /**
     * @see org.apache.lenya.cms.publication.Publication#getContentUri(java.lang.String)
     */
    public String getContentUri(String area) {
        return "lenya://" + getId() + "/" + CONTENT_PATH + "/" + area;
    }

    private Map resourceType2workflow = new HashMap();

    /**
     * @see org.apache.lenya.cms.publication.Publication#getWorkflowSchema(org.apache.lenya.cms.publication.ResourceType)
     */
    public String getWorkflowSchema(ResourceType resourceType) {
        return (String) this.resourceType2workflow.get(resourceType.getName());
    }

    private SortedSet resourceTypes = new TreeSet();

    /**
     * @see org.apache.lenya.cms.publication.Publication#getResourceTypeNames()
     */
    public String[] getResourceTypeNames() {
        loadConfiguration();
        return (String[]) this.resourceTypes.toArray(new String[this.resourceTypes.size()]);
    }

    public String toString() {
        return getId();
    }

    public Area getArea(String name) throws ResourceNotFoundException {
        throw new IllegalStateException("Not implemented!");
    }

    private String[] areas;

    public String[] getAreaNames() {
        // TODO: make this more generic.
        if (this.areas == null) {
            List list = new ArrayList();
            list.add(Publication.AUTHORING_AREA);
            list.add(Publication.LIVE_AREA);
            list.add(Publication.STAGING_AREA);
            list.add(Publication.TRASH_AREA);
            list.add(Publication.ARCHIVE_AREA);
            this.areas = (String[]) list.toArray(new String[list.size()]);
        }
        return this.areas;
    }

    public DocumentFactory getFactory() {
        throw new IllegalStateException("Not implemented!");
    }

    public DocumentBuilder getDocumentBuilder() {
        return null;
    }

    public String getName() {
        loadConfiguration();
        return this.name;
    }

    public Session getRepositorySession() {
        throw new UnsupportedOperationException();
    }

    public void addLanguage(String language) {
        Validate.notNull(language);
        this.languages.add(language);
    }

    public void removeLanguage(String language) {
        Validate.notNull(language);
        if (!Arrays.asList(getLanguages()).contains(language)) {
            throw new IllegalArgumentException("The publication [" + this
                    + "] doesn't contain the language [" + language + "]!");
        }
        if (language.equals(getDefaultLanguage())) {
            throw new IllegalArgumentException("Can't remove the language [" + language
                    + "] because it is the default language.");
        }
        this.languages.remove(language);
    }

    public void setName(String name) {
        Validate.notNull(name);
        Validate.isTrue(name.length() > 0, "name must not be empty");
        this.name = name;
    }

    protected DefaultConfiguration createConfig(String name) {
        return new DefaultConfiguration(name, "", NAMESPACE, NS_PREFIX);
    }

    protected DefaultConfiguration createConfig(String name, String value) {
        DefaultConfiguration config = createConfig(name);
        config.setValue(value);
        return config;
    }

    protected DefaultConfiguration createConfig(String name, String attrName, String attrValue) {
        DefaultConfiguration config = createConfig(name);
        config.setAttribute(attrName, attrValue);
        return config;
    }

    public void saveConfiguration() {

        DefaultConfiguration config = new DefaultConfiguration("publication", "", NAMESPACE,
                NS_PREFIX);

        config.addChild(createConfig(ELEMENT_NAME, getName()));
        config.addChild(createConfig(ELEMENT_DESCRIPTION, this.description));
        config.addChild(createConfig(ELEMENT_VERSION, this.version));
        config.addChild(createConfig(ELEMENT_LENYA_VERSION, this.lenyaVersion));
        config.addChild(createConfig(ELEMENT_LENYA_REVISION, this.lenyaRevision));
        config.addChild(createConfig(ELEMENT_COCOON_VERSION, this.cocoonVersion));

        config.addChild(createConfig(ELEMENT_PATH_MAPPER, getPathMapper().getClass().getName()));
        config.addChild(createConfig(ELEMENT_DOCUMENT_BUILDER, ATTRIBUTE_NAME,
                getDocumentBuilderHint()));

        String[] languages = getLanguages();
        String defaultLanguage = getDefaultLanguage();

        DefaultConfiguration languagesConfig = createConfig(ELEMENT_LANGUAGES);
        for (int i = 0; i < languages.length; i++) {
            DefaultConfiguration languageConfig = createConfig(ELEMENT_LANGUAGE);
            languageConfig.setValue(languages[i]);
            if (languages[i].equals(defaultLanguage)) {
                languageConfig.setAttribute(ATTRIBUTE_DEFAULT_LANGUAGE, true);
            }
            languagesConfig.addChild(languageConfig);
        }
        config.addChild(languagesConfig);

        config.addChild(createConfig(ELEMENT_SITE_MANAGER, ATTRIBUTE_NAME, getSiteManagerHint()));

        DefaultConfiguration proxiesConfig = createConfig(ELEMENT_PROXIES);
        config.addChild(proxiesConfig);
        Boolean[] booleans = { Boolean.FALSE, Boolean.TRUE };
        String[] areas = getAreaNames();
        for (int b = 0; b < booleans.length; b++) {
            for (int a = 0; a < areas.length; a++) {
                boolean ssl = booleans[b].booleanValue();

                Proxy proxy = getProxy(areas[a], ssl);

                // add only proxy URLs for non-default proxies
                if (!proxy.getUrl().equals(proxy.getDefaultUrl())) {
                    DefaultConfiguration proxyConf = createConfig(ELEMENT_PROXY);
                    proxyConf.setAttribute(ATTRIBUTE_AREA, areas[a]);
                    proxyConf.setAttribute(ATTRIBUTE_SSL, ssl);
                    proxyConf.setAttribute(ATTRIBUTE_URL, proxy.getUrl());
                    proxiesConfig.addChild(proxyConf);
                }

            }
        }

        String template = getTemplateId();
        if (template != null) {
            config.addChild(createConfig(ELEMENT_TEMPLATE, ATTRIBUTE_ID, template));
        }

        String instantiatorHint = getInstantiatorHint();
        if (instantiatorHint != null) {
            config.addChild(createConfig(ELEMENT_TEMPLATE_INSTANTIATOR, ATTRIBUTE_NAME,
                    instantiatorHint));
        }

        String contentUri = getContentUri();
        if (!contentUri.equals(getDefaultContentUri())) {
            config.addChild(createConfig(ELEMENT_CONTENT_DIR, ATTRIBUTE_SRC, this.contentUri));
        }

        DefaultConfiguration resourceTypesConf = createConfig(ELEMENT_RESOURCE_TYPES);
        config.addChild(resourceTypesConf);
        String[] resourceTypes = getResourceTypeNames();
        for (int i = 0; i < resourceTypes.length; i++) {
            String type = resourceTypes[i];
            DefaultConfiguration resourceTypeConf = createConfig(ELEMENT_RESOURCE_TYPE,
                    ATTRIBUTE_NAME, type);
            if (this.resourceType2workflow.containsKey(type)) {
                resourceTypeConf.setAttribute(ATTRIBUTE_WORKFLOW,
                        (String) this.resourceType2workflow.get(type));
            }
            resourceTypesConf.addChild(resourceTypeConf);
        }

        DefaultConfiguration modulesConf = createConfig(ELEMENT_MODULES);
        config.addChild(modulesConf);
        String[] modules = getModuleNames();
        for (int i = 0; i < modules.length; i++) {
            DefaultConfiguration moduleConf = createConfig(ELEMENT_MODULE, ATTRIBUTE_NAME,
                    modules[i]);
            modulesConf.addChild(moduleConf);
        }

        config.addChild(createConfig(ELEMENT_BREADCRUMB_PREFIX, getBreadcrumbPrefix()));

        DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();
        serializer.setIndent(true);
        ModifiableSource source = null;
        try {
            source = (ModifiableSource) this.sourceResolver.resolveURI(getConfigurationUri());
            serializer.serialize(source.getOutputStream(), config);
        } catch (final Exception e) {
            throw new RuntimeException("Problem with config file: " + getConfigurationUri(), e);
        } finally {
            if (source != null) {
                this.sourceResolver.release(source);
            }
        }
    }

    public String[] getModuleNames() {
        return (String[]) this.modules.toArray(new String[this.modules.size()]);
    }

    public Session getSession() {
        throw new UnsupportedOperationException();
    }

}
