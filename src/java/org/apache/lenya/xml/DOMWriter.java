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
package org.apache.lenya.xml;

import org.apache.log4j.Category;

import org.w3c.dom.*;

import java.io.*;

import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner (http://www.lenya.com)
 * @author Roger Lacher (http://www.lenya.com)
 * @version 0.9.26
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
