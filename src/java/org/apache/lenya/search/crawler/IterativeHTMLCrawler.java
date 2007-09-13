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

/* $Id: IterativeHTMLCrawler.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.search.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.StringTokenizer;

import websphinx.RobotExclusion;

import org.apache.log4j.Logger;

/**
 * Crawl iteratively
 */
public class IterativeHTMLCrawler {
    static Logger log = Logger.getLogger(IterativeHTMLCrawler.class);

    java.util.Vector urlsToCrawl;
    java.util.TreeSet urlsToCrawlLowerCase;
    String url_list_file = "url_file.txt";
    String html_dump_directory = "html_dump";
    private String rootURL;
    private String[] scopeURL;
    private RobotExclusion robot;

    /**
     * Command line interface
     *
     * @param args Configuration file crawler.xconf
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: IterativeHTMLCrawler crawler.xconf");

            return;
        }

        try {
            if (args.length == 1) {
                CrawlerConfiguration ce = new CrawlerConfiguration(args[0]);
                new IterativeHTMLCrawler(new File(args[0])).crawl(new URL(ce.getBaseURL()), ce.getScopeURL());
	    } else {
                System.err.println("Usage: IterativeHTMLCrawler crawler.xconf");
            }
        } catch (MalformedURLException e) {
            log.error("Malformed URL: ", e);
        }
    }

    /**
     * Creates a new IterativeHTMLCrawler object.
     *
     * @param url_list_file File where all dumped files will be listed
     * @param html_dump_directory Directory where htdocs should be dumped
     * @param userAgent User-agent for robots.txt
     */
    public IterativeHTMLCrawler(String url_list_file, String html_dump_directory, String userAgent) {
        this.url_list_file = url_list_file;
        this.html_dump_directory = html_dump_directory;

        robot = new RobotExclusion(userAgent);
    }

    /**
     * Creates a new IterativeHTMLCrawler object.
     * @param config Configuration File
     */
    public IterativeHTMLCrawler(File config) {
        CrawlerConfiguration ce = new CrawlerConfiguration(config.getAbsolutePath());


        this.url_list_file = ce.getURIListResolved();
        log.debug("URI list file: " + this.url_list_file);

        this.html_dump_directory = ce.getHTDocsDumpDirResolved();
        log.debug("HTDocs Dump Dir: " + this.html_dump_directory);

        robot = new RobotExclusion(ce.getUserAgent());

        String robots_file = ce.getRobotsFileResolved();
        log.debug("Robots File: " + robots_file);
        String robots_domain = ce.getRobotsDomain();
        if (robots_file != null && robots_domain != null) {
            log.debug(robots_file + " " + robots_domain);
            robot.addLocalEntries(robots_domain, new File(robots_file));
        }
    }

    /**
     * Crawl
     *
     * @param start Start crawling at this URL
     * @param scope Limit crawling to this scope
     */
    public void crawl(URL start, String scope) {
        scopeURL = new String[1];
        scopeURL[0] = scope;

        String seedURL = start.toString();
        this.rootURL = seedURL.substring(0, seedURL.indexOf("/", 8));

        urlsToCrawl = new java.util.Vector();
        urlsToCrawlLowerCase = new java.util.TreeSet();

        String currentURLPath = start.toString().substring(0, start.toString().lastIndexOf("/"));

        try {
            log.info("Start crawling at: " + start);

            if (addURL(start.getFile(), currentURLPath) != null) {
                dumpHTDoc(start);
            } else {
                log.warn("Start URL has not been dumped: " + start);
            }
        } catch (MalformedURLException e) {
            log.error("Malformed URL: ", e);
        }

        int currentPosition = 0;

        while (currentPosition < urlsToCrawl.size()) {
            URL currentURL = (URL) urlsToCrawl.elementAt(currentPosition);
            currentURLPath = currentURL.toString().substring(0, currentURL.toString().lastIndexOf("/"));

            log.info("INFO: Current Array Size: " + urlsToCrawl.size() + ", Current Position: " + currentPosition + ", Current URL: " + currentURL.toString());


            java.util.List urlsWithinPage = parsePage(currentURL.toString());

            if (urlsWithinPage != null) {
                java.util.Iterator iterator = urlsWithinPage.iterator();

                while (iterator.hasNext()) {
                    String urlCandidate = (String) iterator.next();

                    try {
                        URL urlToCrawl = null;

                        if ((urlToCrawl = addURL(urlCandidate, currentURLPath)) != null) {
                            dumpHTDoc(urlToCrawl);
                        }
                    } catch (MalformedURLException e) {
                        log.warn(e + " " + urlCandidate);
                    }
                }
            }

            currentPosition = currentPosition + 1;
        }

        log.info("Stop crawling at: " + urlsToCrawl.elementAt(urlsToCrawl.size()-1));



        // Write all crawled URLs into file
        try {
            File parent = new File(new File(url_list_file).getParent());
            if (!parent.isDirectory()) {
                parent.mkdirs();
                log.warn("Directory has been created: " + parent);
            }
            java.io.PrintWriter out = new java.io.PrintWriter(new FileOutputStream(url_list_file));

            for (int i = 0; i < urlsToCrawl.size(); i++) {
                out.println("" + urlsToCrawl.elementAt(i));
            }

            out.close();
        } catch (java.io.FileNotFoundException e) {
            log.error("File not found: ", e);
        }
    }

