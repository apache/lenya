/*
$Id: Sitemap.java,v 1.11 2003/08/28 14:08:12 egli Exp $
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
package org.apache.lenya.cms.cocoon.sitemap;

import org.dom4j.Document;
import org.dom4j.Element;

import org.dom4j.io.SAXReader;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2002.3.18
 */
public class Sitemap {
    private Document doc = null;

    /**
     * Creates a new Sitemap object.
     *
     * @param file DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Sitemap(File file) throws Exception {
        doc = new SAXReader().read("file:" + file.getAbsolutePath());
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Usage: root-sitemap pattern prefix sub-sitemap");

            return;
        }

        try {
            File sitemapFile = new File(args[0]);
            Sitemap sitemap = new Sitemap(sitemapFile);
            sitemap.mount(args[1], args[2], args[3]);
            sitemap.save(sitemapFile);
        } catch (Exception e) {
            System.err.println(".main(): " + e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void save(File file) throws Exception {
        java.io.FileWriter fileWriter = new java.io.FileWriter(file.getAbsolutePath());
        doc.write(fileWriter);
        fileWriter.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @param pattern DOCUMENT ME!
     * @param uri_prefix DOCUMENT ME!
     * @param src DOCUMENT ME!
     */
    public void mount(String pattern, String uri_prefix, String src) {
        Element pipelineElement = (Element) doc.selectSingleNode(
                "/map:sitemap/map:pipelines/map:pipeline");
        Element matchElement = org.dom4j.DocumentHelper.createElement("map:match").addAttribute("pattern",
                pattern);
        java.util.List list = pipelineElement.content();
        list.add(0, matchElement);
        matchElement.addElement("map:mount").addAttribute("uri-prefix", uri_prefix)
                    .addAttribute("src", src).addAttribute("check-reload", "true").addAttribute("reload-method",
            "synchron");
    }
}
