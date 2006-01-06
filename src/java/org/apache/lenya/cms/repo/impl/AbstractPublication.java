/*
 * Copyright  1999-2004 The Apache Software Foundation
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
package org.apache.lenya.cms.repo.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.cms.proxy.Proxy;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.DocumentType;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Abstract publication implementation.
 */
public abstract class AbstractPublication implements Publication, LogEnabled {

    private ArrayList languages = new ArrayList();
    private String defaultLanguage = null;
    private String instantiatorHint = null;
    private String siteManagerName = null;

    public String getSiteManagerHint() {
        try {
            loadConfiguration();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        return this.siteManagerName;
    }

    public String getInstantiatorHint() {
        try {
            loadConfiguration();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        return this.instantiatorHint;
    }

    public String getDefaultLanguage() {
        try {
            loadConfiguration();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        return this.defaultLanguage;
    }

    public String[] getLanguages() {
        try {
            loadConfiguration();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        return (String[]) this.languages.toArray(new String[this.languages.size()]);
    }

    /**
     * Loads the configuration.
     * @throws RepositoryException if an error occurs.
     */
    protected void loadConfiguration() throws RepositoryException {

        if (isConfigLoaded) {
            return;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Loading configuration for publication [" + getPublicationId() + "]");
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

        try {
            config = builder.buildFromFile(configFile);

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
                this.instantiatorHint = templateInstantiatorConfig.getAttribute(ATTRIBUTE_NAME);
            }

            Configuration[] resourceTypeConfigs = config.getChildren(ELEMENT_RESOURCE_TYPE);
            for (int i = 0; i < resourceTypeConfigs.length; i++) {
                String name = resourceTypeConfigs[i].getAttribute(ATTRIBUTE_NAME);
                this.resourceTypes.add(name);

                String workflow = resourceTypeConfigs[i].getAttribute(ATTRIBUTE_WORKFLOW, null);
                if (workflow != null) {
                    this.doctype2workflow.put(name, workflow);
                }
            }

        } catch (final Exception e) {
            throw new RuntimeException("Problem with config file: " + configFile.getAbsolutePath(),
                    e);
        }

        isConfigLoaded = true;
    }

    /**
     * @return The configuration file.
     */
    public abstract File getConfigurationFile();

    private static final String ELEMENT_PROXY = "proxy";
    private static final String ATTRIBUTE_AREA = "area";
    private static final String ATTRIBUTE_URL = "url";
    private static final String ATTRIBUTE_SSL = "ssl";

    private boolean isConfigLoaded = false;

    /**
     * <code>CONFIGURATION_PATH</code> The configuration path
     */
    private static String CONFIGURATION_PATH = "config";

    /**
     * <code>CONFIGURATION_FILE</code> The publication configuration file
     */
    protected static final String CONFIGURATION_FILE = CONFIGURATION_PATH + File.separator
            + "publication.xconf";

    private static final String ELEMENT_TEMPLATES = "templates";
    private static final String ELEMENT_TEMPLATE = "template";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ELEMENT_SITE_MANAGER = "site-manager";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ELEMENT_TEMPLATE_INSTANTIATOR = "template-instantiator";
    private static final String LANGUAGES = "languages";
    private static final String DEFAULT_LANGUAGE_ATTR = "default";
    private static final String ELEMENT_RESOURCE_TYPE = "resource-type";
    private static final String ATTRIBUTE_WORKFLOW = "workflow";

    private Logger logger;

    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    protected Logger getLogger() {
        if (this.logger == null) {
            this.logger = new ConsoleLogger();
        }
        return this.logger;
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

    public Proxy getProxy(Document document, boolean isSslProtected) throws RepositoryException {
        try {
            loadConfiguration();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        Object key = getProxyKey(document.getContentNode().getContent().getArea().getAreaID(),
                isSslProtected);
        Proxy proxy = (Proxy) this.areaSsl2proxy.get(key);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Resolving proxy for [" + document + "] SSL=[" + isSslProtected + "]");
            getLogger().debug("Resolved proxy: [" + proxy + "]");
        }

        return proxy;
    }

    private String[] templates;

    public String[] getTemplateIds() {
        try {
            loadConfiguration();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        List list = Arrays.asList(this.templates);
        return (String[]) list.toArray(new String[list.size()]);
    }

    private Map doctype2workflow = new HashMap();

    public String getWorkflowSchema(DocumentType docType) {
        String workflow = (String) this.doctype2workflow.get(docType.getName());
        return workflow;
    }

    private List resourceTypes = new ArrayList();

    public String[] getResourceTypeNames() {
        return (String[]) resourceTypes.toArray(new String[resourceTypes.size()]);
    }

}
