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
package org.apache.lenya.cms.site.tree2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.LockException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.tree.SiteTree;

/**
 * Site tree implementation which delegates all operations to a shared site tree.
 */
public class DelegatingSiteTree implements SiteTree, RepositoryItem {

    private Area area;
    private Map links = new HashMap();
    private Map nodes = new HashMap();
    private List nodeList;
    private List topLevelNodes;
    private List preOrder;
    private String sourceUri;
    private Session sharedItemStoreSession;
    private String key;
    private SiteTreeFactory factory;

    /**
     * @param area The area which this tree belongs to.
     * @param factory The site tree factory.
     * @param store The shared item store.
     * @param key The key to build the sitetree.
     */
    public DelegatingSiteTree(Area area, SiteTreeFactory factory, Session session, String key) {
        this.area = area;
        this.key = key;
        this.factory = factory;
        this.sharedItemStoreSession = session;
    }

    public Link add(String path, Document doc) throws SiteException {
        throw new UnsupportedOperationException();
    }

    public SiteNode add(String path) throws SiteException {
        throw new UnsupportedOperationException();
    }

    public SiteNode add(String path, String followingSiblingPath) throws SiteException {
        throw new UnsupportedOperationException();
    }

    public boolean contains(String path) {
        return getTree().contains(path);
    }

    public boolean contains(String path, String language) {
        return getTree().contains(path, language);
    }

    public boolean containsByUuid(String uuid, String language) {
        return getTree().containsByUuid(uuid, language);
    }

    public boolean containsInAnyLanguage(String uuid) {
        return getTree().containsInAnyLanguage(uuid);
    }

    public String getArea() {
        return this.area.getName();
    }

    public Link getByUuid(String uuid, String language) throws SiteException {
        Link delegate = getTree().getByUuid(uuid, language);
        return getLink(delegate);
    }

    protected Link getLink(Link delegate) {
        Link link = (Link) this.links.get(delegate);
        if (link == null) {
            link = new DelegatingLink(getNode(delegate.getNode()), delegate.getLabel(), delegate
                    .getLanguage());
        }
        return link;
    }

    protected DelegatingNode getNode(SiteNode delegate) {
        DelegatingNode node = (DelegatingNode) this.nodes.get(delegate);
        if (node == null) {
            node = new DelegatingNode(this, delegate);
            this.nodes.put(delegate, node);
        }
        return node;
    }

    public SiteNode getNode(String path) throws SiteException {
        return getNode(getTree().getNode(path));
    }

    public SiteNode[] getNodes() {
        if (this.nodeList == null) {
            SiteNode[] delegates = getTree().getNodes();
            this.nodeList = new ArrayList();
            for (int i = 0; i < delegates.length; i++) {
                this.nodeList.add(getNode(delegates[i]));
            }
        }
        return (SiteNode[]) this.nodeList.toArray(new SiteNode[this.nodeList.size()]);
    }

    public Publication getPublication() {
        return this.area.getPublication();
    }

    public SiteNode[] getTopLevelNodes() {
        if (this.topLevelNodes == null) {
            SiteNode[] delegates = getTree().getTopLevelNodes();
            this.topLevelNodes = new ArrayList();
            for (int i = 0; i < delegates.length; i++) {
                this.topLevelNodes.add(getNode(delegates[i]));
            }
        }
        return (SiteNode[]) this.topLevelNodes.toArray(new SiteNode[this.topLevelNodes.size()]);
    }

    public Session getRepositorySession() {
        return (Session) this.area.getPublication().getSession();
    }

    private NodeFactory nodeFactory;

    protected NodeFactory getNodeFactory() {
        if (this.nodeFactory == null) {
            this.nodeFactory = (NodeFactory) WebAppContextUtils.getCurrentWebApplicationContext()
                    .getBean(NodeFactory.ROLE);
        }
        return this.nodeFactory;
    }

