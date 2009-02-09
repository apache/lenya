/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.cms.cocoon.generation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.Constants;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.excalibur.source.Source;
import org.apache.lenya.cms.publication.Repository;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 *Generates a list of links that are reachable from the src and their status.
 * 
 * <pre>
 *  &lt;map:generator name=&quot;linkStatus&quot; src=&quot;org.apache.lenya.cms.cocoon.generation.LinkStatusGenerator&quot;/&gt;
 *   &lt;map:generate type=&quot;linkStatus&quot; src=&quot;/{pubid}/{area}/{doc-id}.html&quot;&gt;
 *      &lt;map:parameter name=&quot;depth&quot; value=&quot;1&quot;/&gt;
 *   &lt;/map:generate&gt;
 * </pre>
 **/

public class LinkStatusGenerator extends ServiceableGenerator implements Recyclable, Configurable {

    /** The URI of the namespace of this generator. */
    protected static final String URI = "http://apache.org/cocoon/linkstatus/2.0";

    /** The namespace prefix for this namespace. */
    protected static final String PREFIX = "linkstatus";

    /* Node and attribute names */
    protected static final String TOP_NODE_NAME = "linkstatus";
    protected static final String LINK_NODE_NAME = "link";

    protected static final String HREF_ATTR_NAME = "href";
    protected static final String REFERRER_ATTR_NAME = "referrer";
    protected static final String CONTENT_ATTR_NAME = "content";
    protected static final String STATUS_ATTR_NAME = "status";
    protected static final String MESSAGE_ATTR_NAME = "message";

    protected AttributesImpl attributes;

    /**
     * Config element name specifying expected link content-typ.
     * <p>
     * Its value is <code>link-content-type</code>.
     * </p>
     * 
     * @since
     */
    public final static String LINK_CONTENT_TYPE_CONFIG = "link-content-type";

    /**
     * Default value of <code>link-content-type</code> configuration value.
     * <p>
     * Its value is <code>application/x-cocoon-links</code>.
     * </p>
     * 
     * @since
     */
    public final String LINK_CONTENT_TYPE_DEFAULT = "application/x-cocoon-links";

    /**
     * Config element name specifying query-string appendend for requesting links of an URL.
     * <p>
     * Its value is <code>link-view-query</code>.
     * </p>
     * 
     * @since
     */
    public final static String LINK_VIEW_QUERY_CONFIG = "link-view-query";
    /**
     * Default value of <code>link-view-query</code> configuration value.
     * <p>
     * Its value is <code>?cocoon-view=links</code>.
     * </p>
     * 
     * @since
     */
    public final static String LINK_VIEW_QUERY_DEFAULT = "cocoon-view=links";

    /**
     * Config element name specifying excluding regular expression pattern.
     * <p>
     * Its value is <code>exclude</code>.
     * </p>
     * 
     * @since
     */
    public final static String EXCLUDE_CONFIG = "exclude";

    /**
     * Config element name specifying including regular expression pattern.
     * <p>
     * Its value is <code>include</code>.
     * </p>
     * 
     * @since
     */
    public final static String INCLUDE_CONFIG = "include";

    /**
     * Config element name specifying http header value for user-Agent.
     * <p>
     * Its value is <code>user-agent</code>.
     * </p>
     * 
     * @since
     */
    public final static String USER_AGENT_CONFIG = "user-agent";
    /**
     * Default value of <code>user-agent</code> configuration value.
     * 
     * @see org.apache.cocoon.Constants#COMPLETE_NAME
     * @since
     */
    public final static String USER_AGENT_DEFAULT = Constants.COMPLETE_NAME;

    /**
     * Config element name specifying http header value for accept.
     * <p>
     * Its value is <code>accept</code>.
     * </p>
     * 
     * @since
     */
    public final static String ACCEPT_CONFIG = "accept";
    /**
     * Default value of <code>accept</code> configuration value.
     * <p>
     * Its value is <code>* / *</code>
     * </p>
     * 
     * @since
     */
    public final static String ACCEPT_DEFAULT = "*/*";

    private String linkViewQuery = LINK_VIEW_QUERY_DEFAULT;
    private String linkContentType = LINK_CONTENT_TYPE_DEFAULT;
    private HashSet excludeCrawlingURL;
    private HashSet includeCrawlingURL;

    private HashSet crawled;
    private HashSet linksToProcess;

    /** The depth parameter determines how deep the EnhancedLinkStatusGenerator should delve. */
    protected int depth = 1;

    protected Source inputSource;
    String src;
    private Repository repository;

    private org.apache.lenya.cms.publication.Session session;

    /**
     * Stores links to process and the referrer links
     */
    private static class Link {
        private String uri;
        private String referrer;
        private int linkDepth;

