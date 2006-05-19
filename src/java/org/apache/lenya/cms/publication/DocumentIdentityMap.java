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
package org.apache.lenya.cms.publication;

import org.apache.lenya.transaction.IdentifiableFactory;

/**
 * A DocumentIdentityMap avoids the multiple instanciation of a document object.
 * 
 * @version $Id$
 */
public interface DocumentIdentityMap extends IdentifiableFactory {

    /**
     * Returns a document.
     * @param publication The publication.
     * @param area The area.
     * @param documentId The document ID.
     * @param language The language.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    Document get(Publication publication, String area, String documentId, String language)
            throws DocumentBuildException;

    /**
     * Returns the document identified by a certain web application URL.
     * @param webappUrl The web application URL.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    Document getFromURL(String webappUrl) throws DocumentBuildException;

    /**
     * Builds a clone of a document for another language.
     * @param document The document to clone.
     * @param language The language of the target document.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    Document getLanguageVersion(Document document, String language) throws DocumentBuildException;

    /**
     * Builds a clone of a document for another area.
     * @param document The document to clone.
     * @param area The area of the target document.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    Document getAreaVersion(Document document, String area) throws DocumentBuildException;

    /**
     * Returns the parent of a document.
     * @param document A document.
     * @return A document or <code>null</code> if the document has no parent.
     * @throws DocumentBuildException if an error occurs.
     */
    Document getParent(Document document) throws DocumentBuildException;

    /**
     * Returns the parent of a document.
     * @param document A document.
     * @param defaultDocumentId The document ID to use if the document has no parent.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    Document getParent(Document document, String defaultDocumentId) throws DocumentBuildException;

    /**
     * Builds a document for the default language.
     * @param publication The publication.
     * @param area The area.
     * @param documentId The document ID.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    Document get(Publication publication, String area, String documentId)
            throws DocumentBuildException;

    /**
     * Checks if a string represents a valid document ID.
     * @param id The string.
     * @return A boolean value.
     */
    boolean isValidDocumentId(String id);

    /**
     * Checks if a webapp URL represents a document.
     * @param webappUrl A web application URL.
     * @return A boolean value.
     * @throws DocumentBuildException if an error occurs.
     */
    boolean isDocument(String webappUrl) throws DocumentBuildException;
    
    /**
     * Generic method to build objects using the identity map.
     * @TODO We have to find a better solution for this.
     * @param factory The factory.
     * @param key The key.
     * @return An object.
     * @throws Exception if an error occurs.
     */
    Object buildObject(Object factory, String key) throws Exception;

}
