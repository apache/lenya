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

import java.util.Map;
import org.apache.lenya.cms.publication.util.DocumentSet;

/**
 * Helper to manage documents. It takes care of workflow, attachments etc.
 * 
 * @version $Id$
 */
public interface DocumentManager {

    /**
     * The Avalon component role.
     */
    String ROLE = DocumentManager.class.getName();

    /**
     * Copies a document from one location to another location.
     * @param sourceDocument The document to copy.
     * @param destinationDocument The destination document.
     * @throws PublicationException if a document which destinationDocument depends on does not
     *             exist.
     */
    void copy(Document sourceDocument, Document destinationDocument) throws PublicationException;

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
     * Creates a new document in the same publication the <code>parentDocument</code>
     * belongs to with the given parameters:
     *
     * @param parentDocument The parent document.
     * @param newDocumentNodeName the name of the node representing the new document
     * @param newDocumentId the id of the new document
     * @param documentTypeName the document type (aka resource type) of the new document 
     * @param language language of the new document
     * @param navigationTitle navigation title
     * @param initialContentsURI an URI from which initial contents for the 
     * new document can be read. Optional parameter; may be set to <code>null</code>, 
     * in which case the default initial sample as configured in 
     * <code>doctypes.xconf</code> will be used.
     * @param parameters any parameters the caller needs to pass to the creator
     * @return A document.
     * 
     * @throws DocumentBuildException if the document can not be created
     * @throws PublicationException if the document is already contained.
     */
    Document add(Document parentDocument, 
                 String newDocumentNodeName,
                 String newDocumentId, 
                 String documentTypeName, 
                 String language, 
                 String navigationTitle,
                 String initialContentsURI,
                 Map parameters) 
       throws DocumentBuildException, PublicationException;

    /**
     * Deletes a document.
     * @param document The document to delete.
     * @throws PublicationException when something went wrong.
     */
    void delete(Document document) throws PublicationException;

    /**
     * Moves a document from one location to another.
     * @param sourceDocument The source document.
     * @param destinationDocument The destination document.
     * @throws PublicationException if a document which the destination document depends on does not
     *             exist.
     */
    void move(Document sourceDocument, Document destinationDocument) throws PublicationException;

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
     * Checks if a document can be created. This is the case if the document ID is valid and the
     * document does not yet exist.
     * @param identityMap The identity map to use.
     * @param publication The publication.
     * @param area The area.
     * @param parent The parent of the document or <code>null</code> if the document has no
     *            parent.
     * @param nodeId The node ID.
     * @param language The language.
     * @return An array of error messages. The array is empty if the document can be created.
     * @throws DocumentBuildException if an error occurs.
     * @throws DocumentException if an error occurs.
     */
    String[] canCreate(DocumentIdentityMap identityMap, Publication publication, String area,
            Document parent, String nodeId, String language) throws DocumentBuildException,
            DocumentException;

    /**
     * Moves a document to another location, incl. all requiring documents. If a sitetree is used,
     * this means that the whole subtree is moved.
     * @param source The source document.
     * @param target The target document.
     * @throws PublicationException if an error occurs.
     */
    void moveAll(Document source, Document target) throws PublicationException;

    /**
     * Moves all language versions of a document to another location.
     * @param source The source.
     * @param target The target.
     * @throws PublicationException if the documents could not be moved.
     */
    void moveAllLanguageVersions(Document source, Document target) throws PublicationException;

    /**
     * Copies a document to another location, incl. all requiring documents. If a sitetree is used,
     * this means that the whole subtree is copied.
     * @param source The source document.
     * @param target The target document.
     * @throws PublicationException if an error occurs.
     */
    void copyAll(Document source, Document target) throws PublicationException;

    /**
     * Copies all language versions of a document to another location.
     * @param source The source.
     * @param target The target.
     * @throws PublicationException if the documents could not be copied.
     */
    void copyAllLanguageVersions(Document source, Document target) throws PublicationException;

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
