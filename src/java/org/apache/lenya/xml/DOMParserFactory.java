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

/* $Id: DOMParserFactory.java,v 1.19 2004/03/01 16:18:23 gregor Exp $  */

package org.apache.lenya.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.lenya.xml.parser.Parser;
import org.apache.log4j.Category;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


/**
 * Utility class for creating DOM documents
 * @deprecated replaced by DocumentHelper
 */
public class DOMParserFactory {
    static Category log = Category.getInstance(DOMParserFactory.class);
    public Parser parser = null;

    /**
     * Reads the properties and gets the parser
     */
    public DOMParserFactory() {
        Properties properties = new Properties();
        String propertiesFileName = "conf.properties";

        try {
            properties.load(DOMParserFactory.class.getResourceAsStream(propertiesFileName));
        } catch (Exception e) {
            log.fatal(": Failed to load properties from resource: " + propertiesFileName);
        }

        String parserName = properties.getProperty("Parser");

        if (parserName == null) {
            log.fatal(": No Parser specified in " + propertiesFileName);
        }

        try {
            Class parserClass = Class.forName(parserName);
            parser = (Parser) parserClass.newInstance();
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
        DOMParserFactory dpf = new DOMParserFactory();

        if (args.length != 1) {
            System.out.println("Usage: java " + dpf.getClass().getName() + " example.xml");

            return;
        }

        Document doc = null;

        try {
            doc = dpf.getDocument(args[0]);
        } catch (FileNotFoundException e) {
            System.err.println("No such file or directory: " + e.getMessage());

            return;
        } catch (SAXException e) {
            System.err.println(e);

            return;
        } catch (Exception e) {
            System.err.println(e.getMessage());

            return;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws FileNotFoundException DOCUMENT ME!
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(String filename) throws FileNotFoundException, Exception {
        File file = new File(filename);

        if (!file.exists()) {
            log.error("No such file or directory: " + filename);
            throw new FileNotFoundException(filename);
        }

        return parser.getDocument(filename);
    }

    /**
     * DOCUMENT ME!
     *
     * @param inputStream DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(InputStream inputStream)
        throws Exception {
        return parser.getDocument(inputStream);
    }

    /**
     * Create a document from a reader.
     *
     * @param inputStream DOCUMENT ME!
     * @return DOCUMENT ME!
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(Reader reader) throws Exception {
        return parser.getDocument(reader);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document getDocument() {
        return parser.getDocument();
    }

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element newElementNode(Document document, String name) {
        return parser.newElementNode(document, name);
    }

    /**
     * Creates an element with namespace support.
     *
     * @param document The owner document.
     * @param namespaceUri The namespace URI of the element.
     * @param qualifiedName The qualified name of the element.
     *
     * @return An element.
     */
    public Element newElementNSNode(Document document, String namespaceUri, String qualifiedName) {
        return parser.newElementNSNode(document, namespaceUri, qualifiedName);
    }
        
    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Text newTextNode(Document document, String data) {
        return parser.newTextNode(document, data);
    }

    /**
     * CDATA
     *
     * @param document DOM document
     * @param data Text
     *
     * @return CDATASection
     */
    public CDATASection newCDATASection(Document document, String data) {
        return parser.newCDATASection(document, data);
    }

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Comment newCommentNode(Document document, String data) {
        return parser.newCommentNode(document, data);
    }

    /**
     * Clone node, which means copy a node into another document
     *
     * @param document New document where original nodes shall be attached to
     * @param original Original node from original document
     * @param deep true means clone also all children
     *
     * @return New node, which is clone of original node
     */
    public Node cloneNode(Document document, Node original, boolean deep) {
        Node node = null;
        short nodeType = original.getNodeType();

        switch (nodeType) {
        case Node.ELEMENT_NODE: {
            Element element = newElementNSNode(document, original.getNamespaceURI(), original.getNodeName());
            log.debug(".cloneNode(): Clone element: " + original.getNodeName());
            NamedNodeMap attributes = original.getAttributes();

            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                log.debug(".cloneNode(): LocalName: " + attribute.getLocalName() + ", Prefix: " + attribute.getPrefix() + ", NamespaceURI: " + attribute.getNamespaceURI());
                element.setAttributeNS(attribute.getNamespaceURI(), attribute.getNodeName(), attribute.getNodeValue());
            }

            node = element;

            break;
        }

        case Node.TEXT_NODE: {
            Text text = newTextNode(document, original.getNodeValue());

            node = text;

            break;
        }

        case Node.CDATA_SECTION_NODE: {
            CDATASection cdata = newCDATASection(document, original.getNodeValue());

            node = cdata;

            break;
        }

        case Node.COMMENT_NODE: {
            Comment comment = newCommentNode(document, original.getNodeValue());

            node = comment;

            break;
        }

        default:
            log.warn(".cloneNode(): Node type not implemented: " + nodeType);
            break;
        }

        if (deep && original.hasChildNodes()) {
            NodeList nl = original.getChildNodes();

            for (int i = 0; i < nl.getLength(); i++) {
                node.appendChild(cloneNode(document, nl.item(i), deep));
            }
        }

        return node;
    }

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param element DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public void setElementValue(Document document, Element element, String value) {
        // remove all child nodes
        NodeList nl = element.getChildNodes();

        for (int i = 0; i < nl.getLength(); i++) {
            try {
                element.removeChild(nl.item(i));
            } catch (Exception e) {
                System.err.println("EXCEPTION: " + this.getClass().getName() +
                    ".setElementValue(): " + e);
            }
        }

        // add a new TextNode for storing the new value
        element.appendChild(newTextNode(document, value));
    }
}
