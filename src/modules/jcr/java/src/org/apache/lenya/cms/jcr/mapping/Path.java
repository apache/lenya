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
import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * A path.
 */
public class Path {

    private List elements = new ArrayList();

    /**
     * Creates an empty path.
     */
    public Path() {
    }

    /**
     * Creates a path containing a single element.
     * @param element The element.
     */
    public Path(PathElement element) {
        this.elements.add(element);
    }

    /**
     * Creates a path.
     * @param elements The elements.
     */
    public Path(PathElement[] elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    /**
     * @return The elements.
     */
    public PathElement[] getElements() {
        return (PathElement[]) this.elements.toArray(new PathElement[this.elements.size()]);
    }

    /**
     * @return The number of elements.
     */
    public int getLength() {
        return this.elements.size();
    }

    /**
     * Returns a sub-path.
     * @param index The start index (beginning with 0).
     * @return the sub-path.
     * @throws RepositoryException if an error occurs. 
     */
    public Path getSubPath(int index) throws RepositoryException {
        if (index > this.elements.size()) {
            throw new RepositoryException("The index [" + index
                    + "] is greater than the path length [" + this.elements.size() + "]!");
        }
        List subElements = this.elements.subList(index, this.elements.size());
        return new Path((PathElement[]) subElements.toArray(new PathElement[subElements.size()]));
    }

    /**
     * @param parent The parent node.
     * @return The node identified by this path, starting from the parent.
     * @throws RepositoryException if an error occurs.
     */
    public Node getNode(Node parent) throws RepositoryException {
        switch (getLength()) {
        case 0:
            return parent;
        case 1:
            return getElements()[0].getNode(parent);
        default:
            Node node = getElements()[0].getNode(parent);
            return getSubPath(1).getNode(node);
        }
    }

    /**
     * @param parent The parent node.
     * @return All nodes identified by this path, starting from the parent node.
     * @throws RepositoryException if an error occurs.
     */
    public Node[] getNodes(Node parent) throws RepositoryException {
        switch (getLength()) {
        case 0:
            throw new RepositoryException("The path length must not be 0");
        case 1:
            return getElements()[0].getNodes(parent);
        default:
            Node node = getElements()[0].getNode(parent);
            return getSubPath(1).getNodes(node);
        }
    }

    /**
     * @param session The session.
     * @return The node identified by this path, starting from the session's root node.
     * @throws RepositoryException if an error occurs.
     */
    public Node getNode(Session session) throws RepositoryException {
        try {
            return getNode(session.getRootNode());
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @param session The session.
     * @return All nodes identified by this path, starting from the session's root node.
     * @throws RepositoryException if an error occurs.
     */
    public Node[] getNodes(Session session) throws RepositoryException {
        try {
            return getNodes(session.getRootNode());
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public String toString() {
        int length = getLength();
        switch (length) {
        case 0:
            return "";
        default:
            try {
                return getElements()[0].toString() + getSubPath(1).toString();
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @param pathElement The path element to append.
     * @return The resulting path.
     */
    public Path append(PathElement pathElement) {
        List elements = new ArrayList(Arrays.asList(getElements()));
        elements.add(pathElement);
        return new Path((PathElement[]) elements.toArray(new PathElement[elements.size()]));
    }

    /**
     * @param path The path to append.
     * @return The resulting path.
     */
    public Path append(Path path) {
        List elements = new ArrayList(Arrays.asList(getElements()));
        elements.addAll(Arrays.asList(path.getElements()));
        return new Path((PathElement[]) elements.toArray(new PathElement[elements.size()]));
    }

    /**
     * @param session The session.
     * @return if the node identified by this path exists.
     * @throws RepositoryException if an error occurs.
     */
    public boolean existsNode(Session session) throws RepositoryException {
        return getNodes(session).length > 0;
    }

}
