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

/* $Id: SimpleLinkRewritingTransformer.java,v 1.7 2004/03/16 11:12:16 gregor Exp $  */

package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.activity.Disposable;
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
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.ac.impl.PolicyAuthorizer;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.apache.log4j.Category;


/**
 * This is a simple transformer which rewrites &lt;a
 * href="/lenya/unicom/authoring/doctypes/2columns.html"&gt;
 * to &lt;a
 * href="/lenya/unicom/$AREA/doctypes/2columns.html"&gt;.
 * 
 * It also checks if the target of the link really exists if the area is 
 * "live". If the link target doesn't exist the link will be inactive.
 * 
 * Ideally this transformer could be replaced by the
 * LinkRewrittingTransformer that Forrest uses if we employ the same
 * scheme for internal links.
 */
public class SimpleLinkRewritingTransformer extends AbstractSAXTransformer
implements Disposable {

    Category log = Category.getInstance(SimpleLinkRewritingTransformer.class);

    private String baseURI;
    private PageEnvelope envelope = null;

    public static final String INTERNAL_LINK_PREFIX = "site:";
    private boolean ignoreAElement = false;

    private ServiceSelector serviceSelector;
    private PolicyManager policyManager;
    private AccessControllerResolver acResolver;
    private AccreditableManager accreditableManager;
    private Identity identity;
    private String urlPrefix;
    private String sslPrefix;

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws ProcessingException, SAXException, IOException {

        super.setup(resolver, objectModel, source, parameters);

        try {
            envelope =
                PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (PageEnvelopeException e) {
            throw new ProcessingException(e);
        }

        String mountPoint =
            envelope.getContext() + "/" + envelope.getPublication().getId();
        
        sslPrefix = envelope.getPublication().getSSLPrefix();

        StringBuffer uribuf = new StringBuffer();

        uribuf.append(mountPoint);

        baseURI = uribuf.toString();

        serviceSelector = null;
        acResolver = null;
        policyManager = null;

        identity = Identity.getIdentity(request.getSession(false));

        try {
            String publicationId = envelope.getPublication().getId();
            String area = envelope.getDocument().getArea();

                getLogger().debug("Setting up transformer");
                getLogger().debug("    Identity:       [" + identity + "]");
                getLogger().debug("    Publication ID: [" + publicationId + "]");
                getLogger().debug("    Area:           [" + area + "]");

            urlPrefix = "/" + publicationId + "/" + area;

            Request request = ObjectModelHelper.getRequest(objectModel);

            serviceSelector =
                (ServiceSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");

            acResolver =
                (AccessControllerResolver) serviceSelector.select(
                    AccessControllerResolver.DEFAULT_RESOLVER);

                getLogger().debug("    Resolved AC resolver [" + acResolver + "]");

            String webappUrl = ServletHelper.getWebappURI(request);
            AccessController accessController = acResolver.resolveAccessController(webappUrl);

            if (accessController instanceof DefaultAccessController) {
                DefaultAccessController defaultAccessController =
                    (DefaultAccessController) accessController;

                accreditableManager = defaultAccessController.getAccreditableManager();

                Authorizer[] authorizers = defaultAccessController.getAuthorizers();
                for (int i = 0; i < authorizers.length; i++) {
                    if (authorizers[i] instanceof PolicyAuthorizer) {
                        PolicyAuthorizer policyAuthorizer = (PolicyAuthorizer) authorizers[i];
                        policyManager = policyAuthorizer.getPolicyManager();
                    }
                }
            }

                getLogger().debug("    Using policy manager [" + policyManager + "]");

        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

    public void startElement(
        String uri,
        String name,
        String qname,
        Attributes attrs)
        throws SAXException {
        AttributesImpl newAttrs = null;

        // FIXME: This pattern is extremely similar to the pattern in 
        // org.apache.lenya.cms.publication.xsp.DocumentReferencesHelper and has the 
        // same problems. See DocumentReferencesHelper#getInternalLinkPattern().
        Pattern pattern =
            Pattern.compile(
                envelope.getContext()
                    + "/"
                    + envelope.getPublication().getId()
                    + "/"
                    + Publication.AUTHORING_AREA
                    + "(/[-a-zA-Z0-9_/]+?)(_[a-z][a-z])?\\.html");

        if (lookingAtAElement(name)) {
            for (int i = 0, size = attrs.getLength(); i < size; i++) {
                String attrName = attrs.getLocalName(i);
                if (attrName.equals("href")) {
                    String value = attrs.getValue(i);

                    Matcher matcher = pattern.matcher(value);
                    if (matcher.find()) {
                        // yes, this is an internal link that we need to rewrite
                        if (newAttrs == null)
                            newAttrs = new AttributesImpl(attrs);

                        String languageExtension = "";
                        if (matcher.group(2) != null) {
                            languageExtension = matcher.group(2);
                        }

                        String documentId = matcher.group(1);
                        if (areaIsLive()
                            && !documentIsLive(documentId, languageExtension)) {
                            // check for SSL here:
                            //if ssl, add prefix, if not, ignore A
                            try {
                                String url = urlPrefix + documentId;
                                Policy policy = policyManager.getPolicy(accreditableManager, url);
                                if (policy.isSSLProtected()) {
                                        log.debug(" live is SSL protected");
                                 // add prefix   
                                    newAttrs.setValue(
                                            i,
                                            sslPrefix + documentId);
                                } else {
                                        log.debug(" live is NOT SSL protected");
                                    ignoreAElement = true;
                                }
                            }
                            catch (AccessControlException e) {
                              throw new SAXException(e);
                            }

                        } else {
                            // check for SSL here, too
                            // if ssl, add prefix to newhrefvalue, if not, take just newhrefvalue
                            try {
                                String url = urlPrefix + documentId;
                                String finalurl;
                                Policy policy = policyManager.getPolicy(accreditableManager, url);
                                if (policy.isSSLProtected()) {
                                 // add prefix   
                                        log.debug(" authoring is SSL protected");
                                        log.debug(" ssl prefix: " + sslPrefix);
                                        finalurl = sslPrefix + documentId + languageExtension + ".html";
                                        log.debug(" finalurl: " + finalurl);                                        
                                    newAttrs.setValue(
                                            i, finalurl
                                            );
                                } else {
                                        log.debug(" authoring is NOT SSL protected");
                                    newAttrs.setValue(
                                            i,
                                            getNewHrefValue(languageExtension, documentId));
                                }
                            }
                            catch (AccessControlException e) {
                              throw new SAXException(e);
                            }
                        }
                    }
                }
            }
        }

        if (!ignoreAElement) {
            if (newAttrs == null) {
                super.startElement(uri, name, qname, attrs);
            } else {
                super.startElement(uri, name, qname, newAttrs);
            }
        }
    }

    /** (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String name, String qname)
        throws SAXException {
        if (lookingAtAElement(name) && ignoreAElement) {
            ignoreAElement = false;
        } else {
            super.endElement(uri, name, qname);
        }
    }

    private boolean lookingAtAElement(String name) {
        return name.equals("a");
    }

    private boolean areaIsLive() {
        return envelope.getDocument().getArea().equals(Publication.LIVE_AREA);
    }

    /**
     * @param documentId
     * @return true if the specified document id is live
     */
    private boolean documentIsLive(String documentId, String languageExtension)
        throws SAXException {
        boolean result = false;
        DocumentBuilder builder =
            envelope.getPublication().getDocumentBuilder();

        // trim the '_'
        String language =
            (languageExtension == "") ? null : languageExtension.substring(1);
        String url =
            (language == null)
                ? builder.buildCanonicalUrl(
                    envelope.getPublication(),
                    Publication.LIVE_AREA,
                    documentId)
                : builder.buildCanonicalUrl(
                    envelope.getPublication(),
                    Publication.LIVE_AREA,
                    documentId,
                    language);
        try {
            result =
                builder.buildDocument(envelope.getPublication(), url).exists();
        } catch (PublicationException e) {
            throw new SAXException(e);
        }
        return result;
    }

    private String getNewHrefValue(
        String languageExtension,
        String documentId) {
        // FIXME: this should really use the documentBuilder to
        // build the url 
        return baseURI
            + "/"
            + envelope.getDocument().getArea()
            + documentId
            + languageExtension
            + ".html";
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Disposing transformer");
        }
        if (serviceSelector != null) {
            if (acResolver != null) {
                serviceSelector.release(acResolver);
            }
            manager.release(serviceSelector);
        }
    }

    public void recycle() {
        this.envelope = null;
        this.baseURI = null;
        this.ignoreAElement = false;
    }
}
