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

import org.apache.lenya.cms.jcr.util.Assertion;
import org.apache.lenya.cms.repo.ContentNode;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.SiteNode;

/**
 * JCR site node.
 */
public class JCRSiteNode extends NodeWrapper implements SiteNode {

    private JCRSite site;

    /**
     * Ctor.
     * @param session The session.
     * @param node The JCR node.
     * @param site The site.
     * @param parent The parent or <code>null</code> if this is a top-level node.
     */
    public JCRSiteNode(JCRSession session, Node node, JCRSite site, JCRSiteNode parent) {
        super(node);
        
        Assertion.notNull(session, "session");
        Assertion.notNull(site, "site");
        
        this.site = site;
        this.parent = parent;
        
        this.builder = new JCRSiteNodeBuilder(site);
        this.childManager = new NodeWrapperManager(session, this.builder);
    }

    public String getName() throws RepositoryException {
        try {
            return getNode().getName();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    protected JCRSite getSite() {
        return this.site;
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
        BuilderParameters params = builder.createParameters(getNode(), name, this);
        SiteNode child = (SiteNode) this.childManager.getNode(name, params);
        if (child == null) {
            throw new RepositoryException("The node [" + getPath() + "/" + name
                    + "] does not exist!");
        }
        return child;
    }

    public SiteNode addChild(String name, ContentNode contentNode) throws RepositoryException {
        BuilderParameters params = builder.createParameters(getNode(),
                name,
                (JCRContentNode) contentNode,
                this);
        return (SiteNode) this.childManager.addNode(name, params);
    }

    public String getPath() throws RepositoryException {
        if (getParent() != null) {
            return getParent().getPath() + "/" + getName();
        } else {
            return "/" + getName();
        }
    }

    private SiteNode parent;

    public SiteNode getParent() throws RepositoryException {
        return this.parent;
    }

    public ContentNode getContentNode() throws RepositoryException {
        try {
            Node contentNodeNode = getNode().getProperty(JCRSiteNodeBuilder.CONTENT_NODE_PROPERTY)
                    .getNode();
            String id = contentNodeNode.getProperty(JCRContentNodeBuilder.ID_PROPERTY).getString();
            JCRContent content = (JCRContent) getSite().getArea().getContent();
            return content.getNode(id);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

}
