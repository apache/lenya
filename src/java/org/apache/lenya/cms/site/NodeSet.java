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
package org.apache.lenya.cms.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.util.DocumentSet;

/**
 * A set containing nodes.
 */
public class NodeSet {
    
    /**
     * Ctor.
     */
    public NodeSet() {
    }

    /**
     * Ctor.
     * @param _nodes The initial nodes.
     */
    public NodeSet(Node[] _nodes) {
        for (int i = 0; i < _nodes.length; i++) {
            add(_nodes[i]);
        }
    }

    /**
     * Ctor.
     * @param documents The corresponding documents to derive nodes from.
     */
    public NodeSet(DocumentSet documents) {
        Document[] docs = documents.getDocuments();
        for (int i = 0; i < docs.length; i++) {
            Node node = new Node(docs[i].getPublication(), docs[i].getArea(), docs[i].getId());
            if (!contains(node)) {
                add(node);
            }
        }
    }

    /**
     * @param node A node.
     * @return If the node is contained.
     */
    public boolean contains(Node node) {
        return getList().contains(node);
    }

    private List nodes = new ArrayList();
    
    /**
     * Returns the list object that stores the documents.
     * @return A list.
     */
    protected List getList() {
        return this.nodes;
    }

    /**
     * Returns the documents contained in this set.
     * 
     * @return An array of documents.
     */
    public Node[] getNodes() {
        return (Node[]) this.nodes.toArray(new Node[this.nodes.size()]);
    }

    /**
     * Adds a node to this set.
     * @param node The node to add.
     */
    public void add(Node node) {
        assert node != null;
        assert !this.nodes.contains(node);
        this.nodes.add(node);
    }

    /**
     * Checks if this set is empty.
     * @return A boolean value.
     */
    public boolean isEmpty() {
        return getList().isEmpty();
    }
    
    /**
     * Removes a node.
     * @param resource The node.
     */
    public void remove(Node resource) {
        assert resource != null;
        assert getList().contains(resource);
        getList().remove(resource);
    }
    
    /**
     * Removes all nodes.
     */
    public void clear() {
        getList().clear();
    }
    
    /**
     * Reverses the node order.
     */
    public void reverse() {
        Collections.reverse(getList());
    }
    
}
