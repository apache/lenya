/*
$Id: SiteTreeNodeImpl.java,v 1.21 2003/09/23 13:46:02 edith Exp $
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

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of the <code>SiteTreeNode</code> interface.
 * 
 * @see org.apache.lenya.cms.publication.SiteTreeNode
 *
 * @author $Author: edith $
 * @version $Revision: 1.21 $
 */
public class SiteTreeNodeImpl implements SiteTreeNode {
    private static Category log = Category.getInstance(SiteTreeNodeImpl.class);
    public static final String ID_ATTRIBUTE_NAME = "id";
    public static final String HREF_ATTRIBUTE_NAME = "href";
    public static final String SUFFIX_ATTRIBUTE_NAME = "suffix";
    public static final String LINK_ATTRIBUTE_NAME = "link";
    public static final String LANGUAGE_ATTRIBUTE_NAME = "xml:lang";
    public static final String NODE_NAME = "node";
    public static final String LABEL_NAME = "label";

    private Node node = null;

    /**
     * Creates a new SiteTreeNodeImpl object.
     *
     * @param node the node which is to be wrapped by this SiteTreeNode 
     */
    public SiteTreeNodeImpl(Node node) {
        this.node = node;
    }

    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getParentId()
     */
    public String getParentId() {
        Node parent = node.getParentNode();

        if (parent == null) {
            return "/";
        }

        NamedNodeMap attributes = parent.getAttributes();

        if (attributes == null) {
            return "/";
        }

        Node idAttribute = attributes.getNamedItem(ID_ATTRIBUTE_NAME);

        if (idAttribute == null) {
            return "/";
        }

        return idAttribute.getNodeValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAbsoluteParentId() {
        String absoluteId = "";
        Node parent = node.getParentNode();
        NamedNodeMap attributes = null;
        Node idAttribute = null;

        while (parent != null) {
            attributes = parent.getAttributes();

            if (attributes == null) {
                break;
            }

            idAttribute = attributes.getNamedItem(ID_ATTRIBUTE_NAME);

            if (idAttribute == null) {
                break;
            }

            absoluteId = "/" + idAttribute.getNodeValue() + absoluteId;
            parent = parent.getParentNode();
        }

        return absoluteId;
    }

    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getId()
     */
    public String getId() {
        if (node == node.getOwnerDocument().getDocumentElement()) {
            return "";
        }
        return node.getAttributes().getNamedItem(ID_ATTRIBUTE_NAME).getNodeValue();
    }

    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getLabels()
     */
    public Label[] getLabels() {
        ArrayList labels = new ArrayList();

        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if ((child.getNodeType() == Node.ELEMENT_NODE)
                && child.getNodeName().equals(LABEL_NAME)) {
                String labelName = DocumentHelper.getSimpleElementText((Element) child);
                String labelLanguage = null;
                Node languageAttribute =
                    child.getAttributes().getNamedItem(LANGUAGE_ATTRIBUTE_NAME);

                if (languageAttribute != null) {
                    labelLanguage = languageAttribute.getNodeValue();
                }

                labels.add(new Label(labelName, labelLanguage));
            }
        }

        return (Label[]) labels.toArray(new Label[labels.size()]);
    }

    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getLabel(java.lang.String)
     */
    public Label getLabel(String xmlLanguage) {
        Label label = null;
        Label[] labels = getLabels();
        String language = null;

        for (int i = 0; i < labels.length; i++) {
            language = labels[i].getLanguage();

            if ((((xmlLanguage == null) || (xmlLanguage.equals(""))) && (language == null))
                || ((language != null) && (language.equals(xmlLanguage)))) {
                label = labels[i];

                break;
            }
        }

        return label;
    }

    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#addLabel(org.apache.lenya.cms.publication.Label)
     */
    public void addLabel(Label label) {
        if (getLabel(label.getLanguage()) == null) {
            // only add the label if there is no label with the same language yet.

            NamespaceHelper helper = getNamespaceHelper();
            Element labelElem = helper.createElement(SiteTreeNodeImpl.LABEL_NAME, label.getLabel());

            labelElem.setAttribute(SiteTreeNodeImpl.LANGUAGE_ATTRIBUTE_NAME, label.getLanguage());

            node.appendChild(labelElem);
        }
    }

    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#removeLabel(org.apache.lenya.cms.publication.Label)
     */
    public void removeLabel(Label label) {
        if (getLabel(label.getLanguage()) != null) {
            // this node doesn't contain this label

            NodeList children = node.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);

                if ((child.getNodeType() == Node.ELEMENT_NODE)
                    && child.getNodeName().equals(LABEL_NAME)
                    && child.getFirstChild().getNodeValue().equals(label.getLabel())) {

                    Node languageAttribute =
                        child.getAttributes().getNamedItem(LANGUAGE_ATTRIBUTE_NAME);

                    if (languageAttribute != null
                        && languageAttribute.getNodeValue().equals(label.getLanguage())) {
                        node.removeChild(child);
                        break;
                    }
                }
            }
        }
    }

    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getHref()
     */
    public String getHref() {
        Node attribute = node.getAttributes().getNamedItem(HREF_ATTRIBUTE_NAME);

        if (attribute != null) {
            return attribute.getNodeValue();
        } else {
            return null;
        }
    }

    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getSuffix()
     */
    public String getSuffix() {
        Node attribute = node.getAttributes().getNamedItem(SUFFIX_ATTRIBUTE_NAME);

        if (attribute != null) {
            return attribute.getNodeValue();
        } else {
            return null;
        }
    }

    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#hasLink()
     */
    public boolean hasLink() {
        Node attribute = node.getAttributes().getNamedItem(LINK_ATTRIBUTE_NAME);

        if (attribute != null) {
            return attribute.getNodeValue().equals("true");
        } else {
            return false;
        }
    }
    /**
     * (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getChildren()
     */
    public SiteTreeNode[] getChildren() {
        List childElements = new ArrayList();

        NamespaceHelper helper = getNamespaceHelper();
        Element[] elements = helper.getChildren((Element) node, SiteTreeNodeImpl.NODE_NAME);

        for (int i = 0; i < elements.length; i++) {
            SiteTreeNode newNode = new SiteTreeNodeImpl(elements[i]);
            childElements.add(newNode);
        }

        return (SiteTreeNode[]) childElements.toArray(new SiteTreeNode[childElements.size()]);
    }

    /**
     *  (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#removeChildren()
     */
    public SiteTreeNode[] removeChildren() {
        List childElements = new ArrayList();
        NamespaceHelper helper = getNamespaceHelper();
        Element[] elements = helper.getChildren((Element) node, SiteTreeNodeImpl.NODE_NAME);
        for (int i = 0; i < elements.length; i++) {
            node.removeChild(elements[i]);
            SiteTreeNode newNode = new SiteTreeNodeImpl(elements[i]);
            childElements.add(newNode);
        }
        return (SiteTreeNode[]) childElements.toArray(new SiteTreeNode[childElements.size()]);
    }

    /**
     * (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getChildren()
     */
    public SiteTreeNode[] getNextSiblings() {
        List siblingElements = new ArrayList();

        NamespaceHelper helper = getNamespaceHelper();
        Element[] elements = helper.getNextSiblings((Element) node, SiteTreeNodeImpl.NODE_NAME);

        for (int i = 0; i < elements.length; i++) {
            SiteTreeNode newNode = new SiteTreeNodeImpl(elements[i]);
            siblingElements.add(newNode);
        }

        return (SiteTreeNode[]) siblingElements.toArray(new SiteTreeNode[siblingElements.size()]);
    }

	/** (non-Javadoc)
	 * @see org.apache.lenya.cms.publication.SiteTreeNode#getNextSiblingDocumentId()
	 */
	public String getNextSiblingDocumentId() {
		SiteTreeNode[] siblings = getNextSiblings();
        if (siblings != null && siblings.length >0 ){
			return siblings[0].getAbsoluteParentId()+ "/" + siblings[0].getId();       	 
        } else {
        	return null;
        }
	}

    /**
     * (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#accept(org.apache.lenya.cms.publication.SiteTreeNodeVisitor)
     */
    public void accept(SiteTreeNodeVisitor visitor) throws DocumentException {
        visitor.visitSiteTreeNode(this);
    }

    /**
     * (non-Javadoc)
     * @see org.apache.lenya.cms.publication.SiteTreeNode#acceptSubtree(org.apache.lenya.cms.publication.SiteTreeNodeVisitor)
     */
    public void acceptSubtree(SiteTreeNodeVisitor visitor) throws DocumentException {
        this.accept(visitor);
        SiteTreeNode[] children = this.getChildren();
        if (children == null) {
            log.info("The node " + this.getId() + " has no children");
            return;
        } else {
            for (int i = 0; i < children.length; i++) {
                children[i].acceptSubtree(visitor);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#setLabel(org.apache.lenya.cms.publication.Label)
     */
    public void setLabel(Label label) {
        Label existingLabel = getLabel(label.getLanguage());
        if (existingLabel != null) {
            removeLabel(existingLabel);
        }
        addLabel(label);
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getChildren(java.lang.String)
     */
    public SiteTreeNode[] getChildren(String language) {
        SiteTreeNode[] children = getChildren();
        List languageChildren = new ArrayList();

        for (int i = 0; i < children.length; i++) {
            if (children[i].getLabel(language) != null) {
                languageChildren.add(children[i]);
            }
        }

        return (SiteTreeNode[]) languageChildren.toArray(new SiteTreeNode[languageChildren.size()]);
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getParent()
     */
    public SiteTreeNode getParent() {
        SiteTreeNode parent = null;

        Node parentNode = node.getParentNode();
        if (parentNode.getNodeType() == Node.ELEMENT_NODE
            && parentNode.getLocalName().equals(NODE_NAME)) {
            parent = new SiteTreeNodeImpl(parentNode);
        }

        return parent;
    }

    /**
     * Returns the namespace helper of the sitetree XML document.
     * @return A namespace helper.
     */
    protected NamespaceHelper getNamespaceHelper() {
        NamespaceHelper helper =
            new NamespaceHelper(DefaultSiteTree.NAMESPACE_URI, "", node.getOwnerDocument());
        return helper;
    }

    /**
     * @see org.apache.lenya.cms.publication.SiteTreeNode#getParent(java.lang.String)
     */
    public SiteTreeNode getParent(String language) {
        SiteTreeNode parent = getParent();

        Label label = parent.getLabel(language);
        if (label == null) {
            parent = null;
        }

        return parent;
    }
}
