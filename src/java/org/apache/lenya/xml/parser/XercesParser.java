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

/* $Id: XercesParser.java,v 1.16 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.xml.parser;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;

import org.apache.lenya.xml.DOMWriter;
import org.apache.xerces.dom.CDATASectionImpl;
import org.apache.xerces.dom.CommentImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.TextImpl;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 * Xerces Parser Implementation
 */
public class XercesParser implements Parser {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        Parser parser = new XercesParser();

        if (args.length != 1) {
            System.err.println("Usage: java " + parser.getClass().getName() + " example.xml");

            return;
        }

        Document doc = null;

        try {
            doc = parser.getDocument(args[0]);
        } catch (Exception e) {
            System.err.println(e);
        }

        new DOMWriter(new PrintWriter(System.out)).print(doc);
        System.out.println("");

        Document document = parser.getDocument();
        Element michi = parser.newElementNode(document, "Employee");
        michi.setAttribute("Id", "michi");
        michi.appendChild(parser.newTextNode(document, "Michi"));

        Element employees = parser.newElementNode(document, "Employees");
        employees.appendChild(parser.newTextNode(document, "\n"));
        employees.appendChild(michi);
        employees.appendChild(parser.newTextNode(document, "\n"));
        document.appendChild(employees);
        new DOMWriter(new PrintWriter(System.out)).print(document);
        System.out.println("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(String filename) throws Exception {
        DOMParser parser = new DOMParser();

        org.xml.sax.InputSource in = new org.xml.sax.InputSource(filename);
        parser.parse(in);

        return parser.getDocument();
    }

    /**
     * DOCUMENT ME!
     *
     * @param is DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(InputStream is) throws Exception {
        DOMParser parser = new DOMParser();
        org.xml.sax.InputSource in = new org.xml.sax.InputSource(is);
        parser.parse(in);

        return parser.getDocument();
    }

    /**
     * Creates a document from a reader.
     *
     * @param is DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(Reader reader) throws Exception {
        DOMParser parser = new DOMParser();
        org.xml.sax.InputSource in = new org.xml.sax.InputSource(reader);
        parser.parse(in);

        return parser.getDocument();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document getDocument() {
        return new DocumentImpl();
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
        return new ElementImpl((DocumentImpl) document, name);
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
        return document.createElementNS(namespaceUri, qualifiedName);
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
        return new TextImpl((DocumentImpl) document, data);
    }

    /**
     * CDATA
     *
     * @param document DOM Document
     * @param data Text
     *
     * @return CDATASection
     */
    public CDATASection newCDATASection(Document document, String data) {
        return new CDATASectionImpl((DocumentImpl) document, data);
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
        return new CommentImpl((DocumentImpl) document, data);
    }
}
