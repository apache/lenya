/*
$Id: SiteTree.java,v 1.19 2003/09/23 13:50:40 edith Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.publication;
/**
 * DOCUMENT ME!
 *
 * @author $Author: edith $
 * @version $Revision: 1.19 $
 */
public interface SiteTree {
	
    /**
     * Add a node.
     *
     * @param parentid where the node is to be added
     * @param id e.g. "concepts"
     * @param labels the labels of the node that is to be added
     * 
     * @throws SiteTreeException if the addition failed
     */
    void addNode(String parentid, String id, Label[] labels)
        throws SiteTreeException;

    /**
     * Add a node.
     *
     * @param parentid the node where the new node is to be inserted
     * @param id the node id
     * @param labels the labels 
     * @param href the href of the new node
     * @param suffix the suffix of the new node
     * @param link the link 
     * 
     * @throws SiteTreeException if the addition failed
     */
    void addNode(String parentid, String id, Label[] labels, String href, String suffix,
        boolean link) throws SiteTreeException;

	/**
     * Insert a node before a given node 
     *
     * @param parentid the node where the new node is to be inserted
     * @param id the node id
     * @param labels the labels 
     * @param href the href of the new node
     * @param suffix the suffix of the new node
     * @param link the link 
	 * @param refDocumentId document-id of the node, before which the new node will be inserted.
	 * 
	 * @throws SiteTreeException if the addition failed
     */
	void addNode(String parentid, String id, Label[] labels, String href, String suffix,
		boolean link, String refDocumentId) throws SiteTreeException;
    /**
     * Add a node.
     * Compute the parent id and the id of the node from the document-id
     *
     * @param documentid the document-id of the new node. 
     *  From this the parent-id and the id are computed
     * @param labels the labels
     * @param href the href
     * @param suffix the suffix
     * @param link the link
     * 
     * @throws SiteTreeException if the addition failed
     */
    void addNode(String documentid, Label[] labels, String href, String suffix, boolean link)
        throws SiteTreeException;

	/**
    /**
     * Insert a node before a given node 
     * Compute the parent id and the id of the node from the document-id
     *
     * @param documentid the document-id of the new node. 
     *  From this the parent-id and the id are computed
     * @param labels the labels
     * @param href the href
     * @param suffix the suffix
     * @param link the link
	 * @param refDocumentId document-id of the node, before which the new node will be inserted.
	 * 
     * @throws SiteTreeException if the addition failed
	 */
	void addNode(String documentid, Label[] labels, String href, String suffix, 
		boolean link, String refDocumentId) throws SiteTreeException;
    /**
     * Add a node. This method is typically used when publishing,
     * i.e. when copying a node from the authoring tree to the live
     * tree. The existing node already has a parent node (in the
     * authoring tree). The node that is added will be a copy of
     * the original node and will be inserted at the same parentid
     * as the original node.
     *
     * @param node the <code>SiteTreeNode</code> that is to be added
     * 
     * @throws SiteTreeException if the addition failed
     */
    void addNode(SiteTreeNode node) throws SiteTreeException;
    
    /**
     * Add a node. This method is typically used when publishing,
     * i.e. when copying a node from the authoring tree to the live
     * tree. The existing node already has a parent node (in the
     * authoring tree). The node that is added will be a copy of
     * the original node and will be inserted at the same parentid
     * as the original node.
     *
     * @param node the <code>SiteTreeNode</code> that is to be added
	 * @param refDocumentId document-id of the node, before which the new node will be inserted.
	 * 
     * @throws SiteTreeException if the addition failed
	 */
	void addNode(SiteTreeNode node, String refDocumentId)  throws SiteTreeException;
    
	/**
	 * Add a label to an existing node
	 * 
	 * @param documentId the document-id to which the label is to be added.
	 * @param label the label to add
	 */
	void addLabel(String documentId, Label label);
	
    /**
     * Sets a label of an existing node. If the label does not exist, it is added.
     * Otherwise, the existing label is replaced.
     * 
     * @param documentId the document-id to which the label is to be added.
     * @param label the label to add
     */
    void setLabel(String documentId, Label label);
    
	/**
	 * Remove a label from a node
	 * 
	 * @param documentId the document-id from which the label is to be removed.
	 * @param label the label to remove
	 */
	void removeLabel(String documentId, Label label);
	
	/**
     * Removes the node corresponding to the given document-id
     * from the tree, and returns it.
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
     * @return a <code>SiteTreeNode</code> if there is a node for the given
     * document-id, null otherwise.
     */
    SiteTreeNode getNode(String documentId);

	/**
	 * Move up the node amongst its siblings.
	 * 
	 * @param documentid The document id of the node.
	 * @throws SiteTreeException if the moving failed.
	 */
	void moveUp(String documentid) throws SiteTreeException;

	/**
	 * Move down the node amongst its siblings. 
	 * @param documentid The document id of the node.
	 * @throws SiteTreeException if the moving failed.
	 */
	void moveDown(String documentid) throws SiteTreeException;
	
	/**
	 * Imports a subtree (from this or from another tree) at a certain position.
	 * @param subtreeRoot The root of the subtree to import.
	 * @param newParent The node where the subtree shall be inserted.
	 * @param newid The new id of the inserted subtreeRoot node (to not overwrite
	 * @param refDocumentId The document-id corresponding to the reference node, before which 
	 * the subtree should be inserted. If null, the subtree is inserted at the end. 
	 * in case there is already a node with the same id in the tree).
	 * @throws SiteTreeException when an error occurs.
	 */
	void importSubtree(SiteTreeNode subtreeRoot, SiteTreeNode newParent, String newid, String refDocumentId) throws SiteTreeException;

	/**
	 * Save the SiteTree.
	 *
	 * @throws SiteTreeException if the saving failed
	 */
	void save()throws SiteTreeException;
}
