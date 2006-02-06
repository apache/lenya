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
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;

import org.apache.lenya.cms.jcr.mapping.AbstractNodeProxy;
import org.apache.lenya.cms.jcr.mapping.NamePathElement;
import org.apache.lenya.cms.jcr.mapping.Path;
import org.apache.lenya.cms.jcr.mapping.PathElement;
import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Asset;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Site;
import org.apache.lenya.cms.repo.SiteNode;

/**
 * Site proxy.
 */
public class SiteProxy extends AbstractNodeProxy implements Site {

    protected static final String NODE_NAME = "lenya:site";
    protected static final String NODE_TYPE = "lnt:site";

    public SiteNode[] getChildren() throws RepositoryException {
        Path path = getAbsolutePath().append(getPathElement(SiteNodeProxy.NODE_NAME));
        return (SiteNode[]) getRepository().getProxies(path);
    }

    public SiteNode[] preOrder() throws RepositoryException {
        List nodes = new ArrayList();
        SiteNode[] children = getChildren();
        for (int i = 0; i < children.length; i++) {
            nodes.addAll(Arrays.asList(children[i].preOrder()));
        }
        return (SiteNode[]) nodes.toArray(new SiteNode[nodes.size()]);
    }

    public SiteNode addChild(String name, Asset contentNode) throws RepositoryException {
        SiteNodeProxy proxy = (SiteNodeProxy) getRepository().addByName(getAbsolutePath(),
                SiteNodeProxy.NODE_TYPE,
                SiteNodeProxy.class.getName(),
                name);
        proxy.setContentNode((AssetProxy) contentNode);
        return proxy;
    }

    public SiteNode getChild(String name) throws RepositoryException {
        Path path = getAbsolutePath().append(getPathElement(name));
        return (SiteNode) getRepository().getProxy(path);
    }

    public SiteNode getNode(String path) throws RepositoryException {
        String[] snippets = path.split("/");
        PathElement[] elements = new PathElement[snippets.length];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new NamePathElement(snippets[i]);
        }
        Path nodePath = getAbsolutePath().append(new Path(elements));
        return (SiteNode) getRepository().getProxy(nodePath);
    }

    public Path getAbsolutePath() throws RepositoryException {
        return SiteProxy.getPath((AreaProxy) getParentProxy());
    }

    protected static Path getPath(AreaProxy area) throws RepositoryException {
        return area.getAbsolutePath().append(getPathElement(NODE_NAME));
    }

    public Area getArea() throws RepositoryException {
        return (Area) getParentProxy();
    }

    public PathElement getPathElement() throws RepositoryException {
        return getPathElement(getName());
    }

    public void move(String srcAbsPath, String destAbsPath) throws RepositoryException {
        try {
            String srcPath = getNode().getPath() + srcAbsPath;
            String destPath = getNode().getPath() + destAbsPath;
            Session session = getNode().getSession();
            session.move(srcPath, destPath);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public SiteNode[] getReferences(Asset asset) throws RepositoryException {
        List siteNodes = new ArrayList();
        AssetProxy proxy = (AssetProxy) asset;

        try {
            for (PropertyIterator references = proxy.getNode().getReferences(); references.hasNext();) {
                Property property = references.nextProperty();
                Node node = property.getParent();
                if (node.isNodeType(SiteNodeProxy.NODE_TYPE)) {
                    SiteNode siteNode = (SiteNode) getRepository().getProxy(node);
                    siteNodes.add(siteNode);
                }
            }
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }

        return (SiteNode[]) siteNodes.toArray(new SiteNode[siteNodes.size()]);
    }

    public SiteNode getFirstReference(Asset asset) throws RepositoryException {
        SiteNode[] nodes = getReferences(asset);
        if (nodes.length > 0) {
            return nodes[0];
        }
        else {
            return null;
        }
    }

}
