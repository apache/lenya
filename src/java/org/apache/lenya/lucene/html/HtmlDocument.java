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

/* $Id: HtmlDocument.java,v 1.12 2004/03/01 16:18:15 gregor Exp $  */

package org.apache.lenya.lucene.html;


// Imports commented out since there is a name clash and fully
// qualified class names will be used in the code.  Imports are
// left for ease of maintenance.
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.lucene.document.Field;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.tidy.Tidy;


/**
 * The <code>HtmlDocument</code> class creates a Lucene {@link org.apache.lucene.document.Document}
 * from an HTML document.
 *
 * <P>
 * It does this by using JTidy package. It can take input input from {@link java.io.File} or {@link
 * java.io.InputStream}.
 * </p>
 */
public class HtmlDocument {
    private Element rawDoc;
    private String luceneTagName = null;
    private String luceneClassValue = null;

    /**
     * Constructs an <code>HtmlDocument</code> from a {@link java.io.File}.
     *
     * @param file the <code>File</code> containing the HTML to parse
     * @exception IOException if an I/O exception occurs
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
     * @exception IOException if I/O exception occurs
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
     * @return org.apache.lucene.document.Document
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

    /**
     * Creates a Lucene <code>Document</code> from a {@link java.io.File}.
     *
     * @param file
     * @return org.apache.lucene.document.Document
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

        for (int i = 0; i < metaNL.getLength(); i++) {
            Element metaElement = (Element) metaNL.item(i);
            Attr nameAttr = metaElement.getAttributeNode("name");
            Attr valueAttr = metaElement.getAttributeNode("value");

            if ((nameAttr != null) && (valueAttr != null)) {
                if (nameAttr.getValue().equals("lucene-tag-name")) {
                    luceneTagName = valueAttr.getValue();
                }

                if (nameAttr.getValue().equals("lucene-class-value")) {
                    luceneClassValue = valueAttr.getValue();
                }
            }
        }

        boolean indexByLucene = true;

        if ((luceneTagName != null) && (luceneClassValue != null)) {
            indexByLucene = false;
        }

        System.out.println("HtmlDocument.getBody(): Index By Lucene (Default): " + indexByLucene);

        String body = "";
        NodeList nl = rawDoc.getElementsByTagName("body");

        if (nl.getLength() > 0) {
            body = getBodyText(nl.item(0), indexByLucene);
        }

        return body;
    }

    /**
     * Gets the bodyText attribute of the <code>HtmlDocument</code> object.
     *
     * @param node a DOM Node
     * @param indexByLucene DOCUMENT ME!
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

                if ((luceneTagName != null) && (luceneClassValue != null)) {
                    if (child.getNodeName().equals(luceneTagName)) {
                        Attr attribute = ((Element) child).getAttributeNode("class");

                        if (attribute != null) {
                            if (attribute.getValue().equals(luceneClassValue)) {
                                System.out.println("HtmlDocument.getBodyText(): <" + luceneTagName +
                                    " class=\"" + luceneClassValue + "\"> found!");
                                index = true;
                            }

                        }
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
