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

/* $Id$  */

package org.apache.lenya.cms.ant;

import java.util.StringTokenizer;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;

/**
 * Ant task that copies a node of a tree and inserts it in tree
 */
public class InsertCopyNode extends TwoNodesTask {
    /**
     * Creates a new instance of InsertCopyNode
     */
    public InsertCopyNode() {
        super();
    }

    /**
     * copies a node corresponding to a document with id firstdocumentid and area firstarea and
     * inserts it like a node corresponding to a document with id secdocumentid and area secarea.
     * @param firstDocumentId The document-id of the document corresponding to the source node.
     * @param secDocumentId The document-id of the document corresponding to the destination node.
     * @param firstArea The area of the document corresponding to the source node.
     * @param secArea The area of the document corresponding to the destination node.
     * @throws SiteTreeException if there are problems with creating or saving the site tree.
     */
    public void manipulateTree(String firstDocumentId, String secDocumentId, String firstArea,
            String secArea) throws SiteTreeException {

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
                sectree.copy(node, parentNode, newid, null);
            } else {
                throw new SiteTreeException("The parent node " + parentNode
                        + " where the copied node shall be inserted not found");
            }
        } else {
            throw new SiteTreeException("Node " + node + " couldn't be found");
        }
        if (firstArea.equals(secArea)) {
            firsttree.save();
        } else {
            firsttree.save();
            sectree.save();
        }
    }
}