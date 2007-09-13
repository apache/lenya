/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

/* $Id: HtmlContentHandler.java 473841 2006-11-12 00:46:38Z gregor $  */

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
