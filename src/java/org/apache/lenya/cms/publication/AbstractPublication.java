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

/* $Id$  */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.tree.TreeSiteManager;
import org.apache.log4j.Category;

/**
 * A publication.
 */
public abstract class AbstractPublication implements Publication {

    private static Category log = Category.getInstance(AbstractPublication.class);

    private static final String[] areas = { AUTHORING_AREA, STAGING_AREA, LIVE_AREA, ADMIN_AREA,
            ARCHIVE_AREA, TRASH_AREA, INFO_AREA_PREFIX + AUTHORING_AREA,
            INFO_AREA_PREFIX + STAGING_AREA, INFO_AREA_PREFIX + LIVE_AREA,
            INFO_AREA_PREFIX + ARCHIVE_AREA, INFO_AREA_PREFIX + TRASH_AREA };

    private String id;
    private File servletContext;
    private DocumentIdToPathMapper mapper = null;
    private ArrayList languages = new ArrayList();
    private String defaultLanguage = null;
    private String breadcrumbprefix = null;

    private static final String ELEMENT_PROXY = "proxy";
    private static final String ATTRIBUTE_AREA = "area";
    private static final String ATTRIBUTE_URL = "url";
    private static final String ATTRIBUTE_SSL = "ssl";

    /**
     * Creates a new instance of Publication
     * 
     * @param id the publication id
     * @param servletContextPath the servlet context of this publication
     * 
     * @throws PublicationException if there was a problem reading the config file
     */
    protected AbstractPublication(String id, String servletContextPath) throws PublicationException {
        assert id != null;
        this.id = id;

        assert servletContextPath != null;

        File servletContext = new File(servletContextPath);
        assert servletContext.exists();
        this.servletContext = servletContext;

        File configFile = new File(getDirectory(), CONFIGURATION_FILE);
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

        Configuration config;

        String pathMapperClassName = null;
        String documentBuilderClassName = null;

        try {
            config = builder.buildFromFile(configFile);

            try {
                pathMapperClassName = config.getChild(ELEMENT_PATH_MAPPER).getValue();
                Class pathMapperClass = Class.forName(pathMapperClassName);
                this.mapper = (DocumentIdToPathMapper) pathMapperClass.newInstance();
            } catch (ClassNotFoundException e) {
                throw new PublicationException("Cannot instantiate documentToPathMapper: ["
                        + pathMapperClassName + "]", e);
            }

            try {
                Configuration documentBuilderConfiguration = config.getChild(
                        ELEMENT_DOCUMENT_BUILDER, false);
                if (documentBuilderConfiguration != null) {
                    documentBuilderClassName = documentBuilderConfiguration.getValue();
                    Class documentBuilderClass = Class.forName(documentBuilderClassName);
                    this.documentBuilder = (DocumentBuilder) documentBuilderClass.newInstance();
                }
            } catch (ClassNotFoundException e) {
                throw new PublicationException("Cannot instantiate document builder: ["
                        + pathMapperClassName + "]", e);
            }

            Configuration[] languages = config.getChild(LANGUAGES).getChildren();
            for (int i = 0; i < languages.length; i++) {
                Configuration languageConfig = languages[i];
                String language = languageConfig.getValue();
                this.languages.add(language);
                if (languageConfig.getAttribute(DEFAULT_LANGUAGE_ATTR, null) != null) {
                    defaultLanguage = language;
                }
            }

            String siteManagerClass = TreeSiteManager.class.getName();
            Configuration siteStructureConfiguration = config.getChild(ELEMENT_SITE_STRUCTURE,
                    false);
            if (siteStructureConfiguration != null) {
                siteManagerClass = siteStructureConfiguration.getAttribute(ATTRIBUTE_TYPE);
            }
            Class klass = Class.forName(siteManagerClass);
            this.siteManagerClass = klass;

            Configuration[] proxyConfigs = config.getChildren(ELEMENT_PROXY);
            for (int i = 0; i < proxyConfigs.length; i++) {
                String url = proxyConfigs[i].getAttribute(ATTRIBUTE_URL);
                String ssl = proxyConfigs[i].getAttribute(ATTRIBUTE_SSL);
                String area = proxyConfigs[i].getAttribute(ATTRIBUTE_AREA);

                Proxy proxy = new Proxy();
                proxy.setUrl(url);

                Object key = getProxyKey(area, Boolean.valueOf(ssl).booleanValue());
                this.areaSsl2proxy.put(key, proxy);
                if (log.isDebugEnabled()) {
                    log.debug("Adding proxy: [" + proxy + "] for area=[" + area + "] SSL=[" + ssl
                            + "]");
                }
            }

        } catch (PublicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(e);
            throw new PublicationException("Problem with config file: "
                    + configFile.getAbsolutePath(), e);
        }

        breadcrumbprefix = config.getChild(BREADCRUMB_PREFIX).getValue("");

    }

    /**
     * Returns the publication ID.
     * @return A string value.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the servlet context this publication belongs to (usually, the
     * <code>webapps/lenya</code> directory).
     * @return A <code>File</code> object.
     */
    public File getServletContext() {
        return servletContext;
    }

