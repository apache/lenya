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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import websphinx.RobotExclusion;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


/**
 * Crawl iteratively
 */
public class IterativeHTMLCrawler implements Configurable {
    private static final Logger log = Logger.getLogger(IterativeHTMLCrawler.class);

    java.util.Vector urlsToCrawl;
    java.util.TreeSet urlsToCrawlLowerCase;
    String uriList = "url_file.txt";
    String htdocsDumpDir = "html_dump";
    private String baseURL;
    private String rootURL;
    private String[] scopeURL;
    private String userAgent;
    private RobotExclusion robot;
    private String robotsFile;
    private String robotsDomain;
    private String configurationFilePath;

    /**
     * Command line interface
     * @param args Configuration file crawler.xconf
     */
    public void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: IterativeHTMLCrawler crawler.xconf");

            return;
        }

        try {
            if (args.length == 1) {
                this.configurationFilePath = args[0];
                try {
                    DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
                    Configuration configuration = builder.buildFromFile(this.configurationFilePath);
                    configure(configuration);
                } catch (final ConfigurationException e1) {
                	System.err.println("Cannot load crawler configuration!");
                } catch (final SAXException e1) {
                	System.err.println("Cannot load crawler configuration!");
                } catch (final IOException e1) {
                	System.err.println("Cannot load crawler configuration!");
                }
                new IterativeHTMLCrawler(new File(args[0])).crawl(new URL(this.baseURL), this.scopeURL[0]);
	    } else {
                System.err.println("Usage: IterativeHTMLCrawler crawler.xconf");
            }
        } catch (MalformedURLException e) {
            log.error("" + e);
        }
    }

    /**
     * Configuration of the IterativeHTMLCrawler
     * @param configuration The configuration
     * @throws ConfigurationException if an error occurs
     */
    public void configure(Configuration configuration)
        throws ConfigurationException {

    	this.baseURL = configuration.getChild("base-url").getAttribute("href");
    	this.scopeURL[0] = configuration.getChild("scope-url").getAttribute("href");
    	this.userAgent = configuration.getChild("user-agent").getValue();
    	this.uriList = configuration.getChild("uri-list").getAttribute("src");
    	this.htdocsDumpDir = configuration.getChild("htdocs-dump-dir").getAttribute("src");
    	this.robotsFile = configuration.getChild("robots").getAttribute("src");
    	this.robotsDomain = configuration.getChild("robots").getAttribute("domain");
    }

    /**
     * Creates a new IterativeHTMLCrawler object.
     * @param _uriList File where all dumped files will be listed
     * @param _htdocsDumpDir Directory where htdocs should be dumped
     * @param _userAgent User-agent for robots.txt
     */
    public IterativeHTMLCrawler(String _uriList, String _htdocsDumpDir, String _userAgent) {
        this.uriList = _uriList;
        this.htdocsDumpDir = _htdocsDumpDir;
        this.robot = new RobotExclusion(_userAgent);
    }

    /**
     * Creates a new IterativeHTMLCrawler object.
     * @param config Configuration File
     */
    public IterativeHTMLCrawler(File config) {
        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            Configuration configuration = builder.buildFromFile(config);
            configure(configuration);
        } catch (final ConfigurationException e) {
        	System.err.println("Cannot load crawler configuration! " + e.toString());
        } catch (final SAXException e) {
        	System.err.println("Cannot load crawler configuration! " + e.toString());
        } catch (final IOException e) {
        	System.err.println("Cannot load crawler configuration! " + e.toString());
        }

        this.robot = new RobotExclusion(this.userAgent);

        if (this.robotsFile != null && this.robotsDomain != null) {
            log.debug(this.robotsFile + " " + this.robotsDomain);
            this.robot.addLocalEntries(this.robotsDomain, new File(this.robotsFile));
        }
    }

    /**
     * Crawl
     * @param start Start crawling at this URL
     * @param scope Limit crawling to this scope
     */
    public void crawl(URL start, String scope) {
        this.scopeURL = new String[1];
        this.scopeURL[0] = scope;

        String seedURL = start.toString();
        this.rootURL = seedURL.substring(0, seedURL.indexOf("/", 8));

        this.urlsToCrawl = new java.util.Vector();
        this.urlsToCrawlLowerCase = new java.util.TreeSet();

        String currentURLPath = start.toString().substring(0, start.toString().lastIndexOf("/"));

        try {
            log.info("Start crawling at: " + start);

            if (addURL(start.getFile(), currentURLPath) != null) {
                dumpHTDoc(start);
            } else {
                log.warn("Start URL has not been dumped: " + start);
            }
        } catch (MalformedURLException e) {
            log.error("" + e);
        }

        int currentPosition = 0;

        while (currentPosition < this.urlsToCrawl.size()) {
            URL currentURL = (URL) this.urlsToCrawl.elementAt(currentPosition);
            currentURLPath = currentURL.toString().substring(0, currentURL.toString().lastIndexOf("/"));

            log.info("INFO: Current Array Size: " + this.urlsToCrawl.size() + ", Current Position: " + currentPosition + ", Current URL: " + currentURL.toString());


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
                    } catch (final MalformedURLException e) {
                        log.warn("" + e + " " + urlCandidate);
                    }
                }
            }

            currentPosition = currentPosition + 1;
        }

        log.info("Stop crawling at: " + this.urlsToCrawl.elementAt(this.urlsToCrawl.size()-1));

        // Write all crawled URLs into file
        try {
            File parent = new File(new File(this.uriList).getParent());
            if (!parent.isDirectory()) {
                parent.mkdirs();
                log.warn("Directory has been created: " + parent);
            }
            PrintWriter out = new PrintWriter(new FileOutputStream(this.uriList));

            for (int i = 0; i < this.urlsToCrawl.size(); i++) {
                out.println("" + this.urlsToCrawl.elementAt(i));
            }

            out.close();
        } catch (final FileNotFoundException e) {
            log.error("" + e);
        }
    }

    /**
     * Add URLs to crawl
     * @param urlCandidate URL to add
     * @param currentURLPath The current URL path
     * @return Added URL
     * @throws MalformedURLException if the URL is invalid
     */
    public URL addURL(String urlCandidate, String currentURLPath)
        throws MalformedURLException {
        URL url = new URL(parseHREF(urlCandidate, urlCandidate.toLowerCase(Locale.ENGLISH), currentURLPath));
        //completeURL(currentURL,urlCandidate)  new URL(currentURLPath+"/"+urlCandidate);

        if (filterURL(urlCandidate, currentURLPath, this.urlsToCrawlLowerCase)) {
            if (!this.robot.disallowed(url)) {
                if (url.getQuery() == null) {
                    this.urlsToCrawl.add(url);
                    this.urlsToCrawlLowerCase.add(url.toString().toLowerCase(Locale.ENGLISH));
                    log.debug("URL added: " + url);
                } else {
                    log.info("Don't crawl URLs with query string: " + url);
                }

                return url;
            }
            log.info("Disallowed by robots.txt: " + urlCandidate);
        }

        return null;
    }

    /**
     * Parse a URL
     * @param urlString URL to parse
     * @return a list of URL
     */
    public List parsePage(String urlString) {
    	HttpURLConnection httpCon = null;

    	try {
            URL currentURL = new URL(urlString);
            httpCon = (HttpURLConnection) currentURL.openConnection();
            httpCon.setRequestProperty("User-Agent", "Lenya Lucene Crawler");
            httpCon.connect();

            if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String contentType = httpCon.getContentType();

                if (contentType.indexOf("text/html") != -1) {
                    return handleHTML(httpCon);
                } else if (contentType.indexOf("application/pdf") != -1) {
                    handlePDF(httpCon);
                }
            }

        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            httpCon.disconnect();
        }
        return null;
    }

    /**
     * Returns a list of links for a HTTP connection
     * @param httpCon The HTTP connection
     * @return The list of links
     * @throws IOException if an IO error occurs
     */
    public static List handleHTML(HttpURLConnection httpCon) throws IOException {
        ContentHandler handler = new HTMLHandler();
        handler.parse(httpCon.getInputStream());

        if (handler.getRobotFollow()) {
            List links = handler.getLinks();
            return links;
        }

        return null;
    }

    /**
     * Parse PDF for links
     * @param httpCon The HTTP connection
     */
    public void handlePDF(HttpURLConnection httpCon) {
        log.debug(".handlePDF(): Not handled yet!");
    }

    /**
     * Checks if a URL is in the crawling scope
     * @param url The URL
     * @param currentURLPath The current URL path
     * @param links The list of known links
     * @return whether the URL is in the crawling scope
     */
    public boolean filterURL(String url, String currentURLPath, TreeSet links) {
        String urlLowCase = url.toLowerCase(Locale.ENGLISH);

        if (!(urlLowCase.startsWith("http://") || urlLowCase.startsWith("https://"))) {
            url = parseHREF(url, urlLowCase, currentURLPath);

            if (url != null) {
                urlLowCase = url.toLowerCase(Locale.ENGLISH);
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
     * @param url URL from href
     * @param urlLowCase url is lower case
     * @param currentURLPath URL of current page
     * @return Completed URL
     */
    public String parseHREF(String url, String urlLowCase, String currentURLPath) {
        if (urlLowCase.startsWith("http://") || urlLowCase.startsWith("https://")) {
            return url;
        }

        // Looks for incomplete URL and completes them
        if (urlLowCase.startsWith("/")) {
            url = this.rootURL + url;
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
     * Checks if a URL is in the crawling scope
     * @param url The URL
     * @return whether the URL is in the crawling scope
     */
    public boolean inScope(String url) {
        for (int i = 0; i < this.scopeURL.length; i++) {
            if (url.startsWith(this.scopeURL[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the complete URL for a child
     * @param parent The parent URL
     * @param child The child
     * @return The complete URL
     * @throws MalformedURLException if the URL was not valid
     * FIXME this looks wrong
     */
    public URL completeURL(URL parent, String child) throws MalformedURLException {
        return parent;
    }

    /**
     * Dumps the content of a URL to the dump directory
     * @param url The URL
     */
    public void dumpHTDoc(URL url) {
        InputStream in = null;
        FileOutputStream out = null;
        
        String ext = getExtension(url);

        String filename = this.htdocsDumpDir + url.getFile();
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
                in = httpConnection.getInputStream();

                out = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead = -1;
                while ((bytesRead = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }

                httpConnection.disconnect();

                log.info("URL dumped: " + url + " (" + file + ")");
            } catch (final Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                try {
                    if (in !=null)
                        in.close();
                    if (out != null)
                        out.close();
                } catch (final IOException e1) {
                    log.error("Failed to close stream: " +e1.toString());
                }
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
     * Returns the extension of a URL
     * @param url The URL
     * @return The extension
     */
    public String getExtension(URL url) {
        return getExtension(new File(url.getPath()));
    }

    /**
     * Returns the extension of a file
     * @param file The file
     * @return The extension
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
