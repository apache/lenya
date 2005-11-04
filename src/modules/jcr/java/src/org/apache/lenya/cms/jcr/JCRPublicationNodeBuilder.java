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

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * JCR publication node builder.
 */
public class JCRPublicationNodeBuilder implements NodeWrapperBuilder {

    /**
     * Ctor.
     * @param pubId The publication ID.
     * @param area The area.
     */
    public JCRPublicationNodeBuilder(String pubId, String area) {
        this.pubId = pubId;
        this.area = area;
    }

    private String pubId;
    private String area;

    protected static final String NODE_NAME = "lenya:publication";
    protected static final String ID_ATTRIBUTE = "lenya:id";

    public NodeWrapper buildNode(JCRSession session, boolean create) throws RepositoryException {
        try {
            JCRPublicationNode wrapper = null;
            Node rootNode = session.getSession(area).getRootNode();

            Node pubNode = null;

            for (NodeIterator pubNodes = rootNode.getNodes(NODE_NAME); pubNodes.hasNext();) {
                Node node = pubNodes.nextNode();
                if (node.getProperty(ID_ATTRIBUTE).getString().equals(pubId)) {
                    pubNode = node;
                }
            }
            if (pubNode == null && create) {
                pubNode = rootNode.addNode(NODE_NAME);
                pubNode.setProperty(ID_ATTRIBUTE, pubId);
            }

            if (pubNode != null) {
                wrapper = new JCRPublicationNode(pubNode);
            }
            return wrapper;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

}
