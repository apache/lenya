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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.LockException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.Persistable;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.repository.SessionHolder;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.tree.SiteTree;

/**
 * Simple site tree implementation.
 */
public class SiteTreeImpl implements SiteTree, Persistable, RepositoryItem {

    private Area area;
    private RootNode root;
    private int revision;
    private TreeBuilder builder;
    private TreeWriter writer;
    private NodeFactory nodeFactory;
    private boolean changed = false;

    /**
     * @param area The area.
     * @param logger The logger.
     */
    public SiteTreeImpl(Area area) {
        this.area = area;
        initRoot();
    }

    protected void initRoot() {
        this.root = new RootNode(this);
        nodeAdded(root);
    }

    private String sourceUri;

    protected String getSourceUri() {
        if (this.sourceUri == null) {
            String baseUri = this.area.getPublication().getContentUri(this.area.getName());
            this.sourceUri = baseUri + "/sitetree.xml";
        }
        return this.sourceUri;
    }

    private boolean loading = false;

    protected static final String NAMESPACE = "http://apache.org/cocoon/lenya/sitetree/1.0";

    private static final boolean DEFAULT_VISIBILITY = true;

    private boolean loaded = false;

    protected synchronized void load() {

        if (this.loaded || this.loading) {
            return;
        }

        Node repoNode = getRepositoryNode();

        try {
            repoNode.setPersistable(this);

            if (repoNode.exists()) {
                try {
                    this.loading = true;
                    reset();
                    this.builder.buildTree(this);
                    assert getRevision() == getRevision(getRepositoryNode());
                } finally {
                    this.loading = false;
                }
            }
            this.loaded = true;
            if (!repoNode.exists()) {
                reset();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void reset() {
        this.path2node.clear();
        this.uuidLanguage2link.clear();
        initRoot();
    }

    protected RootNode getRoot() {
        load();
        return this.root;
    }

    public synchronized void save() throws RepositoryException {
        if (loading || !changed) {
            return;
        }
        try {
            this.writer.writeTree(this);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }

    }

    protected int getRevision(Node repoNode) {
        int revision = 0;
        if (repoNode.getHistory().getRevisionNumbers().length > 0) {
            revision = repoNode.getHistory().getLatestRevision().getNumber();
        }
        return revision;
    }

    public Link add(String path, Document doc) throws SiteException {

        if (containsByUuid(doc.getUUID(), doc.getLanguage())) {
            throw new SiteException("The document [" + doc + "] is already contained!");
        }

        TreeNodeImpl node;
        if (contains(path)) {
            node = getTreeNode(path);
            if (node.getUuid() == null) {
                node.setUuid(doc.getUUID());
            } else if (!node.getUuid().equals(doc.getUUID())) {
                throw new SiteException("The node already has a different UUID!");
            }
        } else {
            node = (TreeNodeImpl) add(path);
            node.setUuid(doc.getUUID());
        }
        return node.addLink(doc.getLanguage(), "");
    }

    protected TreeNodeImpl getTreeNode(String path) throws SiteException {
        return (TreeNodeImpl) getNode(path);
    }

    public SiteNode add(String path) throws SiteException {
        String parentPath = getParentPath(path);
        String nodeName = path.substring(parentPath.length() + 1);
        if (!contains(parentPath)) {
            add(parentPath);
        }
        return getTreeNode(parentPath).addChild(nodeName, DEFAULT_VISIBILITY);
    }

    public SiteNode add(String path, String followingSiblingPath) throws SiteException {
        String parentPath = getParentPath(path);
        String nodeName = path.substring(parentPath.length() + 1);

        if (!followingSiblingPath.startsWith(parentPath + "/")) {
            throw new SiteException("Invalid following sibling path [" + followingSiblingPath + "]");
        }

        String followingNodeName = followingSiblingPath.substring(parentPath.length() + 1);

        if (!contains(parentPath)) {
            add(parentPath);
        }
        return getTreeNode(parentPath).addChild(nodeName, followingNodeName, DEFAULT_VISIBILITY);
    }

    protected String getParentPath(String path) {
        int lastIndex = path.lastIndexOf("/");
        String parentPath = path.substring(0, lastIndex);
        return parentPath;
    }

    private Map path2node = new HashMap();
    private Map uuidLanguage2link = new HashMap();

    protected void nodeAdded(SiteNode node) {
        String path = node.getPath();
        assert path != null;
        assert node == this.root || path.length() > 0;
        this.path2node.put(path, node);
    }

    protected void linkAdded(Link link) {
        if (link.getNode().getUuid() != null) {
            this.uuidLanguage2link.put(getKey(link), link);
        }
    }

    protected String getKey(Link link) {
        String uuid = link.getNode().getUuid();
        assert uuid != null;
        String language = link.getLanguage();
        assert language != null;
        return getKey(uuid, language);
    }

    protected String getKey(String uuid, String language) {
        Validate.notNull(uuid);
        Validate.notNull(language);
        return uuid + ":" + language;
    }

    protected void nodeRemoved(String path) {
        Validate.notNull(path);
        Validate.isTrue(this.path2node.containsKey(path), "Path not found: ", path);
        this.path2node.remove(path);
    }

    protected Map getUuidLanguage2Link() {
        load();
        return this.uuidLanguage2link;
    }

    protected Map getPath2Node() {
        load();
        return this.path2node;
    }

    public boolean contains(String path) {
        Validate.notNull(path);
        load();
        return this.path2node.containsKey(path);
    }

    public boolean containsByUuid(String uuid, String language) {
        Validate.notNull(uuid);
        Validate.notNull(language);
        return getUuidLanguage2Link().containsKey(getKey(uuid, language));
    }

    public boolean containsInAnyLanguage(String uuid) {
        Validate.notNull(uuid);
        Set set = getUuidLanguage2Link().keySet();
        String[] keys = (String[]) set.toArray(new String[set.size()]);
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].startsWith(uuid + ":")) {
                return true;
            }
        }
        return false;
    }

