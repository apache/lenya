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

import org.apache.lenya.cms.publication.util.DocumentSet;

/**
 * Helper to manage documents. It takes care of attachments etc.
 * 
 * @version $Id$
 */
public interface DocumentManager {

    /**
     * The Avalon component role.
     */
    String ROLE = DocumentManager.class.getName();

    /**
     * Checks if a document exists.
     * @param factory The document factory.
     * @param pub The publication.
     * @param area The area.
     * @param uuid The UUID.
     * @param language The language.
     * @return A boolean value.
     * @throws PublicationException if an error occurs.
     */
    boolean exists(DocumentFactory factory, Publication pub, String area, String uuid,
            String language) throws PublicationException;

    /**
     * Copies a document from one location to another location.
     * @param sourceDocument The document to copy.
     * @param destination The destination document.
     * @throws PublicationException if a document which destinationDocument depends on does not
     *             exist.
     */
    void copy(Document sourceDocument, DocumentLocator destination) throws PublicationException;

    /**
     * Copies a document to another area.
     * @param sourceDocument The document to copy.
     * @param destinationArea The destination area.
     * @throws PublicationException if a document which the destination document depends on does not
     *             exist.
     */
    void copyToArea(Document sourceDocument, String destinationArea) throws PublicationException;

    /**
     * Copies a document set to another area.
     * @param documentSet The document set to copy.
     * @param destinationArea The destination area.
     * @throws PublicationException if a document which one of the destination documents depends on
     *             does not exist.
     */
    void copyToArea(DocumentSet documentSet, String destinationArea) throws PublicationException;

    /**
     * Creates a new document in the same publication the <code>parentDocument</code> belongs to
     * with the given parameters:
     * 
     * @param sourceDocument The document to initialize the contents and meta data from.
     * @param area The target area.
     * @param path The target path.
     * @param language The target language.
     * @param extension The extension to use for the document source.
     * @param navigationTitle navigation title
     * @param visibleInNav determines the visibility of a node in the navigation
     * @return The added document.
     * 
     * @throws DocumentBuildException if the document can not be created
     * @throws PublicationException if the document is already contained.
     */
    Document add(Document sourceDocument, String area, String path, String language,
            String extension, String navigationTitle, boolean visibleInNav)
            throws DocumentBuildException, PublicationException;

    /**
     * Creates a new document with the given parameters:
     * @param factory The document factory.
     * @param resourceType the document type (aka resource type) of the new document
     * @param contentSourceUri The URI to read the content from.
     * @param pub The publication.
     * @param area The area.
     * @param path The path.
     * @param language The language.
     * @param extension The extension to use for the document source, without the leading dot.
     * @param navigationTitle The navigation title.
     * @param visibleInNav The navigation visibility.
     * @return The added document.
     * 
     * @throws DocumentBuildException if the document can not be created
     * @throws PublicationException if the document is already contained.
     */
    Document add(DocumentFactory factory, ResourceType resourceType, String contentSourceUri,
            Publication pub, String area, String path, String language, String extension,
            String navigationTitle, boolean visibleInNav) throws DocumentBuildException,
            PublicationException;

    /**
     * Creates a new document without adding it to the site structure.
     * @param factory The document factory.
     * @param resourceType the document type (aka resource type) of the new document
     * @param contentSourceUri The URI to read the content from.
     * @param pub The publication.
     * @param area The area.
     * @param language The language.
     * @param extension The extension to use for the document source, without the leading dot.
     * @return The added document.
     * 
     * @throws DocumentBuildException if the document can not be created
     * @throws PublicationException if the document is already contained.
     */
    Document add(DocumentFactory factory, ResourceType resourceType, String contentSourceUri,
            Publication pub, String area, String language, String extension)
            throws DocumentBuildException, PublicationException;

    /**
     * Adds a new version of a document with a different language and / or in a different area.
     * 
     * @param sourceDocument The document to initialize the contents and meta data from.
     * @param area The area.
     * @param language The language of the new document.
     * @return The added document.
     * 
     * @throws DocumentBuildException if the document can not be created
     * @throws PublicationException if the document is already contained.
     */
    Document addVersion(Document sourceDocument, String area, String language)
            throws DocumentBuildException, PublicationException;

