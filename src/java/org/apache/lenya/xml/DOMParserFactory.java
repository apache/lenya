/*
 * $Id: DOMParserFactory.java,v 1.6 2003/02/17 13:21:36 egli Exp $
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
package org.wyona.xml;

import org.apache.log4j.Category;

import org.w3c.dom.*;

import org.wyona.xml.parser.Parser;

import org.xml.sax.SAXException;

import java.io.*;

import java.util.*;
import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 1.9.11
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
    public Document getDocument(Reader reader)
        throws Exception {
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
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param original DOCUMENT ME!
     * @param deep DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Node cloneNode(Document document, Node original, boolean deep) {
        Node node = null;
        short nodeType = original.getNodeType();

        switch (nodeType) {
        case Node.ELEMENT_NODE: {
            Element element = newElementNode(document, original.getNodeName());
            NamedNodeMap attributes = original.getAttributes();

            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                element.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
            }

            node = element;

            break;
        }

        case Node.TEXT_NODE: {
            Text text = newTextNode(document, original.getNodeValue());

            node = text;

            break;
        }

        case Node.COMMENT_NODE: {
            Comment comment = newCommentNode(document, original.getNodeValue());

            node = comment;

            break;
        }

        default:
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
