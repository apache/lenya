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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.linking.LinkTarget;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.Query;
import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.util.StringUtil;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>
 * UUID to URL transformer.
 * </p>
 * 
 * <p>
 * This transformer is applied to an XHMTL document. It processes all links
 * following the {@link LinkResolver} syntax which are configured using
 * <code>&lt;transform/&gt;</code> elements (see below).
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
 * <p>
 * You can add the query parameter <code>uuid2url.extension</code> to
 * <code>lenya-document:</code> URLs to set a specific link extension.
 * </p>
 * <p>
 * The resulting URLs can either be absolute (default) or relative. You can
 * either configure this when declaring the transformer:
 * </p>
 * <code><pre>
 *  &lt;map:transformer ... &gt;
 *    &lt;urls type=&quot;relative&quot;/&gt;
 *    &lt;transform namespace=&quot;http://www.w3.org/1999/xhtml&quot; element=&quot;a&quot; attribute=&quot;href&quot;/&gt;
 *    &lt;transform namespace=&quot;...&quot; ... /&gt;
 *  &lt;/map:transformer&gt;
 * </pre></code>
 * <p>
 * or pass a parameter:
 * </p>
 * <code><pre>
 *  &lt;map:parameter name=&quot;urls&quot; value=&quot;relative&quot;/&gt;
 * </pre></code>
 * 
 * $Id: LinkRewritingTransformer.java,v 1.7 2004/03/16 11:12:16 gregor
 */
public class UuidToUrlTransformer extends AbstractLinkTransformer implements Disposable {

    protected static final String BROKEN_ATTRIB = "class";
    protected static final String BROKEN_VALUE = "brokenlink";
    protected static final String EXTENSION_PARAM = "uuid2url.extension";

    private ServiceSelector serviceSelector;
    private PolicyManager policyManager;
    private AccessControllerResolver acResolver;
    private AccreditableManager accreditableManager;

    private Document currentDocument;
    private String currentUrl;

    private DocumentFactory factory;
    private LinkResolver linkResolver;
    private String contextPath;

    private boolean relativeUrls = false;

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
            if (_parameters.isParameter("urls")) {
                setUrlType(_parameters.getParameter("urls"));
            }

