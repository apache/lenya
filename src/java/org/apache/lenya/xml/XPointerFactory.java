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

/* $Id: XPointerFactory.java,v 1.17 2004/03/01 16:18:23 gregor Exp $  */

package org.apache.lenya.xml;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Vector;

import org.apache.lenya.xml.xpointer.XPointer;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPointerFactory {
    static Category log = Category.getInstance(XPointerFactory.class);
    XPointer xpointer = null;

    /**
     * Creates a new XPointerFactory object.
     */
    public XPointerFactory() {
        Properties properties = new Properties();
        String propertiesFileName = "conf.properties";

        try {
            properties.load(XPointerFactory.class.getResourceAsStream(propertiesFileName));
        } catch (Exception e) {
            log.fatal(": Failed to load properties from resource: " + propertiesFileName);
        }

        String xpointerName = properties.getProperty("XPointer");

        if (xpointerName == null) {
            log.fatal(": No XPointer specified in " + propertiesFileName);
        }

        try {
            Class xpointerClass = Class.forName(xpointerName);
            xpointer = (XPointer) xpointerClass.newInstance();
        } catch (Exception e) {
            log.fatal(": " + e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        XPointerFactory xpf = new XPointerFactory();
        DOMParserFactory dpf = new DOMParserFactory();

        if (args.length != 2) {
            System.err.println("Usage: java " + xpf.getClass().getName() +
                " example.xml \"/Example/People/Person[1]/Name\"");

            return;
        }

        Document document = null;

        try {
            document = dpf.getDocument(args[0]);
        } catch (FileNotFoundException e) {
            System.err.println("No such file or directory: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        String xpath = args[1];

        try {
            Vector nodes = xpf.select(document.getDocumentElement(), "xpointer(" + xpath + ")");
            String[] values = xpf.getNodeValues(nodes);

            for (int i = 0; i < nodes.size(); i++) {
                System.out.println(((Node) nodes.elementAt(i)).getNodeName() + ": " + values[i]);
            }
        } catch (Exception e) {
            System.err.println(xpf.getClass().getName() + ".main(): " + e);
        }

        Document doc = xpf.employees();

        try {
            Vector nodes = xpf.select(doc.getDocumentElement(), "xpointer(/Employees/Employee[2])");
            String[] values = xpf.getNodeValues(nodes);

            for (int i = 0; i < nodes.size(); i++) {
                System.out.println(((Node) nodes.elementAt(i)).getNodeName() + ": " + values[i]);
            }

            Element leviElement = (Element) nodes.elementAt(0);
            leviElement.appendChild(dpf.newTextNode(doc, " Brucker"));
        } catch (Exception e) {
            System.err.println(xpf.getClass().getName() + ".main(): " + e);
        }

        new DOMWriter(new PrintWriter(System.out)).print(doc);
        System.out.println("");
    }

    /**
     * Parse reference for xpointer and namespaces
     *
     * @param reference xmlns(...)xpointer(...)xpointer(...)
     *
     * @exception MalformedXPointerException xpointer(xpath)
     */
    public void parse(String reference, Vector xpaths, Vector namespaces) throws MalformedXPointerException {
        tokenize(reference, xpaths, namespaces);
    }

    /**
     * Select nodes by xpointer
     *
     * @param node Document Node
     * @param reference xmls(...)xpointer(...)
     *
     * @return nodes
     *
     * @exception Exception ...
     */
    public Vector select(Node node, String reference) throws Exception {
        Vector xpaths = new Vector();
        Vector namespaces = new Vector();
        parse(reference, xpaths, namespaces);

        Vector nodes = new Vector();

        for (int i = 0; i < xpaths.size(); i++) {
            Vector n = xpointer.select(node, (String) xpaths.elementAt(i), namespaces);

            for (int j = 0; j < n.size(); j++) {
                nodes.addElement(n.elementAt(j));
            }
        }

        return nodes;
    }

    /**
     * Select nodes by xpointer and return node at specific position
     *
     * @param node Document Node
     * @param reference xmls(...)xpointer(...)
     *
     * @return node
     *
     * @exception Exception ...
     */
    public Node selectAt(Node node, String reference, int i) throws Exception {
        return (Node)select(node, reference).elementAt(i);
    }

    /**
     * DOCUMENT ME!
     *
     * @param xpointer DOCUMENT ME!
     * @param xpaths DOCUMENT ME!
     *
     * @exception MalformedXPointerException xpointer(xpath)xpointer(xpath)
     */
    public void tokenize(String xpointer, Vector xpaths, Vector namespaces) throws MalformedXPointerException {
        if ((xpointer.indexOf("xpointer(") == 0) && (xpointer.charAt(xpointer.length() - 1) == ')')) {

            String substring = xpointer.substring(9, xpointer.length());
            int i = substring.indexOf(")");

            if (i >= 0) {
                log.debug("XPath: " + substring.substring(0, i));
                xpaths.addElement(substring.substring(0, i));
                tokenize(substring.substring(i + 1, substring.length()), xpaths, namespaces);
            } else {
                xpaths.addElement(substring.substring(0, substring.length() - 1));
                return;
            }
	} else if ((xpointer.indexOf("xmlns(") == 0) && (xpointer.charAt(xpointer.length() - 1) == ')')) {
            String substring = xpointer.substring(6, xpointer.length());
            int i = substring.indexOf(")");

            if (i >= 0) {
                log.debug("Namespace: " + substring.substring(0, i));
                namespaces.addElement(substring.substring(0, i));
                tokenize(substring.substring(i + 1, substring.length()), xpaths, namespaces);
            } else {
                xpaths.addElement(substring.substring(0, substring.length() - 1));
                return;
            }
	} else if (xpointer.equals("")) {
                return;
        } else {
            throw new MalformedXPointerException(xpointer);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodes DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public String[] getNodeValues(Vector nodes) throws Exception {
        String[] values = new String[nodes.size()];

        for (int i = 0; i < values.length; i++) {
            Node node = (Node) nodes.elementAt(i);
            short type = node.getNodeType();

            switch (type) {
            case Node.ELEMENT_NODE: {
                values[i] = getElementValue((Element) node);

                break;
            }

            case Node.ATTRIBUTE_NODE: {
                values[i] = node.getNodeValue();

                break;
            }

            default:
                values[i] = "";
                throw new Exception("Neither ELEMENT nor ATTRIBUTE: " + type);
            }
        }

        return values;
    }

    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getElementValue(Element element) {
        String value = "";
        NodeList nl = element.getChildNodes();

        for (int k = 0; k < nl.getLength(); k++) {
            short nodeType = nl.item(k).getNodeType();

            if (nodeType == Node.TEXT_NODE) {
                value = value + nl.item(k).getNodeValue();
            } else if (nodeType == Node.ELEMENT_NODE) {
                value = value + getElementValue((Element) nl.item(k));
            } else {
                System.err.println("EXCEPTION: " + this.getClass().getName() +
                    ".getElementValue(): No TEXT_NODE");
            }
        }

        return value;
    }

    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     * @param text DOCUMENT ME!
     */
    public void getElementValue(Element element, Vector text) {
        NodeList nl = element.getChildNodes();

        for (int k = 0; k < nl.getLength(); k++) {
            short nodeType = nl.item(k).getNodeType();

            if (nodeType == Node.TEXT_NODE) {
                text.addElement(nl.item(k).getNodeValue());
            } else if (nodeType == Node.ELEMENT_NODE) {
                getElementValue((Element) nl.item(k), text);
            } else {
                System.err.println("EXCEPTION: " + this.getClass().getName() +
                    ".getElementValue(): No TEXT_NODE");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document employees() {
        DOMParserFactory dpf = new DOMParserFactory();
        Document doc = dpf.getDocument();
        Element michi = dpf.newElementNode(doc, "Employee");
        michi.setAttribute("Id", "0");
        michi.appendChild(dpf.newTextNode(doc, "Michi"));

        Element levi = dpf.newElementNode(doc, "Employee");
        levi.setAttribute("Id", "1");
        levi.appendChild(dpf.newTextNode(doc, "Levi"));

        Element employees = dpf.newElementNode(doc, "Employees");
        employees.appendChild(dpf.newTextNode(doc, "\n"));
        employees.appendChild(michi);
        employees.appendChild(dpf.newTextNode(doc, "\n"));
        employees.appendChild(levi);
        employees.appendChild(dpf.newTextNode(doc, "\n"));
        doc.appendChild(employees);

        return doc;
    }
}
