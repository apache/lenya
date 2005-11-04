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
import org.apache.lenya.cms.repo.SiteNode;

/**
 * JCR site node.
 */
public class JCRSiteNode extends NodeWrapper implements SiteNode {

    private JCRSite site;

    /**
     * Ctor.
     * @param node The JCR node.
     * @param site The site.
     */
    public JCRSiteNode(Node node, JCRSite site) {
        super(node);
        this.site = site;
        this.childManager = new NodeWrapperManager(site.getArea().getPublication().getSession());
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
        JCRSiteNodeBuilder builder = new JCRSiteNodeBuilder(getSite(), getNode(), name);
        SiteNode child = (SiteNode) this.childManager.getNode(name, builder, false);
        if (child == null) {
            throw new RepositoryException("The node [" + getPath() + "/" + name
                    + "] does not exist!");
        }
        return child;
    }

    public SiteNode addChild(String name, ContentNode contentNode) throws RepositoryException {
        JCRSiteNodeBuilder builder = new JCRSiteNodeBuilder(getSite(), getNode(), name);
        return (SiteNode) this.childManager.getNode(name, builder, false);
    }

    public String getPath() throws RepositoryException {
        // TODO Auto-generated method stub
        return "";
    }

}