            Session session = RepositoryUtil.getSession(this.manager, _request);
            this.factory = DocumentUtil.createDocumentFactory(this.manager, session);
            this.currentUrl = ServletHelper.getWebappURI(_request);
            this.currentDocument = this.factory.getFromURL(currentUrl);
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

    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        Configuration urlConfig = config.getChild("urls", false);
        if (urlConfig != null) {
            String value = urlConfig.getAttribute("type");
            setUrlType(value);
        }
    }

    protected void setUrlType(String value) throws ConfigurationException {
        if (value.equals("relative")) {
            this.relativeUrls = true;
        } else if (value.equals("absolute")) {
            this.relativeUrls = false;
        } else {
            throw new ConfigurationException("Invalid URL type [" + value
                    + "], must be relative or absolute.");
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

    protected void handleLink(String linkUrl, AttributeConfiguration config, AttributesImpl newAttrs)
            throws Exception {
        if (linkUrl.startsWith("lenya-document:")) {
            Document doc = getCurrentDocument();

            String anchor = null;
            String url = null;

            int anchorIndex = linkUrl.indexOf("#");
            if (anchorIndex > -1) {
                url = linkUrl.substring(0, anchorIndex);
                anchor = linkUrl.substring(anchorIndex + 1);
            } else {
                url = linkUrl;
            }

            String[] linkUriAndQuery = url.split("\\?");
            String linkUri = linkUriAndQuery[0];
            String queryString = null;
            String requiredExtension = null;
            if (linkUriAndQuery.length > 1) {
                queryString = linkUriAndQuery[1];
                Query query = new Query(queryString);
                requiredExtension = query.getValue(EXTENSION_PARAM);
                query.removeValue(EXTENSION_PARAM);
                queryString = query.toString();
            }
            LinkTarget target = this.linkResolver.resolve(doc, linkUri);
            if (target.exists() && target.getDocument().hasLink()) {
                Document targetDocument = target.getDocument();

                String extension = getExtension(targetDocument, requiredExtension);

                rewriteLink(newAttrs, config.attribute, targetDocument, anchor, queryString,
                        extension);
            } else if (doc.getArea().equals(Publication.AUTHORING_AREA)) {
                markBrokenLink(newAttrs, config.attribute, linkUrl);
            } else {
                this.ignoreLinkElement = true;
            }
        } else {
            /*
             * This is legacy code. It rewrites links to non-document images (in
             * resources/shared). These images shouldn't be referenced in
             * documents since this violates the separation between content and
             * layout.
             */
            String prefix = "/" + this.currentDocument.getPublication().getId() + "/";
            if (linkUrl.startsWith(prefix)) {
                String pubUrl = linkUrl.substring(prefix.length());
                String area = pubUrl.split("/")[0];

                // don't rewrite /{pub}/modules/...
                if (area.equals(Publication.AUTHORING_AREA)) {
                    String areaUrl = pubUrl.substring(area.length());
                    String newUrl = this.contextPath + prefix + this.currentDocument.getArea()
                            + areaUrl;
                    setAttribute(newAttrs, config.attribute, newUrl);
                }
            }
        }
    }

    protected String getExtension(Document targetDocument, String requiredExtension) {
        String docExtension = targetDocument.getExtension();
        String extension = requiredExtension != null ? requiredExtension : docExtension;
        if (extension.length() > 0) {
            extension = "." + extension;
        }
        return extension;
    }

    /**
     * Marks a <code>&lt;a/&gt;</code> element as broken and removes href
     * attribute.
     * 
     * @param newAttrs The new attributes.
     * @param attrName The attribute name.
     * @param brokenUrl The broken link URI.
     * @throws AccessControlException when something went wrong.
     */
    protected void markBrokenLink(AttributesImpl newAttrs, String attrName, String brokenUrl)
            throws AccessControlException {
        if (newAttrs.getIndex(BROKEN_ATTRIB) > -1)
            newAttrs.removeAttribute(newAttrs.getIndex(BROKEN_ATTRIB));
        if (newAttrs.getIndex("title") > -1)
            newAttrs.removeAttribute(newAttrs.getIndex("title"));
        if (newAttrs.getIndex(attrName) > -1)
            newAttrs.setAttribute(newAttrs.getIndex(attrName), "", attrName, attrName, "CDATA", "");
        String warning = "Broken Link: " + brokenUrl;
        newAttrs.addAttribute("", "title", "title", "CDATA", warning);
        newAttrs.addAttribute("", BROKEN_ATTRIB, BROKEN_ATTRIB, "CDATA", BROKEN_VALUE);
    }

    /**
     * Rewrites a link.
     * 
     * @param newAttrs The new attributes.
     * @param attributeName The name of the attribute to rewrite.
     * @param targetDocument The target document.
     * @param anchor The anchor (the string after the # character in the URL).
     * @param queryString The query string without question mark.
     * @param extension The extension to use.
     * @throws AccessControlException when something went wrong.
     */
    protected void rewriteLink(AttributesImpl newAttrs, String attributeName,
            Document targetDocument, String anchor, String queryString, String extension)
            throws AccessControlException {

        String webappUrl = targetDocument.getCanonicalWebappURL();

        String rewrittenURL;
        if (this.relativeUrls) {
            rewrittenURL = getRelativeUrlTo(webappUrl);
        } else {
            Policy policy = this.policyManager.getPolicy(this.accreditableManager, webappUrl);

            Proxy proxy = targetDocument.getPublication().getProxy(targetDocument,
                    policy.isSSLProtected());

            if (proxy == null) {
                rewrittenURL = this.request.getContextPath() + webappUrl;
            } else {
                rewrittenURL = proxy.getURL(targetDocument);
            }
            if (getLogger().isDebugEnabled()) {
                getLogger()
                        .debug(this.indent + "SSL protection: [" + policy.isSSLProtected() + "]");
                getLogger().debug(this.indent + "Resolved proxy: [" + proxy + "]");
            }
        }

        int lastDotIndex = rewrittenURL.lastIndexOf(".");
        if (lastDotIndex > -1) {
            rewrittenURL = rewrittenURL.substring(0, lastDotIndex) + extension;
        }

        if (anchor != null) {
            rewrittenURL += "#" + anchor;
        }

        if (queryString != null && queryString.length() > 0) {
            rewrittenURL += "?" + queryString;
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this.indent + "Rewriting URL to: [" + rewrittenURL + "]");
        }

        setAttribute(newAttrs, attributeName, rewrittenURL);
    }

    private String getRelativeUrlTo(String webappUrl) {
        List sourceSteps = toList(this.currentUrl);
        List targetSteps = toList(webappUrl);

        while (!sourceSteps.isEmpty() && !targetSteps.isEmpty()
                && sourceSteps.get(0).equals(targetSteps.get(0))) {
            sourceSteps.remove(0);
            targetSteps.remove(0);
        }

        String upDots = "";
        if (sourceSteps.size() > 1) {
            String[] upDotsArray = new String[sourceSteps.size() - 1];
            Arrays.fill(upDotsArray, "..");
            upDots = StringUtil.join(upDotsArray, "/") + "/";
        }

        String[] targetArray = (String[]) targetSteps.toArray(new String[targetSteps.size()]);
        String targetPath = StringUtil.join(targetArray, "/");

        String relativeUrl = upDots + targetPath;
        return relativeUrl;

    }

    protected List toList(String url) {
        return new ArrayList(Arrays.asList(url.substring(1).split("/")));
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
        this.ignoreLinkElement = false;
        this.currentDocument = null;
        this.currentUrl = null;
    }
}