        public Link(String uri, String referrer, int linkDepth) {
            this.uri = uri;
            this.referrer = referrer;
            this.linkDepth = linkDepth;
        }

        public String getURI() {
            return uri;
        }

        public String getReferrer() {
            return referrer;
        }

        public int getDepth() {
            return linkDepth;
        }

        public boolean equals(Link l) {
            return uri.equals(l.getURI());
        }
    }

    /**
     * Configure the crawler component.
     * <p>
     * Configure can specify which URI to include, and which URI to exclude from crawling. You
     * specify the patterns as regular expressions.
     * </p>
     * <p>
     * Morover you can configure the required content-type of crawling request, and the query-string
     * appended to each crawling request.
     * </p>
     * 
     * <pre>
     * &lt;tt&gt;
     * &lt;include&gt;.*\.html?&lt;/include&gt; or &lt;include&gt;.*\.html?, .*\.xsp&lt;/include&gt;
     * &lt;exclude&gt;.*\.gif&lt;/exclude&gt; or &lt;exclude&gt;.*\.gif, .*\.jpe?g&lt;/exclude&gt;
     * &lt;link-content-type&gt; application/x-cocoon-links &lt;/link-content-type&gt;
     * &lt;link-view-query&gt; ?cocoon-view=links &lt;/link-view-query&gt;
     * &lt;user-agent&gt; Cocoon &lt;/user-agent&gt;
     * &lt;accept&gt; text/xml &lt;/accept&gt;
     * &lt;/tt&gt;
     * </pre>
     * 
     * @param configuration XML configuration of this avalon component.
     * @exception ConfigurationException is throwing if configuration is invalid.
     * @since
     */
    public void configure(Configuration configuration) throws ConfigurationException {

        Configuration[] children;
        children = configuration.getChildren(INCLUDE_CONFIG);
        if (children.length > 0) {
            includeCrawlingURL = new HashSet();
            for (int i = 0; i < children.length; i++) {
                String pattern = children[i].getValue();
                try {
                    String params[] = StringUtils.split(pattern, ", ");
                    for (int index = 0; index < params.length; index++) {
                        String tokenized_pattern = params[index];
                        this.includeCrawlingURL.add(new RE(tokenized_pattern));
                    }
                } catch (RESyntaxException rese) {
                    getLogger().error("Cannot create including regular-expression for " + pattern,
                            rese);
                }
            }
        }

        children = configuration.getChildren(EXCLUDE_CONFIG);
        if (children.length > 0) {
            excludeCrawlingURL = new HashSet();
            for (int i = 0; i < children.length; i++) {
                String pattern = children[i].getValue();
                try {
                    String params[] = StringUtils.split(pattern, ", ");
                    for (int index = 0; index < params.length; index++) {
                        String tokenized_pattern = params[index];
                        this.excludeCrawlingURL.add(new RE(tokenized_pattern));
                    }
                } catch (RESyntaxException rese) {
                    getLogger().error("Cannot create excluding regular-expression for " + pattern,
                            rese);
                }
            }
        } else {
            excludeCrawlingURL = new HashSet();
            setDefaultExcludeFromCrawling();
        }

        Configuration child;
        String value;
        child = configuration.getChild(LINK_CONTENT_TYPE_CONFIG, false);
        if (child != null) {
            value = child.getValue();
            if (value != null && value.length() > 0) {
                this.linkContentType = value.trim();
            }
        }
        child = configuration.getChild(LINK_VIEW_QUERY_CONFIG, false);
        if (child != null) {
            value = child.getValue();
            if (value != null && value.length() > 0) {
                this.linkViewQuery = value.trim();
            }
        }
    }

    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
            throws ProcessingException, SAXException, IOException {

        Request request = ObjectModelHelper.getRequest(objectModel);
        this.session = this.repository.getSession(request);

        super.setup(resolver, objectModel, src, par);
        this.src = src;
        this.depth = par.getParameterAsInteger("depth", 1);

        /* Create a reusable attributes for creating nodes */
        this.attributes = new AttributesImpl();
    }

    /**
     * Generate XML data.
     * 
     * @throws SAXException if an error occurs while outputting the document
     * @throws ProcessingException if the requsted URI wasn't found
     */
    public void generate() throws SAXException, ProcessingException {

        crawled = new HashSet();
        linksToProcess = new HashSet();

        // this first node should be handled as a cocoon source
        String root = this.src;
        URL tempurl = null;
        linksToProcess.add(new Link(root, "", 0));

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("crawl URL " + root);
        }

        this.contentHandler.startDocument();
        this.contentHandler.startPrefixMapping(PREFIX, URI);

        attributes.clear();
        super.contentHandler.startElement(URI, TOP_NODE_NAME, PREFIX + ':' + TOP_NODE_NAME,
                attributes);

