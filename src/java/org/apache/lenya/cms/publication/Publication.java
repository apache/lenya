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

import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;

/**
 * A Lenya publication.
 */
public interface Publication {

    /**
     * <code>AUTHORING_AREA</code> The authoring area
     */
    String AUTHORING_AREA = "authoring";
    /**
     * <code>STAGING_AREA</code> The staging area
     */
    String STAGING_AREA = "staging";
    /**
     * <code>LIVE_AREA</code> The live area
     */
    String LIVE_AREA = "live";
    /**
     * <code>ADMIN_AREA</code> The admin area
     */
    String ADMIN_AREA = "admin";
    /**
     * <code>ARCHIVE_AREA</code> The archive area
     */
    String ARCHIVE_AREA = "archive";
    /**
     * <code>TRASH_AREA</code> The trash area
     */
    String TRASH_AREA = "trash";
    /**
     * <code>ELEMENT_PATH_MAPPER</code> The path mapper element
     */
    String ELEMENT_PATH_MAPPER = "path-mapper";
    /**
     * <code>ELEMENT_DOCUMENT_BUILDER</code> The document buider element
     */
    String ELEMENT_DOCUMENT_BUILDER = "document-builder";
    /**
     * <code>ELEMENT_SITE_STRUCTURE</code> The site structure element
     */
    String ELEMENT_SITE_STRUCTURE = "site-structure";
    /**
     * <code>ATTRIBUTE_TYPE</code> The type attribute
     */
    String ATTRIBUTE_TYPE = "type";
    /**
     * <code>ATTRIBUTE_SRC</code> The src attribute
     */
    String ATTRIBUTE_SRC = "type";
    /**
     * <code>LANGUAGES</code> The languages
     */
    String LANGUAGES = "languages";
    /**
     * <code>LANGUAGE</code> The language
     */
    String LANGUAGE = "language";
    /**
     * <code>DEFAULT_LANGUAGE_ATTR</code> The default language attribute
     */
    String DEFAULT_LANGUAGE_ATTR = "default";
    /**
     * <code>BREADCRUMB_PREFIX</code> The breadcrumb prefix
     */
    String BREADCRUMB_PREFIX = "breadcrumb-prefix";
    /**
     * <code>SSL_PREFIX</code> The SSL prefix
     */
    String SSL_PREFIX = "ssl-prefix";
    /**
     * <code>LIVE_MOUNT_POINT</code> The live mount point
     */
    String LIVE_MOUNT_POINT = "live-mount-point";
    /**
     * <code>PUBLICATION_PREFIX</code> The publication prefix
     */
    String PUBLICATION_PREFIX = "lenya" + File.separator + "pubs";
    /**
     * <code>PUBLICATION_PREFIX_URI</code> The publication prefix URI
     */
    String PUBLICATION_PREFIX_URI = "lenya/pubs";
    /**
     * <code>CONFIGURATION_PATH</code> The configuration path
     */
    String CONFIGURATION_PATH = "config";
    /**
     * <code>CONTENT_PATH</code> The content path
     */
    String CONTENT_PATH = "content";
    /**
     * <code>PENDING_PATH</code> The pending path
     */
    String PENDING_PATH = "pending";    
    /**
     * <code>DELETE_PATH</code> The delete path
     */
    String DELETE_PATH = "delete";
    /**
     * <code>INFO_AREA_PREFIX</code> The info area prefix
     */
    String INFO_AREA_PREFIX = "info-";
    /**
     * <code>SEARCH_AREA_PREFIX</code> The search area prefix
     */
    String SEARCH_AREA_PREFIX = "search-";
    /**
     * <code>CONFIGURATION_FILE</code> The publication configuration file
     */
    String CONFIGURATION_FILE = CONFIGURATION_PATH + File.separator + "publication.xconf";

    /**
     * Returns the publication ID.
     * @return A string value.
     */
    String getId();

    /**
     * Returns the servlet context this publication belongs to
     * (usually, the <code>webapps/lenya</code> directory).
     * @return A <code>File</code> object.
     */
    File getServletContext();

    /**
     * Returns the publication directory.
     * @return A <code>File</code> object.
     */
    File getDirectory();

    /**
     * Return the directory of a specific area.
     * @param area a <code>File</code> representing the root of the area content directory.
     * @return the directory of the given content area. 
     */
    File getContentDirectory(String area);

    /**
     * Set the path mapper
     * @param mapper The path mapper
     */
    void setPathMapper(DefaultDocumentIdToPathMapper mapper);

    /**
     * Returns the path mapper.
     * @return a <code>DocumentIdToPathMapper</code>
     */
    DocumentIdToPathMapper getPathMapper();

    /**
     * Get the default language
     * @return the default language
     */
    String getDefaultLanguage();

    /**
     * Set the default language
     * @param language the default language
     */
    void setDefaultLanguage(String language);

    /**
     * Get all available languages for this publication
     * @return an <code>Array</code> of languages
     */
    String[] getLanguages();

    /**
     * Get the breadcrumb prefix. It can be used as a prefix if a publication is part of a larger site
     * @return the breadcrumb prefix
     */
    String getBreadcrumbPrefix();

    /**
     * Returns a site manager of this publication for a certain document identity map. 
     * @param map The document identity map.
     * @return A site manager.
     * @throws SiteException if an error occurs 
     */
    SiteManager getSiteManager(DocumentIdentityMap map) throws SiteException;

    /**
     * Returns the document builder of this instance.
     * @return A document builder.
     */
    DocumentBuilder getDocumentBuilder();
    
    /**
     * Copies a document from one location to another location.
     * @param sourceDocument The document to copy.
     * @param destinationDocument The destination document.
     * @throws PublicationException if a document which destinationDocument depends on
     * does not exist.
     */
    void copyDocument(Document sourceDocument, Document destinationDocument)
        throws PublicationException;

    /**
     * Copies a document to another area.
     * @param sourceDocument The document to copy.
     * @param destinationArea The destination area.
     * @throws PublicationException if a document which the
     * destination document depends on does not exist.
     */
    void copyDocumentToArea(Document sourceDocument, String destinationArea)
        throws PublicationException;

    /**
     * Copies a document set to another area.
     * @param documentSet The document set to copy.
     * @param destinationArea The destination area.
     * @throws PublicationException if a document which one of the
     * destination documents depends on does not exist.
     */
    void copyDocumentSetToArea(DocumentSet documentSet, String destinationArea)
        throws PublicationException;
    
    /**
     * Adds a document.
     * @param document The document.
     * @throws PublicationException if the document is already contained.
     */
    void addDocument(Document document) throws PublicationException;
        
    /**
     * Deletes a document.
     * @param document The document to delete.
     * @throws PublicationException when something went wrong.
     */
    void deleteDocument(Document document) throws PublicationException;
    
    /**
     * Moves a document from one location to another.
     * @param sourceDocument The source document.
     * @param destinationDocument The destination document.
     * @throws PublicationException if a document which the
     * destination document depends on does not exist.
     */
    void moveDocument(Document sourceDocument, Document destinationDocument) throws PublicationException;

    /**
     * Creates a version of the document object in another area.
     * @param document The document to clone.
     * @param area The destination area.
     * @return A document.
     * @throws PublicationException when an error occurs.
     */
    Document getAreaVersion(Document document, String area) throws PublicationException;

    /**
     * Returns the proxy which is used for a particular document. 
     * @param document The document.
     * @param isSslProtected A boolean value.
     * @return A proxy or <code>null</code> if no proxy is defined
     * for this version.
     */
    Proxy getProxy(Document document, boolean isSslProtected);
    
}