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

import org.apache.log4j.Category;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.8 $
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
     * @param node DOCUMENT ME!
     */
    public SiteTreeNodeImpl(Node node) {
        this.node = node;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getId() {
        return node.getAttributes().getNamedItem(ID_ATTRIBUTE_NAME).getNodeValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Label[] getLabels() {
        ArrayList labels = new ArrayList();

        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            NamedNodeMap attributes = children.item(i).getAttributes();
            Node child = children.item(i);

            if ((child.getNodeType() == Node.ELEMENT_NODE) &&
                    child.getNodeName().equals(LABEL_NAME)) {
                String labelName = DocumentHelper.getSimpleElementText((Element) child);
                String labelLanguage = null;
                Node languageAttribute = child.getAttributes().getNamedItem(LANGUAGE_ATTRIBUTE_NAME);

                if (languageAttribute != null) {
                    labelLanguage = languageAttribute.getNodeValue();
                }

                labels.add(new Label(labelName, labelLanguage));
            }
        }

        return (Label[]) labels.toArray(new Label[labels.size()]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param xmlLanguage DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Label getLabel(String xmlLanguage) {
        Label label = null;
        Label[] labels = getLabels();
        String language = null;

        for (int i = 0; i < labels.length; i++) {
            language = labels[i].getLanguage();

            if ((language != null) && (language.equals(xmlLanguage))) {
                label = labels[i];

                break;
            }
        }

        return label;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean hasLink() {
        Node attribute = node.getAttributes().getNamedItem(LINK_ATTRIBUTE_NAME);

        if (attribute != null) {
            return attribute.getNodeValue().equals("true");
        } else {
            return false;
        }
    }
}
