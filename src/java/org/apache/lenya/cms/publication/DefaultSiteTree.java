/*
 * $Id: DefaultSiteTree.java,v 1.1 2003/05/07 07:45:16 egli Exp $
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

import org.apache.log4j.Category;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.StringTokenizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;



public class DefaultSiteTree
    implements SiteTree {
    static Category log = Category.getInstance(DefaultSiteTree.class);

    private Document doc = null;
    private File treefile = null;

    public DefaultSiteTree(String treefilename) throws DocumentException {
 	this(new File(treefilename));
	log.debug(treefilename);
    }

    public DefaultSiteTree(File treefile) throws DocumentException {
        // Read tree
	this.treefile = treefile;
        Document doc = new SAXReader().read("file:" + treefile);
    }

    public void addNode(String parentid, String id, Label[] labels) {
	addNode(parentid, id, labels, null, null, false);
    }

    public void addNode(String parentid, String id, Label[] labels,
			String href, String suffix, boolean link) {
        // Get parent element
        StringTokenizer st = new StringTokenizer(parentid, "/");
        String xpath_string = "/site"; // Trunk of tree
	
        while (st.hasMoreTokens()) {
            xpath_string = xpath_string + "/node[@id='" + st.nextToken() + "']";
        }
	
        log.debug("XPATH: " + xpath_string);
	
        XPath xpathSelector = DocumentHelper.createXPath(xpath_string);
        List nodes = xpathSelector.selectNodes(doc);
	
        if (nodes.isEmpty()) {
            log.error(".act(): No nodes: " + xpath_string);
            log.error(".act(): No child added!");
	    
            return;
        }
	
        Element parent_element = (Element) nodes.get(0);
        log.debug("PARENT ELEMENT: " + parent_element.getPath());
	
        // Check if child already exists
        String newChildXPath = xpath_string + "/" + "node";
        log.debug("CHECK: " + newChildXPath);
	
        if (doc.selectSingleNode(newChildXPath + "[@id='" + id + "']") != null) {
            log.error("Exception: XPath exists: " + newChildXPath + "[@id='" + id + "']");
            log.error("No child added");
	    
            return;
        }

        // Add node
        Element node = parent_element.addElement("node").addAttribute("id", id);
	if (href != null && href.length() > 0) {
	    node.addAttribute("href", href);
	}
	if (suffix != null && suffix.length() > 0) {
	    node.addAttribute("suffix", suffix);
	}
	if (link == true) {
	    node.addAttribute("link", "true");
	}
	for (int i = 0; i < labels.length; i++) {
	    String labelName = labels[i].getLabel();
	    node.addElement("label").setText(labelName);
	    String labelLanguage = labels[i].getLanguage();
	    if (labelLanguage != null && labelLanguage.length() > 0) {
		node.addAttribute("xml:lang", labelLanguage);
	    }
	}
	log.debug("Tree has been modified: " + doc.asXML());
    }

    public void deleteNode(String id) {}

    public void serialize() throws IOException {
        // Write the tree
        FileWriter fileWriter = new FileWriter(treefile);
        doc.write(fileWriter);
        fileWriter.close();
    }
}
