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

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Category;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * DOCUMENT ME!
 *
 * @author Christian Egli
 * @version $Id: DefaultSiteTree.java,v 1.40 2004/03/02 15:33:33 michi Exp $
 */
public class DefaultSiteTree implements SiteTree {
    private static Category log = Category.getInstance(DefaultSiteTree.class);

    public static final String NAMESPACE_URI =
        "http://apache.org/cocoon/lenya/sitetree/1.0";
    public static final String SITE_TREE_FILENAME = "sitetree.xml";

    private Document document = null;
    private File treefile = null;
    // the area is only retained to provide some more info when raising an exception.
    private String area = "";

    /**
     * Create a DefaultSiteTree
     * 
     * @param pubDir the publication directory
     * @param area the area
     * 
     * @throws SiteTreeException if an error occurs
     */
    protected DefaultSiteTree(File pubDir, String area)
        throws SiteTreeException {
        this(
            new File(
                pubDir,
                Publication.CONTENT_PATH
                    + File.separator
                    + area
                    + File.separator
                    + SITE_TREE_FILENAME));
        this.area = area;
    }

    /**
     * Create a DefaultSiteTree from a filename.
     *
     * @param treefilename file name of the tree
     *
     * @throws SiteTreeException if an error occurs
     *  
     * @deprecated use the DefaultSiteTree(File pubDir, String area) constructor instead.
     */
    public DefaultSiteTree(String treefilename)
        throws SiteTreeException {
        this(new File(treefilename));
    }

    /**
     * Create a DefaultSiteTree from a file.
     *
     * @param treefile the file containing the tree
     * 
     * @throws SiteTreeException if an error occurs
     * 
     * @deprecated this constructor will be private in the future
     */
    public DefaultSiteTree(File treefile) throws SiteTreeException {
        this.treefile = treefile;

        try {
            if (!treefile.isFile()) {
                //the treefile doesn't exist, so create it

                document = createDocument();
            } else {
                // Read tree
                document = DocumentHelper.readDocument(treefile);
            }
        } catch (ParserConfigurationException e) {
			throw new SiteTreeException(e);
        } catch (SAXException e) {
			throw new SiteTreeException(e);
        } catch (IOException e) {
			throw new SiteTreeException(e);
        }

    }

    /**
     * Create a new DefaultSiteTree xml document.
     *
     * @return the new site document
     * 
     * @throws ParserConfigurationException if an error occurs
     */
    public Document createDocument() throws ParserConfigurationException {
        document = DocumentHelper.createDocument(NAMESPACE_URI, "site", null);

        Element root = document.getDocumentElement();
        root.setAttribute(
            "xmlns:xsi",
            "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute(
            "xsi:schemaLocation",
            "http://apache.org/cocoon/lenya/sitetree/1.0  ../../../../resources/entities/sitetree.xsd");

        return document;
    }

    /**
     * Find a node in a subtree. The search is started at the
     * given node. The list of ids contains the document-id
     * split by "/".
     *
     * @param node where to start the search
     * @param ids list of node ids
     * 
     * @return the node that matches the path given in the list of ids
     */
    protected Node findNode(Node node, List ids) {
        if (ids.size() < 1) {
            return node;
        } else {
            NodeList nodes = node.getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {
                NamedNodeMap attributes = nodes.item(i).getAttributes();

                if (attributes != null) {
                    Node idAttribute = attributes.getNamedItem("id");

                    if (idAttribute != null
                        && !"".equals(idAttribute)
                        && idAttribute.getNodeValue().equals(ids.get(0))) {
                        return findNode(
                            nodes.item(i),
                            ids.subList(1, ids.size()));
                    }
                }
            }
        }

        // node wasn't found
        return null;
    }

