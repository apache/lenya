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
package org.apache.lenya.xml.xpointer;

import org.apache.lenya.xml.*;

import org.apache.log4j.Category;

import org.apache.xpath.XPathAPI;

import org.w3c.dom.*;

import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner, lenya
 * @version 0.4.16
 */
public class XalanXPointer implements XPointer {
    private static Category log = Category.getInstance(XalanXPointer.class);

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
        NodeList children = node.getChildNodes();

        log.debug(node.getNodeName() + "  " + xpath);

        NodeList nl = XPathAPI.selectNodeList(node, xpath);

        Vector nodes = new Vector();

        for (int i = 0; i < nl.getLength(); i++) {
            nodes.addElement(nl.item(i));
        }

        return nodes;
    }
}
