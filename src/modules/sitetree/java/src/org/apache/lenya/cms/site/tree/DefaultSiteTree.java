/*
 * Copyright  1999-2005 The Apache Software Foundation
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

package org.apache.lenya.cms.site.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.RepositorySource;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Default sitetree implementation.
 * 
 * @version $Id: DefaultSiteTree.java 208764 2005-07-01 15:57:21Z andreas $
 */
public class DefaultSiteTree extends AbstractLogEnabled implements SiteTree {

    /**
     * The sitetree namespace.
     */
    public static final String NAMESPACE_URI = "http://apache.org/cocoon/lenya/sitetree/1.0";

    /**
     * The name of the sitetree file.
     */
    public static final String SITE_TREE_FILENAME = "sitetree.xml";

    private String sourceUri;
    // the area is only retained to provide some more info when raising an
    // exception.
    private String area = "";
    private Publication pub;
    protected ServiceManager manager;
    private Document document;
    private DocumentFactory factory;

    /**
     * Create a DefaultSiteTree
     * @param factory The document factory.
     * @param publication The publication.
     * @param _area The area.
     * @param manager The service manager.
     * @param logger The logger.
     * @throws SiteException if an error occurs.
     */
    protected DefaultSiteTree(DocumentFactory factory, Publication publication, String _area,
            ServiceManager manager, Logger logger) throws SiteException {

        ContainerUtil.enableLogging(this, logger);

        this.factory = factory;
        this.pub = publication;
        this.sourceUri = publication.getSourceURI() + "/content/" + _area + "/"
                + SITE_TREE_FILENAME;
        this.area = _area;
        this.manager = manager;
        try {
            this.document = SourceUtil.readDOM(this.sourceUri, this.manager);
            if (this.document == null) {
                getLogger().info("Empty sitetree will be created/initialized!");
                this.document = createDocument();
            }
        } catch (Exception e) {
            throw new SiteException(e);
        }
    }

    protected void saveDocument() throws SiteException {
        try {
            SourceUtil.writeDOM(this.document, this.sourceUri, this.manager);
        } catch (Exception e) {
            throw new SiteException(e);
        }
    }

    /**
     * Checks if the tree file has been modified externally and reloads the site tree. protected
     * synchronized void checkModified() { if (this.area.equals(Publication.LIVE_AREA) &&
     * this.treefile.lastModified() > this.lastModified) {
     * 
     * if (getLogger().isDebugEnabled()) { getLogger().debug("Sitetree [" + this.treefile + "] has
     * changed: reloading."); }
     * 
     * try { this.document = DocumentHelper.readDocument(this.treefile); } catch (Exception e) {
     * throw new IllegalStateException(e.getMessage()); } this.lastModified =
     * this.treefile.lastModified(); } }
     */

    /**
     * Create a new DefaultSiteTree xml document.
     * @return the new site document
     * @throws ParserConfigurationException if an error occurs
     */
    public synchronized Document createDocument() throws ParserConfigurationException {
        Document document = DocumentHelper.createDocument(NAMESPACE_URI, "site", null);

        Element root = document.getDocumentElement();
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:schemaLocation",
                "http://apache.org/cocoon/lenya/sitetree/1.0  ../../../../resources/entities/sitetree.xsd");

