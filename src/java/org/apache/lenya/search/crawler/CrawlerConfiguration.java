/*
 * $Id: CrawlerConfiguration.java,v 1.1 2003/03/18 15:06:22 michi Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
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
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.search.crawler;

import org.lenya.xml.DOMParserFactory;
import org.lenya.xml.DOMUtil;
import org.lenya.xml.XPath;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.avalon.excalibur.io.FileUtil;

import org.apache.log4j.Category;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 */
public class CrawlerConfiguration {
    static Category log = Category.getInstance(CrawlerConfiguration.class);

    private String configurationFilePath;
    private String base_url;
    private String user_agent;
    private String scope_url;
    private String uri_list;
    private String htdocs_dump_dir;

    /**
     * Creates a new CrawlerConfiguration object.
     *
     * @param configurationFilePath DOCUMENT ME!
     */
    public CrawlerConfiguration(String configurationFilePath) {
        this.configurationFilePath = configurationFilePath;

        File configurationFile = new File(configurationFilePath);

        try {
            Document document = new DOMParserFactory().getDocument(configurationFilePath);
            configure(document.getDocumentElement());
        } catch (Exception e) {
            log.error("Cannot load publishing configuration! ", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println(
                "Usage: org.lenya.search.crawler.CrawlerConfiguration crawler.xconf [-name <name>]");

            return;
        }

        CrawlerConfiguration ce = new CrawlerConfiguration(args[0]);
        String parameter;

        String name = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-name")) {
                if ((i + 1) < args.length) {
                    name = args[i + 1];
                }
            }
        }

        if (name != null) {
            if (name.equals("htdocs-dump-dir")) {
                parameter = ce.getHTDocsDumpDir();
                System.out.println(ce.resolvePath(parameter));
            } else {
                System.out.println("No such element: " + name);
            }
        } else {
            parameter = ce.getBaseURL();
            System.out.println(parameter);

            parameter = ce.getScopeURL();
            System.out.println(parameter);

            parameter = ce.getUserAgent();
            System.out.println(parameter);

            parameter = ce.getURIList();
            System.out.println(parameter);
            System.out.println(ce.resolvePath(parameter));

            parameter = ce.getHTDocsDumpDir();
            System.out.println(parameter);
            System.out.println(ce.resolvePath(parameter));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void configure(Element root) throws Exception {
        DOMUtil du = new DOMUtil();

        base_url = du.getAttributeValue(root, new XPath("base-url/@href"));
        scope_url = du.getAttributeValue(root, new XPath("scope-url/@href"));
        user_agent = du.getElementValue(root, new XPath("user-agent"));
        uri_list = du.getAttributeValue(root, new XPath("uri-list/@src"));
        htdocs_dump_dir = du.getAttributeValue(root, new XPath("htdocs-dump-dir/@src"));
/*
        scope_url = configuration.getChild("scope-url").getAttribute("href");
        user_agent = configuration.getChild("user-agent").getValue(null);
        uri_list = configuration.getChild("uri-list").getAttribute("src");
        htdocs_dump_dir = configuration.getChild("htdocs-dump-dir").getAttribute("src");
*/
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getBaseURL() {
        log.debug(".getBaseURL(): " + base_url);

        return base_url;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getScopeURL() {
        log.debug(".getScopeURL(): " + scope_url);

        return scope_url;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getUserAgent() {
        log.debug(".getUserAgent(): " + user_agent);

        return user_agent;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getURIList() {
        log.debug(".getURIList(): " + uri_list);

        return uri_list;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHTDocsDumpDir() {
        log.debug(".getHTDocsDumpDir(): " + htdocs_dump_dir);

        return htdocs_dump_dir;
    }

    /**
     * DOCUMENT ME!
     *
     * @param path DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String resolvePath(String path) {
        if (path.indexOf(File.separator) == 0) {
            return path;
        }

        return FileUtil.catPath(configurationFilePath, path);
    }
}
