/*
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

import org.apache.lenya.xml.parser.Parser;

import org.apache.log4j.Category;

import org.w3c.dom.*;

import org.xml.sax.SAXException;

import java.io.*;

import java.util.Properties;


/**
 * Utility class for creating DOM documents
 *
 * @author Michael Wechner
 * @version $Id: DOMParserFactory.java,v 1.15 2003/08/13 16:24:12 michi Exp $
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
