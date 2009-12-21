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
package org.apache.lenya.cms.site;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.util.DocumentSet;

/**
 * A set containing nodes.
 */
public class NodeSet {

    private ServiceManager manager;

    /**
     * Ctor.
     * @param manager The service manager.
     */
    public NodeSet(ServiceManager manager) {
        this.manager = manager;
    }

    /**
     * Ctor.
     * @param manager The service manager.
     * @param _nodes The initial nodes.
     */
    public NodeSet(ServiceManager manager, SiteNode[] _nodes) {
        this(manager);
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
            SiteNode node;
            try {
                node = docs[i].getLink().getNode();
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
            if (!contains(node)) {
                add(node);
            }
        }
    }

    /**
     * @param node A node.
     * @return If the node is contained.
     */
    public boolean contains(SiteNode node) {
        return getSet().contains(node);
    }

    private Set nodes = new HashSet();

    /**
     * Returns the list object that stores the documents.
     * @return A list.
     */
    protected Set getSet() {
        return this.nodes;
    }

    /**
     * Returns the documents contained in this set.
     * 
     * @return An array of documents.
     */
    public SiteNode[] getNodes() {
        return (SiteNode[]) this.nodes.toArray(new SiteNode[this.nodes.size()]);
    }

    /**
     * Adds a node to this set.
     * @param node The node to add.
     */
    public void add(SiteNode node) {
        assert node != null;
        assert !this.nodes.contains(node);
        this.nodes.add(node);
    }

    /**
     * Checks if this set is empty.
     * @return A boolean value.
     */
    public boolean isEmpty() {
        return getSet().isEmpty();
    }

    /**
     * Removes a node.
     * @param resource The node.
     */
    public void remove(SiteNode resource) {
        assert resource != null;
        assert getSet().contains(resource);
        getSet().remove(resource);
    }

    /**
     * Removes all nodes.
     */
    public void clear() {
        getSet().clear();
    }

    /**
     * @return An iterator iterating in undetermined order.
     */
    public NodeIterator iterator() {
        return new NodeIterator(getNodes());
    }

    /**
     * @return An iterator iterating in ascending order.
     */
    public NodeIterator ascending() {
        SiteNode[] nodes = getNodesAscending();
        return new NodeIterator(nodes);
    }

    /**
     * @return An iterator iterating in descending order.
     */
    public NodeIterator descending() {
        SiteNode[] nodes = getNodesAscending();
        List list = Arrays.asList(nodes);
        Collections.reverse(list);
        return new NodeIterator(list);
    }

    protected SiteNode[] getNodesAscending() {
        if (isEmpty()) {
            return new SiteNode[0];
        }
        
        SiteNode[] nodes;
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(getNodes()[0].getStructure()
                    .getPublication()
                    .getSiteManagerHint());
            nodes = siteManager.sortAscending(getNodes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                manager.release(selector);
            }
        }
        return nodes;
    }

    /**
     * @return All documents referenced by this node set.
     */
    public Document[] getDocuments() {
        List documents = new ArrayList();
        for (NodeIterator i = iterator(); i.hasNext(); ) {
            SiteNode node = i.next();
            String[] langs = node.getLanguages();
            for (int l = 0; l < langs.length; l++) {
                try {
                    documents.add(node.getLink(langs[l]).getDocument());
                } catch (SiteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return (Document[]) documents.toArray(new Document[documents.size()]);
    }

    /**
     * Adds all nodes from a node set to this.
     * @param set The set.
     */
    public void addAll(NodeSet set) {
        this.nodes.addAll(set.getSet());
    }

}
