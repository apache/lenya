/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.publication;

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

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.22 $
 */
public class DefaultSiteTree implements SiteTree {
    private static Category log = Category.getInstance(DefaultSiteTree.class);
    public static final String NAMESPACE_URI = "http://www.lenya.org/2003/sitetree";
    private Document document = null;
    private File treefile = null;

    /**
     * Create a DefaultSiteTree from a filename.
     *
     * @param treefilename file name of the tree
     * 
     * @throws ParserConfigurationException if an error occurs
     * @throws SAXException if the xml in the treefile is not valid
     * @throws IOException if the file cannot be opened
     */
    public DefaultSiteTree(String treefilename)
        throws ParserConfigurationException, SAXException, IOException {
        this(new File(treefilename));
    }

    /**
     * Create a DefaultSiteTree from a file.
     *
     * @param treefile the file containing the tree
     * 
     * @throws ParserConfigurationException if an error occurs
     * @throws SAXException  if the xml in the treefile is not valid
     * @throws IOException  if the file cannot be opened
     */
    public DefaultSiteTree(File treefile)
        throws ParserConfigurationException, SAXException, IOException {
        this.treefile = treefile;

        if (!treefile.isFile()) {
            //the treefile doesn't exist, so create it
            document = createDocument();
        } else {
            // Read tree
            document = DocumentHelper.readDocument(treefile);
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
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:schemaLocation",
            "http://www.lenya.org/2003/sitetree  ../../../../resources/entities/sitetree.xsd");

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

                    if ((idAttribute != null) && idAttribute.getNodeValue().equals(ids.get(0))) {
                        return findNode(nodes.item(i), ids.subList(1, ids.size()));
                    }
                }
            }
        }

        // node wasn't found
        return null;
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
        this.addNode(node.getAbsoluteParentId(), node.getId(), node.getLabels(), node.getHref(),
            node.getSuffix(), node.hasLink());
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTree#addNode(java.lang.String, org.apache.lenya.cms.publication.Label[], java.lang.String, java.lang.String, boolean)
     */
    public void addNode(String documentid, Label[] labels, String href, String suffix, boolean link)
        throws SiteTreeException {
        String parentid = "";
        StringTokenizer st = new StringTokenizer(documentid, "/");
        int length = st.countTokens();

        for (int i = 0; i < (length - 1); i++) {
            parentid = parentid + "/" + st.nextToken();
        }

        String id = st.nextToken();
        this.addNode(parentid, id, labels, href, suffix, link);
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTree#addNode(java.lang.String, java.lang.String, org.apache.lenya.cms.publication.Label[], java.lang.String, java.lang.String, boolean)
     */
    public void addNode(String parentid, String id, Label[] labels, String href, String suffix,
        boolean link) throws SiteTreeException {
        Node parentNode = getNodeInternal(parentid);

        if (parentNode == null) {
            throw new SiteTreeException("Parentid: " + parentid + " not found");
        }

        log.debug("PARENT ELEMENT: " + parentNode);

        // Check if child already exists
        Node childNode = getNodeInternal(parentid + "/" + id);

        if (childNode != null) {
            log.info("This node: " + parentid + "/" + id + " has already been inserted");

            return;
        }

        // Add node
        NamespaceHelper helper = new NamespaceHelper(NAMESPACE_URI, "", document);
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
            Element label = helper.createElement(SiteTreeNodeImpl.LABEL_NAME, labelName);
            String labelLanguage = labels[i].getLanguage();

            if ((labelLanguage != null) && (labelLanguage.length() > 0)) {
                label.setAttribute(SiteTreeNodeImpl.LANGUAGE_ATTRIBUTE_NAME, labelLanguage);
            }

            child.appendChild(label);
        }

        parentNode.appendChild(child);
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

        if (node == null) {
            return null;
        }

        return node;
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTree#getNode(java.lang.String)
     */
    public SiteTreeNode getNode(String documentId) {
        assert documentId != null;

        Node node = getNodeInternal(documentId);

        if (node == null) {
            return null;
        }

        return new SiteTreeNodeImpl(node);
    }

	/**
	 * Move up the node amongst its siblings.
	 * 
	 * @param documentid The document id for the node.
	 * @throws SiteTreeException if the moving failed.
	 */
	public void moveUp(String documentid)
		throws SiteTreeException {
			Node node = this.getNodeInternal(documentid);
			if (node == null) {
			  throw new SiteTreeException("Node to move: " + documentid + " not found");
			}
			Node parentNode = node.getParentNode();
			if (parentNode == null) {
			  throw new SiteTreeException("Parentid of node with documentid: " + documentid + " not found");
			}
			
			Node previousNode;
			try {
				previousNode = XPathAPI.selectSingleNode(node, "(preceding-sibling::*[local-name() = 'node'])[last()]");
			} catch (TransformerException e) {
				throw new SiteTreeException(e);
			}
			
			if (previousNode == null){
			  log.warn("Couldn't found a preceding sibling");
              return;
			}
			Node insertNode=parentNode.removeChild(node);
			parentNode.insertBefore(insertNode,previousNode);
	}

	/**
	 * Move down the node amongst its siblings.
	 * 
	 * @param documentid The document id for the node.
	 * @throws SiteTreeException if the moving failed.
	 */
	public void moveDown(String documentid)
		throws SiteTreeException {
			Node node = this.getNodeInternal(documentid);
			if (node == null) {
			  throw new SiteTreeException("Node to move: " + documentid + " not found");
			}
			Node parentNode = node.getParentNode();
			if (parentNode == null) {
			  throw new SiteTreeException("Parentid of node with documentid: " + documentid + " not found");
			}
			Node nextNode;
			try {
				nextNode = XPathAPI.selectSingleNode(node, "following-sibling::*[local-name() = 'node'][position()=2]");
			} catch (TransformerException e) {
				throw new SiteTreeException(e);
			}
			
			Node insertNode=parentNode.removeChild(node);
	
			if (nextNode == null){
				log.warn("Couldn't found the second following sibling");
                parentNode.appendChild(insertNode);
			} else {
 				parentNode.insertBefore(insertNode,nextNode);
			}
	}

    /**
     * Save the DefaultSiteTree.
     *
     * @throws IOException if the save failed
     * @throws TransformerException if the document could not be transformed
     */
    public void save() throws IOException, TransformerException {
       DocumentHelper.writeDocument(document, treefile);
    }
}
