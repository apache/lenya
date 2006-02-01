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

package org.apache.lenya.search.crawler;

import java.io.File;
import java.io.IOException;

import org.apache.lenya.xml.DOMUtil;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.XPath;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Web-Crawler (it might make sense to replace this by Nutch)
 */
public class CrawlerConfiguration {
    static Category log = Category.getInstance(CrawlerConfiguration.class);
    private String configurationFilePath;
    private String base_url;
    private String user_agent;
    private String scope_url;
    private String uri_list;
    private String htdocs_dump_dir;
    private String robots_file;
    private String robots_domain;

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
            throw new RuntimeException(e);
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
            System.out.println("Crawler Config: Base URL: " + parameter);

            parameter = ce.getScopeURL();
            System.out.println("Crawler Config: Scope URL: " + parameter);

            parameter = ce.getUserAgent();
            System.out.println("Crawler Config: User Agent: " + parameter);

            parameter = ce.getURIList();
            System.out.println("Crawler Config: URI List: " + ce.resolvePath(parameter) + " (" + parameter + ")");

            parameter = ce.getHTDocsDumpDir();
            System.out.println("Crawler Config: HTDocs Dump Dir: " + ce.resolvePath(parameter) + " (" + parameter + ")");

            parameter = ce.getRobotsFile();
            if (parameter != null) {
                System.out.println("Crawler Config: Robots File: " + ce.resolvePath(parameter + " (" + parameter + ")"));
            }

            parameter = ce.getRobotsDomain();
            if (parameter != null) {
                System.out.println("Crawler Config: Robots Domain: " + parameter);
            }
        }
    }

    /**
     * Extract parameters from configuration
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
        if (du.elementExists(root, new XPath("robots"))) {
            robots_file = du.getAttributeValue(root, new XPath("robots/@src"));
            robots_domain = du.getAttributeValue(root, new XPath("robots/@domain"));
        }
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
     * Get URI list path
     *
     * @return URI list path
     */
    public String getURIList() {
        log.debug(".getURIList(): " + uri_list);

        return uri_list;
    }

    /**
     * Get URI list path as absolute path
     *
     * @return URI list path
     */
    public String getURIListResolved() {
        log.debug(".getURIList(): " + uri_list);

        return resolvePath(uri_list);
    }

    /**
     * Get htdocs-dump-dir/@src
     *
     * @return htdocs-dump-dir/@src
     */
    public String getHTDocsDumpDir() {
        log.debug(".getHTDocsDumpDir(): " + htdocs_dump_dir);

        return htdocs_dump_dir;
    }

    /**
     * Get htdocs-dump-dir/@src as absolute path
     *
     * @return htdocs-dump-dir/@src
     */
    public String getHTDocsDumpDirResolved() {

        return resolvePath(htdocs_dump_dir);
    }

    /**
     * Get robots/@src
     *
     * @return robots/@src
     */
    public String getRobotsFile() {
        log.debug(robots_file);

        return robots_file;
    }

    /**
     * Get robots/@src as absolute path
     *
     * @return robots/@src
     */
    public String getRobotsFileResolved() {
        log.debug(robots_file);

        return resolvePath(robots_file);
    }

    /**
     * Get robots/@domain
     *
     * @return robots/@domain
     */
    public String getRobotsDomain() {
        log.debug(robots_domain);

        return robots_domain;
    }

    /**
     * Resolve path
     *
     * @param path Original path
     *
     * @return Resolved path
     */
    public String resolvePath(String path) {

		// nothing to do if we already have an absolute pathname
		if ( new File(path) .isAbsolute() ) {
			return path;
		}

		// from the Java API doc:  "A canonical pathname is both absolute and unique."
		// however we may get an exception while converting a path to it's canonical form
		try {
			String configDir = new File(configurationFilePath) .getAbsoluteFile() .getParent();
			return new File(configDir, path) .getCanonicalPath();

		} catch (java.io.IOException e) {
			// FIXME: maybe this Exception should be thrown to the caller ?
			e.printStackTrace();
			return null;
		}

    }
}
