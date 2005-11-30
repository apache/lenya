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
package org.apache.lenya.cms.jcr.mapping;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.query.InvalidQueryException;

import org.apache.lenya.cms.jcr.util.Assertion;
import org.apache.lenya.cms.repo.DocumentTypeRegistry;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Repository facade.
 */
public class RepositoryFacade {

    public RepositoryFacade(Session session, DocumentTypeRegistry doctypeRegistry) {
        this.session = session;
        this.doctypeRegistry = doctypeRegistry;
    }

    private Map node2proxy = new HashMap();
    private Session session;
    private DocumentTypeRegistry doctypeRegistry;

    protected String CLASS_PROPERTY = "lnt:className";

    public Session getSession() {
        return this.session;
    }

    public NodeProxy[] getProxies(Path path) throws RepositoryException {
        try {
            Node[] nodes = getNodes(path);
            NodeProxy[] proxies = new NodeProxy[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                proxies[i] = (NodeProxy) this.node2proxy.get(nodes[i]);
                if (proxies[i] == null) {
                    proxies[i] = createProxy(nodes[i]);
                }
            }
            return proxies;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public NodeProxy getProxy(Path path) throws RepositoryException {
        try {
            Node node = getNode(path);
            return getProxy(node);
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public boolean containsProxy(Path path) throws RepositoryException {
        try {
            return path.existsNode(getSession());
        } catch (Exception e) {
            throw new RepositoryException("Error with path [" + path + "]: " + e.getMessage(), e);
        }
    }

    protected NodeProxy createProxy(Node node) throws ValueFormatException,
            javax.jcr.RepositoryException, PathNotFoundException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, RepositoryException {
        String className = node.getProperty(CLASS_PROPERTY).getString();
        NodeProxy proxy = createProxy(className);
        proxy.setup(this, node);
        this.node2proxy.put(node, proxy);
        return proxy;
    }

    protected NodeProxy createProxy(String className) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        NodeProxy proxy;
        Class clazz = Class.forName(className);
        proxy = (NodeProxy) clazz.newInstance();
        return proxy;
    }

    protected Node getNode(Path path) throws InvalidQueryException, javax.jcr.RepositoryException,
            RepositoryException {
        Assertion.notNull(path, "path");
        if (path.getLength() == 0) {
            return getSession().getRootNode();
        } else {
            return path.getNode(getSession());
        }
    }

    protected Node[] getNodes(Path path) throws InvalidQueryException,
            javax.jcr.RepositoryException, RepositoryException {
        Assertion.notNull(path, "path");
        return path.getNodes(getSession());
    }

    public NodeProxy addByName(Path parentPath, String primaryNodeType, String className,
            String name) throws RepositoryException {
        try {
            Node parent = getNode(parentPath);
            if (parent.hasNode(name)) {
                throw new RepositoryException("The node [" + parentPath + "/" + name
                        + "] already exists!");
            } else {
                Node child = parent.addNode(name, primaryNodeType);
                child.setProperty(CLASS_PROPERTY, className);
                return createProxy(child);
            }
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }

    }

    public NodeProxy addByProperty(Path parentPath, String primaryNodeType, String className,
            String name, String propertyName, String propertyValue) throws RepositoryException {
        try {
            Assertion.notNull(parentPath, "parentPath");
            Node parent = getNode(parentPath);

            Path path = parentPath.append(new PropertyPathElement(name, propertyName, propertyValue));
            if (path.existsNode(getSession())) {
                throw new RepositoryException("The node [" + path + "] already exists!");
            }

            Node child = parent.addNode(name, primaryNodeType);
            child.setProperty(CLASS_PROPERTY, className);
            child.setProperty(propertyName, propertyValue);
            return createProxy(child);
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }

    }

    protected NodeProxy getProxy(Node parentNode) throws ValueFormatException,
            javax.jcr.RepositoryException, PathNotFoundException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, RepositoryException {
        NodeProxy parentProxy = (NodeProxy) this.node2proxy.get(parentNode);
        if (parentProxy == null) {
            parentProxy = createProxy(parentNode);
        }
        return parentProxy;
    }

    public DocumentTypeRegistry getDocumentTypeRegistry() {
        return this.doctypeRegistry;
    }

}
