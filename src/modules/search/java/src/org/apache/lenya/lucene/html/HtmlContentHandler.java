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

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * HTML content handler
 */
public class HtmlContentHandler extends DefaultHandler {

    /**
     * Command line interface
     * @param args Command line args
     */
    public static void main(String[] args) {
        ContentHandler ch = new HtmlContentHandler();
        org.apache.excalibur.xml.sax.JTidyHTMLParser parser = new org.apache.excalibur.xml.sax.JTidyHTMLParser();

        try {
            parser.parse(new org.xml.sax.InputSource(new java.io.FileInputStream("/usr/local/apache/htdocs/index.html")), ch);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * End the document
     * @throws SAXException
     */
    public void endDocument() throws SAXException {
        // do nothing
    }

    /**
     * Start the prefix mapping
     * @param prefix The prefix
     * @param uri The URI
     * @throws SAXException if a parser error occurs
     */
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException {
        // do nothing
    }

    /**
     * Set the document locator
     * @param locator The locator
     */
    public void setDocumentLocator(Locator locator) {
        // do nothing
    }

    /**
     * Start document
     * @throws SAXException if a parser error occurs
     */
    public void startDocument() throws SAXException {
        // do nothing
    }
}
