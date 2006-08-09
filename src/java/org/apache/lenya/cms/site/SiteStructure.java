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
package org.apache.lenya.cms.site;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryItem;

/**
 * Object to hold a site structure information.
 *
 * @version $Id$
 */
public interface SiteStructure extends RepositoryItem {
    
    /**
     * @return The repository node the site structure is stored in.
     */
    Node getRepositoryNode();
    
    /**
     * @return The publication.
     */
    Publication getPublication();
    
    /**
     * @return The area.
     */
    String getArea();

    /**
     * @param path The path.
     * @return A site node.
     * @throws SiteException if no node is contained for the path.
     */
    SiteNode getNode(String path) throws SiteException;

    /**
     * Checks if a node is contained for a certain path.
     * @param path The path.
     * @return A boolean value.
     */
    boolean contains(String path);

    /**
     * Checks if the structure contains a node with a certain UUID.
     * @param uuid The UUID.
     * @return A boolean value.
     */
    boolean containsUuid(String uuid);
    
    /**
     * Returns a node for a certain UUID.
     * @param uuid The UUID.
     * @return a site node.
     * @throws SiteException if no node is contained for the UUID.
     */
    SiteNode getByUuid(String uuid) throws SiteException;
    
}
