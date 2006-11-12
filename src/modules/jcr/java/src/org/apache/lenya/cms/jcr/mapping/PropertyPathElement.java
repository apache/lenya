/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * A path element that identifies a node by a property value.
 */
public class PropertyPathElement extends NamePathElement {

    /**
     * Ctor.
     * @param name The node name.
     * @param propertyName The name of the identifying property.
     * @param propertyValue The value identifying the node.
     */
    public PropertyPathElement(String name, String propertyName, String propertyValue) {
        super(name);
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    private String propertyName;
    private String propertyValue;

    public String toString() {
        return "/" + getName() + "[@" + propertyName + "='" + propertyValue + "']";
    }

    /**
     * @return The name of the identifying property.
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * @return The value that identifies the node.
     */
    public String getPropertyValue() {
        return this.propertyValue;
    }

    public Node getNode(Node parent) throws RepositoryException {
        try {
            Node node = null;
            for (NodeIterator i = parent.getNodes(getName()); i.hasNext();) {
                Node aNode = i.nextNode();
                String name = getPropertyName();
                String value = getPropertyValue();
                String aValue = aNode.getProperty(name).getString();
                if (aValue.equals(value)) {
                    if (node == null) {
                        node = aNode;
                    } else {
                        throw new RepositoryException("More than 1 node [" + parent.getPath()
                                + this + "] exists!");
                    }
                }
            }
            if (node == null) {
                String workspace = parent.getSession().getWorkspace().getName();
                throw new RepositoryException("Node [" + parent.getPath() + this
                        + "] does not exist in area [" + workspace + "]!");
            }
            return node;
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public Node[] getNodes(Node parent) throws RepositoryException {
        try {
            List nodes = new ArrayList();
            for (NodeIterator i = parent.getNodes(getName()); i.hasNext();) {
                Node aNode = i.nextNode();
                String name = getPropertyName();
                String value = getPropertyValue();
                String aValue = aNode.getProperty(name).getString();
                if (aValue.equals(value)) {
                    nodes.add(aNode);
                }
            }
            return (Node[]) nodes.toArray(new Node[nodes.size()]);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

}
