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

package org.apache.lenya.cms.site.tree;

import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * A sitetree.
 * 
 * @version $Id: SiteTree.java 177923 2005-05-23 05:15:51Z gregor $
 */
public interface SiteTree extends SiteStructure {

    /**
     * The type of sitetree identifiable objects.
     */
    String IDENTIFIABLE_TYPE = "site";

    /**
     * Add a node.
     * 
     * @param parentid where the node is to be added
     * @param id e.g. "concepts"
     * @param labels the labels of the node that is to be added
     * @param visibleInNav the visibility of a node in the navigation. It is meant to hide specific nodes within the "public" navigation whereas the node is visible within the info/site area.
     * 
     * @throws SiteException if the addition failed
     */
    void addNode(String parentid, String id, Label[] labels, boolean visibleInNav) throws SiteException;

    /**
     * Add a node. TODO: Lenya 1.2.X supports argument visibleInNav
     * 
     * @param parentid the node where the new node is to be inserted
     * @param id the node id
     * @param labels the labels
     * @param visibleInNav determines the visibility of a node in the navigation. It is meant to hide specific nodes within the "public" navigation whereas the node is visible within the info/site area.
     * @param href the href of the new node (internal and external references)     
     * @param suffix the suffix of the new node
     * @param link Visibility of link respectively href. It is meant to support "grouping" nodes in the navigation which do not relate to a document (internal) or external link (www).
     * 
     * @throws SiteException if the addition failed
     */
    void addNode(String parentid, String id, Label[] labels, boolean visibleInNav, String href, String suffix,
            boolean link) throws SiteException;

    /**
     * Insert a node before a given node
     * 
     * @param parentid the node where the new node is to be inserted
     * @param id the node id
     * @param labels the labels
     * @param visibleInNav determines the visibility of a node in the navigation
     * @param href the href of the new node
     * @param suffix the suffix of the new node
     * @param link the link
     * @param refDocumentId document-id of the node, before which the new node will be inserted.
     * 
     * @throws SiteException if the addition failed
     */
    void addNode(String parentid, String id, Label[] labels, boolean visibleInNav, String href, String suffix,
            boolean link, String refDocumentId) throws SiteException;

    /**
     * Add a node. Compute the parent id and the id of the node from the document-id
     * 
     * @param documentid the document-id of the new node. From this the parent-id and the id are
     *            computed
     * @param labels the labels
     * @param visibleInNav determines the visibility of a node in the navigation
     * @param href the href
     * @param suffix the suffix
     * @param link the link
     * 
     * @throws SiteException if the addition failed
     */
    void addNode(String documentid, Label[] labels, boolean visibleInNav, String href, String suffix, boolean link)
            throws SiteException;

    /**
     * Insert a node before a given node Compute the parent id and the id of the node from the
     * document-id
     * 
     * @param documentid the document-id of the new node. From this the parent-id and the id are
     *            computed
     * @param labels the labels
     * @param visibleInNav determines the visibility of a node in the navigation
     * @param href the href
     * @param suffix the suffix
     * @param link the link
     * @param refDocumentId document-id of the node, before which the new node will be inserted.
     * 
     * @throws SiteException if the addition failed
     */
    void addNode(String documentid, Label[] labels, boolean visibleInNav, String href, String suffix, boolean link,
            String refDocumentId) throws SiteException;

    /**
     * Add a node. This method is typically used when publishing, i.e. when copying a node from the
     * authoring tree to the live tree. The existing node already has a parent node (in the
     * authoring tree). The node that is added will be a copy of the original node and will be
     * inserted at the same parentid as the original node.
     * 
     * @param node the <code>SiteTreeNode</code> that is to be added
     * 
     * @throws SiteException if the addition failed
     */
    void addNode(SiteTreeNode node) throws SiteException;

    /**
     * Add a node. This method is typically used when publishing, i.e. when copying a node from the
     * authoring tree to the live tree. The existing node already has a parent node (in the
     * authoring tree). The node that is added will be a copy of the original node and will be
     * inserted at the same parentid as the original node.
     * 
     * @param node the <code>SiteTreeNode</code> that is to be added
     * @param refDocumentId document-id of the node, before which the new node will be inserted.
     * 
     * @throws SiteException if the addition failed
     */
    void addNode(SiteTreeNode node, String refDocumentId) throws SiteException;

    /**
     * Add a label to an existing node
     * 
     * @param documentId the document-id to which the label is to be added.
     * @param label the label to add
     */
    void addLabel(String documentId, Label label);

    /**
     * Sets a label of an existing node. If the label does not exist, it is added. Otherwise, the
     * existing label is replaced.
     * 
     * @param documentId the document-id to which the label is to be added.
     * @param label the label to add
     */
    void setLabel(String documentId, Label label);

    /**
     * Sets the visibility in the navigation of an existing node. 
     * 
     * @param documentId the document-id to which the visibility is to be added.
     * @param visiblieInNav 
     */
    void setVisibleInNav (String documentId, boolean visibleInNav) throws SiteException;

    /**
     * Remove a label from a node
     * 
     * @param documentId the document-id from which the label is to be removed.
     * @param label the label to remove
     */
    void removeLabel(String documentId, Label label);

    /**
     * Removes the node corresponding to the given document-id from the tree, and returns it.
     * 
     * @param documentId the document-id of the node that is to be removed
     * 
     * @return the removed node
     */
    SiteTreeNode removeNode(String documentId);

    /**
     * Return the Node for a given document-id.
     * 
     * @param documentId the document-id of the node that is requested
     * 
     * @return a <code>SiteTreeNode</code> if there is a node for the given document-id, null
     *         otherwise.
     */
    SiteTreeNode getNode(String documentId);

    /**
     * Return the top level nodes in the sitetree.
     * @return the top nodes in the sitetree, or empty array if there are none
     */
    SiteTreeNode[] getTopNodes();

    /**
     * Move up the node amongst its siblings.
     * 
     * @param documentid The document id of the node.
     * @throws SiteException if the moving failed.
     */
    void moveUp(String documentid) throws SiteException;

    /**
     * Move down the node amongst its siblings.
     * @param documentid The document id of the node.
     * @throws SiteException if the moving failed.
     */
    void moveDown(String documentid) throws SiteException;

    /**
     * Imports a subtree (from this or from another tree) at a certain position.
     * @param subtreeRoot The root of the subtree to import.
     * @param newParent The node where the subtree shall be inserted.
     * @param newid The new id of the inserted subtreeRoot node (to not overwrite
     * @param refDocumentId The document-id corresponding to the reference node, before which the
     *            subtree should be inserted. If null, the subtree is inserted at the end. in case
     *            there is already a node with the same id in the tree).
     * @throws SiteException when an error occurs.
     */
    void importSubtree(SiteTreeNode subtreeRoot, SiteTreeNode newParent, String newid,
            String refDocumentId) throws SiteException;

}
