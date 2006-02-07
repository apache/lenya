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

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;

import org.apache.lenya.cms.jcr.JCRSession;
import org.apache.lenya.cms.jcr.util.Assertion;
import org.apache.lenya.cms.repo.AssetTypeRegistry;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.metadata.MetaDataRegistry;

/**
 * Repository facade.
 */
public class RepositoryFacade {

    /**
     * Ctor.
     * @param jcrSession The repo session.
     * @param session The JCR session.
     * @param doctypeRegistry The document type registry.
     * @param metaDataRegistry The meta data registry.
     */
    public RepositoryFacade(JCRSession jcrSession, Session session, AssetTypeRegistry doctypeRegistry,
            MetaDataRegistry metaDataRegistry) {
        this.jcrSession = jcrSession;
        this.session = session;
        this.doctypeRegistry = doctypeRegistry;
        this.metaDataRegistry = metaDataRegistry;
    }

    private JCRSession jcrSession;
    private Session session;
    private AssetTypeRegistry doctypeRegistry;

    protected String CLASS_PROPERTY = "lnt:className";

    /**
     * @return The JCR session.
     */
    public Session getSession() {
        return this.session;
    }

    /**
     * @param path The path.
     * @return All proxies identified by this path.
     * @throws RepositoryException if an error occurs.
     */
    public NodeProxy[] getProxies(Path path) throws RepositoryException {
        try {
            Node[] nodes = getNodes(path);
            NodeProxy[] proxies = new NodeProxy[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                proxies[i] = getProxy(nodes[i]);
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

    /**
     * @param path A path.
     * @return The node identified by this path.
     * @throws RepositoryException if not exactly one node is found.
     */
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

    /**
     * @param path A path.
     * @return if the node identified by the path exists.
     * @throws RepositoryException if an error occurs.
     */
    public boolean containsProxy(Path path) throws RepositoryException {
        try {
            return path.existsNode(getSession());
        } catch (Exception e) {
            throw new RepositoryException("Error with path [" + path + "]: " + e.getMessage(), e);
        }
    }

    protected NodeProxy createProxy(Node node) throws RepositoryException {
        String className;
        try {
            className = node.getProperty(CLASS_PROPERTY).getString();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
        NodeProxy proxy = createProxy(className);
        proxy.setup(this, node);
        return proxy;
    }

    protected NodeProxy createProxy(String className) throws RepositoryException {
        NodeProxy proxy;
        try {
            Class clazz = Class.forName(className);
            proxy = (NodeProxy) clazz.newInstance();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
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

    /**
     * Adds a node by name.
     * @param parentPath The parent path.
     * @param primaryNodeType The primary node type of the new node.
     * @param className The class name.
     * @param name The name of the node.
     * @return The created node.
     * @throws RepositoryException if the node already exists.
     */
    public NodeProxy addByName(Path parentPath, String primaryNodeType, String className,
            String name) throws RepositoryException {
        try {
            Node parent = getNode(parentPath);

            if (parent.hasNode(name)) {
                throw new RepositoryException("The node [" + getProxy(parent) + "/" + name
                        + "] already exists!");
            } else {
                Node child = parent.addNode(name, primaryNodeType);
                child.setProperty(CLASS_PROPERTY, className);
                NodeProxy proxy = createProxy(child);
                return proxy;
            }
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }

    }

    /**
     * Adds a node by name without checking for duplicates.
     * @param parentPath The parent path.
     * @param primaryNodeType The primary node type of the new node.
     * @param className The class name.
     * @param name The name of the node.
     * @return The created node.
     * @throws RepositoryException if an error occurs.
     */
    public NodeProxy addByNameWithoutCheck(Path parentPath, String primaryNodeType,
            String className, String name) throws RepositoryException {
        try {
            Node parent = getNode(parentPath);
            Node child = parent.addNode(name, primaryNodeType);
            child.setProperty(CLASS_PROPERTY, className);
            NodeProxy proxy = createProxy(child);
            return proxy;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }

    }

    /**
     * Adds a node by property.
     * @param parentPath The parent path.
     * @param primaryNodeType The primary node type of the new node.
     * @param className The class name of the proxy.
     * @param name The node name.
     * @param propertyName The name of the identifying property.
     * @param propertyValue The identifying property value.
     * @return The new node.
     * @throws RepositoryException if the node already exists.
     */
    public NodeProxy addByProperty(Path parentPath, String primaryNodeType, String className,
            String name, String propertyName, String propertyValue) throws RepositoryException {
        try {
            Assertion.notNull(parentPath, "parentPath");
            Node parent = getNode(parentPath);

            PathElement element = new PropertyPathElement(name, propertyName, propertyValue);
            Path path = parentPath.append(element);
            if (path.existsNode(getSession())) {
                throw new RepositoryException("The node [" + getProxy(parent) + "/" + element
                        + "] already exists!");
            }

            Node child = parent.addNode(name, primaryNodeType);
            child.setProperty(CLASS_PROPERTY, className);
            child.setProperty(propertyName, propertyValue);
            NodeProxy proxy = createProxy(child);
            return proxy;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }

    }

    /**
     * @param node A node.
     * @return The proxy representing this node.
     * @throws RepositoryException if an error occurs.
     */
    public NodeProxy getProxy(Node node) throws RepositoryException {
        return createProxy(node);
    }

    /**
     * @return The document type registry.
     */
    public AssetTypeRegistry getDocumentTypeRegistry() {
        return this.doctypeRegistry;
    }

    private MetaDataRegistry metaDataRegistry;

    /**
     * @return The meta data registry.
     */
    public MetaDataRegistry getMetaDataRegistry() {
        return this.metaDataRegistry;
    }

    /**
     * @return The repo session.
     */
    public JCRSession getRepositorySession() {
        return this.jcrSession;
    }

}
