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
package org.apache.lenya.cms.ac;

import org.apache.lenya.xml.DOMUtil;

import org.apache.log4j.Category;

import org.apache.xpath.XPathAPI;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Vector;


/**
 * Identity class representing an IML (Identity Markup Language) document
 *
 * @author Michael Wechner
 * @version 2003.4.20
 */
public class Identity {
    static private Category log = Category.getInstance(Identity.class);
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

        // the comment node is optional
        Node commentNode = XPathAPI.selectSingleNode(doc, "/" + ROOT + "/comment");

        if (commentNode != null) {
            comment = commentNode.getFirstChild().getNodeValue();
            log.debug("Comment: " + comment);
        } else {
            log.warn("IMLFile: " + doc.getLocalName() + " doesn't contain a comment field.");
        }

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
     * @param filename
     *
     * @throws Exception Noch such file
     */
    public Identity(String filename) throws Exception {
        this(javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new java.io.FileInputStream(
                    filename)));
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
        Document iml = du.create("<?xml version=\"1.0\"?><identity id=\"" + username +
                "\"></identity>");
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
    public boolean changePassword(String oldP, String newP, String confirmedP)
        throws Exception {
        if (Password.encrypt(oldP).equals(getEncryptedPassword()) && newP.equals(confirmedP) &&
                (newP.length() > 4) && (newP.length() < 9)) {
            setPassword(newP);

            return true;
        }

        return false;
    }
}
