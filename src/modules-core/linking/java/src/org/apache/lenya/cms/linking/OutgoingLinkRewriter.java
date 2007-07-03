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
 * using the publication's proxy settings. If the current request is
 * SSL-encrypted, all link URLs will use the SSL proxy.
 * </p>
 * <p>
 * Objects of this class are not thread-safe.
 * </p>
 */
public class OutgoingLinkRewriter extends ServletLinkRewriter {

    private boolean relativeUrls;
    private PolicyManager policyManager;
    private AccreditableManager accreditableManager;
    private DocumentFactory factory;
    private boolean ssl;
    private GlobalProxies globalProxies;

    /**
     * @param manager The service manager to use.
     * @param session The current session.
     * @param requestUrl The request URL where the links should be rewritten.
     * @param ssl If the current page is SSL-encrypted.
     * @param relativeUrls If relative URLs should be created.
     */
    public OutgoingLinkRewriter(ServiceManager manager, Session session, String requestUrl,
            boolean ssl, boolean relativeUrls) {

        super(manager);
        this.requestUrl = requestUrl;
        this.relativeUrls = relativeUrls;
        this.ssl = ssl;

        ServiceSelector serviceSelector = null;
        AccessControllerResolver acResolver = null;

        try {
            this.factory = DocumentUtil.createDocumentFactory(this.manager, session);

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

    protected GlobalProxies getGlobalProxies() {
        if (this.globalProxies == null) {
            try {
                this.globalProxies = (GlobalProxies) this.manager.lookup(GlobalProxies.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.globalProxies;
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
                boolean useSsl = this.ssl;
                if (!useSsl && this.policyManager != null) {
                    Policy policy = this.policyManager.getPolicy(this.accreditableManager, url);
                    useSsl = policy.isSSLProtected();
                }

                URLInformation info = new URLInformation(url);
                String pubId = info.getPublicationId();

                // link points to publication
                if (pubId != null && isPublication(pubId)) {
                    Publication pub = this.factory.getPublication(pubId);
                    rewrittenUrl = rewriteLink(url, pub, useSsl);
                }

                // link doesn't point to publication
                else {
                    Proxy proxy = getGlobalProxies().getProxy(ssl);
                    rewrittenUrl = proxy.getUrl() + url;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rewrittenUrl;
    }

    private String requestUrl;

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
            Proxy proxy = getGlobalProxies().getProxy(ssl);
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