    /**
     * Adds a new version of a document with a different language and / or in a different area.
     * 
     * @param sourceDocument The document to initialize the contents and meta data from.
     * @param area The area.
     * @param language The language of the new document.
     * @param addToSite If the new version should be added to the site structure.
     * @return The added document.
     * 
     * @throws DocumentBuildException if the document can not be created
     * @throws PublicationException if the document is already contained.
     */
    Document addVersion(Document sourceDocument, String area, String language, boolean addToSite)
            throws DocumentBuildException, PublicationException;

    /**
     * Deletes a document from the content repository and from the site structure.
     * @param document The document to delete.
     * @throws PublicationException when something went wrong.
     */
    void delete(Document document) throws PublicationException;

    /**
     * Moves a document from one location to another.
     * @param sourceDocument The source document.
     * @param destination The destination document.
     * @throws PublicationException if a document which the destination document depends on does not
     *             exist.
     */
    void move(Document sourceDocument, DocumentLocator destination) throws PublicationException;

    /**
     * Moves a document set from one location to another. A source is moved to the destination of
     * the same position in the set.
     * @param sources The source documents.
     * @param destinations The destination documents.
     * @throws PublicationException if a document which the destination document depends on does not
     *             exist.
     */
    void move(DocumentSet sources, DocumentSet destinations) throws PublicationException;

    /**
     * Copies a document set from one location to another. A source is copied to the destination of
     * the same position in the set.
     * @param sources The source documents.
     * @param destinations The destination documents.
     * @throws PublicationException if a document which the destination document depends on does not
     *             exist.
     */
    void copy(DocumentSet sources, DocumentSet destinations) throws PublicationException;

    /**
     * Moves a document to another location, incl. all requiring documents. If a sitetree is used,
     * this means that the whole subtree is moved.
     * @param sourceArea The source area.
     * @param sourcePath The source path.
     * @param targetArea The target area.
     * @param targetPath The target path.
     * @throws PublicationException if an error occurs.
     */
    void moveAll(Area sourceArea, String sourcePath, Area targetArea, String targetPath)
            throws PublicationException;

    /**
     * Moves all language versions of a document to another location.
     * @param sourceArea The source area.
     * @param sourcePath The source path.
     * @param targetArea The target area.
     * @param targetPath The target path.
     * @throws PublicationException if the documents could not be moved.
     */
    void moveAllLanguageVersions(Area sourceArea, String sourcePath, Area targetArea,
            String targetPath) throws PublicationException;

    /**
     * Copies a document to another location, incl. all requiring documents. If a sitetree is used,
     * this means that the whole subtree is copied.
     * @param sourceArea The source area.
     * @param sourcePath The source path.
     * @param targetArea The target area.
     * @param targetPath The target path.
     * @throws PublicationException if an error occurs.
     */
    void copyAll(Area sourceArea, String sourcePath, Area targetArea, String targetPath)
            throws PublicationException;

    /**
     * Copies all language versions of a document to another location.
     * @param sourceArea The source area.
     * @param sourcePath The source path.
     * @param targetArea The target area.
     * @param targetPath The target path.
     * @throws PublicationException if the documents could not be copied.
     */
    void copyAllLanguageVersions(Area sourceArea, String sourcePath, Area targetArea,
            String targetPath) throws PublicationException;

    /**
     * Deletes a document, incl. all requiring documents. If a sitetree is used, this means that the
     * whole subtree is deleted.
     * @param document The document.
     * @throws PublicationException if an error occurs.
     */
    void deleteAll(Document document) throws PublicationException;

    /**
     * Deletes all language versions of a document.
     * @param document The document.
     * @throws PublicationException if the documents could not be copied.
     */
    void deleteAllLanguageVersions(Document document) throws PublicationException;

    /**
     * Deletes a set of documents.
     * @param documents The documents.
     * @throws PublicationException if an error occurs.
     */
    void delete(DocumentSet documents) throws PublicationException;

}