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
package org.apache.lenya.search.crawler;

import org.apache.avalon.excalibur.io.FileUtil;

import org.apache.lenya.xml.DOMParserFactory;
import org.apache.lenya.xml.DOMUtil;
import org.apache.lenya.xml.XPath;

import org.apache.log4j.Category;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
