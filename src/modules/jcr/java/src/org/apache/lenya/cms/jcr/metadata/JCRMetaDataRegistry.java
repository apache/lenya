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
package org.apache.lenya.cms.jcr.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.metadata.Element;
import org.apache.lenya.cms.repo.metadata.ElementSet;
import org.apache.lenya.cms.repo.metadata.MetaDataRegistry;
import org.apache.lenya.cms.repo.metadata.impl.ElementImpl;

/**
 * JCR-based meta data registry.
 */
public class JCRMetaDataRegistry implements MetaDataRegistry {

    private Session session;

    /**
     * Ctor.
     * @param session The session.
     */
    public JCRMetaDataRegistry(Session session) {
        this.session = session;
    }

    public String[] getElementSetNames() throws RepositoryException {
        List names = new ArrayList();
        try {
            for (NodeIterator i = getRegistryNode().getNodes(SET_NODE_NAME); i.hasNext();) {
                Node setNode = i.nextNode();
                names.add(setNode.getProperty(NAME_PROPERTY).getString());
            }
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
        return (String[]) names.toArray(new String[names.size()]);
    }

    public ElementSet getElementSet(String name) throws RepositoryException {
        try {
            Node setNode = getElementSetNode(name);
            if (setNode == null) {
                throw new RepositoryException("The element set [" + name + "] is not registered!");
            }
            return new JCRElementSet(setNode);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected static final String REGISTRY_NODE_NAME = "lenya:metaDataRegistry";
    protected static final String REGISTRY_NODE_TYPE = "lnt:metaDataRegistry";
    protected static final String SET_NODE_NAME = "lenya:elementSet";
    protected static final String SET_NODE_TYPE = "lnt:elementSet";
    protected static final String ELEMENT_NODE_NAME = "lenya:element";
    protected static final String ELEMENT_NODE_TYPE = "lnt:element";
    protected static final String NAME_PROPERTY = "lenya:name";
    protected static final String IS_MULTIPLE_PROPERTY = "lenya:isMultiple";

    public void register(String name, Element[] elements) throws RepositoryException {
        try {
            Node elementSetNode = getElementSetNode(name);
            if (elementSetNode != null) {
                throw new RepositoryException("The element set [" + name
                        + "]Êis already registered!");
            }
            Node registryNode = getRegistryNode();
            Node setNode = registryNode.addNode(SET_NODE_NAME, SET_NODE_TYPE);
            setNode.setProperty(NAME_PROPERTY, name);

            for (int i = 0; i < elements.length; i++) {
                Node elementNode = setNode.addNode(ELEMENT_NODE_NAME, ELEMENT_NODE_TYPE);
                elementNode.setProperty(NAME_PROPERTY, elements[i].getName());
                elementNode.setProperty(IS_MULTIPLE_PROPERTY, elements[i].isMultiple());
            }
            this.session.save();

        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected Node getRegistryNode() throws javax.jcr.RepositoryException {
        if (!session.getRootNode().hasNode(REGISTRY_NODE_NAME)) {
            session.getRootNode().addNode(REGISTRY_NODE_NAME, REGISTRY_NODE_TYPE);
        }
        return session.getRootNode().getNode(REGISTRY_NODE_NAME);
    }

    protected Node getElementSetNode(String name) throws javax.jcr.RepositoryException {
        for (NodeIterator i = getRegistryNode().getNodes(SET_NODE_NAME); i.hasNext();) {
            Node node = i.nextNode();
            if (node.getProperty(NAME_PROPERTY).getString().equals(name)) {
                return node;
            }
        }
        return null;
    }

    /**
     * JCR-based element set.
     */
    public class JCRElementSet implements ElementSet {

        private Node setNode;

        /**
         * Ctor.
         * @param setNode The element set node.
         */
        public JCRElementSet(Node setNode) {
            this.setNode = setNode;
        }

        public Element[] getElements() {
            try {
                List elements = new ArrayList();
                for (NodeIterator i = setNode.getNodes(ELEMENT_NODE_NAME); i.hasNext();) {
                    Node node = i.nextNode();
                    String elementName = node.getProperty(NAME_PROPERTY).getString();
                    boolean isMultiple = node.getProperty(IS_MULTIPLE_PROPERTY).getBoolean();
                    elements.add(new ElementImpl(elementName, isMultiple));
                }
                return (Element[]) elements.toArray(new Element[elements.size()]);
            } catch (javax.jcr.RepositoryException e) {
                throw new RuntimeException(e);
            }
        }

        public Element getElement(String name) throws RepositoryException {
            Element[] elements = getElements();
            for (int i = 0; i < elements.length; i++) {
                if (elements[i].getName().equals(name)) {
                    return elements[i];
                }
            }
            throw new RepositoryException("The element name [" + name + "] is not supported.");
        }

    }

    public boolean isRegistered(String name) throws RepositoryException {
        return Arrays.asList(getElementSetNames()).contains(name);
    }

}
