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
import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.lenya.cms.jcr.mapping.AbstractNodeProxy;
import org.apache.lenya.cms.jcr.mapping.NodeProxy;
import org.apache.lenya.cms.jcr.mapping.Path;
import org.apache.lenya.cms.jcr.mapping.PathElement;
import org.apache.lenya.cms.repo.ContentNode;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Site;
import org.apache.lenya.cms.repo.SiteNode;

/**
 * Site node proxy.
 */
public class SiteNodeProxy extends AbstractNodeProxy implements SiteNode {

    protected static final String NODE_NAME = "lenya:siteNode";
    protected static final String NODE_TYPE = "lnt:siteNode";
    protected static final String CONTENT_NODE_PROPERTY = "lenya:contentNode";

    public SiteNode[] getChildren() throws RepositoryException {
        try {
            List proxies = new ArrayList();
            for (NodeIterator i = getNode().getNodes(); i.hasNext();) {
                Node node = i.nextNode();
                proxies.add(getChild(node.getName()));
            }
            return (SiteNode[]) proxies.toArray(new SiteNode[proxies.size()]);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public SiteNode getChild(String name) throws RepositoryException {
        Path path = getAbsolutePath().append(getPathElement(name));
        return (SiteNode) getRepository().getProxy(path);
    }

    public SiteNode addChild(String name, ContentNode contentNode) throws RepositoryException {
        SiteNodeProxy proxy = (SiteNodeProxy) getRepository().addByName(getAbsolutePath(),
                SiteNodeProxy.NODE_TYPE,
                SiteNodeProxy.class.getName(),
                name);
        proxy.setContentNode((ContentNodeProxy) contentNode);
        return proxy;
    }

    public String getPath() throws RepositoryException {
        String sitePath = ((SiteProxy) getSite()).getAbsolutePath().toString();
        return getAbsolutePath().toString().substring(sitePath.length());
    }

    public Path getAbsolutePath() throws RepositoryException {
        SiteProxy site = (SiteProxy) getSite();
        return site.getAbsolutePath().append(getPathElement(getName()));
    }

    public ContentNode getContentNode() throws RepositoryException {
        try {
            Node node = getPropertyNode(CONTENT_NODE_PROPERTY);
            String id = node.getUUID();
            return getSite().getArea().getContent().getNode(id);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected void setContentNode(ContentNodeProxy proxy) throws RepositoryException {
        setProperty(CONTENT_NODE_PROPERTY, proxy.getNode());
    }

    public SiteNode getParent() throws RepositoryException {
        NodeProxy parent = getParentProxy();
        if (parent instanceof SiteNode) {
            return (SiteNode) parent;
        } else {
            return null;
        }
    }

    public Site getSite() throws RepositoryException {
        SiteNode parent = getParent();
        if (parent == null) {
            return (Site) getParentProxy();
        } else {
            return parent.getSite();
        }
    }

    public PathElement getPathElement() throws RepositoryException {
        return getPathElement(getName());
    }

    public SiteNode[] preOrder() throws RepositoryException {
        try {
            List proxies = new ArrayList();
            proxies.add(this);
            for (NodeIterator i = getNode().getNodes(); i.hasNext();) {
                Node node = i.nextNode();
                SiteNode child = getChild(node.getName());
                proxies.addAll(Arrays.asList(child.preOrder()));
            }
            return (SiteNode[]) proxies.toArray(new SiteNode[proxies.size()]);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

}
