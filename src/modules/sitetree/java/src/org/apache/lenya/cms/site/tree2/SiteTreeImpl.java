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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.util.Assert;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * Simple site tree implementation.
 */
public class SiteTreeImpl extends AbstractLogEnabled implements SiteStructure, SiteTree {

    private Area area;
    protected ServiceManager manager;
    private RootNode root;

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
            this.sourceUri = baseUri + "/sitetree.xml";
        }
        return this.sourceUri;
    }

    private long lastModified = -1;
    private boolean loading = false;

    protected static final String NAMESPACE = "http://apache.org/cocoon/lenya/sitetree/1.0";

    private static final boolean DEFAULT_VISIBILITY = true;

    private boolean loaded = false;

    protected synchronized void load() {

        Node repoNode = getRepositoryNode();

        if (this.loaded) {
            return;
        }

        try {
            // lastModified check is necessary for clustering, but can cause 404s
            // because of the 1s file system last modification granularity
            if (repoNode.exists() /* && repoNode.getLastModified() > this.lastModified */) {
                long lastModified = repoNode.getLastModified();
                org.w3c.dom.Document xml = DocumentHelper.readDocument(repoNode.getInputStream());

                NamespaceHelper helper = new NamespaceHelper(NAMESPACE, "", xml);
                Assert.isTrue("document element is site", xml.getDocumentElement().getLocalName()
                        .equals("site"));
                this.loading = true;
                reset();
                loadNodes(this.root, helper, xml.getDocumentElement());
                this.loading = false;
                this.lastModified = lastModified;
            }

            if (!repoNode.exists() && this.lastModified > -1) {
                reset();
                this.lastModified = -1;
            }

            this.loaded = true;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        checkInvariants();
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

    protected void loadNodes(TreeNode parent, NamespaceHelper helper, Element element) {
        Element[] nodeElements = helper.getChildren(element, "node");
        for (int n = 0; n < nodeElements.length; n++) {
            String name = nodeElements[n].getAttribute("id");
            boolean visible = DEFAULT_VISIBILITY;
            if (nodeElements[n].hasAttribute("visibleinnav")) {
                String visibleString = nodeElements[n].getAttribute("visibleinnav");
                visible = Boolean.valueOf(visibleString).booleanValue();
            }
            TreeNodeImpl node = (TreeNodeImpl) parent.addChild(name, visible);
            if (nodeElements[n].hasAttribute("uuid")) {
                String uuid = nodeElements[n].getAttribute("uuid");
                node.setUuid(uuid);
            }
            loadLinks(node, helper, nodeElements[n]);
            loadNodes(node, helper, nodeElements[n]);
        }
    }

    protected void loadLinks(TreeNodeImpl node, NamespaceHelper helper, Element element) {
        Element[] linkElements = helper.getChildren(element, "label");
        for (int l = 0; l < linkElements.length; l++) {
            String lang = linkElements[l].getAttribute("xml:lang");
            String label = DocumentHelper.getSimpleElementText(linkElements[l]);
            node.addLink(lang, label);
        }
    }

    protected void save() {
        if (loading) {
            return;
        }
        try {
            Node repoNode = getRepositoryNode();
            NamespaceHelper helper = new NamespaceHelper(NAMESPACE, "", "site");

            int revision = getRevision(repoNode) + 1;
            helper.getDocument().getDocumentElement().setAttribute("revision",
                    Integer.toString(revision));

            saveNodes(getRoot(), helper, helper.getDocument().getDocumentElement());
            helper.save(repoNode.getOutputStream());
            this.lastModified = repoNode.getLastModified();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected int getRevision(Node repoNode) {
        int revision = 0;
        if (repoNode.getHistory().getRevisionNumbers().length > 0) {
            revision = repoNode.getHistory().getLatestRevision().getNumber();
        }
        return revision;
    }

    protected void saveNodes(TreeNode parent, NamespaceHelper helper, Element parentElement)
            throws SiteException {
        SiteNode[] children = parent.getChildren();
        for (int i = 0; i < children.length; i++) {
            Element nodeElement = helper.createElement("node");
            nodeElement.setAttribute("id", children[i].getName());
            String uuid = children[i].getUuid();
            if (uuid != null) {
                nodeElement.setAttribute("uuid", uuid);
            }
            nodeElement.setAttribute("visibleinnav", Boolean.toString(children[i].isVisible()));
            saveLinks(children[i], helper, nodeElement);
            saveNodes((TreeNode) children[i], helper, nodeElement);
            parentElement.appendChild(nodeElement);
        }
    }

    protected void saveLinks(SiteNode node, NamespaceHelper helper, Element nodeElement)
            throws SiteException {
        String[] languages = node.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            Link link = node.getLink(languages[i]);
            Element linkElement = helper.createElement("label", link.getLabel());
            linkElement.setAttribute("xml:lang", languages[i]);
            nodeElement.appendChild(linkElement);
        }
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
        this.uuidLanguage2link.put(getKey(link), link);
    }

    protected String getKey(Link link) {
        String uuid = link.getDocument().getUUID();
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

    protected void checkInvariants() {
        if (true) {
            return;
        }
        for (Iterator paths = this.path2node.keySet().iterator(); paths.hasNext();) {
            String path = (String) paths.next();
            SiteNode node = (SiteNode) this.path2node.get(path);
            String uuid = node.getUuid();
            if (uuid != null) {
                String[] langs = node.getLanguages();
                for (int i = 0; i < langs.length; i++) {
                    String key = getKey(uuid, langs[i]);
                    Assert.isTrue("contains link for [" + key + "]", this.uuidLanguage2link
                            .containsKey(key));
                }
            }
        }
        for (Iterator keys = this.uuidLanguage2link.keySet().iterator(); keys.hasNext();) {
            String key = (String) keys.next();
            Link link = (Link) this.uuidLanguage2link.get(key);
            Assert.isTrue("contains path for [" + key + "]", this.path2node.containsKey(link
                    .getNode().getPath()));
        }

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

    public Node getRepositoryNode() {
        NodeFactory factory = null;
        try {
            factory = (NodeFactory) manager.lookup(NodeFactory.ROLE);
            return (Node) getSession().getRepositoryItem(factory, getSourceUri());
        } catch (Exception e) {
            throw new RuntimeException("Creating repository node failed: ", e);
        } finally {
            if (factory != null) {
                manager.release(factory);
            }
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
}
