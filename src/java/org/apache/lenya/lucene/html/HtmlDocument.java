/*
 * $Id: HtmlDocument.java,v 1.4 2003/03/04 17:46:47 gregor Exp $
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
package org.lenya.lucene.html;


// Imports commented out since there is a name clash and fully
// qualified class names will be used in the code.  Imports are
// left for ease of maintenance.
import org.apache.lucene.document.Field;

//import org.apache.lucene.document.Document;

import org.w3c.dom.Attr;
//import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.w3c.tidy.Tidy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;


/**
 * The <code>HtmlDocument</code> class creates a Lucene {@link org.apache.lucene.document.Document}
 * from an HTML document.
 * 
 * <P>
 * It does this by using JTidy package. It can take input input from {@link java.io.File} or {@link
 * java.io.InputStream}.
 * </p>
 *
 * @author Erik Hatcher
 * @author Michael Wechner
 * @author Andreas Hartmann
 *
 */
public class HtmlDocument {
    private Element rawDoc;

    private String luceneTagName=null;
    private String luceneClassValue=null;

    //-------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------

    /**
     * Constructs an <code>HtmlDocument</code> from a {@link java.io.File}.
     *
     * @param file the <code>File</code> containing the HTML to parse
     *
     * @exception IOException if an I/O exception occurs
     *
     * @since
     */
    public HtmlDocument(File file) throws IOException {
        Tidy tidy = new Tidy();
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);

