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

import org.apache.avalon.framework.parameters.Parameters;
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
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Proxy transformer.
 */
public class ProxyTransformer extends AbstractLinkTransformer {
    
    private DocumentFactory factory;
    private Publication publication;
    private String url;
    private ServiceSelector serviceSelector;
    private AccessControllerResolver acResolver;
    private AccreditableManager accreditableManager;
    private PolicyManager policyManager;

    protected static final String PARAMETER_FACTORY = "private.factory";
    private static final String ATTRIBUTE_ROOT = "root";

    public void setup(SourceResolver _resolver, Map _objectModel, String _source,
            Parameters _parameters) throws ProcessingException, SAXException, IOException {
        super.setup(_resolver, _objectModel, _source, _parameters);
        Request _request = ObjectModelHelper.getRequest(_objectModel);

        try {
            Session session = RepositoryUtil.getSession(this.manager, _request);
            this.factory = DocumentUtil.createDocumentFactory(this.manager, session);
            this.url = ServletHelper.getWebappURI(_request);
            this.publication = PublicationUtil.getPublicationFromUrl(this.manager, factory, url);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        this.serviceSelector = null;
        try {
            this.serviceSelector = (ServiceSelector) this.manager
                    .lookup(AccessControllerResolver.ROLE + "Selector");
            this.acResolver = (AccessControllerResolver) this.serviceSelector
                    .select(AccessControllerResolver.DEFAULT_RESOLVER);
            AccessController accessController = this.acResolver.resolveAccessController(url);
            if (accessController != null) {
                this.accreditableManager = accessController.getAccreditableManager();
                this.policyManager = accessController.getPolicyManager();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected void handleLink(String linkUrl, AttributeConfiguration config, AttributesImpl newAttrs) throws Exception {
        if (linkUrl.startsWith("/")) {
            rewriteLink(newAttrs, config.attribute, linkUrl);
        }
    }

    private void rewriteLink(AttributesImpl newAttrs, String attributeName, String linkUrl)
            throws AccessControlException, DocumentBuildException {
        String rewrittenURL = "";
        Policy policy = null;
        if (policyManager != null)
            policy = this.policyManager.getPolicy(this.accreditableManager, linkUrl);
        String area = "";
        if (factory.isDocument(linkUrl)) {
            area = factory.getFromURL(linkUrl).getArea();
        }
        if (PublicationUtil.isValidArea(area)) {
            Proxy proxy = this.publication.getProxy(area, policy.isSSLProtected());
            if (proxy == null) {
                rewrittenURL = this.request.getContextPath() + linkUrl;
            } else {
                String prefix = "/" + publication.getId() + "/" + area;
                if (linkUrl.startsWith(prefix))
                    rewrittenURL = proxy.getUrl() + linkUrl.substring(prefix.length());
                else
                    rewrittenURL = proxy.getUrl() + linkUrl;
            }
            if (getLogger().isDebugEnabled()) {
                getLogger()
                        .debug(this.indent + "SSL protection: [" + policy.isSSLProtected() + "]");
                getLogger().debug(this.indent + "Resolved proxy: [" + proxy + "]");
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this.indent + "Rewriting URL to: [" + rewrittenURL + "]");
            }
        } else {
            // Since we came here the link is not covered by the area proxies.
            // Now we try the global proxy for the pub of our initial request.
            Proxy proxy = this.publication.getProxy(ATTRIBUTE_ROOT, (policy == null) ? false
                    : policy.isSSLProtected());
            if (proxy == null) {
                rewrittenURL = this.request.getContextPath() + linkUrl;
            } else {
                rewrittenURL = proxy.getUrl() + linkUrl.substring(1);
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this.indent + "Rewriting URL to: [" + rewrittenURL + "]");
            }
        }
        setAttribute(newAttrs, attributeName, rewrittenURL);
    }

}
