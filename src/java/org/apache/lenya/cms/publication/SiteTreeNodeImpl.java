/*
 * $Id: SiteTreeNodeImpl.java,v 1.3 2003/05/14 13:55:11 edith Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.publication;

import java.util.ArrayList;

import org.apache.log4j.Category;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import org.apache.lenya.xml.DocumentHelper;

public class SiteTreeNodeImpl 
    implements SiteTreeNode {
    static Category log = Category.getInstance(SiteTreeNodeImpl.class);

    public static final String ID_ATTRIBUTE_NAME = "id";
    public static final String HREF_ATTRIBUTE_NAME = "href";
    public static final String SUFFIX_ATTRIBUTE_NAME = "suffix";
    public static final String LINK_ATTRIBUTE_NAME = "link";
    public static final String LANGUAGE_ATTRIBUTE_NAME = "xml:lang";

    public static final String NODE_NAME = "node";
    public static final String LABEL_NAME = "label";

    private Node node = null;

    public SiteTreeNodeImpl(Node node) {
 	this.node = node;
    }

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

    public String getId() {
	return node.getAttributes().getNamedItem(ID_ATTRIBUTE_NAME).getNodeValue();
}

    public Label[] getLabels() {
	ArrayList labels = new ArrayList();

	NodeList children = node.getChildNodes();
	for (int i = 0; i < children.getLength(); i++) {
	    NamedNodeMap attributes = children.item(i).getAttributes();
	    Node child = children.item(i);
	    if (child.getNodeType() == Node.ELEMENT_NODE &&
		child.getNodeName().equals(LABEL_NAME)) {

		String labelName = DocumentHelper.getSimpleElementText((Element)child);
		String labelLanguage = null;
		Node languageAttribute = child.getAttributes().getNamedItem(LANGUAGE_ATTRIBUTE_NAME);
		if (languageAttribute != null) {
		    labelLanguage = languageAttribute.getNodeValue();
		}
		labels.add(new Label(labelName, labelLanguage));
	    }
	}
	return (Label[])labels.toArray(new Label[labels.size()]);
    }

    public Label getLabel(String xmlLanguage) {
	Label label = null;
	Label[] labels = getLabels();
	for (int i = 0; i < labels.length; i++) {
	    if (labels[i].getLanguage().equals(xmlLanguage)) {
		label = labels[i];
		break;
	    }
	}
	return label;
    }

    public String getHref() {
	Node attribute = node.getAttributes().getNamedItem(HREF_ATTRIBUTE_NAME);
	if (attribute != null) {
	    return attribute.getNodeValue();
	} else {
	    return null;
	}
    }

    public String getSuffix() {
	Node attribute = node.getAttributes().getNamedItem(SUFFIX_ATTRIBUTE_NAME);
	if (attribute != null) {
	    return attribute.getNodeValue();
	} else {
	    return null;
	}
    }

    public boolean hasLink() {
	Node attribute = node.getAttributes().getNamedItem(LINK_ATTRIBUTE_NAME);
	if (attribute != null) {
	    return attribute.getNodeValue().equals("true");
	} else {
	    return false;
	}
    }
}