        org.w3c.dom.Document root = tidy.parseDOM(new FileInputStream(file), null);
        rawDoc = root.getDocumentElement();
    }

    /**
     * Constructs an <code>HtmlDocument</code> from an {@link java.io.InputStream}.
     *
     * @param is the <code>InputStream</code> containing the HTML
     *
     * @exception IOException if I/O exception occurs
     *
     * @since
     */
    public HtmlDocument(InputStream is) throws IOException {
        Tidy tidy = new Tidy();
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);

        org.w3c.dom.Document root = tidy.parseDOM(is, null);
        rawDoc = root.getDocumentElement();
    }

    /**
     * Creates a Lucene <code>Document</code> from an {@link java.io.InputStream}.
     *
     * @param is
     *
     * @return org.apache.lucene.document.Document
     *
     * @exception IOException
     */
    public static org.apache.lucene.document.Document getDocument(InputStream is)
        throws IOException {
        HtmlDocument htmlDoc = new HtmlDocument(is);
        org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();

        luceneDoc.add(Field.Text("title", htmlDoc.getTitle()));
        luceneDoc.add(Field.Text("contents", htmlDoc.getBody()));

        return luceneDoc;
    }

    //-------------------------------------------------------------
    // Public methods
    //-------------------------------------------------------------

    /**
     * Creates a Lucene <code>Document</code> from a {@link java.io.File}.
     *
     * @param file
     *
     * @return org.apache.lucene.document.Document
     *
     * @exception IOException
     */
    public static org.apache.lucene.document.Document Document(File file)
        throws IOException {
        HtmlDocument htmlDoc = new HtmlDocument(file);
        org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();

        luceneDoc.add(Field.Text("title", htmlDoc.getTitle()));
        luceneDoc.add(Field.Text("contents", htmlDoc.getBody()));

        String contents = null;
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringWriter sw = new StringWriter();
        String line = br.readLine();

        while (line != null) {
            sw.write(line);
            line = br.readLine();
        }

        br.close();
        contents = sw.toString();
        sw.close();

        luceneDoc.add(Field.UnIndexed("rawcontents", contents));

        return luceneDoc;
    }

    //-------------------------------------------------------------
    // Private methods
    //-------------------------------------------------------------

    /**
     * Runs <code>HtmlDocument</code> on the files specified on the command line.
     *
     * @param args Command line arguments
     *
     * @exception Exception Description of Exception
     */
    private static void main(String[] args) throws Exception {
        //         HtmlDocument doc = new HtmlDocument(new File(args[0]));
        //         System.out.println("Title = " + doc.getTitle());
        //         System.out.println("Body  = " + doc.getBody());
        HtmlDocument doc = new HtmlDocument(new FileInputStream(new File(args[0])));
        System.out.println("Title = " + doc.getTitle());
        System.out.println("Body  = " + doc.getBody());
    }

    /**
     * Gets the title attribute of the <code>HtmlDocument</code> object.
     *
     * @return the title value
     */
    public String getTitle() {
        if (rawDoc == null) {
            return null;
        }

        String title = "";

        NodeList nl = rawDoc.getElementsByTagName("title");

        if (nl.getLength() > 0) {
            Element titleElement = ((Element) nl.item(0));
            Text text = (Text) titleElement.getFirstChild();

            if (text != null) {
                title = text.getData();
            }
        }

        return title;
    }

    /**
     * Gets the body text attribute of the <code>HtmlDocument</code> object.
     *
     * @return the body text value
     */
    public String getBody() {
        if (rawDoc == null) {
            return null;
        }




        // NOTE: JTidy will insert a meta tag: <meta name="generator" content="HTML Tidy, see www.w3.org" />
        //       This means that getLength is always greater than 0
        NodeList metaNL = rawDoc.getElementsByTagName("meta");
        //System.out.println("HtmlDocument.getBody(): Number of META tags: " + metaNL.getLength());
        for (int i = 0; i < metaNL.getLength(); i++) {
            Element metaElement = (Element)metaNL.item(i);
            Attr nameAttr = metaElement.getAttributeNode("name");
            Attr valueAttr = metaElement.getAttributeNode("value");
            if ( (nameAttr != null) && (valueAttr != null)) {
                //System.out.println("HtmlDocument.getBody(): <meta name=\"" + nameAttr.getValue()+"\" value=\""+valueAttr.getValue()+"\" />");
                if ( nameAttr.getValue().equals("lucene-tag-name")) {
                    luceneTagName=valueAttr.getValue();
                }
                if ( nameAttr.getValue().equals("lucene-class-value")) {
                    luceneClassValue=valueAttr.getValue();
                }
            }
        }



        boolean indexByLucene = true;
        if ( (luceneTagName != null) && (luceneClassValue != null)) {
            indexByLucene = false;
        }
        System.out.println("HtmlDocument.getBody(): Index By Lucene (Default): "+indexByLucene);


        String body = "";
        NodeList nl = rawDoc.getElementsByTagName("body");

        if (nl.getLength() > 0) {
            //System.out.println("HtmlDocument.getBody(): "+body);
            body = getBodyText(nl.item(0), indexByLucene);
        }

        return body;
    }

    /**
     * Gets the bodyText attribute of the <code>HtmlDocument</code> object.
     *
     * @param node a DOM Node
     * @param indexByLucene DOCUMENT ME!
     *
     * @return The bodyText value
     */
    private String getBodyText(Node node, boolean indexByLucene) {
        NodeList nl = node.getChildNodes();
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < nl.getLength(); i++) {
            boolean index = indexByLucene;
            Node child = nl.item(i);

            switch (child.getNodeType()) {
            case Node.ELEMENT_NODE:



                if ( (luceneTagName != null) && (luceneClassValue != null)) {
                    if (child.getNodeName().equals(luceneTagName)) {
                        Attr attribute = ((Element) child).getAttributeNode("class");

                        if (attribute != null) {
                            //System.out.println("HtmlDocument.getBodyText(): <"+luceneTagName+" class=\""+attribute.getValue()+"!");
                            if (attribute.getValue().equals(luceneClassValue)) {
                                System.out.println("HtmlDocument.getBodyText(): <"+luceneTagName+" class=\""+luceneClassValue+"\"> found!");
                                index = true;
                            }
                            //System.out.println("HtmlDocument.getBodyText(): <"+luceneTageName+" class!");
                        }
                        //System.out.println("HtmlDocument.getBodyText(): <"+luceneTagName+"!");
                    }
                }




                buffer.append(getBodyText(child, index));

                if (index) {
                    buffer.append(" ");
                }

                break;

            case Node.TEXT_NODE:

                if (indexByLucene) {
                    buffer.append(((Text) child).getData());
                }

                break;
            }
        }

        return buffer.toString();
    }
}
