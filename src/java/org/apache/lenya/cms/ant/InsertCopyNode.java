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

/* $Id: InsertCopyNode.java,v 1.10 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import java.util.StringTokenizer;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;

/**
 * Ant task that copies a node of a tree and inserts it in  tree
 **/
public class InsertCopyNode extends TwoNodesTask {
	/**
	 * Creates a new instance of InsertCopyNode
	 */
	public InsertCopyNode() {
		super();
	}

	/**
	 * copies a node corresponding to a document with id firstdocumentid and area firstarea
	 * and inserts it like a node corresponding to a document with id secdocumentid and area secarea.
 	 * @param firstdocumentid The document-id of the document corresponding to the source node.
	 * @param secdocumentid  The document-id of the document corresponding to the destination node.
	 * @param firstarea The area of the document corresponding to the source node.
	 * @param secarea The area of the document corresponding to the destination node.
	 * @throws SiteTreeException if there are problems with creating or saving the site tree.  
	 */
	public void manipulateTree(
		String firstdocumentid,
		String secdocumentid,
		String firstarea,
		String secarea)
		throws SiteTreeException {

		Publication publication = getPublication();
		SiteTree firsttree = publication.getSiteTree(firstarea);
		SiteTree sectree = publication.getSiteTree(secarea);

		String parentid = "";
		StringTokenizer st = new StringTokenizer(secdocumentid, "/");
		int length = st.countTokens();

		for (int i = 0; i < (length - 1); i++) {
			parentid = parentid + "/" + st.nextToken();
		}
		String newid = st.nextToken();

		SiteTreeNode node = firsttree.getNode(firstdocumentid);

		if (node != null) {
			SiteTreeNode parentNode = sectree.getNode(parentid);
			if (parentNode != null) {
				sectree.importSubtree(parentNode, node, newid, null);
			} else {
				throw new SiteTreeException(
					"The parent node "
						+ parentNode
						+ " where the copied node shall be inserted not found");
			}
		} else {
			throw new SiteTreeException("Node " + node + " couldn't be found");
		}
		if (firstarea.equals(secarea)) {
			firsttree.save();
		} else {
			firsttree.save();
			sectree.save();
		}
	}
}
