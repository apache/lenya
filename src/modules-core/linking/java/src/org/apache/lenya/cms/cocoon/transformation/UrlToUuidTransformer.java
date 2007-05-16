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
package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.cms.linking.Link;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>
 * URL to UUID transformer.
 * </p>
 * 
 * <p>
 * This transformer is applied to an XHMTL document. It processes all URL-based
 * links to links following the {@link LinkResolver} syntax.
 * </p>
 * 
 * $Id: LinkRewritingTransformer.java,v 1.7 2004/03/16 11:12:16 gregor
 */
public class UrlToUuidTransformer extends AbstractSAXTransformer {

    private boolean ignoreLinkElement = false;
    private Document currentDocument;
    private DocumentFactory factory;
    private String contextPath;

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver,
     *      java.util.Map, java.lang.String,
     *      org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver _resolver, Map _objectModel, String _source,
            Parameters _parameters) throws ProcessingException, SAXException, IOException {
        super.setup(_resolver, _objectModel, _source, _parameters);

        Request _request = ObjectModelHelper.getRequest(_objectModel);
        this.contextPath = _request.getContextPath();

        try {
            Session session = RepositoryUtil.getSession(this.manager, _request);
            this.factory = DocumentUtil.createDocumentFactory(this.manager, session);
            String url = ServletHelper.getWebappURI(_request);
            this.currentDocument = this.factory.getFromURL(url);
        } catch (final Exception e1) {
            throw new ProcessingException(e1);
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Setting up transformer");
            getLogger().debug("    Processed version:       [" + getCurrentDocument() + "]");
        }
    }

    /**
     * Returns the currently processed document.
     * 
     * @return A document.
     */
    protected Document getCurrentDocument() {
        return this.currentDocument;
    }

    private String indent = "";

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String name, String qname, Attributes attrs)
            throws SAXException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                    this.indent + "<" + qname + "> (ignoreAElement = " + this.ignoreLinkElement
                            + ")");
            this.indent += "  ";
        }

        AttributesImpl newAttrs = null;
        if (lookingAtLinkElement(name)) {

            this.ignoreLinkElement = false;

            for (int i = 0; i < UuidToUrlTransformer.attributeNames.length; i++) {
                String completeUrl = attrs.getValue(UuidToUrlTransformer.attributeNames[i]);
                if (completeUrl != null) {
                    try {
                        newAttrs = new AttributesImpl(attrs);

                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug(this.indent + "link URL: [" + completeUrl + "]");
                        }

                        String pubId = this.currentDocument.getPublication().getId();
                        String area = this.currentDocument.getArea();

                        if (completeUrl.startsWith(this.contextPath + "/" + pubId + "/" + area
                                + "/")) {
                            final String webappUrl = completeUrl.substring(this.contextPath
                                    .length());

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

                            if (factory.isDocument(linkUrl)) {
                                Document targetDocument = factory.getFromURL(linkUrl);

                                if (getLogger().isDebugEnabled()) {
                                    getLogger().debug(
                                            "Convert links: Check webapp URL [" + linkUrl + "]");
                                }

                                rewriteLink(newAttrs, UuidToUrlTransformer.attributeNames[i],
                                        targetDocument, anchor, queryString);
                            }
                            else {
                                /*
                                 * This is legacy code. It rewrites links to
                                 * non-document images (in resources/shared). These
                                 * images shouldn't be referenced in documents since
                                 * this violates the separation between content and
                                 * layout.
                                 */
                                String newUrl = completeUrl.substring(this.contextPath.length());
                                setAttribute(newAttrs, UuidToUrlTransformer.attributeNames[i], newUrl);
                            }
                        }

                    } catch (final Exception e) {
                        getLogger().error("startElement failed: ", e);
                        throw new SAXException(e);
                    }
                }
            }
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this.indent + "ignoreAElement: " + this.ignoreLinkElement);
        }

        if (!(lookingAtLinkElement(name) && this.ignoreLinkElement)) {
            if (newAttrs != null) {
                attrs = newAttrs;
            }
            super.startElement(uri, name, qname, attrs);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this.indent + "<" + qname + "> sent");
            }
        }
    }

    /**
     * Rewrites a link.
     * 
     * @param newAttrs The new attributes.
     * @param attributeName The name of the attribute to rewrite.
     * @param targetDocument The target document.
     * @param anchor The anchor (the string after the # character in the URL).
     * @param queryString The query string without question mark.
     * @throws AccessControlException when something went wrong.
     */
    protected void rewriteLink(AttributesImpl newAttrs, String attributeName,
            Document targetDocument, String anchor, String queryString)
            throws AccessControlException {

        Link link = new Link();
        link.setUuid(targetDocument.getUUID());

        String linkUri = link.getUri();

        if (anchor != null) {
            linkUri += "#" + anchor;
        }

        if (queryString != null) {
            linkUri += "?" + queryString;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this.indent + "Rewriting URL to: [" + linkUri + "]");
        }

        setAttribute(newAttrs, attributeName, linkUri);
    }

    /**
     * Sets the value of the href attribute.
     * 
     * @param attr The attributes.
     * @param name The attribute name.
     * @param value The value.
     * @throws IllegalArgumentException if the href attribute is not contained
     *         in this attributes.
     */
    protected void setAttribute(AttributesImpl attr, String name, String value) {
        int position = attr.getIndex(name);
        if (position == -1) {
            throw new IllegalArgumentException("The href attribute is not available!");
        }
        attr.setValue(position, value);
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String name, String qname) throws SAXException {
        if (getLogger().isDebugEnabled()) {
            this.indent = this.indent.substring(2);
            getLogger().debug(this.indent + "</" + qname + ">");
        }
        if (lookingAtLinkElement(name) && this.ignoreLinkElement) {
            this.ignoreLinkElement = false;
        } else {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this.indent + "</" + qname + "> sent");
            }
            super.endElement(uri, name, qname);
        }
    }

    private boolean lookingAtLinkElement(String name) {
        return Arrays.asList(UuidToUrlTransformer.elementNames).contains(name);
    }

    /**
     * @see org.apache.avalon.excalibur.pool.Recyclable#recycle()
     */
    public void recycle() {
        this.ignoreLinkElement = false;
    }
}