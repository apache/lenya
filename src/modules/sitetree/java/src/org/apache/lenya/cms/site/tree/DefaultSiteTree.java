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
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.RepositorySource;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Category;
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

    private Category log = Category.getInstance(DefaultSiteTree.class);

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
    protected ServiceManager manager;
    private Document document;

    /**
     * Create a DefaultSiteTree
     * @param publication The publication.
     * @param _area The area.
     * @param manager The service manager.
     * @throws SiteException if an error occurs.
     */
    protected DefaultSiteTree(Publication publication, String _area, ServiceManager manager)
            throws SiteException {
        this.sourceUri = publication.getSourceURI() + "/content/" + _area + "/"
                + SITE_TREE_FILENAME;
        this.area = _area;
        this.manager = manager;
        try {
            this.document = SourceUtil.readDOM(this.sourceUri, this.manager);
            if (this.document == null) {
                log.warn("No such file or directory: " + this.sourceUri);
                log.warn("Empty sitetree will be created/initialized!");
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
    public synchronized void addNode(SiteTreeNode node, String refDocumentId) throws SiteException {
        addNode(node.getParent().getAbsoluteId(),
                node.getId(),
                node.getLabels(),
                node.visibleInNav(),
                node.getHref(),
                node.getSuffix(),
                node.hasLink(),
                refDocumentId);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String, java.lang.String,
     *      org.apache.lenya.cms.site.Label[], boolean)
     */
    public synchronized void addNode(String parentid, String id, Label[] labels,
            boolean visibleInNav) throws SiteException {
        addNode(parentid, id, labels, visibleInNav, null, null, false);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(org.apache.lenya.cms.site.tree.SiteTreeNode)
     */
    public synchronized void addNode(SiteTreeNode node) throws SiteException {
        addNode(node, null);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String,
     *      org.apache.lenya.cms.site.Label[], boolean, java.lang.String, java.lang.String, boolean,
     *      java.lang.String)
     */
    public synchronized void addNode(String documentid, Label[] labels, boolean visibleInNav,
            String href, String suffix, boolean link, String refDocumentId) throws SiteException {
        StringBuffer buf = new StringBuffer();
        StringTokenizer st = new StringTokenizer(documentid, "/");
        int length = st.countTokens();

        for (int i = 0; i < (length - 1); i++) {
            buf.append("/" + st.nextToken());
        }
        String parentid = buf.toString();
        String id = st.nextToken();
        addNode(parentid, id, labels, visibleInNav, href, suffix, link, refDocumentId);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String,
     *      org.apache.lenya.cms.site.Label[], boolean, java.lang.String, java.lang.String, boolean)
     */
    public synchronized void addNode(String documentid, Label[] labels, boolean visibleInNav,
            String href, String suffix, boolean link) throws SiteException {
        addNode(documentid, labels, visibleInNav, href, suffix, link, null);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String, java.lang.String,
     *      org.apache.lenya.cms.site.Label[], boolean, java.lang.String, java.lang.String, boolean)
     */
    public synchronized void addNode(String parentid, String id, Label[] labels,
            boolean visibleInNav, String href, String suffix, boolean link) throws SiteException {
        addNode(parentid, id, labels, visibleInNav, href, suffix, link, null);
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addNode(java.lang.String, java.lang.String,
     *      org.apache.lenya.cms.site.Label[], boolean, java.lang.String, java.lang.String, boolean,
     *      java.lang.String)
     */
    public synchronized void addNode(String parentid, String id, Label[] labels,
            boolean visibleInNav, String href, String suffix, boolean link, String refDocumentId)
            throws SiteException {

        Node parentNode = getNodeInternal(parentid);

        if (parentNode == null) {
            throw new SiteException("Parentid: " + parentid + " in " + this.area
                    + " tree not found");
        }

        getLogger().debug("PARENT ELEMENT: " + parentNode);
        getLogger().debug("VISIBLEINNAV IS: " + visibleInNav);

        // Check if child already exists
        Node childNode = getNodeInternal(parentid + "/" + id);

        if (childNode != null) {
            getLogger().info("This node: " + parentid + "/" + id + " has already been inserted");

            return;
        }

        // Create node
        NamespaceHelper helper = new NamespaceHelper(NAMESPACE_URI, "", this.document);
        Element child = helper.createElement(SiteTreeNodeImpl.NODE_NAME);
        child.setAttribute(SiteTreeNodeImpl.ID_ATTRIBUTE_NAME, id);

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

        for (int i = 0; i < labels.length; i++) {
            String labelName = labels[i].getLabel();
            Element label = helper.createElement(SiteTreeNodeImpl.LABEL_NAME, labelName);
            String labelLanguage = labels[i].getLanguage();

            if ((labelLanguage != null) && (labelLanguage.length() > 0)) {
                label.setAttribute(SiteTreeNodeImpl.LANGUAGE_ATTRIBUTE_NAME, labelLanguage);
            }

            child.appendChild(label);
        }

        // Add Node
        if (refDocumentId != null && !refDocumentId.equals("")) {
            Node nextSibling = getNodeInternal(refDocumentId);
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
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#addLabel(java.lang.String,
     *      org.apache.lenya.cms.site.Label)
     */
    public synchronized void addLabel(String documentId, Label label) {
        SiteTreeNode node = getNode(documentId);
        if (node != null) {
            node.addLabel(label);
        }
        try {
            saveDocument();
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#removeLabel(java.lang.String,
     *      org.apache.lenya.cms.site.Label)
     */
    public synchronized void removeLabel(String documentId, Label label) {
        SiteTreeNode node = getNode(documentId);
        if (node != null) {
            node.removeLabel(label);
        }
        try {
            saveDocument();
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#removeNode(java.lang.String)
     */
    public synchronized SiteTreeNode removeNode(String documentId) {
        assert documentId != null;

        Node node;
        try {
            node = removeNodeInternal(documentId);
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
        if (node == null) {
            return null;
        }

        SiteTreeNode newNode = new SiteTreeNodeImpl(node);
        ContainerUtil.enableLogging(newNode, getLogger());
        return newNode;
    }

    /**
     * removes the node corresponding to the given document-id and returns it
     * @param documentId the document-id of the Node to be removed
     * @return the <code>Node</code> that was removed
     * @throws SiteException
     */
    private synchronized Node removeNodeInternal(String documentId) throws SiteException {
        Node node = this.getNodeInternal(documentId);
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
     * @param documentId the document-id of the Node that we're trying to get
     * 
     * @return the Node if there is a Node for the given document-id, null otherwise
     * @throws SiteException
     */
    private synchronized Node getNodeInternal(String documentId) throws SiteException {
        StringTokenizer st = new StringTokenizer(documentId, "/");
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
    public synchronized SiteTreeNode getNode(String documentId) {
        assert documentId != null;

        SiteTreeNode treeNode = null;

        Node node;
        try {
            node = getNodeInternal(documentId);
        } catch (SiteException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        if (node != null) {
            treeNode = new SiteTreeNodeImpl(node);
            ContainerUtil.enableLogging(treeNode, getLogger());
        } else {
            log.warn("No such node: " + documentId);
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
            SiteTreeNode newNode = new SiteTreeNodeImpl(elements[i]);
            childElements.add(newNode);
        }

        return (SiteTreeNode[]) childElements.toArray(new SiteTreeNode[childElements.size()]);
    }

    /**
     * Move up the node amongst its siblings.
     * @param documentid The document id for the node.
     * @throws SiteException if the moving failed.
     */
    public synchronized void moveUp(String documentid) throws SiteException {
        Node node = this.getNodeInternal(documentid);
        if (node == null) {
            throw new SiteException("Node to move: " + documentid + " not found");
        }
        Node parentNode = node.getParentNode();
        if (parentNode == null) {
            throw new SiteException("Parentid of node with documentid: " + documentid
                    + " not found");
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
     * @param documentid The document id for the node.
     * @throws SiteException if the moving failed.
     */
    public synchronized void moveDown(String documentid) throws SiteException {
        Node node = this.getNodeInternal(documentid);
        if (node == null) {
            throw new SiteException("Node to move: " + documentid + " not found");
        }
        Node parentNode = node.getParentNode();
        if (parentNode == null) {
            throw new SiteException("Parentid of node with documentid: " + documentid
                    + " not found");
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
            String newid, String refDocumentId) throws SiteException {
        assert subtreeRoot != null;
        assert newParent != null;
        String parentId = newParent.getAbsoluteId();
        String id = newid;

        this.addNode(parentId,
                id,
                subtreeRoot.getLabels(),
                subtreeRoot.visibleInNav(),
                subtreeRoot.getHref(),
                subtreeRoot.getSuffix(),
                subtreeRoot.hasLink(),
                refDocumentId);
        newParent = this.getNode(parentId + "/" + id);
        if (newParent == null) {
            throw new SiteException("The added node was not found.");
        }
        SiteTreeNode[] children = subtreeRoot.getChildren();
        if (children == null) {
            getLogger().info("The node " + subtreeRoot.toString() + " has no children");
            return;
        }
        for (int i = 0; i < children.length; i++) {
            importSubtree(newParent, children[i], children[i].getId(), null);
        }
        saveDocument();
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#setLabel(java.lang.String,
     *      org.apache.lenya.cms.site.Label)
     */
    public synchronized void setLabel(String documentId, Label label) {
        SiteTreeNode node = getNode(documentId);
        if (node != null) {
            node.setLabel(label);
        }
        try {
            saveDocument();
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.tree.SiteTree#setVisibleInNav(java.lang.String, boolean)
     */
    public synchronized void setVisibleInNav(String documentId, boolean visibleInNav)
            throws SiteException {
        SiteTreeNode node = getNode(documentId);
        if (visibleInNav) {
            node.setNodeAttribute(SiteTreeNodeImpl.VISIBLEINNAV_ATTRIBUTE_NAME, "true");
        } else {
            node.setNodeAttribute(SiteTreeNodeImpl.VISIBLEINNAV_ATTRIBUTE_NAME, "false");
        }

        try {
            saveDocument();
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.transaction.Identifiable#getIdentifiableType()
     */
    public String getIdentifiableType() {
        return SiteTree.IDENTIFIABLE_TYPE;
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

    public boolean isVisibleInNav(String documentId) throws SiteException {
        SiteTreeNode node = getNode(documentId);
        String value = node.getNodeAttribute(SiteTreeNodeImpl.VISIBLEINNAV_ATTRIBUTE_NAME);
        if (value != null && !value.equals("")) {
            return Boolean.valueOf(value).booleanValue();
        } else {
            return true;
        }
    }

}
