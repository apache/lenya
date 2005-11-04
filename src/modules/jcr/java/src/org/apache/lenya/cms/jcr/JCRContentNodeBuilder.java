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
 * JCR content node builder.
 */
public class JCRContentNodeBuilder implements NodeWrapperBuilder {

    protected static final String NODE_NAME = "lenya:contentnode";
    protected static final String ID_ATTRIBUTE = "lenya:id";

    private String id;
    private JCRContent content;

    /**
     * Ctor.
     * @param content The content.
     * @param id The id.
     */
    public JCRContentNodeBuilder(JCRContent content, String id) {
        this.id = id;
        this.content = content;
    }

    public NodeWrapper buildNode(JCRSession session, boolean create) throws RepositoryException {
        try {
            JCRContentNode jcrContentNode = null;
            Node contentNode = null;
            Node content = this.content.getNode();
            for (NodeIterator nodes = content.getNodes(NODE_NAME); nodes.hasNext();) {
                Node node = nodes.nextNode();
                if (node.getProperty(ID_ATTRIBUTE).getString().equals(this.id)) {
                    contentNode = node;
                }
            }
            if (contentNode == null && create) {
                contentNode = content.addNode(NODE_NAME);
                contentNode.setProperty(ID_ATTRIBUTE, this.id);
            }
            if (contentNode != null) {
                jcrContentNode = new JCRContentNode(this.content, contentNode);
            }

            return jcrContentNode;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

}
