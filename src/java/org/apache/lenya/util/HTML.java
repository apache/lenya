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
