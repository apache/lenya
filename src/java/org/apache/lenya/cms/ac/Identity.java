/*
 * $Id: Identity.java,v 1.11 2003/04/24 13:52:37 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment: "This product
 *    includes software developed by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor
 *    may "lenya" appear in their names without prior written permission
 *    of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment: "This product includes software developed by lenya
 *    (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS
 * OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * lenya WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS A RESULT
 * OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS BEEN
 * ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE
 * FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.ac;

import org.apache.log4j.Category;
import org.apache.xpath.XPathAPI;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Vector;

import org.apache.lenya.xml.DOMUtil;


/**
 * Identity class representing an IML (Identity Markup Language) document
 *
 * @author Michael Wechner
 * @version 2003.4.20
 */
public class Identity {
    static Category log = Category.getInstance(Identity.class);

    private static String ROOT = "identity";
    private String username = null;
    private String encryptedPassword = null;
    private String comment = null;
    private Vector groupnames = null;

    /**
     * Creates a new Identity object.
     *
     * @param doc XML Document
     *
     * @throws Exception No such nodes
     */
    public Identity(Document doc) throws Exception {
        Node usernameNode = XPathAPI.selectSingleNode(doc, "/" + ROOT + "/@id");
        username = usernameNode.getNodeValue();
        log.debug("Username: " + username);

        Node passwordNode = XPathAPI.selectSingleNode(doc, "/" + ROOT + "/password");
        encryptedPassword = passwordNode.getFirstChild().getNodeValue();
        log.debug("Encrypted Password: " + encryptedPassword);

        Node commentNode = XPathAPI.selectSingleNode(doc, "/" + ROOT + "/comment");
        comment = commentNode.getFirstChild().getNodeValue();
        log.debug("Comment: " + comment);

        NodeList groupNodes = XPathAPI.selectNodeList(doc, "/" + ROOT + "/groups/group");

        groupnames = new Vector();
        for (int i = 0; i < groupNodes.getLength(); i++) {
            Node groupNode = groupNodes.item(i);
            addGroupname(groupNode.getFirstChild().getNodeValue());
        }
    }

    /**
     * Creates a new Identity object.
     *
     * @param String filename
     *
     * @throws Exception Noch such file
     */
    public Identity(String filename) throws Exception {
        this(javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new java.io.FileInputStream(filename)));
    }

    /**
     * Get username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get encrypted password
     *
     * @param doc XML Document
     *
     * @return encrypted password
     *
     * @throws Exception No such node
     */
    public static String getPassword(Document doc) throws Exception {
        Node passwordNode = XPathAPI.selectSingleNode(doc, "/" + ROOT + "/password");

        return passwordNode.getFirstChild().getNodeValue();
    }

    /**
     * Get encrypted password
     *
     * @return encrypted password
     */
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    /**
     * Encrypt and set password
     */
    public void setPassword(String plainTextPassword) throws Exception {
        this.encryptedPassword = Password.encrypt(plainTextPassword);
    }

    /**
     * DOCUMENT ME!
     *
     * @param groupname DOCUMENT ME!
     */
    public void addGroupname(String groupname) {
        groupnames.addElement(groupname);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getGroupnames() {
        String[] gn = new String[groupnames.size()];

        for (int i = 0; i < gn.length; i++) {
            gn[i] = (String) groupnames.elementAt(i);
        }

        return gn;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        String s = "username=" + username;

        for (int i = 0; i < groupnames.size(); i++) {
            s = s + ", groupname=" + (String) groupnames.elementAt(i);
        }

        return s;
    }

    /**
     * Create XML Document
     */
     public Document createDocument() throws Exception {
         DOMUtil du = new DOMUtil();
         Document iml = du.create("<?xml version=\"1.0\"?><identity id=\"" +  username + "\"></identity>");
         du.setAttributeValue(iml, "/identity/password/@type", "md5");
         du.setElementValue(iml, "/identity/password", encryptedPassword);
         du.setElementValue(iml, "/identity/comment", comment);
         for (int i = 0; i < groupnames.size(); i++) {
             du.addElement(iml, "/identity/groups/group", (String) groupnames.elementAt(i));
         }

         //new org.apache.lenya.xml.DOMWriter(System.out).printWithoutFormatting(iml);
         return iml;
     }

    /**
     * Write XML Document to the filesystem
     */
     public void writeDocument(String filename) throws Exception {
         new org.apache.lenya.xml.DOMWriter(new java.io.FileOutputStream(filename)).printWithoutFormatting(createDocument());
     }

    /**
     * Change Password
     */
     public boolean changePassword(String oldP, String newP, String confirmedP) throws Exception {
         if (Password.encrypt(oldP).equals(getEncryptedPassword()) && newP.equals(confirmedP) && newP.length() > 4 && newP.length() < 9) {
             setPassword(newP);
             return true;
         }
         return false;
     }
}