        while (linksToProcess.size() > 0) {
            Iterator i = linksToProcess.iterator();

            if (i.hasNext()) {
                // fetch a URL
                Link link = (Link) i.next();
                String uri = link.getURI();
                int referrerDepth = link.getDepth();
                // remove it from the to-do list
                linksToProcess.remove(link);
                String new_url_link = processURL(uri, link.getReferrer(), referrerDepth);

                // calc all links from this url
                if (new_url_link != null && referrerDepth < this.depth) {

                    List url_links = getLinksFromConnection(new_url_link, uri, referrerDepth);
                    if (url_links != null) {
                        // add links of this url to the to-do list
                        linksToProcess.addAll(url_links);
                    }
                }
            }
        }

        super.contentHandler.endElement(URI, TOP_NODE_NAME, PREFIX + ':' + TOP_NODE_NAME);
        this.contentHandler.endPrefixMapping(PREFIX);
        this.contentHandler.endDocument();
    }

    /**
     * Default exclude patterns.
     * <p>
     * By default URLs matching following patterns are excluded:
     * </p>
     * <ul>
     * <li>.*\\.gif(\\?.*)?$ - exclude gif images</li>
     * <li>.*\\.png(\\?.*)?$ - exclude png images</li>
     * <li>.*\\.jpe?g(\\?.*)?$ - exclude jpeg images</li>
     * <li>.*\\.js(\\?.*)?$ - exclude javascript</li>
     * <li>.*\\.css(\\?.*)?$ - exclude cascaded stylesheets</li>
     * </ul>
     * 
     * @since
     */
    private void setDefaultExcludeFromCrawling() {
        String[] EXCLUDE_FROM_CRAWLING_DEFAULT = { ".*\\.gif(\\?.*)?$", ".*\\.png(\\?.*)?$",
                ".*\\.jpe?g(\\?.*)?$", ".*\\.js(\\?.*)?$", ".*\\.css(\\?.*)?$", ".*\\?.*",
                ".*\\@.*" };

        for (int i = 0; i < EXCLUDE_FROM_CRAWLING_DEFAULT.length; i++) {
            String pattern = EXCLUDE_FROM_CRAWLING_DEFAULT[i];
            try {
                excludeCrawlingURL.add(new RE(pattern));
            } catch (RESyntaxException rese) {
                getLogger()
                        .error("Cannot create excluding regular-expression for " + pattern, rese);
            }
        }
    }

    /**
     * Retrieve a list of links of a url
     * 
     * @param url_link_string url for requesting links, it is assumed that url_link_string queries
     *            the cocoon view links, ie of the form
     *            <code>http://host/foo/bar?cocoon-view=links</code>
     * @param url_of_referrer base url of which links are requested, ie of the form
     *            <code>http://host/foo/bar</code>
     * @return List of links from url_of_referrer, as result of requesting url url_link_string
     */
    protected List getLinksFromConnection(String url_link_string, String url_of_referrer,
            int referrerDepth) {
        List url_links = null;
        BufferedReader br = null;
        try {

            url_links = new ArrayList();
            url_link_string = "cocoon:/" + url_link_string;

            inputSource = super.resolver.resolveURI(url_link_string);
            InputStream is = inputSource.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            // content is supposed to be a list of links,
            // relative to current URL
            String line;
            String referrer = url_of_referrer;

            while ((line = br.readLine()) != null) {
                String new_url = line;
                boolean add_url = true;
                // don't add new_url twice
                if (add_url) {
                    add_url &= !url_links.contains(new_url);
                }

                // don't add new_url if it has been crawled already
                if (add_url) {
                    add_url &= !crawled.contains(new_url);
                }

                Link new_link = new Link(line, referrer, referrerDepth + 1);
                if (add_url) {
                    add_url &= !linksToProcess.contains(new_link);
                }

                // don't add if is not matched by existing include definition
                if (add_url) {
                    add_url &= isIncludedURL(new_url);
                }

                // don't add id matched by existing exclude definition
                if (add_url) {
                    add_url &= !(isExcludedURL(new_url));
                }

                if (add_url) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Add URL: " + new_url);
                    }
                    url_links.add(new_link);
                }
            }
            // now we have a list of URL which should be examined

        } catch (IOException ioe) {
            getLogger().warn("Problems get links of " + url_link_string, ioe);
        } finally {
            // explictly close the stream
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException ignored) {
                }
            }
        }
        return url_links;
    }

    /**
     * Generate xml attributes of a url, calculate url for retrieving links
     * 
     * @param url to process
     * @param referrer of the url
     * @return String url for retrieving links, or null if url is an excluded-url, and not an
     *         included-url.
     */
    protected String processURL(String uri, String referrer, int referrerDepth) throws SAXException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("getLinks URL " + uri);
        }

        String result = null;

        // don't try to investigate a url which has been crawled already
        if (crawled.contains(uri)) {
            return null;
        }

        // TODO: need to respect robots.txt

        // mark it as crawled
        crawled.add(uri);

        attributes.clear();
        attributes.addAttribute("", HREF_ATTR_NAME, HREF_ATTR_NAME, "CDATA", uri);
        attributes.addAttribute("", REFERRER_ATTR_NAME, REFERRER_ATTR_NAME, "CDATA", referrer);

        // Output url, referrer, content-type, status, message for traversable url's
        HttpURLConnection h = null;
        URL url = null;
        String newURL = null;
        try {
            String content_type = "text/html";
            String responseMessage = "not found";
            int responseCode = 404;
            if (uri.startsWith("http://")) {
                url = new URL(uri);
                URLConnection links_url_connection = url.openConnection();
                h = (HttpURLConnection) links_url_connection;
                h.setRequestMethod("HEAD"); // lets be kind to external sites
                content_type = links_url_connection.getContentType();
                responseMessage = h.getResponseMessage();
                responseCode = h.getResponseCode();
            } else {
                String tempURI = uri;
                if (!(uri.startsWith("/"))) {
                    String contextURI = referrer.substring(0, referrer.lastIndexOf("/") + 1);
                    tempURI = contextURI + uri;
                }

                // see if the document exists
                if (this.session.getUriHandler().isDocument(tempURI)) {
                    content_type = "text/html";
                    responseMessage = "ok";
                    responseCode = 200;
                    newURL = tempURI;
                } else {
                    // see if the resource exists
                }
            }

            attributes
                    .addAttribute("", CONTENT_ATTR_NAME, CONTENT_ATTR_NAME, "CDATA", content_type);

            attributes.addAttribute("", MESSAGE_ATTR_NAME, MESSAGE_ATTR_NAME, "CDATA",
                    responseMessage);

            attributes.addAttribute("", STATUS_ATTR_NAME, STATUS_ATTR_NAME, "CDATA", String
                    .valueOf(responseCode));
        } catch (IOException ioe) {
            attributes.addAttribute("", MESSAGE_ATTR_NAME, MESSAGE_ATTR_NAME, "CDATA", ioe
                    .getMessage());
        } catch (final Exception e1) {
            attributes.addAttribute("", MESSAGE_ATTR_NAME, MESSAGE_ATTR_NAME, "CDATA", e1
                    .getMessage());
        } finally {
            if (h != null) {
                h.disconnect();
            }
        }

        // don't try to get links of a url which is excluded from crawling
        // try to get links of a url which is included for crawling
        if (!isExcludedURL(uri) && isIncludedURL(uri)) {
            // add prefix and query to get data from the linkserializer.
            if (newURL != null) {
                if (newURL.indexOf("?") > -1) {
                    newURL = newURL.substring(0, newURL.indexOf("?")) + linkViewQuery;
                } else {
                    newURL = newURL + "?" + linkViewQuery;
                }
            }
        }

        // linkrewriter transformer takes care of internal links
        if (uri.startsWith("http://")) {
            super.contentHandler.startElement(URI, LINK_NODE_NAME, PREFIX + ':' + LINK_NODE_NAME,
                    attributes);
            super.contentHandler.endElement(URI, LINK_NODE_NAME, PREFIX + ':' + LINK_NODE_NAME);
        }

        return newURL;
    }

    /**
     * check if URL is a candidate for indexing
     * 
     * @param url Description of Parameter
     * @return The excludedURL value
     * @since
     */
    private boolean isExcludedURL(String url) {
        // by default include URL for crawling
        if (excludeCrawlingURL == null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("exclude no URL " + url);
            }
            return false;
        }

        final String s = url;
        Iterator i = excludeCrawlingURL.iterator();
        while (i.hasNext()) {
            RE pattern = (RE) i.next();
            if (pattern.match(s)) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("exclude URL " + url);
                }
                return true;
            }
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("exclude not URL " + url);
        }
        return false;
    }

    /**
     * check if URL is a candidate for indexing
     * 
     * @param url Description of Parameter
     * @return The includedURL value
     * @since
     */
    private boolean isIncludedURL(String url) {
        // by default include URL for crawling
        if (includeCrawlingURL == null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("include all URL " + url);
            }
            return true;
        }

        final String s = url;
        Iterator i = includeCrawlingURL.iterator();
        while (i.hasNext()) {
            RE pattern = (RE) i.next();
            if (pattern.match(s)) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("include URL " + url);
                }
                return true;
            }
        }
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("include not URL " + url);
        }
        return false;
    }

    public void recycle() {
        if (null != this.inputSource) {
            super.resolver.release(this.inputSource);
            this.inputSource = null;
        }
        this.manager.release(super.resolver);
        super.resolver = null;
        this.manager = null;
        this.attributes = null;
        super.recycle();
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}
