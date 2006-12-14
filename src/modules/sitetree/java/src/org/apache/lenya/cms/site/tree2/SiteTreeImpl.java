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
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.RepositorySource;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
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

    private boolean loaded = false;

    protected static final String NAMESPACE = "http://apache.org/cocoon/lenya/sitetree/1.0";

    private static final boolean DEFAULT_VISIBILITY = true;

    protected void load() {
        try {
            if (!loaded) {
                if (SourceUtil.exists(getSourceUri(), this.manager)) {
                    org.w3c.dom.Document xml = SourceUtil.readDOM(getSourceUri(), this.manager);
                    NamespaceHelper helper = new NamespaceHelper(NAMESPACE, "", xml);
                    Assert.isTrue("document element is site", xml.getDocumentElement()
                            .getLocalName().equals("site"));
                    loadNodes(getRoot(), helper, xml.getDocumentElement());
                }
                loaded = true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected RootNode getRoot() {
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
        if (!loaded) {
            return;
        }
        try {
            NamespaceHelper helper = new NamespaceHelper(NAMESPACE, "", "site");
            saveNodes(getRoot(), helper, helper.getDocument().getDocumentElement());
            SourceUtil.writeDOM(helper.getDocument(), getSourceUri(), this.manager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
        this.path2node.put(node.getPath(), node);
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

    private Node repositoryNode;

    public Node getRepositoryNode() {
        if (this.repositoryNode == null) {
            SourceResolver resolver = null;
            RepositorySource source = null;
            try {
                resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                source = (RepositorySource) resolver.resolveURI(getSourceUri());
                this.repositoryNode = source.getNode();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (resolver != null) {
                    if (source != null) {
                        resolver.release(source);
                    }
                    this.manager.release(resolver);
                }
            }
        }
        return this.repositoryNode;
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
        TreeNode parent = (TreeNode) node.getParent();
        parent.moveDown(node.getName());
        
    }

    public void moveUp(String path) throws SiteException {
        TreeNode node = getTreeNode(path);
        TreeNode parent = (TreeNode) node.getParent();
        parent.moveUp(node.getName());
    }

}
