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
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;

/**
 * <p>
 * A site structure management component.
 * </p>
 * 
 * <p>
 * A site manager has a dependence relation, which is always applied to documents of a single
 * language. This means a document may not require a document of another language. Dependence on a
 * set of resources must be a strict partial order <strong>&lt; </strong>:
 * </p>
 * <ul>
 * <li><em>irreflexive:</em> d <strong>&lt; </strong>d does not hold for any resource d</li>
 * <li><em>antisymmetric:</em> d <strong>&lt; </strong>e and e <strong>&lt; </strong>d implies
 * d=e</li>
 * <li><em>transitive:</em> d <strong>&lt; </strong>e and e <strong>&lt; </strong>f implies d
 * <strong>&lt; </strong>f</li>
 * </ul>
 * 
 * @version $Id$
 */
public interface SiteManager {

    /**
     * The Avalon role.
     */
    String ROLE = SiteManager.class.getName();

    /**
     * Checks if a resource requires another one.
     * @param map The identity map to operate on.
     * @param dependingResource The depending resource.
     * @param requiredResource The required resource.
     * @return A boolean value.
     * @throws SiteException if an error occurs.
     */
    boolean requires(DocumentFactory map, SiteNode dependingResource, SiteNode requiredResource)
            throws SiteException;

    /**
     * Returns the resources which are required by a certain resource.
     * 
     * @param map The identity map to operate on.
     * @param locator The depending locator.
     * @return An array of resources.
     * @throws SiteException if an error occurs.
     */
    DocumentLocator[] getRequiredResources(DocumentFactory map, DocumentLocator locator) throws SiteException;

    /**
     * Returns the resources which require a certain resource.
     * 
     * @param map The identity map to operate on.
     * @param resource The required resource.
     * @return An array of resources.
     * @throws SiteException if an error occurs.
     */
    SiteNode[] getRequiringResources(DocumentFactory map, SiteNode resource) throws SiteException;

    /**
     * Adds a document to the site structure.
     * @param path The path.
     * @param document The document to add.
     * @throws SiteException if the document is already contained.
     */
    void add(String path, Document document) throws SiteException;

    /**
     * Sets a document to the site structure.
     * @param path The path.
     * @param document The document to add.
     * @throws SiteException if the document is already contained or if the path doesn't exist.
     */
    void set(String path, Document document) throws SiteException;

    /**
     * Checks if the site structure contains a certain resource in a certain area.
     * 
     * @param resource The resource.
     * @return A boolean value.
     * @throws SiteException if an error occurs.
     */
    boolean contains(Document resource) throws SiteException;

    /**
     * Checks if the site structure contains any language version of a certain resource in a certain
     * area.
     * 
     * @param resource The resource.
     * @return A boolean value.
     * @throws SiteException if an error occurs.
     */
    boolean containsInAnyLanguage(Document resource) throws SiteException;

    /**
     * Copies a document in the site structure.
     * 
     * @param sourceDocument The source document.
     * @param destinationDocument The destination document.
     * @throws SiteException when something went wrong.
     */
    void copy(Document sourceDocument, Document destinationDocument) throws SiteException;
    
    /**
     * Sets the visibility of a node in the navigation. It is meant to hide specific nodes within
     * the "public" navigation whereas the node is visible within the info/site area.
     * 
     * @param document The document.
     * @param visibleInNav The visibility.
     * @throws SiteException if an error occurs.
     */
    void setVisibleInNav(Document document, boolean visibleInNav) throws SiteException;

    /**
     * Returns the visibility of a node in the navigation.
     * 
     * @param document The document.
     * @return A boolean value.
     * @throws SiteException if an error occurs.
     */
    boolean isVisibleInNav(Document document) throws SiteException;

    /**
     * Returns all documents in a certain area.
     * 
     * @param identityMap The identityMap to use.
     * @param publication The publication.
     * @param area The area.
     * @return An array of documents.
     * @throws SiteException if an error occurs.
     */
    Document[] getDocuments(DocumentFactory identityMap, Publication publication, String area)
            throws SiteException;

    /**
     * Sorts a set of nodes using the "requires" relation.
     * 
     * @param nodes The set.
     * @return A sorted array of nodes.
     * @throws SiteException if an error occurs.
     */
    SiteNode[] sortAscending(SiteNode[] nodes) throws SiteException;

    /**
     * @param map The identity map.
     * @param publication The publication.
     * @param area The area.
     * @return The object that holds the site structure information.
     * @throws SiteException if an error occurs.
     */
    SiteStructure getSiteStructure(DocumentFactory map, Publication publication, String area)
            throws SiteException;

    /**
     * Checks if the document does already exist. If it does, returns a non-existing document with a
     * similar document ID. If it does not, the original document is returned.
     * @param factory The document factory.
     * @param locator The locator.
     * @return A locator.
     * @throws SiteException if the new document could not be built.
     */
    DocumentLocator getAvailableLocator(DocumentFactory factory, DocumentLocator locator) throws SiteException;
    
}
