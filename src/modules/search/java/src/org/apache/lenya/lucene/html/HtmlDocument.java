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

/* $Id$  */

package org.apache.lenya.lucene.html;


// Imports commented out since there is a name clash and fully
// qualified class names will be used in the code.  Imports are
// left for ease of maintenance.
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.log4j.Logger;
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
    private static final Logger log = Logger.getLogger(HtmlDocument.class);
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
        this.rawDoc = root.getDocumentElement();
    }

    /**
     * Constructs an <code>HtmlDocument</code> from an {@link java.io.InputStream}.
     * @param is the <code>InputStream</code> containing the HTML
     */
    public HtmlDocument(InputStream is) {
        Tidy tidy = new Tidy();
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);

        org.w3c.dom.Document root = tidy.parseDOM(is, null);
        this.rawDoc = root.getDocumentElement();
    }

    /**
     * Creates a Lucene <code>Document</code> from an {@link java.io.InputStream}.
     * @param is the <code>InputStream</code> containing the HTML
     * @return org.apache.lucene.document.Document
     */
    public static org.apache.lucene.document.Document getDocument(InputStream is) {
        HtmlDocument htmlDoc = new HtmlDocument(is);
        org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();

        luceneDoc.add(Field.Text("title", htmlDoc.getTitle()));
        luceneDoc.add(Field.Text("contents", htmlDoc.getBody()));

        return luceneDoc;
    }

    /**
     * Creates a Lucene <code>Document</code> from a {@link java.io.File}.
     * @param file The tile
     * @return org.apache.lucene.document.Document
     * @exception IOException when an IO error occurs
     */
    public static org.apache.lucene.document.Document document(File file)
        throws IOException {
        BufferedReader br = null;
        StringWriter sw = null;
        org.apache.lucene.document.Document luceneDoc = null;

        String contents;

        try {
            HtmlDocument htmlDoc = new HtmlDocument(file);
            luceneDoc = new org.apache.lucene.document.Document();

            luceneDoc.add(Field.Text("title", htmlDoc.getTitle()));
            luceneDoc.add(Field.Text("contents", htmlDoc.getBody()));

            contents = null;
            br = new BufferedReader(new FileReader(file));
            sw = new StringWriter();
            String line = br.readLine();

            while (line != null) {
                sw.write(line);
                line = br.readLine();
            }
	        contents = sw.toString();
            luceneDoc.add(Field.UnIndexed("rawcontents", contents));

        } catch (final FileNotFoundException e) {
            log.error("File not found " +e.toString());
        } catch (final IOException e) {
            log.error("IO error " +e.toString());
        } finally {
	        if (br != null)
	            br.close();
	        if (sw != null)
	            sw.close();
        }

        return luceneDoc;
    }

    /**
     * Gets the title attribute of the <code>HtmlDocument</code> object.
     * @return the title value
     */
    public String getTitle() {
        if (this.rawDoc == null) {
            return null;
        }

        String title = "";

        NodeList nl = this.rawDoc.getElementsByTagName("title");

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
     * @return the body text value
     */
    public String getBody() {
        if (this.rawDoc == null) {
            return null;
        }

        // NOTE: JTidy will insert a meta tag: <meta name="generator" content="HTML Tidy, see www.w3.org" />
        //       This means that getLength is always greater than 0
        NodeList metaNL = this.rawDoc.getElementsByTagName("meta");

        for (int i = 0; i < metaNL.getLength(); i++) {
            Element metaElement = (Element) metaNL.item(i);
            Attr nameAttr = metaElement.getAttributeNode("name");
            Attr valueAttr = metaElement.getAttributeNode("value");

            if ((nameAttr != null) && (valueAttr != null)) {
                if (nameAttr.getValue().equals("lucene-tag-name")) {
                    this.luceneTagName = valueAttr.getValue();
                }

                if (nameAttr.getValue().equals("lucene-class-value")) {
                    this.luceneClassValue = valueAttr.getValue();
                }
            }
        }

        boolean indexByLucene = true;

        if ((this.luceneTagName != null) && (this.luceneClassValue != null)) {
            indexByLucene = false;
        }

        System.out.println("HtmlDocument.getBody(): Index By Lucene (Default): " + indexByLucene);

        String body = "";
        NodeList nl = this.rawDoc.getElementsByTagName("body");

        if (nl.getLength() > 0) {
            body = getBodyText(nl.item(0), indexByLucene);
        }

        return body;
    }

    /**
     * Gets the bodyText attribute of the <code>HtmlDocument</code> object.
     * @param node a DOM Node
     * @param indexByLucene Whether the index is by Lucene
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

                if ((this.luceneTagName != null) && (this.luceneClassValue != null)) {
                    if (child.getNodeName().equals(this.luceneTagName)) {
                        Attr attribute = ((Element) child).getAttributeNode("class");

                        if (attribute != null) {
                            if (attribute.getValue().equals(this.luceneClassValue)) {
                                System.out.println("HtmlDocument.getBodyText(): <" + this.luceneTagName +
                                    " class=\"" + this.luceneClassValue + "\"> found!");
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
