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

/* $Id$  */

package org.apache.lenya.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.html.parser.ParserDelegator;


/**
 * http://developer.java.sun/developer/TechTips/1999/tt0923.html
 */
public class HTML {
    HTMLHandler htmlHandler;

    /**
     * Creates a new HTML object from a URI
     * @param uri The URI
     * @throws IOException if a IO error occurs
     */
    public HTML(String uri) throws IOException {
        ParserDelegator pd = new ParserDelegator();
        this.htmlHandler = new HTMLHandler();
        pd.parse(getReader(uri), this.htmlHandler, true);
    }

    /**
     * Command line interface
     * @param args Command line args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: HTML uri (file or url)");

            return;
        }

        try {
            HTML html = new HTML(args[0]);

            List img_src_list = html.getImageSrcs(false);
            System.out.println("<im src");

            Iterator img_src_iterator = img_src_list.iterator();

            while (img_src_iterator.hasNext()) {
                System.out.println((String) img_src_iterator.next());
            }

            List a_href_list = html.getAnchorHRefs(false);
            System.out.println("<a href");

            Iterator a_href_iterator = a_href_list.iterator();

            while (a_href_iterator.hasNext()) {
                System.out.println((String) a_href_iterator.next());
            }

            List link_href_list = html.getLinkHRefs(false);
            System.out.println("<link href");

            Iterator link_href_iterator = link_href_list.iterator();

            while (link_href_iterator.hasNext()) {
                System.out.println((String) link_href_iterator.next());
            }
        } catch (final IOException e) {
            System.err.println("IO error : " + e);
        }
    }

    /**
     * Get Anchor Hrefs
     * @param duplicate Whether you want duplicate HREFS
     * @return A list of Hrefs
     */
    public List getAnchorHRefs(boolean duplicate) {
        if (duplicate) {
            return this.htmlHandler.getAllAHRefs();
        }
        return this.htmlHandler.getAHRefs();
    }

    /**
     * Get Link hrefs
     * @param duplicate Whether you want duplicate Hrefs
     * @return A list of Hrefs
     */
    public List getLinkHRefs(boolean duplicate) {
        if (duplicate) {
            return this.htmlHandler.getAllLinkHRefs();
        }
        return this.htmlHandler.getLinkHRefs();
    }

    /**
     * Get Image src attributes
     * @param duplicate Whether you want duplicates
     * @return A list of src Attributes
     */
    public List getImageSrcs(boolean duplicate) {
        if (duplicate) {
            return this.htmlHandler.getAllImageSrcs();
        }
        return this.htmlHandler.getImageSrcs();
    }

    private Reader getReader(String uri) throws IOException {
        if (uri.startsWith("http:")) {
            // uri is url
            URLConnection connection = new URL(uri).openConnection();

            return new InputStreamReader(connection.getInputStream());
        }
        // uri is file
        return new FileReader(uri);
    }
}
