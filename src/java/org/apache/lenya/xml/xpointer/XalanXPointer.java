/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: XalanXPointer.java,v 1.18 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.xml.xpointer;

import java.util.Vector;

import org.apache.lenya.xml.DOMParserFactory;
import org.apache.log4j.Category;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * XPointer implementation
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
            Vector namespaces = new Vector();
            Vector nodes = xpointer.select(root, xpath, namespaces);

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
     * Select node by specified XPath
     *
     * @param node Node to select from
     * @param xpath XPath to select nodes
     *
     * @return Selected nodes
     *
     * @exception Exception ...
     */
    public Vector select(Node node, String xpath, Vector namespaces) throws Exception {
        NodeList children = node.getChildNodes();

        log.debug("Select " + xpath + " from node " + node.getNodeName());

        NodeList nl = null;
        if (namespaces.size() > 0) {
            org.w3c.dom.Document doc = org.apache.lenya.xml.DocumentHelper.createDocument("", "foo", null);
            for (int i = 0; i < namespaces.size(); i++) {
                String namespace = (String)namespaces.elementAt(i);
                String prefix = namespace.substring(0, namespace.indexOf("="));
                String namespaceURI = namespace.substring(namespace.indexOf("=") + 1);
                log.debug("Namespace: " + prefix + " " + namespaceURI);

                doc.getDocumentElement().setAttribute("xmlns:" + prefix, namespaceURI);
            }
            nl = XPathAPI.selectNodeList(node, xpath, doc.getDocumentElement());
        } else {
            nl = XPathAPI.selectNodeList(node, xpath);
        }


	if (nl != null && nl.getLength() == 0) {
            log.info("No such nodes: " + xpath);
            return new Vector();
        }

        Vector nodes = new Vector();

        for (int i = 0; i < nl.getLength(); i++) {
            nodes.addElement(nl.item(i));
        }

        return nodes;
    }
}
