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

/* $Id: DOMWriter.java,v 1.14 2004/03/01 16:18:23 gregor Exp $  */

package org.apache.lenya.xml;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;

import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * DOCUMENT ME!
 * @deprecated replaced by DocumentHelper
 */
public class DOMWriter {
    static Category log = Category.getInstance(DOMWriter.class);
    PrintWriter out = null;
    String encoding = null;

    /**
     * Creates a new DOMWriter object.
     *
     * @param out DOCUMENT ME!
     */
    public DOMWriter(PrintWriter out) {
        this.out = out;
    }

    /**
     * Creates a new DOMWriter object.
     *
     * @param out DOCUMENT ME!
     * @param encoding DOCUMENT ME!
     */
    public DOMWriter(PrintWriter out, String encoding) {
        this(out);
        this.encoding = encoding;
    }

    /**
     * Creates a new DOMWriter object.
     *
     * @param os DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public DOMWriter(OutputStream os) throws Exception {
        this(os, "utf-8");
    }

    /**
     * Creates a new DOMWriter object.
     *
     * @param os DOCUMENT ME!
     * @param encoding DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public DOMWriter(OutputStream os, String encoding)
        throws Exception {
        out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(os, XMLEncToJavaEnc.getJava(encoding))));
        this.encoding = encoding;
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java org.apache.lenya.xml.DOMWriter \"file.xml\"");
            System.err.println("Description: Reads \"file.xml\" and writes it to standard output");

            return;
        }

        DOMParserFactory dpf = new DOMParserFactory();
        Document document = null;

        try {
            document = dpf.getDocument(args[0]);
        } catch (FileNotFoundException e) {
            System.err.println("No such file: " + e.getMessage());

            return;
        } catch (Exception e) {
            System.err.println(e.getMessage());

            return;
        }

        try {
            new DOMWriter(System.out, "iso-8859-1").printWithoutFormatting(document);
        } catch (Exception e) {
            System.err.println(e.getMessage());

            return;
        }

        log.fatal("\n");

        log.fatal(".main(): System.exit(0)");
        System.exit(0);

        new DOMWriter(new PrintWriter(System.out)).print(document);
        System.out.print("\n");

        XPointerFactory xpf = new XPointerFactory();

        try {
            Vector nodes = xpf.select(document.getDocumentElement(),
                    "xpointer(/Example/People/Person/City)");
            String[] values = xpf.getNodeValues(nodes);

            for (int i = 0; i < values.length; i++) {
                System.out.println(values[i]);
            }

            Document doc = dpf.getDocument();
            Element root = dpf.newElementNode(doc, "Root");

            //
            for (int i = 0; i < values.length; i++) {
                root.appendChild(dpf.newTextNode(doc, values[i]));
            }

            doc.appendChild(root);
            new DOMWriter(new PrintWriter(System.out)).print(doc);
            System.out.print("\n");
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     */
    public void print(Node node) {
        if (node == null) {
            return;
        }

        short type = node.getNodeType();

        switch (type) {
        case Node.DOCUMENT_NODE: {
            out.print("<?xml version=\"1.0\"");

            if (encoding != null) {
                out.print(" encoding=\"" + encoding + "\"");
            }

            out.print("?>\n\n");
            print(((Document) node).getDocumentElement());
            out.flush();

            break;
        }

        case Node.ELEMENT_NODE: {
            out.print("<" + node.getNodeName());

            NamedNodeMap attributes = node.getAttributes();

            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                out.print(" " + attribute.getNodeName() + "=\"" +
                    Normalize.normalize(attribute.getNodeValue()) + "\"");
            }

            if (node.hasChildNodes()) {
                out.print(">");

                NodeList children = node.getChildNodes();

                for (int i = 0; i < children.getLength(); i++) {
                    print(children.item(i));
                }

                out.print("</" + node.getNodeName() + ">");
            } else {
                out.print("/>");
            }

            break;
        }

        case Node.TEXT_NODE: {
            out.print(Normalize.normalize(node.getNodeValue()));

            break;
        }

        case Node.COMMENT_NODE: {
            out.print("<!--" + node.getNodeValue() + "-->");

            break;
        }

        default: {
            System.err.println(this.getClass().getName() + ".print(): Node type not implemented: " +
                type);

            break;
        }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     */
    public void printWithoutFormatting(Node node) {
        if (node == null) {
            return;
        }

        short type = node.getNodeType();

        switch (type) {
        case Node.DOCUMENT_NODE: {
            out.print("<?xml version=\"1.0\"");

            if (encoding != null) {
                out.print(" encoding=\"" + encoding + "\"");
            }

            out.print("?>\n\n");

            Element root = ((Document) node).getDocumentElement();
            root.setAttribute("xmlns:xlink", "http://www.w3.org/xlink");
            printWithoutFormatting(root);
            out.flush();

            break;
        }

        case Node.ELEMENT_NODE: {
            out.print("<" + node.getNodeName());

            NamedNodeMap attributes = node.getAttributes();

            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                out.print(" " + attribute.getNodeName() + "=\"" +
                    replaceSpecialCharacters(attribute.getNodeValue()) + "\"");
            }

            if (node.hasChildNodes()) {
                out.print(">");

                NodeList children = node.getChildNodes();

                for (int i = 0; i < children.getLength(); i++) {
                    printWithoutFormatting(children.item(i));
                }

                out.print("</" + node.getNodeName() + ">");
            } else {
                out.print("/>");
            }

            break;
        }

        case Node.TEXT_NODE: {
            out.print(replaceSpecialCharacters(node.getNodeValue()));

            break;
        }

        case Node.COMMENT_NODE: {
            out.print("<!--" + node.getNodeValue() + "-->");

            break;
        }

        default: {
            System.err.println(this.getClass().getName() + ".print(): Node type not implemented: " +
                type);

            break;
        }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String replaceSpecialCharacters(String s) {
        StringBuffer str = new StringBuffer();

        int len = (s != null) ? s.length() : 0;

        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);

            switch (ch) {
            case '<': {
                str.append("&#60;");

                break;
            }

            case '>': {
                str.append("&#62;");

                break;
            }

            case '&': {
                str.append("&#38;");

                break;
            }

            default:
                str.append(ch);
            }
        }

        return (str.toString());
    }
}
