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

/* $Id: HtmlContentHandler.java,v 1.6 2004/03/01 16:18:15 gregor Exp $  */

package org.apache.lenya.lucene.html;

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 */
public class HtmlContentHandler extends DefaultHandler {

    /**
     *
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
     *
     */
    public void endDocument() throws SAXException {
    }

    /**
     *
     */
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException {
    }

    /**
     *
     */
    public void setDocumentLocator(Locator locator) {
    }

    /**
     *
     */
    public void startDocument() throws SAXException {
    }
}
