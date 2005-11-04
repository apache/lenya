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
package org.apache.lenya.cms.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

public class JCRArea {

    private String area;
    private JCRPublication publication;

    public JCRArea(JCRPublication publication, String area) {
        this.publication = publication;
        this.area = area;
    }

    protected JCRPublication getPublication() {
        return this.publication;
    }

    protected String getArea() {
        return this.area;
    }

    public Session getSession() throws RepositoryException {
        return getPublication().getSession().getSession(getArea());
    }

    public Node getPublicationNode() throws RepositoryException {
        Node rootNode = getSession().getRootNode();

        Node pubNode = null;

        for (NodeIterator pubNodes = rootNode.getNodes("lenya:publication"); pubNodes.hasNext();) {
            Node node = pubNodes.nextNode();
            if (node.getProperty("lenya:id")
                    .getString()
                    .equals(getPublication().getPublicationId())) {
                pubNode = node;
            }
        }
        if (pubNode == null) {
            pubNode = rootNode.addNode("lenya:publication");
            pubNode.setProperty("lenya:id", getPublication().getPublicationId());
        }

        return pubNode;
    }

    protected Node getSubNode(Node parent, String childName) throws RepositoryException {
        Node child = null;
        if (parent.hasNode(childName)) {
            child = parent.getNode(childName);
        } else {
            child = parent.addNode(childName);
        }
        return child;
    }

    public Node getContentNode() throws RepositoryException {
        return getSubNode(getPublicationNode(), "lenya:content");
    }

    public Node getSiteNode() throws RepositoryException {
        return getSubNode(getPublicationNode(), "lenya:site");
    }

}
