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

/* $Id: CrawlerConfiguration.java,v 1.8 2004/03/01 16:18:19 gregor Exp $  */

package org.apache.lenya.search.crawler;

import java.io.File;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.lenya.xml.DOMUtil;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.XPath;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * DOCUMENT ME!
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
            Document document = DocumentHelper.readDocument(configurationFile);
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
                "Usage: org.apache.lenya.search.crawler.CrawlerConfiguration crawler.xconf [-name <name>]");

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
