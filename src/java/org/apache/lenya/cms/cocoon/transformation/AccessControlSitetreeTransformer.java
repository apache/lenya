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

/* $Id: AccessControlSitetreeTransformer.java,v 1.7 2004/03/01 16:18:20 gregor Exp $  */

package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.parameters.Parameters;
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
import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.SiteTreeNodeImpl;
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

    public static final String ATTRIBUTE_PROTECTED = "protected";
    public static final String PARAMETER_PUBLICATION_ID = "publication-id";
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
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
        throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, par);

        serviceSelector = null;
        acResolver = null;
        policyManager = null;

        identity = Identity.getIdentity(request.getSession(false));

        try {
            String publicationId = par.getParameter(PARAMETER_PUBLICATION_ID);
            String area = par.getParameter(PARAMETER_AREA);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Setting up transformer");
                getLogger().debug("    Identity:       [" + identity + "]");
                getLogger().debug("    Publication ID: [" + publicationId + "]");
                getLogger().debug("    Area:           [" + area + "]");
            }

            urlPrefix = "/" + publicationId + "/" + area;

            Request request = ObjectModelHelper.getRequest(objectModel);

            serviceSelector =
                (ServiceSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");

            acResolver =
                (AccessControllerResolver) serviceSelector.select(
                    AccessControllerResolver.DEFAULT_RESOLVER);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("    Resolved AC resolver [" + acResolver + "]");
            }

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

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("    Using policy manager [" + policyManager + "]");
            }
        } catch (Exception e) {
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
        if (serviceSelector != null) {
            if (acResolver != null) {
                serviceSelector.release(acResolver);
            }
            manager.release(serviceSelector);
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
        super.startDocument();
        documentId = "";
    }

    /** (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String raw, Attributes attr)
        throws SAXException {

        Attributes attributes = attr;

        if (isNode(uri, localName)) {
            String id = attr.getValue(SiteTreeNodeImpl.ID_ATTRIBUTE_NAME);
            if (id != null) {
                documentId += "/" + id;
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Checking node");
                getLogger().debug("    Document ID: [" + documentId + "]");
                getLogger().debug("    URL:         [" + urlPrefix + documentId + "]");
            }

            try {
                String url = urlPrefix + documentId;
                Policy policy = policyManager.getPolicy(accreditableManager, url);
                Role[] roles = policy.getRoles(identity);

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
        if (isNode(uri, localName) && documentId.length() > 0) {
            documentId = documentId.substring(0, documentId.lastIndexOf("/"));
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

}