    /**
     * Returns the publication directory.
     * @return A <code>File</code> object.
     */
    public File getDirectory() {
        return new File(getServletContext(), PUBLICATION_PREFIX + File.separator + getId());
    }

    /**
     * Return the directory of a specific area.
     * 
     * @param area a <code>File</code> representing the root of the area content directory.
     * 
     * @return the directory of the given content area.
     */
    public File getContentDirectory(String area) {
        return new File(getDirectory(), CONTENT_PATH + File.separator + area);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param mapper DOCUMENT ME!
     */
    public void setPathMapper(DefaultDocumentIdToPathMapper mapper) {
        assert mapper != null;
        this.mapper = mapper;
    }

    /**
     * Returns the path mapper.
     * 
     * @return a <code>DocumentIdToPathMapper</code>
     */
    public DocumentIdToPathMapper getPathMapper() {
        return mapper;
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
     * 
     * @return the default language
     */
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * Set the default language
     * 
     * @param language the default language
     */
    public void setDefaultLanguage(String language) {
        defaultLanguage = language;
    }

    /**
     * Get all available languages for this publication
     * 
     * @return an <code>Array</code> of languages
     */
    public String[] getLanguages() {
        return (String[]) languages.toArray(new String[languages.size()]);
    }

    /**
     * Get the breadcrumb prefix. It can be used as a prefix if a publication is part of a larger
     * site
     * 
     * @return the breadcrumb prefix
     */
    public String getBreadcrumbPrefix() {
        return breadcrumbprefix;
    }

    private DocumentBuilder documentBuilder;

    /**
     * Returns the document builder of this instance.
     * @return A document builder.
     */
    public DocumentBuilder getDocumentBuilder() {

        if (documentBuilder == null) {
            throw new IllegalStateException(
                    "The document builder was not defined in publication.xconf!");
        }

        return documentBuilder;
    }

    /**
     * Creates a version of the document object in another area.
     * @param document The document to clone.
     * @param area The destination area.
     * @return A document.
     * @throws PublicationException when an error occurs.
     */
    public Document getAreaVersion(Document document, String area) throws PublicationException {
        DocumentBuilder builder = getDocumentBuilder();
        String url = builder
                .buildCanonicalUrl(this, area, document.getId(), document.getLanguage());
        Document destinationDocument = builder.buildDocument(document.getIdentityMap(), url);
        return destinationDocument;
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

    /**
     * Template method to copy a document. Override {@link #copyDocumentSource(Document, Document)}
     * to implement access to a custom repository.
     * @see org.apache.lenya.cms.publication.Publication#copyDocument(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copyDocument(Document sourceDocument, Document destinationDocument)
            throws PublicationException {

        copyDocumentSource(sourceDocument, destinationDocument);
        getSiteManager(sourceDocument.getIdentityMap()).copy(sourceDocument, destinationDocument);
    }

    /**
     * Copies a document source.
     * @param sourceDocument The source document.
     * @param destinationDocument The destination document.
     * @throws PublicationException when something went wrong.
     */
    protected abstract void copyDocumentSource(Document sourceDocument, Document destinationDocument)
            throws PublicationException;

    /**
     * @see org.apache.lenya.cms.publication.Publication#deleteDocument(org.apache.lenya.cms.publication.Document)
     */
    public void deleteDocument(Document document) throws PublicationException {
        if (!document.exists()) {
            throw new PublicationException("Document [" + document + "] does not exist!");
        }
        getSiteManager(document.getIdentityMap()).delete(document);
        deleteDocumentSource(document);
    }

    /**
     * Deletes the source of a document.
     * @param document The document to delete.
     * @throws PublicationException when something went wrong.
     */
    protected abstract void deleteDocumentSource(Document document) throws PublicationException;

    /**
     * @see org.apache.lenya.cms.publication.Publication#moveDocument(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void moveDocument(Document sourceDocument, Document destinationDocument)
            throws PublicationException {
        copyDocument(sourceDocument, destinationDocument);
        deleteDocument(sourceDocument);
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

        Object key = getProxyKey(document.getArea(), isSslProtected);
        Proxy proxy = (Proxy) this.areaSsl2proxy.get(key);

        if (log.isDebugEnabled()) {
            log.debug("Resolving proxy for [" + document + "] SSL=[" + isSslProtected + "]");
            log.debug("Resolved proxy: [" + proxy + "]");
        }

        return proxy;
    }

    private Class siteManagerClass;

    /**
     * @see org.apache.lenya.cms.publication.Publication#getSiteManager(org.apache.lenya.cms.publication.DocumentIdentityMap)
     */
    public SiteManager getSiteManager(DocumentIdentityMap map) throws SiteException {
        SiteManager manager;
        try {
            manager = (SiteManager) this.siteManagerClass.newInstance();
        } catch (Exception e) {
            throw new SiteException(e);
        }
        manager.setIdentityMap(map);
        return manager;
    }
}