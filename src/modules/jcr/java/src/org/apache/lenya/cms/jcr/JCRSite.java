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

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.lenya.cms.repo.ContentNode;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Site;
import org.apache.lenya.cms.repo.SiteNode;

/**
 * JCR site.
 */
public class JCRSite extends NodeWrapper implements Site {

    private JCRPublicationNode area;

    /**
     * Ctor.
     * @param session The session.
     * @param node The node.
     * @param area The area.
     */
    public JCRSite(JCRSession session, Node node, JCRPublicationNode area) {
        super(node);
        this.area = area;
        this.builder = new JCRSiteNodeBuilder(this);
        this.childManager = new NodeWrapperManager(session, this.builder);
    }

    protected JCRPublicationNode getArea() {
        return this.area;
    }

    private NodeWrapperManager childManager;
    private JCRSiteNodeBuilder builder;

    public SiteNode[] getChildren() throws RepositoryException {
        try {
            NodeIterator i = getNode().getNodes();
            List nodes = new ArrayList();
            while (i.hasNext()) {
                Node child = i.nextNode();
                nodes.add(getChild(child.getName()));
            }
            return (SiteNode[]) nodes.toArray(new SiteNode[nodes.size()]);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public SiteNode getChild(String name) throws RepositoryException {
        BuilderParameters params = builder.createParameters(getNode(), name, null);
        return (SiteNode) this.childManager.getNode(name, params);
    }

    public SiteNode[] getNodes() throws RepositoryException {
        String[] keys = this.childManager.getKeys(getNode());
        SiteNode[] nodes = new SiteNode[keys.length];
        for (int i = 0; i < keys.length; i++) {
            BuilderParameters params = this.builder.createParameters(getNode(), keys[i], null);
            nodes[i] = (SiteNode) this.childManager.getNode(keys[i], params);
        }
        return nodes;
    }

    public SiteNode addChild(String name, ContentNode contentNode) throws RepositoryException {
        BuilderParameters params = builder.createParameters(getNode(),
                name,
                (JCRContentNode) contentNode,
                null);
        return (SiteNode) this.childManager.addNode(name, params);
    }

}