    /**
     * Add URLs to crawl
     *
     * @param urlCandidate DOCUMENT ME!
     * @param currentURLPath DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws MalformedURLException DOCUMENT ME!
     */
    public URL addURL(String urlCandidate, String currentURLPath)
        throws MalformedURLException {
        URL url = new URL(parseHREF(urlCandidate, urlCandidate.toLowerCase(), currentURLPath));
        //completeURL(currentURL,urlCandidate)  new URL(currentURLPath+"/"+urlCandidate);

        if (filterURL(urlCandidate, currentURLPath, urlsToCrawlLowerCase)) {
            if (!robot.disallowed(url)) {
                if (url.getQuery() == null) {
                    urlsToCrawl.add(url);
                    urlsToCrawlLowerCase.add(url.toString().toLowerCase());
                    log.debug("URL added: " + url);
                } else {
                    log.info("Don't crawl URLs with query string: " + url);
                }

                return url;
            } else {
                log.info("Disallowed by robots.txt: " + urlCandidate);
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param urlString DOCUMENT ME!
     *
     * @return ok, 404
     */
    public List parsePage(String urlString) {
        //String status = "ok";

        try {
            URL currentURL = new java.net.URL(urlString);
            //String currentURLPath = urlString.substring(0, urlString.lastIndexOf("/"));
            HttpURLConnection httpCon = (HttpURLConnection) currentURL.openConnection();

            httpCon.setRequestProperty("User-Agent", "Lenya Lucene Crawler");

            httpCon.connect();

            //long lastModified = httpCon.getLastModified();

            if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String contentType = httpCon.getContentType();

                if (contentType.indexOf("text/html") != -1) {
                    return handleHTML(httpCon);
                } else if (contentType.indexOf("application/pdf") != -1) {
                    handlePDF(httpCon);
                } else {
                    //status = "Not an excepted content type : " + contentType;
                }
            } else {
                //status = "bad";
            }

            httpCon.disconnect();
        } catch (MalformedURLException mue) {
            log.debug("status=" + mue);
        } catch (UnknownHostException uh) {
            log.debug("status=" + uh); // Mark as a bad URL
        } catch (IOException ioe) {
            log.debug("status=" + ioe); // Mark as a bad URL
        } catch (Exception e) {
            log.debug("status=" + e); // Mark as a bad URL
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param httpCon DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws java.io.IOException DOCUMENT ME!
     */
    public static java.util.List handleHTML(HttpURLConnection httpCon)
        throws java.io.IOException {
        ContentHandler handler = new HTMLHandler();
        handler.parse(httpCon.getInputStream());

        if (handler.getRobotFollow()) {
            java.util.List links = handler.getLinks();

            return links;
        }

        return null;
    }

    /**
     * Parse PDF for links
     *
     * @param httpCon DOCUMENT ME!
     */
    public void handlePDF(HttpURLConnection httpCon) {
        log.debug(".handlePDF(): Not handled yet!");
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     * @param currentURLPath DOCUMENT ME!
     * @param links DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean filterURL(String url, String currentURLPath, java.util.TreeSet links) {
        String urlLowCase = url.toLowerCase();

        if (!(urlLowCase.startsWith("http://") || urlLowCase.startsWith("https://"))) {
            url = parseHREF(url, urlLowCase, currentURLPath);

            if (url != null) {
                urlLowCase = url.toLowerCase();
            }
        }

        if ((url != null) && inScope(url)) {
            if (!links.contains(urlLowCase)) {
                return true;
            }
        } else {
            log.debug("Not in scope: " + url);
        }

        return false;
    }

    /**
     * Parse URL and complete if necessary
     *
     * @param url URL from href
     * @param urlLowCase url is lower case
     * @param currentURLPath URL of current page
     *
     * @return Completed URL
     */
    public String parseHREF(String url, String urlLowCase, String currentURLPath) {
        if (urlLowCase.startsWith("http://") || urlLowCase.startsWith("https://")) {
            return url;
        }

        // Looks for incomplete URL and completes them
        if (urlLowCase.startsWith("/")) {
            url = rootURL + url;
        } else if (urlLowCase.startsWith("./")) {
            url = currentURLPath + url.substring(1, url.length());
        } else if (urlLowCase.startsWith("../")) {
            int back = 1;

            // Count number of "../"s
            while (urlLowCase.indexOf("../", back * 3) != -1)
                back++;

            int pos = currentURLPath.length();
            int count = back;

            while (count-- > 0) {
                pos = currentURLPath.lastIndexOf("/", pos) - 1;
            }

            String dotsRemoved = url.substring(3 * back, url.length());
            if (dotsRemoved.length() > 0 && dotsRemoved.charAt(0) == '.') {
                log.error("Parsing failed: " + url + " (" + currentURLPath + ")");
                url = null;
            } else {
                url = currentURLPath.substring(0, pos + 2) + dotsRemoved;
            }
        } else if (urlLowCase.startsWith("javascript:")) {
            // handle javascript:...
            log.debug("\"javascript:\" is not implemented yet!");
            url = null;
        } else if (urlLowCase.startsWith("#")) {
            log.debug("\"#\" (anchor) will be ignored!");

            // internal anchor... ignore.
            url = null;
        } else if (urlLowCase.startsWith("mailto:")) {
            log.debug("\"mailto:\" is not a URL to be followed!");

            // handle mailto:...
            url = null;
        } else if (urlLowCase.equals("")) {
            // handle empty href's
            url = null;
        } else {
            url = currentURLPath + "/" + url;
        }

        // strip anchor if exists otherwise crawler may index content multiple times
        // links to the same url but with unique anchors would be considered unique
        // by the crawler when they should not be
        if (url != null) {
            int i;

            if ((i = url.indexOf("#")) != -1) {
                url = url.substring(0, i);
            }
        }


        return url;
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean inScope(String url) {
        for (int i = 0; i < scopeURL.length; i++) {
            if (url.startsWith(scopeURL[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param child DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws MalformedURLException DOCUMENT ME!
     */
    public URL completeURL(URL parent, String child) throws MalformedURLException {
        return parent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     */
    public void dumpHTDoc(URL url) {
        String ext = getExtension(url);

        String filename = html_dump_directory + url.getFile();
        File file = new File(filename);

        if (filename.charAt(filename.length() - 1) == '/') {
            file = new File(filename + "index.html");
            ext = getExtension(file);
        }

        if (ext.equals("html") || ext.equals("htm") || ext.equals("txt") || ext.equals("pdf")) {
            try {
                File parent = new File(file.getParent());

                if (!parent.exists()) {
                    parent.mkdirs();
                }

                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                java.io.InputStream in = httpConnection.getInputStream();

		FileOutputStream out = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead = -1;
                while ((bytesRead = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
                out.close();

/*
                BufferedInputStream bin = new BufferedInputStream(in);
                BufferedReader reader = new BufferedReader(new InputStreamReader(bin));

                java.io.FileWriter fw = new java.io.FileWriter(file);
                int i;

                while ((i = reader.read()) != -1) {
                    fw.write(i);
                }

                fw.close();

                bin.close();
*/
                in.close();
                httpConnection.disconnect();

                log.info("URL dumped: " + url + " (" + file + ")");
            } catch (Exception e) {
                log.error("" + e);
                log.error("URL not dumped: " + url);
            }
        } else {
            log.info("URL not dumped: " + url);
        }
    }

    /**
     *
     */
/*
    public void saveToFile(String filename, byte[] bytes)
        throws FileNotFoundException, IOException {
        File file = new File(filename);

        if (filename.charAt(filename.length() - 1) == '/') {
            file = new File(filename + "index.html");
        }

        File parent = new File(file.getParent());

        if (!parent.exists()) {
            log.warn("Directory will be created: " + parent.getAbsolutePath());
            parent.mkdirs();
        }

        FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
        out.write(bytes);
        out.close();
    }
*/

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getExtension(URL url) {
        return getExtension(new File(url.getPath()));
    }

    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getExtension(File file) {
        StringTokenizer st = new StringTokenizer(file.getPath(), ".");
        String extension = null;

        while (st.hasMoreElements()) {
            extension = st.nextToken();
        }
        return extension;
    }
}
