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

import org.apache.lenya.cms.repo.Content;
import org.apache.lenya.cms.repo.ContentNode;
import org.apache.lenya.cms.repo.DocumentType;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * JCR content implementation.
 */
public class JCRContent extends NodeWrapper implements Content {

    /**
     * Ctor.
     * @param session The session.
     * @param node The JCR node.
     * @param area The area.
     */
    public JCRContent(JCRSession session, Node node, JCRPublicationNode area) {
        super(node);
        this.area = area;
        this.nodeManager = new NodeWrapperManager(session, this.builder);
    }

    private JCRPublicationNode area;

    protected JCRPublicationNode getArea() {
        return this.area;
    }

    private NodeWrapperManager nodeManager;
    private JCRContentNodeBuilder builder = new JCRContentNodeBuilder();

    public ContentNode getNode(String id) throws RepositoryException {
        BuilderParameters params =  builder.createParameters(this, id);
        ContentNode node = (ContentNode) this.nodeManager.getNode(id, params);
        if (node == null) {
            throw new RepositoryException("The content node [" + id + "]Êdoes not exist.");
        }
        return node;
    }

    public ContentNode[] getNodes() throws RepositoryException {
        
        String[] keys = this.nodeManager.getKeys(getNode());
        ContentNode[] contentNodes = new ContentNode[keys.length];
        for (int i = 0; i < contentNodes.length; i++) {
            BuilderParameters params = this.builder.createParameters(this, keys[i]);
            contentNodes[i] = (ContentNode) this.nodeManager.getNode(keys[i], params);
        }
        
        return contentNodes;
    }

    public ContentNode addNode(String id, DocumentType documentType) throws RepositoryException {
        BuilderParameters params = builder.createParameters(this, id, documentType);
        return (ContentNode) this.nodeManager.addNode(id, params);
    }

}
