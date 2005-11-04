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
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * JCR content implementation.
 */
public class JCRContent extends NodeWrapper implements Content {

    /**
     * Ctor.
     * @param node The JCR node.
     * @param area The area.
     */
    public JCRContent(Node node, JCRArea area) {
        super(node);
        this.area = area;
        this.nodeManager = new NodeWrapperManager(area.getPublication().getSession());
    }

    private JCRArea area;

    protected JCRArea getArea() {
        return this.area;
    }

    private NodeWrapperManager nodeManager;

    public ContentNode getNode(String id) throws RepositoryException {
        NodeWrapperBuilder builder = new JCRContentNodeBuilder(this, id);
        ContentNode node = (ContentNode) this.nodeManager.getNode(id, builder, false);
        if (node == null) {
            throw new RepositoryException("The content node [" + id + "]Êdoes not exist.");
        }
        return node;
    }

    public ContentNode[] getNodes() {
        // TODO Auto-generated method stub
        return null;
    }

    public ContentNode addNode(String id) throws RepositoryException {
        NodeWrapperBuilder builder = new JCRContentNodeBuilder(this, id);
        return (ContentNode) this.nodeManager.getNode(id, builder, true);
    }

}
