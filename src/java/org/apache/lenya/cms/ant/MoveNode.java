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

import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.lenya.transaction.TransactionException;

/**
 * Ant task that moves a node in a tree.
 */
public class MoveNode extends TwoNodesTask {
    private String refdocumentid;

    /**
     * Constructor 
     */
    public MoveNode() {
        super();
    }

    /**
     * Move a node.
     * @param firstdocumentid The document-id of the document corresponding to the source node.
     * @param secdocumentid The document-id of the document corresponding to the destination node.
     * @param firstarea The area of the document corresponding to the source node.
     * @param secarea The area of the document corresponding to the destination node.
     * @throws SiteException if there are problems with creating or saving the site tree.
     * @throws TransactionException
     */
    public void manipulateTree(String firstdocumentid, String secdocumentid, String firstarea,
            String secarea) throws SiteException, TransactionException {

        SiteTree firsttree = getSiteTree(firstarea);
        SiteTree sectree = getSiteTree(secarea);

        String parentid = "";
        StringBuffer buf = new StringBuffer();
        StringTokenizer st = new StringTokenizer(secdocumentid, "/");
        int length = st.countTokens();

        for (int i = 0; i < (length - 1); i++) {
            buf.append("/" + st.nextToken());
        }
        String newid = st.nextToken();
        parentid = buf.toString();

        SiteTreeNode node = firsttree.removeNode(firstdocumentid);
        if (node != null) {
            SiteTreeNode parentNode = sectree.getNode(parentid);
            if (parentNode != null) {
                sectree.importSubtree(parentNode, node, newid, this.getRefdocumentid());
            } else {
                throw new SiteException("The parent node " + parentNode
                        + " where the removed node shall be inserted not found");
            }
        } else {
            throw new SiteException("Node " + node + " couldn't be removed");
        }

        if (firstarea.equals(secarea)) {
            firsttree.save();
        } else {
            firsttree.save();
            sectree.save();
        }
    }

    /**
     * @return string The document-id corresponding to the reference node, before which the moved
     *         node shoul be inserted. If null, the node is inserted at the end.
     */
    public String getRefdocumentid() {
        return this.refdocumentid;
    }

    /**
     * @param string The document-id corresponding to the reference node, before which the moved
     *            node shoul be inserted. If null, the node is inserted at the end.
     */
    public void setRefdocumentid(String string) {
        this.refdocumentid = string;
    }

}