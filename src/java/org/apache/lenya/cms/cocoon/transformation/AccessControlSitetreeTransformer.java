/*
$Id: AccessControlSitetreeTransformer.java,v 1.3 2004/02/17 14:06:25 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.cocoon.transformation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.ComponentSelector;
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
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 * @version CVS $Id: AccessControlSitetreeTransformer.java,v 1.3 2004/02/17 14:06:25 andreas Exp $
 */
public class AccessControlSitetreeTransformer
    extends AbstractSAXTransformer
    implements Disposable {

    public static final String ATTRIBUTE_PROTECTED = "protected";
    public static final String PARAMETER_PUBLICATION_ID = "publication-id";
    public static final String PARAMETER_AREA = "area";

    private String documentId;
    private ComponentSelector componentSelector;
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

        componentSelector = null;
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

            Object selector = manager.lookup(AccessControllerResolver.ROLE + "Selector");

            if (selector instanceof ComponentSelector) {
                componentSelector = (ComponentSelector) selector;
                acResolver =
                    (AccessControllerResolver) componentSelector.select(
                        AccessControllerResolver.DEFAULT_RESOLVER);
            }
            if (selector instanceof ServiceSelector) {
                serviceSelector = (ServiceSelector) selector;
                acResolver =
                    (AccessControllerResolver) serviceSelector.select(
                        AccessControllerResolver.DEFAULT_RESOLVER);
            }

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
        if (componentSelector != null) {
            if (acResolver != null) {
                componentSelector.release(acResolver);
            }
            manager.release(componentSelector);
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

                //                if (getLogger().isDebugEnabled()) {
                getLogger().debug("    Roles:       [" + roles.length + "]");
                //                }

                if (roles.length == 0) {
                    //                    if (getLogger().isDebugEnabled()) {
                    getLogger().debug("    Adding attribute [protected='true']");
                    //                    }

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
