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

/* $Id: DOMUtil.java,v 1.14 2004/03/05 11:01:21 michi Exp $  */

package org.apache.lenya.xml;

import java.io.StringReader;
import java.util.Vector;

import org.apache.log4j.Category;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This class is a utility class for miscellaneous DOM functions, similar to org.apache.cocoon.xml.dom.DOMUtil
 * FIXME: Merge classes or extend functionality
 */
public class DOMUtil {
    static Category log = Category.getInstance(DOMUtil.class);
    DOMParserFactory dpf = null;
    XPointerFactory xpf = null;

    /**
       *
       */
    public DOMUtil() {
        dpf = new DOMParserFactory();
        xpf = new XPointerFactory();
    }

    /**
       *
       */
    public static void main(String[] args) {
        try {
            DOMUtil du = new DOMUtil();
            Document document = du.create(
                    "<?xml version=\"1.0\"?><Artikel><Datum><Monat Name=\"Juli\"/><Tag>23</Tag></Datum><Content/></Artikel>");
            new DOMWriter(System.out).printWithoutFormatting(document);
            du.setElementValue(document, "/Artikel/Datum/Tag", "25");
            du.setElementValue(document, "/Artikel/Datum/Monat", "7");
            du.setElementValue(document, "/Artikel/Datum/Monat", "9");
            du.setElementValue(document, "/Artikel/Datm/Mont", "13");
            du.setAttributeValue(document, "/Artikel/Datum/Monat/@Name", "Oktober");
            du.setAttributeValue(document, "/Artikel/Datu/Monat/@Nam", "August");
            du.setElementValue(document, "/Artikel/Datu/Monat", "8");
            du.addElement(document, "/Artikel/Datum/Tag", "26");
            du.setElementValue(document, "/Artikel/Datum/Tag", "24");

            new DOMWriter(System.out).printWithoutFormatting(document);
            System.out.print("\n");
            System.out.print("\n");

            String[] elements = du.getAllElementValues(document, new XPath("/Artikel/Datum/Monat"));

            for (int i = 0; i < elements.length; i++) {
                System.out.println("Elements=" + elements[i]);
            }

            System.out.print("\n");
            System.out.println("Datum/Monat=" +
                du.getElementValue(document.getDocumentElement(), new XPath("Datum/Monat")));
            System.out.println("Datm=" +
                du.getElementValue(document.getDocumentElement(), new XPath("Datm")));

            System.out.println("Datum/Monat/@Name=" +
                du.getAttributeValue(document.getDocumentElement(), new XPath("Datum/Monat/@Name")));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
       *
       */
    public Document create(String xml) throws Exception {
        return dpf.getDocument(new StringReader(xml));
    }

    /**
       *
       */
    public Element[] select(Document document, String xpath)
        throws Exception {
        log.debug(".select(): " + xpath);

        Vector nodes = xpf.select(document, "xpointer(" + xpath + ")");
        Element[] elements = new Element[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            elements[i] = (Element) nodes.elementAt(i);
        }

        return elements;
    }

    /**
       *
       */
    public void replaceText(Element element, String text) {
        NodeList nl = element.getChildNodes();

        for (int i = nl.getLength() - 1; i >= 0; i--) {
            element.removeChild(nl.item(i));
        }

        element.appendChild(dpf.newTextNode(element.getOwnerDocument(), text));
    }

    /**
       *
       */
    public String getElementValue(Document document, XPath xpath)
        throws Exception {
        return getElementValue(document.getDocumentElement(), xpath);
    }

    /**
       *
       */
    public String getElementValue(Element element, XPath xpath)
        throws Exception {
        String value = "";
        NodeList nl = getElement(element, xpath).getChildNodes();

        for (int i = 0; i < nl.getLength(); i++) {
            short nodeType = nl.item(i).getNodeType();

            if (nodeType == Node.TEXT_NODE) {
                value = value + nl.item(i).getNodeValue();
            } else {
                log.warn("XPath " + xpath + " contains node types other than just TEXT_NODE");
            }
        }

        return value;
    }

    /**
     * Check if elements exists
     */
    public boolean elementExists(Element element, XPath xpath)
        throws Exception {
        log.debug(xpath);

        if (xpath.parts.length > 0) {
            NodeList nl = element.getElementsByTagName(xpath.parts[0]);

            if (nl.getLength() == 0) {
                return false;
            } else if (nl.getLength() == 1) {
                return true;
            }
        }
    return false;
    }

    /**
     * Get element via XPath
     */
    public Element getElement(Element element, XPath xpath)
        throws Exception {
        log.debug(xpath);

        if (xpath.parts.length > 0) {
            NodeList nl = element.getElementsByTagName(xpath.parts[0]);

            if (nl.getLength() == 0) {
                throw new Exception("There are no elements with Name \"" + xpath.parts[0] + "\".");
            } else if (nl.getLength() == 1) {
                log.debug("There is one element with Name \"" + xpath.parts[0] + "\" (" +
                    xpath.parts.length + ").");

                if (xpath.parts.length == 1) {
                    return (Element) nl.item(0);
                } else {
                    String newXPathString = xpath.parts[1];

                    for (int i = 2; i < xpath.parts.length; i++) {
                        newXPathString = newXPathString + "/" + xpath.parts[i];
                    }

                    return getElement((Element) nl.item(0), new XPath(newXPathString));
                }
            } else {
                throw new Exception("There are more elements than one with Name \"" +
                    xpath.parts[0] + "\".");
            }
        }

        return null;
    }

    /**
     * get all elements with |xpath|, xpath has to start with the root node
     *
     * @param document a value of type 'Document'
     * @param xpath a value of type 'XPath'
     *
     * @return a value of type 'Element[]'
     *
     * @exception Exception if an error occurs
     */
    public Element[] getAllElements(Document document, XPath xpath)
        throws Exception {
        Vector nodes = xpf.select(document.getDocumentElement(),
                "xpointer(" + xpath.toString() + ")");
        Element[] elements = new Element[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            elements[i] = (Element) nodes.elementAt(i);
        }

        return elements;
    }

    /**
     * get all elements values from |xpath|, xpath has to start with the root
     * node
     *
     * @param document a value of type 'Document'
     * @param xpath a value of type 'XPath'
     *
     * @return a value of type 'String[]'
     *
     * @exception Exception if an error occurs
     */
    public String[] getAllElementValues(Document document, XPath xpath)
        throws Exception {
        Vector nodes = xpf.select(document.getDocumentElement(),
                "xpointer(" + xpath.toString() + ")");
        log.debug("n elements " + nodes.size());

        String[] values = new String[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            values[i] = getElementValue((Element) nodes.elementAt(i));
        }

        return values;
    }

    /**
       *
       */
    public String getElementValue(Element element) throws Exception {
        String value = "";
        NodeList nl = element.getChildNodes();

        for (int i = 0; i < nl.getLength(); i++) {
            short nodeType = nl.item(i).getNodeType();

            if (nodeType == Node.TEXT_NODE) {
                value = value + nl.item(i).getNodeValue();
            } else {
                log.warn("Element " + element.getNodeName() +
                    " contains node types other than just TEXT_NODE");
            }
        }

        return value;
    }

    /**
     * Return the value of an attribte named e.g.
     * "this/myelement/(at)myattribute"
     *
     * @param element a value of type 'Element'
     * @param xpath a value of type 'XPath'
     *
     * @return a value of type 'String'
     *
     * @exception Exception if an error occurs
     */
    public String getAttributeValue(Element element, XPath xpath)
        throws Exception {
        Element el = getElement(element, new XPath(xpath.getElementName()));

        return el.getAttribute(xpath.getName());
    }

    /**
     * Describe 'setElementValue' method here.
     *
     * @param document a value of type 'Document'
     * @param xpath a value of type 'String'
     * @param value a value of type 'String'
     *
     * @exception Exception if an error occurs
     */
    public void setElementValue(Document document, String xpath, String value)
        throws Exception {
        Element[] elements = select(document, xpath);

        if (elements.length >= 1) {
            if (elements.length > 1) {
                log.warn("There are more elements than one with XPath \"" + xpath +
                    "\". The value of the first element will be replaced");
            }

            replaceText(elements[0], value);
        } else {
            XPath xp = new XPath(xpath);
            log.warn("XPath does not exist, but will be created: " + xp);

            Element element = (Element) createNode(document, xp);
            replaceText(element, value);
        }
    }

    /**
       *
       */
    public void addElement(Document document, String xpath, String value)
        throws Exception {
        XPath xp = new XPath(xpath);
        Node parent = createNode(document, xp.getParent());
        Element element = dpf.newElementNode(document, xp.getName());
        parent.appendChild(element);

        if (value != null) {
            element.appendChild(dpf.newTextNode(element.getOwnerDocument(), value));
        }
    }

    /**
       *
       */
    public void setAttributeValue(Document document, String xpath, String value)
        throws Exception {
        Vector nodes = xpf.select(document, "xpointer(" + xpath + ")");

        if (nodes.size() >= 1) {
            Attr attribute = (Attr) nodes.elementAt(0);
            attribute.setValue(value);
        } else {
            XPath xp = new XPath(xpath);
            log.debug("XPath does not exist, but will be created: " + xp);

            Attr attribute = (Attr) createNode(document, xp);
            attribute.setValue(value);
        }
    }

    /**
       *
       */
    public void setValue(Document document, XPath xpath, String value)
        throws Exception {
        short type = xpath.getType();

        if (type == Node.ATTRIBUTE_NODE) {
            setAttributeValue(document, xpath.toString(), value);
        } else if (type == Node.ELEMENT_NODE) {
            setElementValue(document, xpath.toString(), value);
        } else {
            log.error("No such type: " + type);
        }
    }

    /**
       *
       */
    public Node createNode(Document document, XPath xpath)
        throws Exception {
        log.debug(xpath);

        Node node = null;
        Vector nodes = xpf.select(document, "xpointer(" + xpath + ")");

        if (nodes.size() >= 1) {
            node = (Node) nodes.elementAt(0);
        } else {
            Node parentNode = createNode(document, xpath.getParent());

            if (xpath.getType() == Node.ATTRIBUTE_NODE) {
                ((Element) parentNode).setAttribute(xpath.getNameWithoutPredicates(), "null");
                node = ((Element) parentNode).getAttributeNode(xpath.getNameWithoutPredicates());
            } else {
                node = dpf.newElementNode(document, xpath.getNameWithoutPredicates());
                parentNode.appendChild(node);
            }
        }

        return node;
    }
}