    public String getArea() {
        return this.area.getName();
    }

    public Link getByUuid(String uuid, String language) throws SiteException {
        Validate.notNull(uuid);
        Validate.notNull(language);
        String key = getKey(uuid, language);
        if (!getUuidLanguage2Link().containsKey(key)) {
            throw new SiteException("No link for [" + key + "]");
        }
        return (Link) getUuidLanguage2Link().get(key);
    }

    public SiteNode getNode(String path) throws SiteException {
        Validate.notNull(path);
        if (!getPath2Node().containsKey(path)) {
            throw new SiteException("No node for path [" + path + "]");
        }
        return (SiteNode) this.path2node.get(path);
    }

    public SiteNode[] getNodes() {
        return getRoot().preOrder();
    }

    public Publication getPublication() {
        return this.area.getPublication();
    }

    public Session getRepositorySession() {
        SessionHolder holder = (SessionHolder) this.area.getPublication().getSession();
        return holder.getRepositorySession();
    }

    public Node getRepositoryNode() {
        try {
            return (Node) getRepositorySession()
                    .getRepositoryItem(this.nodeFactory, getSourceUri());
        } catch (RepositoryException e) {
            throw new RuntimeException("Creating repository node failed: ", e);
        }
    }

    public SiteNode[] getTopLevelNodes() {
        return getRoot().getChildren();
    }

    protected void linkRemoved(String uuid, String language) {
        Validate.notNull(uuid);
        Validate.notNull(language);
        String key = getKey(uuid, language);
        assert this.uuidLanguage2link.containsKey(key);
        this.uuidLanguage2link.remove(key);
    }

    protected String getPath() {
        return "";
    }

    /**
     * @return The nodes in pre-order enumeration.
     */
    public SiteNode[] preOrder() {
        return getRoot().preOrder();
    }

    public void moveDown(String path) throws SiteException {
        TreeNode node = getTreeNode(path);
        TreeNode parent = getParent(node);
        parent.moveDown(node.getName());

    }

    public void moveUp(String path) throws SiteException {
        TreeNode node = getTreeNode(path);
        TreeNode parent = getParent(node);
        parent.moveUp(node.getName());
    }

    /**
     * @param node A node.
     * @return The parent of the node, which is the root node for top level nodes.
     * @throws SiteException if an error occurs.
     */
    protected TreeNode getParent(TreeNode node) throws SiteException {
        TreeNode parent;
        if (node.isTopLevel()) {
            parent = getRoot();
        } else {
            parent = (TreeNode) node.getParent();
        }
        return parent;
    }

    public boolean contains(String path, String language) {
        if (contains(path)) {
            SiteNode node;
            try {
                node = getNode(path);
            } catch (SiteException e) {
                throw new RuntimeException(e);
            }
            return node.hasLink(language);
        }
        return false;
    }

    protected void changed() {
        if (!this.loading) {
            this.changed = true;
        }
    }

    public boolean isModified() {
        return this.changed;
    }

    protected int getRevision() {
        return this.revision;
    }

    protected void setRevision(int revision) {
        this.revision = revision;
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

    public void setBuilder(TreeBuilder treeBuilder) {
        this.builder = treeBuilder;
    }

    public void setNodeFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public void setWriter(TreeWriter treeWriter) {
        this.writer = treeWriter;
    }

}
