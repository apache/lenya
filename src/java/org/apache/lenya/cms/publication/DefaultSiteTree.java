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
 * @version $Revision: 1.18 $
 */
public class DefaultSiteTree implements SiteTree {
    private static Category log = Category.getInstance(DefaultSiteTree.class);
    public static final String NAMESPACE_URI = "http://www.lenya.org/2003/sitetree";
    private Document document = null;
    private File treefile = null;

    /**
     * Create a DefaultSiteTree from a filename.
     *
     * @param treefilename
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public DefaultSiteTree(String treefilename)
        throws ParserConfigurationException, SAXException, IOException {
        this(new File(treefilename));
    }

    /**
     * Create a DefaultSiteTree from a file.
     *
     * @param treefile
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
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
     * @return
     * @throws DOMException
     * @throws ParserConfigurationException
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
     * @return
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

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTree#addNode(java.lang.String, java.lang.String, org.apache.lenya.cms.publication.Label[])
     */
    public void addNode(String parentid, String id, Label[] labels)
        throws SiteTreeException {
        addNode(parentid, id, labels, null, null, false);
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTree#addNode(org.apache.lenya.cms.publication.SiteTreeNode)
     */
    public void addNode(SiteTreeNode node) throws SiteTreeException {
        this.addNode(node.getAbsoluteParentId(), node.getId(), node.getLabels(), node.getHref(),
            node.getSuffix(), node.hasLink());
    }

    /* (non-Javadoc)
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

    /* (non-Javadoc)
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

    /* (non-Javadoc)
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
     * @param documentId
     * @return a org.w3c.dom.Node
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
     * @param documentId
     * @return
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

    /* (non-Javadoc)
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
     * Save the DefaultSiteTree.
     *
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void save() throws IOException, TransformerException {
        DocumentHelper.writeDocument(document, treefile);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        try {
            DefaultSiteTree sitetree = new DefaultSiteTree(args[0]);
            Label label = new Label("Foo", null);
            Label[] labels = { label };

            sitetree.addNode("/tutorial", "foo", labels);

            Label label_de = new Label("Qualit?t", "de");
            Label label_en = new Label("Quality", "en");
            Label[] labels2 = { label_de, label_en };
            sitetree.addNode("/tutorial/features", "here", labels2);

            sitetree.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