    public Node getRepositoryNode() {
        try {
            return (Node) getRepositorySession()
                    .getRepositoryItem(getNodeFactory(), getSourceUri());
        } catch (RepositoryException e) {
            throw new RuntimeException("Creating repository node failed: ", e);
        }
    }

    protected String getSourceUri() {
        if (this.sourceUri == null) {
            String baseUri = this.area.getPublication().getContentUri(this.area.getName());
            this.sourceUri = baseUri + "/sitetree.xml";
        }
        return this.sourceUri;
    }

    public void moveDown(String path) throws SiteException {
        throw new UnsupportedOperationException();
    }

    public void moveUp(String path) throws SiteException {
        throw new UnsupportedOperationException();
    }

    public SiteNode[] preOrder() {
        if (this.preOrder == null) {
            SiteNode[] delegates = getTree().preOrder();
            this.preOrder = new ArrayList();
            for (int i = 0; i < delegates.length; i++) {
                this.preOrder.add(getNode(delegates[i]));
            }
        }
        return (SiteNode[]) this.preOrder.toArray(new SiteNode[this.preOrder.size()]);
    }

    protected SiteTree getTree() {
        try {
            return (SiteTree) this.sharedItemStoreSession.getRepositoryItem(this.factory, this.key);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() throws RepositoryException {
        throw new UnsupportedOperationException();
    }

    public void checkin() throws org.apache.lenya.cms.publication.RepositoryException {
        try {
            getRepositoryNode().checkin();
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.publication.RepositoryException(e);
        }
    }

    public void checkout() throws org.apache.lenya.cms.publication.RepositoryException {
        try {
            getRepositoryNode().checkout();
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.publication.RepositoryException(e);
        }
    }

    public void checkout(boolean restrictedToSession)
            throws org.apache.lenya.cms.publication.RepositoryException {
        try {
            getRepositoryNode().checkout(restrictedToSession);
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.publication.RepositoryException(e);
        }
    }

    public void forceCheckIn() throws org.apache.lenya.cms.publication.RepositoryException {
        try {
            getRepositoryNode().forceCheckIn();
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.publication.RepositoryException(e);
        }
    }

    public String getCheckoutUserId() throws org.apache.lenya.cms.publication.RepositoryException {
        try {
            return getRepositoryNode().getCheckoutUserId();
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.publication.RepositoryException(e);
        }
    }

    public org.apache.lenya.cms.publication.Session getSession() {
        return this.area.getPublication().getSession();
    }

    public String getSourceURI() {
        return getRepositoryNode().getSourceURI();
    }

    public boolean isCheckedOut() throws org.apache.lenya.cms.publication.RepositoryException {
        try {
            return getRepositoryNode().isCheckedOut();
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.publication.RepositoryException(e);
        }
    }

    public boolean isCheckedOutBySession(String sessionId, String userId)
            throws org.apache.lenya.cms.publication.RepositoryException {
        try {
            return getRepositoryNode().isCheckedOutBySession(sessionId, userId);
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.publication.RepositoryException(e);
        }
    }

    public boolean isLocked() throws org.apache.lenya.cms.publication.RepositoryException {
        try {
            return getRepositoryNode().isLocked();
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.publication.RepositoryException(e);
        }
    }

    public void lock() throws LockException, org.apache.lenya.cms.publication.RepositoryException {
        try {
            getRepositoryNode().lock();
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.publication.RepositoryException(e);
        }
    }

    public void registerDirty() throws org.apache.lenya.cms.publication.RepositoryException {
        try {
            getRepositoryNode().registerDirty();
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.publication.RepositoryException(e);
        }
    }

    public void rollback(int revision) throws org.apache.lenya.cms.publication.RepositoryException {
        try {
            getRepositoryNode().rollback(revision);
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.publication.RepositoryException(e);
        }
    }

    public void unlock() throws org.apache.lenya.cms.publication.RepositoryException {
        try {
            getRepositoryNode().unlock();
        } catch (RepositoryException e) {
            throw new org.apache.lenya.cms.publication.RepositoryException(e);
        }
    }

}
