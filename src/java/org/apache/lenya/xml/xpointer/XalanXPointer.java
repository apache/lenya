/*
 * $Id: XalanXPointer.java,v 1.2 2003/02/07 12:14:26 ah Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.xml.xpointer;

import org.apache.log4j.Category;

import org.w3c.dom.*;

import org.wyona.xml.*;

import java.io.*;

import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner, wyona
 * @version 0.4.16
 */
public class XalanXPointer implements XPointer {
    static Category log = Category.getInstance(XalanXPointer.class);

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        XPointer xpointer = new XalanXPointer();

        if (args.length != 2) {
            System.err.println("Usage: java " + xpointer.getClass().getName() +
                " example.xml \"/Example/People/Person[position() < 2]/Street/@Number\"");

            return;
        }

        DOMParserFactory dpf = new DOMParserFactory();
        Document document = null;

        try {
            document = dpf.getDocument(args[0]);
        } catch (Exception e) {
            System.err.println(xpointer.getClass().getName() + ".main(): " + e);
        }

        Element root = document.getDocumentElement();
        String xpath = args[1];

        try {
            Vector nodes = xpointer.select(root, xpath);

            for (int i = 0; i < nodes.size(); i++) {
                Node node = (Node) nodes.elementAt(i);
                short type = node.getNodeType();

                if (type == Node.ATTRIBUTE_NODE) {
                    System.out.println("Attribute (" + node.getNodeName() + "): " +
                        node.getNodeValue());
                } else if (type == Node.ELEMENT_NODE) {
                    System.out.println("Element (" + node.getNodeName() + "): " +
                        node.getFirstChild().getNodeValue());
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     * @param xpath DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @exception Exception ...
     */
    public Vector select(Node node, String xpath) throws Exception {
        //System.err.println("XalanXPointer: "+xpath);
        //System.err.println("XalanXPointer: "+node.getNodeName());
        NodeList children = node.getChildNodes();

        /*
                  for(int i=0;i<children.getLength();i++)
                     {
                     System.err.println(((Node)children.item(i)).getNodeName());
                     }
        */
        log.debug(node.getNodeName() + "  " + xpath);

        //NodeList nl=null; //new XPathAPI().selectNodeList(node,xpath);
        NodeList nl = new org.apache.xpath.XPathAPI().selectNodeList(node, xpath);

        //System.err.println("XalanXPointer: "+nl.getLength());

        /*
                  NodeList kinder=node.getChildNodes();
                  for(int i=0;i<kinder.getLength();i++)
                     {
                     System.err.println(((Node)kinder.item(i)).getNodeName());
                     }
        */
        Vector nodes = new Vector();

        for (int i = 0; i < nl.getLength(); i++) {
            nodes.addElement(nl.item(i));
        }

        return nodes;
    }
}
