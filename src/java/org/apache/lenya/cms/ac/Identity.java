/*
 * $Id: Identity.java,v 1.7 2003/04/20 01:02:33 michi Exp $
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
package org.lenya.cms.ac;

import org.apache.xpath.XPathAPI;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Vector;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 1.12.22
 */
public class Identity {
    private static String ROOT = "identity";
    private String username = null;
    private Vector groupnames = null;

    /**
     * Creates a new Identity object.
     *
     * @param username DOCUMENT ME!
     */
    public Identity(String username) {
        this.username = username;
        groupnames = new Vector();
    }

    /**
     * Creates a new Identity object.
     *
     * @param username DOCUMENT ME!
     * @param doc DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Identity(String username, Document doc) throws Exception {
        this(username);

        NodeList groupNodes = XPathAPI.selectNodeList(doc,
                "/" + ROOT + "/groups/group");

        for (int i = 0; i < groupNodes.getLength(); i++) {
            Node groupNode = groupNodes.item(i);
            addGroupname(groupNode.getFirstChild().getNodeValue());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: org.lenya.cms.ac.Identity lenya.iml");

            return;
        }

        try {
            javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new java.io.FileInputStream(args[0]));
            Identity id = new Identity("dummy", doc);
            System.out.println(id);
            System.out.println(id.getPassword(doc));
        } catch (Exception e) {
            System.err.println(".main(): " + e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getUsername() {
        return username;
    }

    /**
     * DOCUMENT ME!
     *
     * @param doc DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public static String getPassword(Document doc) throws Exception {
        Node passwordNode = XPathAPI.selectSingleNode(doc,
                "/" + ROOT + "/password");

        return passwordNode.getFirstChild().getNodeValue();
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
     * Change Password
     */
     public static boolean changePassword(String oldP, String newP, String confirmedP) {
         if (oldP.equals("levi") && newP.equals(confirmedP)) return true;
         return false;
     }
}
