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

/* $Id$  */

package org.apache.lenya.cms.ant;

import java.util.StringTokenizer;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;

/**
 * Ant task that moves a node in a tree.
 */
public class MoveNode extends TwoNodesTask {
	private String refdocumentid;
	/**
	 *
	 */
	public MoveNode() {
		super();
	}

	/**
	 * Move a node.
	 * 
	 * @param firstDocumentId The document-id of the document corresponding to the source node.
	 * @param secDocumentId  The document-id of the document corresponding to the destination node.
	 * @param firstArea The area of the document corresponding to the source node.
	 * @param secArea The area of the document corresponding to the destination node.
	 * @throws SiteTreeException if there are problems with creating or saving the site tree.  
	 */
	public void manipulateTree(
		String firstDocumentId,
		String secDocumentId,
		String firstArea,
		String secArea)
		throws SiteTreeException {

		Publication publication = getPublication();
		SiteTree firsttree = publication.getTree(firstArea);
		SiteTree sectree = publication.getTree(secArea);

		StringTokenizer st = new StringTokenizer(secDocumentId, "/");
		int length = st.countTokens() - 1;
        StringBuffer parentId = new StringBuffer(secDocumentId.length());
		for (int i = 0; i < length; i++) {
			parentId.append("/").append(st.nextToken());
		}
		String newid = st.nextToken();

		SiteTreeNode node = firsttree.getNode(firstDocumentId);
		if (node != null) {
		    SiteTreeNode parentNode = sectree.getNode(parentId.toString());
			if (parentNode != null) {
                sectree.move(node, parentNode, newid, this.getRefdocumentid());
			} else {
				throw new SiteTreeException(
					"The parent node "
						+ parentNode
						+ " where the removed node shall be inserted not found");
			}
		} else {
			throw new SiteTreeException(
				"Node " + node + " couldn't be removed");
		}

		if (firstArea.equals(secArea)) {
			firsttree.save();
		} else {
			firsttree.save();
			sectree.save();
		}
	}
	/**
	 * @return string The document-id corresponding to the reference node, before which 
	 * the moved node shoul be inserted. If null, the node is inserted at the end. 
	 */
	public String getRefdocumentid() {
		return refdocumentid;
	}

	/**
	 * @param string The document-id corresponding to the reference node, before which 
	 * the moved node shoul be inserted. If null, the node is inserted at the end.
	 */
	public void setRefdocumentid(String string) {
		refdocumentid = string;
	}

}