	/** (non-Javadoc)
	 * @see org.apache.lenya.cms.publication.SiteTree#addNode(org.apache.lenya.cms.publication.SiteTreeNode, java.lang.String)
	 */
	public void addNode(SiteTreeNode node, String refDocumentId)  throws SiteTreeException {
		this.addNode(
			node.getAbsoluteParentId(),
			node.getId(),
			node.getLabels(),
			node.getHref(),
			node.getSuffix(),
			node.hasLink(),
		    refDocumentId);
	}
    /** (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTree#addNode(java.lang.String, java.lang.String, org.apache.lenya.cms.publication.Label[])
     */
    public void addNode(String parentid, String id, Label[] labels)
        throws SiteTreeException {
        addNode(parentid, id, labels, null, null, false);
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTree#addNode(org.apache.lenya.cms.publication.SiteTreeNode)
     */
    public void addNode(SiteTreeNode node) throws SiteTreeException {
        this.addNode(node, null);
    }

	/** (non-Javadoc)
	 * @see org.apache.lenya.cms.publication.SiteTree#addNode(java.lang.String, org.apache.lenya.cms.publication.Label[], java.lang.String, java.lang.String, boolean, java.lang.String)
	 */
	public void addNode(
        String documentid,
        Label[] labels,
        String href,
        String suffix,
        boolean link,
 	    String refDocumentId)
        throws SiteTreeException {
        String parentid = "";
        StringTokenizer st = new StringTokenizer(documentid, "/");
        int length = st.countTokens();

        for (int i = 0; i < (length - 1); i++) {
            parentid = parentid + "/" + st.nextToken();
        }

        String id = st.nextToken();
        this.addNode(parentid, id, labels, href, suffix, link, refDocumentId);
    }

	/** (non-Javadoc)
	 * @see org.apache.lenya.cms.publication.SiteTree#addNode(java.lang.String, org.apache.lenya.cms.publication.Label[], java.lang.String, java.lang.String, boolean)
	 */
	public void addNode(
		String documentid,
		Label[] labels,
		String href,
		String suffix,
		boolean link)
		throws SiteTreeException {
		this.addNode(documentid, labels, href, suffix, link, null);
	}
	/** (non-Javadoc)
	 * @see org.apache.lenya.cms.publication.SiteTree#addNode(java.lang.String, java.lang.String, org.apache.lenya.cms.publication.Label[], java.lang.String, java.lang.String, boolean)
	 */
	public void addNode(
		String parentid,
		String id,
		Label[] labels,
		String href,
		String suffix,
		boolean link)
		throws SiteTreeException {
			this.addNode(parentid, id, labels, href, suffix, link, null);
	}

	/** (non-Javadoc)
	 * @see org.apache.lenya.cms.publication.SiteTree#addNode(java.lang.String, java.lang.String, org.apache.lenya.cms.publication.Label[], java.lang.String, java.lang.String, boolean)
	 */
	public void addNode(
		String parentid,
		String id,
		Label[] labels,
		String href,
		String suffix,
		boolean link,
		String refDocumentId)
		throws SiteTreeException {

			Node parentNode = getNodeInternal(parentid);

			if (parentNode == null) {
				throw new SiteTreeException("Parentid: " + parentid + " in " + area + " tree not found");
			}

			log.debug("PARENT ELEMENT: " + parentNode);

			// Check if child already exists
			Node childNode = getNodeInternal(parentid + "/" + id);

			if (childNode != null) {
				log.info(
					"This node: "
						+ parentid
						+ "/"
						+ id
						+ " has already been inserted");

				return;
			}

			// Create node
			NamespaceHelper helper =
				new NamespaceHelper(NAMESPACE_URI, "", document);
			Element child = helper.createElement(SiteTreeNodeImpl.NODE_NAME);
			child.setAttribute(SiteTreeNodeImpl.ID_ATTRIBUTE_NAME, id);

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
				Element label =
					helper.createElement(SiteTreeNodeImpl.LABEL_NAME, labelName);
				String labelLanguage = labels[i].getLanguage();

				if ((labelLanguage != null) && (labelLanguage.length() > 0)) {
					label.setAttribute(
						SiteTreeNodeImpl.LANGUAGE_ATTRIBUTE_NAME,
						labelLanguage);
				}

				child.appendChild(label);
			}

            // Add Node 
            if (refDocumentId != null && !refDocumentId.equals("")){
	 			Node nextSibling = getNodeInternal(refDocumentId);
				if (nextSibling != null) {
					parentNode.insertBefore(child, nextSibling);
				} else {
					parentNode.appendChild(child);
				}	
            } else {
				parentNode.appendChild(child);
            }
			log.debug("Tree has been modified: " + document.getDocumentElement());

		}
    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTree#addLabel(java.lang.String, org.apache.lenya.cms.publication.Label)
     */
    public void addLabel(String documentId, Label label) {
        SiteTreeNode node = getNode(documentId);
        if (node != null) {
            node.addLabel(label);
        }
    }

    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTree#removeLabel(java.lang.String, org.apache.lenya.cms.publication.Label)
     */
    public void removeLabel(String documentId, Label label) {
        SiteTreeNode node = getNode(documentId);
        if (node != null) {
            node.removeLabel(label);
        }
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTree#removeNode(java.lang.String)
     */
    public SiteTreeNode removeNode(String documentId) {
        assert documentId != null;

        Node node = removeNodeInternal(documentId);

        if (node == null) {
            return null;
        }

        return new SiteTreeNodeImpl(node);
    }

    /**
     * removes the node corresponding to the given document-id
     * and returns it
     * 
     * @param documentId the document-id of the Node to be removed
     * 
     * @return the <code>Node</code> that was removed
     */
    private Node removeNodeInternal(String documentId) {
        Node node = this.getNodeInternal(documentId);
        Node parentNode = node.getParentNode();
        Node newNode = parentNode.removeChild(node);

        return newNode;
    }

    /**
     * Find a node for a given document-id
     *
     * @param documentId the document-id of the Node that we're trying to get
     * 
     * @return the Node if there is a Node for the given document-id, null otherwise
     */
    private Node getNodeInternal(String documentId) {
        StringTokenizer st = new StringTokenizer(documentId, "/");
        ArrayList ids = new ArrayList();

        while (st.hasMoreTokens()) {
            ids.add(st.nextToken());
        }

        Node node = findNode(document.getDocumentElement(), ids);
        return node;
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTree#getNode(java.lang.String)
     */
    public SiteTreeNode getNode(String documentId) {
        assert documentId != null;
        
        SiteTreeNode treeNode = null;

        Node node = getNodeInternal(documentId);
        if (node != null) {
            treeNode = new SiteTreeNodeImpl(node);
        }

        return treeNode;
    }

    /**
     * Move up the node amongst its siblings.
     * 
     * @param documentid The document id for the node.
     * @throws SiteTreeException if the moving failed.
     */
    public void moveUp(String documentid) throws SiteTreeException {
        Node node = this.getNodeInternal(documentid);
        if (node == null) {
            throw new SiteTreeException(
                "Node to move: " + documentid + " not found");
        }
        Node parentNode = node.getParentNode();
        if (parentNode == null) {
            throw new SiteTreeException(
                "Parentid of node with documentid: "
                    + documentid
                    + " not found");
        }

        Node previousNode;
        try {
            previousNode =
                XPathAPI.selectSingleNode(
                    node,
                    "(preceding-sibling::*[local-name() = 'node'])[last()]");
        } catch (TransformerException e) {
            throw new SiteTreeException(e);
        }

        if (previousNode == null) {
            log.warn("Couldn't found a preceding sibling");
            return;
        }
        Node insertNode = parentNode.removeChild(node);
        parentNode.insertBefore(insertNode, previousNode);
    }

    /**
     * Move down the node amongst its siblings.
     * 
     * @param documentid The document id for the node.
     * @throws SiteTreeException if the moving failed.
     */
    public void moveDown(String documentid) throws SiteTreeException {
        Node node = this.getNodeInternal(documentid);
        if (node == null) {
            throw new SiteTreeException(
                "Node to move: " + documentid + " not found");
        }
        Node parentNode = node.getParentNode();
        if (parentNode == null) {
            throw new SiteTreeException(
                "Parentid of node with documentid: "
                    + documentid
                    + " not found");
        }
        Node nextNode;
        try {
            nextNode =
                XPathAPI.selectSingleNode(
                    node,
                    "following-sibling::*[local-name() = 'node'][position()=2]");
        } catch (TransformerException e) {
            throw new SiteTreeException(e);
        }

        Node insertNode = parentNode.removeChild(node);

        if (nextNode == null) {
            log.warn("Couldn't found the second following sibling");
            parentNode.appendChild(insertNode);
        } else {
            parentNode.insertBefore(insertNode, nextNode);
        }
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTree#importSubtree(org.apache.lenya.cms.publication.SiteTreeNode, org.apache.lenya.cms.publication.SiteTreeNode, java.lang.String)
     */
    public void importSubtree(
        SiteTreeNode newParent,
        SiteTreeNode subtreeRoot,
        String newid,
	    String refDocumentId)
        throws SiteTreeException {
        assert subtreeRoot != null;
        assert newParent != null;
        String parentId = newParent.getAbsoluteId();
        String id = newid;

        this.addNode(
            parentId,
            id,
            subtreeRoot.getLabels(),
            subtreeRoot.getHref(),
            subtreeRoot.getSuffix(),
            subtreeRoot.hasLink(),
			refDocumentId);
        newParent = this.getNode(parentId + "/" + id);
        if (newParent == null) {
            throw new SiteTreeException(
                "The added node  " + newParent.toString() + " not found");
        }
        SiteTreeNode[] children = subtreeRoot.getChildren();
        if (children == null) {
            log.info("The node " + subtreeRoot.toString() + " has no children");
            return;
        } else {
            for (int i = 0; i < children.length; i++) {
                importSubtree(newParent, children[i], children[i].getId(), null);
            }
        }
    }

    /** (non-Javadoc)
	 * @see org.apache.lenya.cms.publication.SiteTree#save()
	 */
	public void save() throws SiteTreeException {
        try {
			DocumentHelper.writeDocument(document, treefile);
		} catch (TransformerException e) {
			throw new SiteTreeException(
				"The document [" + document.getLocalName() + "] could not be transformed");
		} catch (IOException e) {
			throw new SiteTreeException(
				"The saving of document [" + document.getLocalName() + "] failed");
		}
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTree#setLabel(java.lang.String, org.apache.lenya.cms.publication.Label)
     */
    public void setLabel(String documentId, Label label) {
        SiteTreeNode node = getNode(documentId);
        if (node != null) {
            node.setLabel(label);
        }
    }


}
