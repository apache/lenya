/*
 * $Id: HTML.java,v 1.7 2003/02/07 12:14:24 ah Exp $
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
package org.wyona.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.html.parser.ParserDelegator;


/**
 * http://developer.java.sun/developer/TechTips/1999/tt0923.html
 */
public class HTML {
    HTMLHandler htmlHandler;

    /**
     * Creates a new HTML object.
     *
     * @param uri DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public HTML(String uri) throws IOException {
        ParserDelegator pd = new ParserDelegator();
        htmlHandler = new HTMLHandler();
        pd.parse(getReader(uri), htmlHandler, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
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
        } catch (Exception e) {
            System.err.println(".main(): " + e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param duplicate DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getAnchorHRefs(boolean duplicate) {
        if (duplicate) {
            return htmlHandler.getAllAHRefs();
        } else {
            return htmlHandler.getAHRefs();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param duplicate DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getLinkHRefs(boolean duplicate) {
        if (duplicate) {
            return htmlHandler.getAllLinkHRefs();
        } else {
            return htmlHandler.getLinkHRefs();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param duplicate DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getImageSrcs(boolean duplicate) {
        if (duplicate) {
            return htmlHandler.getAllImageSrcs();
        } else {
            return htmlHandler.getImageSrcs();
        }
    }

    private Reader getReader(String uri) throws IOException {
        if (uri.startsWith("http:")) {
            // uri is url
            URLConnection connection = new URL(uri).openConnection();

            return new InputStreamReader(connection.getInputStream());
        } else {
            // uri is file
            return new FileReader(uri);
        }
    }
}
