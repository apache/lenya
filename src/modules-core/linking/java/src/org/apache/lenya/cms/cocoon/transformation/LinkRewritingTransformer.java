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
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>
 * Link rewriting transformer.
 * </p>
 * 
 * <p>
 * This transformer is applied to an XHMTL document. It processes all links
 * following the {@link LinkResolver} syntax which are denoted by
 * {@link ResourceType#getLinkAttributeXPaths()}.
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
 * 
 * $Id: LinkRewritingTransformer.java,v 1.7 2004/03/16 11:12:16 gregor
 */
public class LinkRewritingTransformer extends AbstractSAXTransformer implements Disposable {

    protected static final String BROKEN_ATTRIB = "class";
    protected static final String BROKEN_VALUE = "brokenlink";

    private boolean ignoreAElement = false;
    private ServiceSelector serviceSelector;
    private PolicyManager policyManager;
    private AccessControllerResolver acResolver;
    private AccreditableManager accreditableManager;

    private Document currentDocument;

    private DocumentFactory factory;
    private LinkResolver linkResolver;

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver,
     *      java.util.Map, java.lang.String,
     *      org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver _resolver, Map _objectModel, String _source,
            Parameters _parameters) throws ProcessingException, SAXException, IOException {
        super.setup(_resolver, _objectModel, _source, _parameters);

        Request _request = ObjectModelHelper.getRequest(_objectModel);

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

        this.serviceSelector = null;
        this.acResolver = null;
        this.policyManager = null;
        this.linkResolver = null;

        try {
            this.serviceSelector = (ServiceSelector) this.manager
                    .lookup(AccessControllerResolver.ROLE + "Selector");
            this.acResolver = (AccessControllerResolver) this.serviceSelector
                    .select(AccessControllerResolver.DEFAULT_RESOLVER);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("    Resolved AC resolver [" + this.acResolver + "]");
            }
            String webappUrl = ServletHelper.getWebappURI(_request);
            AccessController accessController = this.acResolver.resolveAccessController(webappUrl);
            this.accreditableManager = accessController.getAccreditableManager();
            this.policyManager = accessController.getPolicyManager();
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("    Using policy manager [" + this.policyManager + "]");
            }
            this.linkResolver = (LinkResolver) this.manager.lookup(LinkResolver.ROLE);
        } catch (final ServiceException e) {
            throw new ProcessingException(e);
        } catch (final AccessControlException e) {
            throw new ProcessingException(e);
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

    /**
     * The local name of the HTML &lt;a&gt; href attribute.
     */
    public static final String ATTRIBUTE_HREF = "href";

    private String indent = "";

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String name, String qname, Attributes attrs)
            throws SAXException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                    this.indent + "<" + qname + "> (ignoreAElement = " + this.ignoreAElement + ")");
            this.indent += "  ";
        }

        AttributesImpl newAttrs = null;
        if (lookingAtAElement(name)) {

            this.ignoreAElement = false;

            String href = attrs.getValue(ATTRIBUTE_HREF);
            if (href != null) {

                Document doc = getCurrentDocument();

                try {
                    newAttrs = new AttributesImpl(attrs);

                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug(this.indent + "href URL: [" + href + "]");
                    }

                    if (href.startsWith("lenya-document:")) {

                        String anchor = null;
                        String url = null;

                        int anchorIndex = href.indexOf("#");
                        if (anchorIndex > -1) {
                            url = href.substring(0, anchorIndex);
                            anchor = href.substring(anchorIndex + 1);
                        } else {
                            url = href;
                        }

                        String[] linkUriAndQuery = url.split("\\?");
                        String linkUri = linkUriAndQuery[0];
                        String queryString = null;
                        if (linkUriAndQuery.length > 1) {
                            queryString = linkUriAndQuery[1];
                        }
                        Document targetDocument = this.linkResolver.resolve(doc, linkUri).getDocument();
                        if (targetDocument != null) {
                            String extension = targetDocument.getExtension();
                            if (extension.length() > 0) {
                                extension = "." + extension;
                            }
                            rewriteLink(newAttrs, targetDocument, anchor, queryString, extension);
                        } else if (doc.getArea().equals(Publication.AUTHORING_AREA)) {
                            markBrokenLink(newAttrs, href);
                        } else {
                            this.ignoreAElement = true;
                        }
                    }
                } catch (final Exception e) {
                    getLogger().error("startElement failed: ", e);
                    throw new SAXException(e);
                }
            }

        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this.indent + "ignoreAElement: " + this.ignoreAElement);
        }

        if (!(lookingAtAElement(name) && this.ignoreAElement)) {
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
     * Marks a <code>&lt;a/&gt;</code> element as broken and removes href
     * attribute.
     * 
     * @param newAttrs The new attributes.
     * @param brokenHref The broken link URI.
     * @throws AccessControlException when something went wrong.
     */
    protected void markBrokenLink(AttributesImpl newAttrs, String brokenHref)
            throws AccessControlException {
        if (newAttrs.getIndex(BROKEN_ATTRIB) > -1)
            newAttrs.removeAttribute(newAttrs.getIndex(BROKEN_ATTRIB));
        if (newAttrs.getIndex("title") > -1)
            newAttrs.removeAttribute(newAttrs.getIndex("title"));
        if (newAttrs.getIndex("href") > -1)
            newAttrs.setAttribute(newAttrs.getIndex("href"), "", "href", "href", "CDATA", "");
        String warning = "Broken Link: " + brokenHref;
        newAttrs.addAttribute("", "title", "title", "CDATA", warning);
        newAttrs.addAttribute("", BROKEN_ATTRIB, BROKEN_ATTRIB, "CDATA", BROKEN_VALUE);
    }

    /**
     * Rewrites a link.
     * 
     * @param newAttrs The new attributes.
     * @param targetDocument The target document.
     * @param anchor The anchor (the string after the # character in the URL).
     * @param queryString The query string without question mark.
     * @param extension The extension to use.
     * @throws AccessControlException when something went wrong.
     */
    protected void rewriteLink(AttributesImpl newAttrs, Document targetDocument, String anchor,
            String queryString, String extension) throws AccessControlException {

        String webappUrl = targetDocument.getCanonicalWebappURL();
        Policy policy = this.policyManager.getPolicy(this.accreditableManager, webappUrl);

        Proxy proxy = targetDocument.getPublication().getProxy(targetDocument,
                policy.isSSLProtected());

        String rewrittenURL;
        if (proxy == null) {
            rewrittenURL = this.request.getContextPath() + webappUrl;
        } else {
            rewrittenURL = proxy.getURL(targetDocument);
        }

        int lastDotIndex = rewrittenURL.lastIndexOf(".");
        if (lastDotIndex > -1) {
            rewrittenURL = rewrittenURL.substring(0, lastDotIndex) + extension;
        }

        if (anchor != null) {
            rewrittenURL += "#" + anchor;
        }

        if (queryString != null) {
            rewrittenURL += "?" + queryString;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this.indent + "SSL protection: [" + policy.isSSLProtected() + "]");
            getLogger().debug(this.indent + "Resolved proxy: [" + proxy + "]");
            getLogger().debug(this.indent + "Rewriting URL to: [" + rewrittenURL + "]");
        }

        setHrefAttribute(newAttrs, rewrittenURL);
    }

    /**
     * Sets the value of the href attribute.
     * 
     * @param attr The attributes.
     * @param value The value.
     * @throws IllegalArgumentException if the href attribute is not contained
     *         in this attributes.
     */
    protected void setHrefAttribute(AttributesImpl attr, String value) {
        int position = attr.getIndex(ATTRIBUTE_HREF);
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
        if (lookingAtAElement(name) && this.ignoreAElement) {
            this.ignoreAElement = false;
        } else {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this.indent + "</" + qname + "> sent");
            }
            super.endElement(uri, name, qname);
        }
    }

    private boolean lookingAtAElement(String name) {
        return name.equals("a");
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Disposing transformer");
        }
        if (this.serviceSelector != null) {
            if (this.acResolver != null) {
                this.serviceSelector.release(this.acResolver);
            }
            this.manager.release(this.serviceSelector);
        }
        if (this.linkResolver != null) {
            this.manager.release(this.linkResolver);
        }
    }

    /**
     * @see org.apache.avalon.excalibur.pool.Recyclable#recycle()
     */
    public void recycle() {
        this.ignoreAElement = false;
    }
}