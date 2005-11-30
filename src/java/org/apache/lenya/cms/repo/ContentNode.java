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
package org.apache.lenya.cms.repo;

/**
 * Content node.
 */
public interface ContentNode {

    /**
     * @return The documents belonging to this node.
     * @throws RepositoryException if an error occurs.
     */
    Document[] getDocuments() throws RepositoryException;

    /**
     * Adds a document.
     * @param language The language of the document.
     * @param label The label.
     * @return the added document.
     * @throws RepositoryException if the language version already exists.
     */
    Document addDocument(String language, String label) throws RepositoryException;

    /**
     * @param document The document.
     * @throws RepositoryException
     */
    void removeDocument(Document document) throws RepositoryException;

    /**
     * Returns a document of a specific language.
     * @param language The document's language.
     * @return A document.
     * @throws RepositoryException if the language version does not exist.
     */
    Document getDocument(String language) throws RepositoryException;

    /**
     * @return The document type which this node's documents belong to.
     * @throws RepositoryException if an error occurs.
     */
    DocumentType getDocumentType() throws RepositoryException;
    
    /**
     * @return The ID of this node.
     * @throws RepositoryException if an error occurs.
     */
    String getNodeId() throws RepositoryException;
    
    /**
     * @return If the node should be visible in the navigation.
     * @throws RepositoryException if an error occurs.
     */
    boolean isVisibleInNav() throws RepositoryException;
    
    /**
     * @param visible If the node should be visible in the navigation.
     * @throws RepositoryException if an error occurs.
     */
    void setVisibleInNav(boolean visible) throws RepositoryException;
    
}
