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

import org.apache.lenya.cms.repo.metadata.MetaDataOwner;

/**
 * Content node.
 */
public interface Asset extends MetaDataOwner {

    /**
     * @return The documents belonging to this node.
     * @throws RepositoryException if an error occurs.
     */
    Translation[] getTranslations() throws RepositoryException;

    /**
     * Adds a document.
     * @param language The language of the document.
     * @param label The label.
     * @param mimeType The mime type.
     * @return the added document.
     * @throws RepositoryException if the language version already exists.
     */
    Translation addTranslation(String language, String label, String mimeType) throws RepositoryException;

    /**
     * @param document The document.
     * @throws RepositoryException
     */
    void removeTranslation(Translation document) throws RepositoryException;

    /**
     * Returns a document of a specific language.
     * @param language The document's language.
     * @return A document.
     * @throws RepositoryException if the language version does not exist.
     */
    Translation getTranslation(String language) throws RepositoryException;

    /**
     * @return The document type which this node's documents belong to.
     * @throws RepositoryException if an error occurs.
     */
    AssetType getAssetType() throws RepositoryException;

    /**
     * @return The ID of this node. The ID is unique among all content nodes in this area.
     *         Corresponding content nodes in other areas have the same ID.
     * @throws RepositoryException if an error occurs.
     */
    String getAssetId() throws RepositoryException;

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

    /**
     * Removes the node and all its documents.
     * @throws RepositoryException if the node is still referenced from the site structure.
     */
    void remove() throws RepositoryException;

    /**
     * @return The content this asset belongs to.
     * @throws RepositoryException if an error occurs.
     */
    Content getContent() throws RepositoryException;
    
}
