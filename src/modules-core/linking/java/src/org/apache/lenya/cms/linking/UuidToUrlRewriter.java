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
package org.apache.lenya.cms.linking;

import java.net.MalformedURLException;
import java.util.StringTokenizer;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.util.Assert;
import org.apache.lenya.util.Query;

/**
 * <p>
 * Rewrite lenya-document: URLs to /{pub}/{area}/... URLs.
 * </p>
 * <p>
 * To support legacy documents, image URLs starting with /{pub}/{area} are also
 * handled.
 * </p>
 * <p>
 * These links are resolved using the following rules:
 * </p>
 * <ul>
 * <li>The current area (obtained from the page envelope) is used.</li>
 * <li>A URL prefix is added depending on the proxy configuration of the
 * publication.</li>
 * <li>If the target document does not exist and is in the authoring area, the
 * href attribute is removed and a class="brokenlink" attribute is added to the
 * <code>&lt;a/&gt;</code> element.</li>
 * <li>If the target document does not exist and is in the live area, the
 * <code>&lt;a/&gt;</code> element is removed to disable the link.</li>
 * </ul>
 * <p>
 * You can add the query parameter <code>uuid2url.extension</code> to
 * <code>lenya-document:</code> URLs to set a specific link extension.
 * </p>
 * <p>
 * The resulting URLs are absolute web application URLs (without the servlet
 * context path).
 * </p>
 */
public class UuidToUrlRewriter implements LinkRewriter {

    protected static final String EXTENSION_PARAM = "uuid2url.extension";

    private String currentUrl;
    private Document currentDoc;
    private LinkResolver linkResolver;
    private DocumentFactory factory;

    /**
     * @param currentUrl The current request URL.
     * @param linkResolver The link resolver to use.
     * @param factory The document factory to use.
     */
    public UuidToUrlRewriter(String currentUrl, LinkResolver linkResolver, DocumentFactory factory) {
        Assert.notNull("current URL", currentUrl);
        Assert.notNull("link resolver", linkResolver);
        Assert.notNull("document factory", factory);
        this.currentUrl = currentUrl;
        this.factory = factory;
        this.linkResolver = linkResolver;
    }

    public void setCurrentDocument(Document doc) {
        Assert.notNull("current document", doc);
        this.currentDoc = doc;
    }

    protected static final String SCHEME = "lenya-document:";

    public boolean matches(String url) {
        return url.startsWith(SCHEME) || url.startsWith("/");
    }

    public String rewrite(String linkUrl) {

        String rewrittenUrl = null;

        URLInformation info = new URLInformation(this.currentUrl);
        if (linkUrl.startsWith(SCHEME)) {

            String anchor = null;
            String url = null;

            int anchorIndex = linkUrl.indexOf("#");
            if (anchorIndex > -1) {
                url = linkUrl.substring(0, anchorIndex);
                anchor = linkUrl.substring(anchorIndex + 1);
            } else {
                url = linkUrl;
            }

            StringTokenizer tokenizer = new StringTokenizer(url, "?");
            String linkUri = tokenizer.nextToken();
            String queryString = null;
            String requiredExtension = null;
            if (tokenizer.hasMoreTokens()) {
                queryString = tokenizer.nextToken();
                Query query = new Query(queryString);
                requiredExtension = query.getValue(EXTENSION_PARAM);
                query.removeValue(EXTENSION_PARAM);
                queryString = query.toString();
            }

            LinkTarget target;
            try {
                if (this.currentDoc != null) {
                    target = this.linkResolver.resolve(this.currentDoc, linkUri);
                } else {
                    Link link = getAbsoluteLink(info, linkUri);
                    target = this.linkResolver.resolve(this.factory, link.getUri());
                }

                if (target.exists() && target.getDocument().hasLink()) {
                    Document targetDocument = target.getDocument();
                    String extension = getExtension(targetDocument, requiredExtension);
                    rewrittenUrl = getWebappUrl(targetDocument, anchor, queryString, extension);
                } else {
                    rewrittenUrl = null;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            /*
             * This is legacy code. It rewrites links to non-document images (in
             * resources/shared). These images shouldn't be referenced in
             * documents since this violates the separation between content and
             * layout.
             */
            String prefix = "/" + info.getPublicationId() + "/";
            if (linkUrl.startsWith(prefix)) {
                String pubUrl = linkUrl.substring(prefix.length());
                StringTokenizer tokenizer = new StringTokenizer(pubUrl, "/");
                String area = tokenizer.nextToken();

                // don't rewrite /{pub}/modules/...
                if (area.equals(Publication.AUTHORING_AREA)) {
                    String areaUrl = pubUrl.substring(area.length());
                    rewrittenUrl = prefix + area + areaUrl;
                }
            }
            if (rewrittenUrl == null) {
                rewrittenUrl = linkUrl;
            }
        }
        return rewrittenUrl;
    }

    /**
     * Rewrites a link.
     * 
     * @param targetDocument The target document.
     * @param anchor The anchor (the string after the # character in the URL).
     * @param queryString The query string without question mark.
     * @param extension The extension to use.
     * @return a web application URL.
     * @throws AccessControlException when something went wrong.
     */
    protected String getWebappUrl(Document targetDocument, String anchor, String queryString,
            String extension) throws AccessControlException {

        String webappUrl = targetDocument.getCanonicalWebappURL();

        int lastDotIndex = webappUrl.lastIndexOf(".");
        if (lastDotIndex > -1) {
            webappUrl = webappUrl.substring(0, lastDotIndex) + extension;
        }

        if (anchor != null) {
            webappUrl += "#" + anchor;
        }

        if (queryString != null && queryString.length() > 0) {
            webappUrl += "?" + queryString;
        }

        return webappUrl;
    }

    /**
     * The link is constructed from the linkUri string. If it lacks the area or
     * publication ID information, these are obtained from the current URL
     * information.
     * 
     * @param info The current URL information.
     * @param linkUri The link URI to use.
     * @return A link.
     * @throws MalformedURLException if the linkUri parameter is malformed.
     */
    protected Link getAbsoluteLink(URLInformation info, String linkUri)
            throws MalformedURLException {
        Link link = new Link(linkUri);
        if (link.getPubId() == null) {
            link.setPubId(info.getPublicationId());
        }
        if (link.getArea() == null) {
            link.setArea(info.getArea());
        }
        return link;
    }

    /**
     * Get the extension of a document.
     * 
     * @param targetDocument The document.
     * @param requiredExtension The required extension.
     * @return The required extension or, if it is null, the document's default
     *         extension.
     */
    protected String getExtension(Document targetDocument, String requiredExtension) {
        String extension = requiredExtension != null ? requiredExtension : targetDocument
                .getExtension();
        if (extension.length() > 0) {
            extension = "." + extension;
        }
        return extension;
    }

}
