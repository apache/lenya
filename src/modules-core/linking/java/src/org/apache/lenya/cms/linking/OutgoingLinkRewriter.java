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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.StringUtil;

/**
 * <p>
 * Converts web application links to links which will be sent to the browser by
 * using the publication's proxy settings.
 * </p>
 * <p>
 * Objects of this class are not thread-safe.
 * </p>
 */
public class OutgoingLinkRewriter implements LinkRewriter {

    private static final String ATTRIBUTE_ROOT = "root";

    private boolean relativeUrls;
    private ServiceManager manager;
    private PolicyManager policyManager;
    private AccreditableManager accreditableManager;
    private DocumentFactory factory;
    private Publication publication;

    /**
     * @param manager The service manager to use.
     * @param session The current session.
     * @param requestUrl The request URL where the links should be rewritten.
     * @param relativeUrls If relative URLs should be created.
     */
    public OutgoingLinkRewriter(ServiceManager manager, Session session, String requestUrl,
            boolean relativeUrls) {
        
        this.manager = manager;
        this.requestUrl = requestUrl;
        this.relativeUrls = relativeUrls;

        ServiceSelector serviceSelector = null;
        AccessControllerResolver acResolver = null;

        try {
            this.factory = DocumentUtil.createDocumentFactory(this.manager, session);
            URLInformation info = new URLInformation(requestUrl);
            String pubId = info.getPublicationId();
            if (pubId != null && isPublication(pubId)) {
                this.publication = this.factory.getPublication(pubId);
            }
            
            serviceSelector = (ServiceSelector) this.manager.lookup(AccessControllerResolver.ROLE
                    + "Selector");
            acResolver = (AccessControllerResolver) serviceSelector
                    .select(AccessControllerResolver.DEFAULT_RESOLVER);
            AccessController accessController = acResolver.resolveAccessController(requestUrl);
            if (accessController != null) {
                this.accreditableManager = accessController.getAccreditableManager();
                this.policyManager = accessController.getPolicyManager();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (serviceSelector != null) {
                if (acResolver != null) {
                    serviceSelector.release(acResolver);
                }
                this.manager.release(serviceSelector);
            }
        }

    }

    public boolean matches(String url) {
        return url.startsWith("/");
    }

    public String rewrite(String url) {
        String rewrittenUrl = "";

        try {
            if (this.relativeUrls) {
                rewrittenUrl = getRelativeUrlTo(url);
            } else {
                boolean ssl = false;
                if (this.policyManager != null) {
                    Policy policy = this.policyManager.getPolicy(this.accreditableManager, url);
                    ssl = policy.isSSLProtected();
                }

                URLInformation info = new URLInformation(url);
                String pubId = info.getPublicationId();

                // link points to publication
                if (pubId != null && isPublication(pubId)) {
                    Publication pub = this.factory.getPublication(pubId);
                    rewrittenUrl = rewriteLink(url, pub, ssl);
                }

                // link doesn't point to publication -> use own publication if
                // exists
                else if (this.publication != null) {
                    rewrittenUrl = rewriteLink(url, this.publication, ssl);
                }

                // link doesn't point to publication, no own publication
                else {
                    rewrittenUrl = getContextPath() + url;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rewrittenUrl;
    }

    private String contextPath;

    private String requestUrl;
    
    protected String getContextPath() {
        if (this.contextPath == null) {
            ContextUtility context = null;
            try {
                context = (ContextUtility) this.manager.lookup(ContextUtility.ROLE);
                this.contextPath = context.getRequest().getContextPath();
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
            finally {
                if (context != null) {
                    this.manager.release(context);
                }
            }
        }
        return this.contextPath;
    }

    /**
     * @param linkUrl The original link URL.
     * @param pub The publication to use for proxy resolving.
     * @param ssl If the URL uses SSL.
     * @return A link URL.
     */
    protected String rewriteLink(String linkUrl, Publication pub, boolean ssl) {
        URLInformation info = new URLInformation(linkUrl);
        String rewrittenUrl;
        String areaName = info.getArea();

        // valid area
        if (areaName != null && Arrays.asList(pub.getAreaNames()).contains(areaName)) {
            Proxy proxy = pub.getProxy(areaName, ssl);
            rewrittenUrl = proxy.getUrl() + info.getDocumentUrl();
        }

        // invalid area
        else {
            Proxy proxy = pub.getProxy(ATTRIBUTE_ROOT, ssl);
            rewrittenUrl = proxy.getUrl() + linkUrl;
        }
        return rewrittenUrl;
    }

    protected String getRelativeUrlTo(String webappUrl) {
        List sourceSteps = toList(this.requestUrl);
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
        return new ArrayList(Arrays.asList(url.substring(1).split("/", -1)));
    }

    /**
     * @param pubId The publication ID.
     * @return If a publication with this ID exists.
     */
    protected boolean isPublication(String pubId) {
        Publication[] pubs = this.factory.getPublications();
        List pubIds = new ArrayList();
        for (int i = 0; i < pubs.length; i++) {
            pubIds.add(pubs[i].getId());
        }
        return pubIds.contains(pubId);
    }

}
