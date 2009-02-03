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

/* $Id$  */

package org.apache.lenya.cms.publication;

import java.io.File;

import org.apache.lenya.cms.repository.RepositoryItem;

/**
 * A Lenya publication.
 */
public interface Publication extends RepositoryItem {

    /**
     * <code>AUTHORING_AREA</code> The authoring area
     */
    String AUTHORING_AREA = "authoring";
    /**
     * <code>DAV_AREA</code> The webDAV authoring area
     */
    String DAV_AREA = "webdav";
    /**
     * <code>STAGING_AREA</code> The staging area
     */
    String STAGING_AREA = "staging";
    /**
     * <code>LIVE_AREA</code> The live area
     */
    String LIVE_AREA = "live";
    /**
     * <code>ARCHIVE_AREA</code> The archive area
     */
    String ARCHIVE_AREA = "archive";
    /**
     * <code>TRASH_AREA</code> The trash area
     */
    String TRASH_AREA = "trash";
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
     * The item type.
     */
    String ITEM_TYPE = "publication";

    /**
     * Returns the publication ID.
     * @return A string value.
     */
    String getId();
    
    /**
     * @return The name of the publication as specified in the configuration file.
     */
    String getName();
    
    /**
     * @param name The name of the publication.
     */
    void setName(String name);

    /**
     * Returns the servlet context this publication belongs to (usually, the
     * <code>webapps/lenya</code> directory).
     * @return A <code>File</code> object.
     */
    File getServletContext();

    /**
     * @return if this publication exists.
     */
    boolean exists();

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
     * @param language The default language.
     * @throws IllegalArgumentException if the publication doesn't contain this language.
     */
    void setDefaultLanguage(String language);

    /**
     * Get all available languages for this publication
     * @return an <code>Array</code> of languages
     */
    String[] getLanguages();
    
    /**
     * @param language The language to add.
     * @throws IllegalArgumentException if the language is invalid or already contained.
     */
    void addLanguage(String language);
    
    /**
     * @param language The language to remove.
     * @throws IllegalArgumentException if the language is not contained or if the language
     * is the default language.
     */
    void removeLanguage(String language);

    /**
     * Get the breadcrumb prefix. It can be used as a prefix if a publication is part of a larger
     * site
     * @return the breadcrumb prefix
     */
    String getBreadcrumbPrefix();

    /**
     * Returns the hint of the site manager service that is used by this publication.
     * @return A hint to use for service selection.
     */
    String getSiteManagerHint();

    /**
     * Returns the document builder of this instance.
     * @return A hint to use for service selection.
     */
    DocumentBuilder getDocumentBuilder();

    /**
     * Returns the publication template instantiator hint. If the publication does not allow
     * templating, <code>null</code> is returned.
     * @return A hint to use for service selection.
     */
    String getInstantiatorHint();

    /**
     * Returns the content dir.
     * If the publication does not specify a content dir, then <code>null</code> is returned.
     * @return A base directory where all the content is located.
     */
    String getContentDir();

    /**
     * Returns the proxy which is used for a particular document's area
     * (see {@link #getProxy(String, boolean)}).
     * @param document The document.
     * @param isSslProtected A boolean value.
     * @return A proxy.
     */
    Proxy getProxy(Document document, boolean isSslProtected);

    /**
     * <p>
     * Returns the proxy which is used for the given area and the ssl parameter.
     * </p>
     * <p>
     * If no proxy is defined in the publication configuration file, a default proxy
     * with the URL <code>{contextPath}/{pubId}/{area}</code> is returned.
     * </p>
     * <p>
     * If the area string is "root", the global proxy is returned. If no global proxy
     * is defined, a default global proxy with the URL <code>{contextPath}</code>
     * is returned. 
     * </p>
     * @param area area
     * @param isSslProtected A boolean value.
     * @return A proxy.
     */
    Proxy getProxy(String area, boolean isSslProtected);

    /**
     * @return The ID of the template of the publication or <code>null</code> if the
     * publication has no template.
     */
    String getTemplateId();

    /**
     * @return the URI base for this publication
     */
    String getSourceURI();

    /**
     * @param area The area.
     * @return the URI base under which contents in this publication are accessed
     */
    String getContentURI(String area);

    /**
     * @param resourceType The resource type or <code>null</code> if no workflow is registered for
     *            this resource type.
     * @return The workflow schema to use for this resource type.
     */
    String getWorkflowSchema(ResourceType resourceType);
    
    /**
     * @return The resource types that are supported by this publication.
     */
    String[] getResourceTypeNames();
    
    /**
     * @param name The name.
     * @return An area object.
     * @throws PublicationException if an error occurs.
     */
    Area getArea(String name) throws PublicationException;
    
    /**
     * @return The names of all available areas.
     */
    String[] getAreaNames();
    
    /**
     * @return The document factory.
     */
    public DocumentFactory getFactory();
    
    /**
     * Saves the configuration of this publication.
     */
    void saveConfiguration();
    
    /**
     * @return The names of all modules which are used by this publication.
     */
    String[] getModuleNames();

}
