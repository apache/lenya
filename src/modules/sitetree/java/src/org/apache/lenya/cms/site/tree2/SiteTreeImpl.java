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

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.Persistable;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.util.Assert;

/**
 * Simple site tree implementation.
 */
public class SiteTreeImpl extends AbstractLogEnabled implements SiteStructure, SiteTree, Persistable {

    protected static final String SITETREE_FILE_NAME = "sitetree.xml";
    private Area area;
    protected ServiceManager manager;
    private RootNode root;
    private int revision;

    /**
     * @param manager The service manager.
     * @param area The area.
     * @param logger The logger.
     */
    public SiteTreeImpl(ServiceManager manager, Area area, Logger logger) {
        ContainerUtil.enableLogging(this, logger);
        this.area = area;
        this.manager = manager;
        initRoot();
    }

    protected void initRoot() {
        this.root = new RootNode(this, getLogger());
        nodeAdded(root);
    }

    private String sourceUri;

    protected String getSourceUri() {
        if (this.sourceUri == null) {
            String baseUri = this.area.getPublication().getContentURI(this.area.getName());
            this.sourceUri = baseUri + "/" + SITETREE_FILE_NAME;
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
                TreeBuilder builder = null;
                try {
                    this.loading = true;
                    builder = (TreeBuilder) this.manager.lookup(TreeBuilder.ROLE);
                    reset();
                    builder.buildTree(this);
                    Assert.isTrue("Latest revision loaded", getRevision() == getRevision(getRepositoryNode()));
                } finally {
                    this.loading = false;
                    if (builder != null) {
                        this.manager.release(builder);
                    }
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
        TreeWriter writer = null;
        try {
            writer = (TreeWriter) this.manager.lookup(TreeWriter.ROLE);
            int revision = getRevision(getRepositoryNode()) + 1;
            writer.writeTree(this);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {
            if (writer != null) {
                this.manager.release(writer);
            }
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
        Assert.notNull("path", path);
        if (node != this.root) {
            Assert.isTrue("path not empty", path.length() > 0);
        }
        this.path2node.put(path, node);
    }

    protected void linkAdded(Link link) {
        if (link.getNode().getUuid() != null) {
            this.uuidLanguage2link.put(getKey(link), link);
        }
    }

    protected String getKey(Link link) {
        String uuid = link.getNode().getUuid();
        Assert.notNull("uuid", uuid);
        String language = link.getLanguage();
        Assert.notNull("language", language);
        return getKey(uuid, language);
    }

    protected String getKey(String uuid, String language) {
        Assert.notNull("uuid", uuid);
        Assert.notNull("language", language);
        return uuid + ":" + language;
    }

    protected void nodeRemoved(String path) {
        Assert.notNull("path", path);
        Assert.isTrue("path [" + path + "] contained", this.path2node.containsKey(path));
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
        load();
        Assert.notNull("path", path);
        return this.path2node.containsKey(path);
    }

    public boolean containsByUuid(String uuid, String language) {
        Assert.notNull("uuid", uuid);
        Assert.notNull("language", language);
        return getUuidLanguage2Link().containsKey(getKey(uuid, language));
    }

    public boolean containsInAnyLanguage(String uuid) {
        Assert.notNull("uuid", uuid);
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
        Assert.notNull("uuid", uuid);
        Assert.notNull("language", language);
        String key = getKey(uuid, language);
        if (!getUuidLanguage2Link().containsKey(key)) {
            throw new SiteException("No link for [" + key + "]");
        }
        return (Link) getUuidLanguage2Link().get(key);
    }

    public SiteNode getNode(String path) throws SiteException {
        Assert.notNull("path", path);
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

    public Session getSession() {
        return this.area.getPublication().getFactory().getSession();
    }
    
    private NodeFactory nodeFactory;
    private boolean changed = false;
    
    protected NodeFactory getNodeFactory() {
        if (this.nodeFactory == null) {
            try {
                this.nodeFactory = (NodeFactory) manager.lookup(NodeFactory.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException("Creating repository node failed: ", e);
            }
        }
        return this.nodeFactory;
    }

    public Node getRepositoryNode() {
        try {
            return (Node) getSession().getRepositoryItem(getNodeFactory(), getSourceUri());
        } catch (RepositoryException e) {
            throw new RuntimeException("Creating repository node failed: ", e);
        }
    }

    public SiteNode[] getTopLevelNodes() {
        return getRoot().getChildren();
    }

    protected void linkRemoved(String uuid, String language) {
        Assert.notNull("uuid", uuid);
        Assert.notNull("language", language);
        String key = getKey(uuid, language);
        Assert.isTrue("contained", this.uuidLanguage2link.containsKey(key));
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
        load();
        return this.revision;
    }
    
    protected void setRevision(int revision) {
        this.revision = revision;
    }

}
