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
package org.apache.lenya.cms.site;

import org.apache.lenya.cms.publication.Document;
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
     * @return All nodes in this structure.
     */
    SiteNode[] getNodes();

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
     * Checks if a link is contained for a certain path and language.
     * @param path The path.
     * @param language The language.
     * @return A boolean value.
     */
    boolean contains(String path, String language);

    /**
     * Checks if the structure contains a link with a certain UUID and language.
     * @param uuid The UUID.
     * @param language The language.
     * @return A boolean value.
     */
    boolean containsByUuid(String uuid, String language);

    /**
     * Checks if the structure contains any language version of a document.
     * @param uuid The uuid.
     * @return A boolean value.
     */
    boolean containsInAnyLanguage(String uuid);

    /**
     * Returns a node for a certain UUID.
     * @param uuid The UUID.
     * @param language The language.
     * @return a link.
     * @throws SiteException if no node is contained for the UUID.
     */
    Link getByUuid(String uuid, String language) throws SiteException;

    /**
     * Adds a link to a document.
     * @param path The path.
     * @param doc The document.
     * @return A link.
     * @throws SiteException if the document is already contained or the node
     *         for this path already contains a link for this language.
     */
    Link add(String path, Document doc) throws SiteException;

    /**
     * Adds a site node.
     * @param path The path.
     * @return A site node.
     * @throws SiteException if the path is already contained.
     */
    SiteNode add(String path) throws SiteException;

    /**
     * Adds a site node before a specific other node.
     * @param path The path.
     * @param followingSiblingPath The path of the node which will be the
     *        following sibling of the node to insert.
     * @return A site node.
     * @throws SiteException if the path is already contained.
     */
    SiteNode add(String path, String followingSiblingPath) throws SiteException;
    
    /**
     * @return The top level nodes.
     */
    SiteNode[] getTopLevelNodes();

}
