/*
 * $Id: Sitemap.java,v 1.5 2003/03/04 17:46:34 gregor Exp $
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
package org.lenya.cms.cocoon.sitemap;

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
    private File file = null;
    private Document doc = null;

    /**
     * Creates a new Sitemap object.
     *
     * @param file DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Sitemap(File file) throws Exception {
        this.file = file;
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
