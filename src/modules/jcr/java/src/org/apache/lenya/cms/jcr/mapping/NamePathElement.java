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

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Path element which identifies a node by its name.
 */
public class NamePathElement implements PathElement {

    private String name;

    /**
     * Ctor.
     * @param name The node name.
     */
    public NamePathElement(String name) {
        this.name = name;
    }

    /**
     * @return The node name.
     */
    public String getName() {
        return this.name;
    }

    public String toString() {
        return "/" + getName();
    }

    public Node getNode(Node parent) throws RepositoryException {
        try {
            if (existsNode(parent)) {
                return parent.getNode(getName());
            } else {
                throw new RepositoryException("The node [" + parent.getPath() + "/" + getName()
                        + "] does not exist!");
            }
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public boolean existsNode(Node parent) throws RepositoryException {
        try {
            return parent.hasNode(getName());
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public Node[] getNodes(Node parent) throws RepositoryException {
        try {
            List nodes = new ArrayList();
            for (NodeIterator i = parent.getNodes(getName()); i.hasNext();) {
                nodes.add(i.next());
            }
            return (Node[]) nodes.toArray(new Node[nodes.size()]);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

}
