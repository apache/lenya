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

package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.parameters.ParameterException;
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
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.ac.impl.PolicyAuthorizer;
import org.apache.lenya.cms.site.tree.DefaultSiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNodeImpl;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This transformer is applied to the sitetree.
 * It marks the site element and all node elements the
 * current identity is not allowed to access 
 * with a <code>protected="true"</code> attribute.
 */
public class AccessControlSitetreeTransformer
    extends AbstractSAXTransformer
    implements Disposable {

    /**
     * <code>ATTRIBUTE_PROTECTED</code> The attribute for protected
     */
    public static final String ATTRIBUTE_PROTECTED = "protected";
    /**
     * <code>PARAMETER_PUBLICATION_ID</code> The publication id parameter
     */
    public static final String PARAMETER_PUBLICATION_ID = "publication-id";
    /**
     * <code>PARAMETER_AREA</code> The area parameter
     */
    public static final String PARAMETER_AREA = "area";

    private String documentId;
    private ServiceSelector serviceSelector;
    private PolicyManager policyManager;
    private AccessControllerResolver acResolver;
    private AccreditableManager accreditableManager;
    private Identity identity;
    private String urlPrefix;

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver _resolver, Map _objectModel, String src, Parameters par)
        throws ProcessingException, SAXException, IOException {
        super.setup(_resolver, _objectModel, src, par);

        this.serviceSelector = null;
        this.acResolver = null;
        this.policyManager = null;

        this.identity = Identity.getIdentity(this.request.getSession(false));

        try {
            String publicationId = par.getParameter(PARAMETER_PUBLICATION_ID);
            String area = par.getParameter(PARAMETER_AREA);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Setting up transformer");
                getLogger().debug("    Identity:       [" + this.identity + "]");
                getLogger().debug("    Publication ID: [" + publicationId + "]");
                getLogger().debug("    Area:           [" + area + "]");
            }

            this.urlPrefix = "/" + publicationId + "/" + area;

            Request _request = ObjectModelHelper.getRequest(_objectModel);

            this.serviceSelector =
                (ServiceSelector) this.manager.lookup(AccessControllerResolver.ROLE + "Selector");

            this.acResolver =
                (AccessControllerResolver) this.serviceSelector.select(
                    AccessControllerResolver.DEFAULT_RESOLVER);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("    Resolved AC resolver [" + this.acResolver + "]");
            }

            String webappUrl = ServletHelper.getWebappURI(_request);
            AccessController accessController = this.acResolver.resolveAccessController(webappUrl);

            if (accessController instanceof DefaultAccessController) {
                DefaultAccessController defaultAccessController =
                    (DefaultAccessController) accessController;

                this.accreditableManager = defaultAccessController.getAccreditableManager();

                Authorizer[] authorizers = defaultAccessController.getAuthorizers();
                for (int i = 0; i < authorizers.length; i++) {
                    if (authorizers[i] instanceof PolicyAuthorizer) {
                        PolicyAuthorizer policyAuthorizer = (PolicyAuthorizer) authorizers[i];
                        this.policyManager = policyAuthorizer.getPolicyManager();
                    }
                }
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("    Using policy manager [" + this.policyManager + "]");
            }
        } catch (final ParameterException e) {
            throw new ProcessingException(e);
        } catch (final ServiceException e) {
            throw new ProcessingException(e);
        } catch (final AccessControlException e) {
            throw new ProcessingException(e);
        }

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
    }

    /**
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        super.startDocument();
        this.documentId = "";
    }

    /** (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String raw, Attributes attr)
        throws SAXException {

        Attributes attributes = attr;

        if (isFragmentNode(uri, localName)) {
            String area = attr.getValue("area"); // FIXME: don't hardcode
            String base = attr.getValue("base");
            if (area!=null && base!=null) {
                this.documentId = "/"+area+base;
            }
        }
        if (isNode(uri, localName)) {
            String id = attr.getValue(SiteTreeNodeImpl.ID_ATTRIBUTE_NAME);
            if (id != null) {
                this.documentId += "/" + id;
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Checking node");
                getLogger().debug("    Document ID: [" + this.documentId + "]");
                getLogger().debug("    URL:         [" + this.urlPrefix + this.documentId + "]");
            }

            try {
                String url = this.urlPrefix + this.documentId;
                Policy policy = this.policyManager.getPolicy(this.accreditableManager, url);
                Role[] roles = policy.getRoles(this.identity);

                getLogger().debug("    Roles:       [" + roles.length + "]");

                if (roles.length == 0) {
                    getLogger().debug("    Adding attribute [protected='true']");

                    AttributesImpl attributesImpl = new AttributesImpl(attributes);
                    attributesImpl.addAttribute(
                        "",
                        ATTRIBUTE_PROTECTED,
                        ATTRIBUTE_PROTECTED,
                        "",
                        Boolean.toString(true));
                    attributes = attributesImpl;
                }
            } catch (AccessControlException e) {
                throw new SAXException(e);
            }
        }

        super.startElement(uri, localName, raw, attributes);
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String raw) throws SAXException {
        super.endElement(uri, localName, raw);
        if (isNode(uri, localName) && this.documentId.length() > 0) {
            this.documentId = this.documentId.substring(0, this.documentId.lastIndexOf("/"));
        }
    }

    /**
     * Returns if an element represents a sitetree node.
     * @param uri The namespace URI.
     * @param localName The local name.
     * @return A boolean value.
     */
    protected boolean isNode(String uri, String localName) {
        return uri.equals(DefaultSiteTree.NAMESPACE_URI)
            && (localName.equals(SiteTreeNodeImpl.NODE_NAME) || localName.equals("site"));
    }

   /**
     * Returns if an element represents a fragment node.
     * @param uri The namespace URI.
     * @param localName The local name.
     * @return A boolean value.
     */
    protected boolean isFragmentNode(String uri, String localName) {
        return uri.equals(DefaultSiteTree.NAMESPACE_URI)
            && (localName.equals("fragment"));
    }

}