        return document;
    }

    /**
     * Find a node in a subtree. The search is started at the given node. The list of ids contains
     * the document-id split by "/".
     * @param node where to start the search
     * @param ids list of node ids
     * @return the node that matches the path given in the list of ids
     */
    protected synchronized Node findNode(Node node, List ids) {
        if (ids.size() < 1) {
            return node;
        }
        NodeList nodes = node.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            NamedNodeMap attributes = nodes.item(i).getAttributes();

            if (attributes != null) {
                Node idAttribute = attributes.getNamedItem("id");

                if (idAttribute != null && !"".equals(idAttribute.getNodeValue())
                        && idAttribute.getNodeValue().equals(ids.get(0))) {
                    return findNode(nodes.item(i), ids.subList(1, ids.size()));
                }
            }
        }

        // node wasn't found
        return null;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(org.apache.lenya.cms.site.tree.SiteTreeNode,
     *      java.lang.String)
     */
    public synchronized void addNode(SiteTreeNode node, String refpath) throws SiteException {
        SiteTreeNode target = addNode(node.getParent().getPath(),
                node.getName(),
                node.getUuid(),
                node.visibleInNav(),
                node.getHref(),
                node.getSuffix(),
                node.hasLink(),
                refpath);
        copyLinks(node, target);
    }

    protected void copyLinks(SiteTreeNode source, SiteTreeNode target) throws SiteException {
        String[] languages = source.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            addLabel(target.getPath(), languages[i], source.getLink(languages[i]).getLabel());
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String, java.lang.String,
     *      String, boolean)
     */
    public synchronized void addNode(String parentid, String id, String uuid, boolean visibleInNav)
            throws SiteException {
        addNode(parentid, id, uuid, visibleInNav, null, null, false);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(org.apache.lenya.cms.site.tree.SiteTreeNode)
     */
    public synchronized void addNode(SiteTreeNode node) throws SiteException {
        addNode(node, null);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String, String,
     *      org.apache.lenya.cms.site.Label[], boolean, java.lang.String, java.lang.String, boolean,
     *      java.lang.String)
     */
    public synchronized SiteTreeNode addNode(String path, String uuid, boolean visibleInNav,
            String href, String suffix, boolean link, String refpath) throws SiteException {
        StringBuffer buf = new StringBuffer();
        StringTokenizer st = new StringTokenizer(path, "/");
        int length = st.countTokens();

        for (int i = 0; i < (length - 1); i++) {
            buf.append("/" + st.nextToken());
        }
        String parentid = buf.toString();
        String id = st.nextToken();
        return addNode(parentid, id, uuid, visibleInNav, href, suffix, link, refpath);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String, String,
     *      org.apache.lenya.cms.site.Label[], boolean, java.lang.String, java.lang.String, boolean)
     */
    public synchronized SiteTreeNode addNode(String path, String uuid, boolean visibleInNav,
            String href, String suffix, boolean link) throws SiteException {
        return addNode(path, uuid, visibleInNav, href, suffix, link, null);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String, java.lang.String,
     *      String, org.apache.lenya.cms.site.Label[], boolean, java.lang.String, java.lang.String,
     *      boolean)
     */
    public synchronized SiteTreeNode addNode(String parentid, String id, String uuid,
            boolean visibleInNav, String href, String suffix, boolean link) throws SiteException {
        return addNode(parentid + "/" + id, uuid, visibleInNav, href, suffix, link, null);
    }
    
    protected void createParents(final String path) throws SiteException {
        String[] steps = path.substring(1).split("/");
        int s = 0;
        String ancestorPath = "";
        while (s < steps.length) {
            if (!contains(ancestorPath)) {
                add(ancestorPath);
            }
            ancestorPath += "/" + steps[s];
            s++;
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String, java.lang.String,
     *      String, org.apache.lenya.cms.site.Label[], boolean, java.lang.String, java.lang.String,
     *      boolean, java.lang.String)
     */
    public synchronized SiteTreeNode addNode(String parentPath, String name, String uuid,
            boolean visibleInNav, String href, String suffix, boolean link, String refpath)
            throws SiteException {
        
        String path = parentPath + "/" + name;
        createParents(path);

        Node parentNode = getNodeInternal(parentPath);

        getLogger().debug("PARENT ELEMENT: " + parentNode);
        getLogger().debug("VISIBLEINNAV IS: " + visibleInNav);

        // Check if child already exists
        Node childNode = getNodeInternal(path);

        if (childNode != null) {
            getLogger().info("This node: " + path + " has already been inserted");
            return (SiteTreeNode) getNode(path);
        }

        // Create node
        NamespaceHelper helper = new NamespaceHelper(NAMESPACE_URI, "", this.document);
        Element child = helper.createElement(SiteTreeNodeImpl.NODE_NAME);
        child.setAttribute(SiteTreeNodeImpl.ID_ATTRIBUTE_NAME, name);
        if (uuid != null) {
            child.setAttribute(SiteTreeNodeImpl.UUID_ATTRIBUTE_NAME, uuid);
        }

        if (visibleInNav) {
            child.setAttribute(SiteTreeNodeImpl.VISIBLEINNAV_ATTRIBUTE_NAME, "true");
        } else {
            child.setAttribute(SiteTreeNodeImpl.VISIBLEINNAV_ATTRIBUTE_NAME, "false");
        }

        if ((href != null) && (href.length() > 0)) {
            child.setAttribute(SiteTreeNodeImpl.HREF_ATTRIBUTE_NAME, href);
        }

        if ((suffix != null) && (suffix.length() > 0)) {
            child.setAttribute(SiteTreeNodeImpl.SUFFIX_ATTRIBUTE_NAME, suffix);
        }

        if (link) {
            child.setAttribute(SiteTreeNodeImpl.LINK_ATTRIBUTE_NAME, "true");
        }

        // Add Node
        if (refpath != null && !refpath.equals("")) {
            Node nextSibling = getNodeInternal(refpath);
            if (nextSibling != null) {
                parentNode.insertBefore(child, nextSibling);
            } else {
                parentNode.appendChild(child);
            }
        } else {
            parentNode.appendChild(child);
        }
        getLogger().debug("Tree has been modified: " + document.getDocumentElement());
        saveDocument();
        return (SiteTreeNode) getNode(path);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addLabel(java.lang.String,
     *      org.apache.lenya.cms.site.Label)
     */
    public synchronized void addLabel(String path, String language, String label) {
        try {
            SiteTreeNode node = (SiteTreeNode) getNode(path);
            if (node != null) {
                node.addLabel(language, label);
            }
            saveDocument();
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#removeLabel(java.lang.String,
     *      org.apache.lenya.cms.site.Label)
     */
    public synchronized void removeLabel(String path, String language) {
        try {
            SiteTreeNode node = (SiteTreeNode) getNode(path);
            node.removeLabel(language);
            saveDocument();
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#removeNode(java.lang.String)
     */
    public synchronized SiteTreeNode removeNode(String path) {
        assert path != null;

        Node node;
        try {
            node = removeNodeInternal(path);
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
        if (node == null) {
            return null;
        }

        SiteTreeNode newNode = new SiteTreeNodeImpl(this.factory, this, (Element) node, getLogger());
        ContainerUtil.enableLogging(newNode, getLogger());
        return newNode;
    }

    /**
     * removes the node corresponding to the given document-id and returns it
     * @param path the document-id of the Node to be removed
     * @return the <code>Node</code> that was removed
     * @throws SiteException
     */
    private synchronized Node removeNodeInternal(String path) throws SiteException {
        Node node = this.getNodeInternal(path);
        Node parentNode = node.getParentNode();
        Node newNode = parentNode.removeChild(node);
        try {
            saveDocument();
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
        
        return newNode;
    }

    /**
     * Find a node for a given document-id
     * 
     * @param path the document-id of the Node that we're trying to get
     * 
     * @return the Node if there is a Node for the given document-id, null otherwise
     * @throws SiteException
     */
    private synchronized Node getNodeInternal(String path) throws SiteException {
        StringTokenizer st = new StringTokenizer(path, "/");
        ArrayList ids = new ArrayList();

        while (st.hasMoreTokens()) {
            ids.add(st.nextToken());
        }

        Node node = findNode(this.document.getDocumentElement(), ids);
        return node;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#getNode(java.lang.String)
     */
    public synchronized SiteNode getNode(String path) throws SiteException {
        assert path != null;

        SiteTreeNode treeNode = null;

        Node node;
        try {
            node = getNodeInternal(path);
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
        if (node != null) {
            treeNode = new SiteTreeNodeImpl(this.factory, this, (Element) node, getLogger());
            ContainerUtil.enableLogging(treeNode, getLogger());
        } else {
            throw new SiteException("No node contained for path [" + path + "]!");
        }

        return treeNode;
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#getTopNodes()
     */
    public SiteTreeNode[] getTopNodes() {
        List childElements = new ArrayList();

        NamespaceHelper helper = new NamespaceHelper(NAMESPACE_URI, "", this.document);

        Element[] elements = helper.getChildren(this.document.getDocumentElement(),
                SiteTreeNodeImpl.NODE_NAME);

        for (int i = 0; i < elements.length; i++) {
            SiteTreeNode newNode = new SiteTreeNodeImpl(this.factory,
                    this,
                    elements[i],
                    getLogger());
            childElements.add(newNode);
        }

        return (SiteTreeNode[]) childElements.toArray(new SiteTreeNode[childElements.size()]);
    }

    /**
     * Move up the node amongst its siblings.
     * @param path The document id for the node.
     * @throws SiteException if the moving failed.
     */
    public synchronized void moveUp(String path) throws SiteException {
        Node node = this.getNodeInternal(path);
        if (node == null) {
            throw new SiteException("Node to move: " + path + " not found");
        }
        Node parentNode = node.getParentNode();
        if (parentNode == null) {
            throw new SiteException("Parentid of node with path: " + path + " not found");
        }

        Node previousNode;
        try {
            previousNode = XPathAPI.selectSingleNode(node,
                    "(preceding-sibling::*[local-name() = 'node'])[last()]");
        } catch (TransformerException e) {
            throw new SiteException(e);
        }

        if (previousNode == null) {
            getLogger().warn("Couldn't found a preceding sibling");
            return;
        }
        Node insertNode = parentNode.removeChild(node);
        parentNode.insertBefore(insertNode, previousNode);
        saveDocument();
    }

    /**
     * Move down the node amongst its siblings.
     * 
     * @param path The document id for the node.
     * @throws SiteException if the moving failed.
     */
    public synchronized void moveDown(String path) throws SiteException {
        Node node = this.getNodeInternal(path);
        if (node == null) {
            throw new SiteException("Node to move: " + path + " not found");
        }
        Node parentNode = node.getParentNode();
        if (parentNode == null) {
            throw new SiteException("Parentid of node with path: " + path + " not found");
        }
        Node nextNode;
        try {
            nextNode = XPathAPI.selectSingleNode(node,
                    "following-sibling::*[local-name() = 'node'][position()=2]");
        } catch (TransformerException e) {
            throw new SiteException(e);
        }

        Node insertNode = parentNode.removeChild(node);

        if (nextNode == null) {
            getLogger().warn("Couldn't found the second following sibling");
            parentNode.appendChild(insertNode);
        } else {
            parentNode.insertBefore(insertNode, nextNode);
        }
        saveDocument();
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#importSubtree(org.apache.lenya.cms.site.tree.SiteTreeNode,
     *      org.apache.lenya.cms.site.tree.SiteTreeNode, java.lang.String, java.lang.String)
     */
    public synchronized void importSubtree(SiteTreeNode newParent, SiteTreeNode subtreeRoot,
            String newid, String refpath) throws SiteException {
        assert subtreeRoot != null;
        assert newParent != null;
        String parentId = newParent.getPath();
        String id = newid;

        String uuid = subtreeRoot.getUuid();

        SiteTreeNode targetRoot = this.addNode(parentId,
                id,
                uuid,
                subtreeRoot.visibleInNav(),
                subtreeRoot.getHref(),
                subtreeRoot.getSuffix(),
                subtreeRoot.hasLink(),
                refpath);
        copyLinks(subtreeRoot, targetRoot);

        newParent = (SiteTreeNode) this.getNode(parentId + "/" + id);
        if (newParent == null) {
            throw new SiteException("The added node was not found.");
        }
        SiteTreeNode[] children = subtreeRoot.getChildren();
        if (children == null) {
            getLogger().info("The node " + subtreeRoot.toString() + " has no children");
            return;
        }
        for (int i = 0; i < children.length; i++) {
            importSubtree(newParent, children[i], children[i].getName(), null);
        }
        saveDocument();
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#setLabel(java.lang.String,
     *      org.apache.lenya.cms.site.Label)
     */
    public synchronized void setLabel(String path, String language, String label) {
        try {
            SiteTreeNode node = (SiteTreeNode) getNode(path);
            node.getLink(language).setLabel(label);
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.SiteStructure#getRepositoryNode()
     */
    public org.apache.lenya.cms.repository.Node getRepositoryNode() {
        SourceResolver resolver = null;
        RepositorySource source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = (RepositorySource) resolver.resolveURI(this.sourceUri);
            return source.getNode();
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

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#save()
     */
    public void save() throws SiteException {
        saveDocument();
    }

    public String getArea() {
        return this.area;
    }

    public Publication getPublication() {
        return this.pub;
    }

    public boolean contains(String path) {
        try {
            return getNodeInternal(path) != null;
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean containsByUuid(String uuid, String language) {
        return getByUuidInternal(uuid, language) != null;
    }

    protected SiteNode getByUuidInternal(String uuid, String language) {
        // FIXME remove when UUID introduction is complete
        if (uuid.startsWith("/")) {
            if (contains(uuid)) {
                try {
                    return getNode(uuid);
                } catch (SiteException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        } else {
            String xPath = "//*[@uuid = '" + uuid + "']";
            SiteNode[] nodes = getNodesByXpath(xPath);
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i].hasLink(language)) {
                    return nodes[i];
                }
            }
            return null;
        }
    }

    protected SiteNode getNodeByXpath(String xPath) {
        try {
            Element element = (Element) XPathAPI.selectSingleNode(this.document, xPath);
            if (element == null) {
                return null;
            } else {
                return new SiteTreeNodeImpl(this.factory, this, element, getLogger());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected SiteNode[] getNodesByXpath(String xPath) {
        try {
            NodeList list = XPathAPI.selectNodeList(this.document, xPath);
            SiteNode[] nodes = new SiteNode[list.getLength()];
            for (int i = 0; i < nodes.length; i++) {
                Element element = (Element) list.item(i);
                nodes[i] = new SiteTreeNodeImpl(this.factory, this, element, getLogger());
            }
            return nodes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Link getByUuid(String uuid, String language) throws SiteException {
        SiteNode node = getByUuidInternal(uuid, language);
        if (node == null) {
            throw new SiteException("The link for [" + uuid + ":" + language
                    + "] is not contained!");
        }
        return node.getLink(language);
    }

    protected DocumentFactory getFactory() {
        return this.factory;
    }

    public Link add(String path, org.apache.lenya.cms.publication.Document doc)
            throws SiteException {

        if (contains(path)) {
            SiteNode node = getNode(path);
            if (node.getLanguages().length > 0 && !node.getUuid().equals(doc.getUUID())) {
                throw new SiteException("Node for path [" + path + "] exists with different UUID!");
            }
        }

        SiteTreeNode node = addNode(path, doc.getUUID(), true, null, "", false);
        node.addLabel(doc.getLanguage(), "");

        if (node.getLanguages().length == 1) {
            node.setUUID(doc.getUUID());
        }

        return node.getLink(doc.getLanguage());
    }

    public SiteNode add(String path) throws SiteException {
        SiteTreeNode node = addNode(path, null, true, null, "", false);
        return node;
    }

    public boolean containsInAnyLanguage(String uuid) {
        String xPath = "//*[@uuid = '" + uuid + "']";
        return getNodeByXpath(xPath) != null;
    }

    public SiteNode[] getNodes() {
        SiteTreeNode root;
        try {
            root = (SiteTreeNode) getNode("/");
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
        List nodes = root.preOrder();
        return (SiteNode[]) nodes.toArray(new SiteNode[nodes.size()]);
    }

}
