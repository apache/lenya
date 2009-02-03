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

import java.util.Arrays;

import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;

/**
 * <p>
 * Converts webapp URLs to UUID-based internal links. If the URL doesn't refer to a document, the
 * original URL is returned.
 * </p>
 * <p>
 * Objects of this class are not thread-safe.
 * </p>
 */
public class UrlToUuidRewriter implements LinkRewriter {

    private DocumentFactory factory;

    /**
     * @param factory The document factory to use.
     */
    public UrlToUuidRewriter(DocumentFactory factory) {
        this.factory = factory;
    }

    public boolean matches(String url) {
        if (url.startsWith("/")) {
            URLInformation info = new URLInformation(url);
            String pubId = info.getPublicationId();
            String area = info.getArea();
            if (pubId != null && area != null) {
                if (this.factory.existsPublication(pubId)) {
                    try {
                        Publication pub = this.factory.getPublication(pubId);
                        if (Arrays.asList(pub.getAreaNames()).contains(area)) {
                            return true;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return false;
    }

    public String rewrite(String webappUrl) {

        String anchor = null;
        String url = null;

        int anchorIndex = webappUrl.indexOf("#");
        if (anchorIndex > -1) {
            url = webappUrl.substring(0, anchorIndex);
            anchor = webappUrl.substring(anchorIndex + 1);
        } else {
            url = webappUrl;
        }

        String[] linkUrlAndQuery = url.split("\\?");
        String linkUrl = linkUrlAndQuery[0];
        String queryString = null;
        if (linkUrlAndQuery.length > 1) {
            queryString = linkUrlAndQuery[1];
        }

        String rewrittenUrl;

        try {
            if (factory.isDocument(linkUrl)) {
                Document targetDocument = factory.getFromURL(linkUrl);
                rewrittenUrl = getUuidBasedUri(targetDocument, anchor, queryString);
            } else {
                rewrittenUrl = webappUrl;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rewrittenUrl;
    }

    /**
     * Rewrites a link.
     * 
     * @param targetDocument The target document.
     * @param anchor The anchor (the string after the # character in the URL).
     * @param queryString The query string without question mark.
     * @return A UUID-based link URI.
     */
    protected String getUuidBasedUri(Document targetDocument, String anchor, String queryString) {

        Link link = new Link();
        link.setUuid(targetDocument.getUUID());

        String linkUri = link.getUri();

        if (anchor != null) {
            linkUri += "#" + anchor;
        }

        if (queryString != null) {
            linkUri += "?" + queryString;
        }

        return linkUri;
    }

}
