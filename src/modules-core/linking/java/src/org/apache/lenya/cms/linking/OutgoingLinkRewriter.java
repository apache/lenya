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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.StringUtil;

/**
 * <p>
 * Converts web application links to links which will be sent to the browser by using the
 * publication's proxy settings. If the current request is SSL-encrypted, all link URLs will use the
 * SSL proxy.
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
    private boolean considerSslPolicies;

    /**
     * @param manager The service manager to use.
     * @param session The current session.
     * @param requestUrl The requested web application URL (without servlet context path) where
     *        the links should be rewritten.
     * @param ssl If the current page is SSL-encrypted.
     * @param considerSslPolicies If the SSL protection of policies should be considered when
     *        resolving the corresponding proxy. Setting this to <code>true</code> leads to a
     *        substantial performance overhead.
     * @param relativeUrls If relative URLs should be created.
     */
    public OutgoingLinkRewriter(ServiceManager manager, Session session, String requestUrl,
            boolean ssl, boolean considerSslPolicies, boolean relativeUrls) {

        super(manager);
        this.requestUrl = requestUrl;
        this.relativeUrls = relativeUrls;
        this.ssl = ssl;
        this.considerSslPolicies = considerSslPolicies;

        ServiceSelector serviceSelector = null;
        AccessControllerResolver acResolver = null;

        try {
            this.factory = DocumentUtil.createDocumentFactory(this.manager, session);

            if (this.considerSslPolicies) {
                serviceSelector = (ServiceSelector) this.manager
                        .lookup(AccessControllerResolver.ROLE + "Selector");
                acResolver = (AccessControllerResolver) serviceSelector
                        .select(AccessControllerResolver.DEFAULT_RESOLVER);
                AccessController accessController = acResolver.resolveAccessController(requestUrl);
                if (accessController != null) {
                    this.accreditableManager = accessController.getAccreditableManager();
                    this.policyManager = accessController.getPolicyManager();
                }
            }

            Publication[] pubs = this.factory.getPublications();
            for (int i = 0; i < pubs.length; i++) {
                this.publicationCache.put(pubs[i].getId(), pubs[i]);
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

    private Map publicationCache = new HashMap();

    protected Publication getPublication(String pubId) throws PublicationException {
        return (Publication) this.publicationCache.get(pubId);
    }

    public String rewrite(final String url) {

        String rewrittenUrl = "";
        
        String path;
        String suffix;
        
        int numIndex = url.indexOf('#');
        if (numIndex > -1) {
            path = url.substring(0, numIndex);
            suffix = url.substring(numIndex);
        }
        else {
            int qmIndex = url.indexOf('?');
            if (qmIndex > -1) {
                path = url.substring(0, qmIndex);
                suffix = url.substring(qmIndex);
            }
            else {
                path = url;
                suffix = "";
            }
        }
        
        try {
            String normalizedUrl = normalizeUrl(path);
            if (this.relativeUrls) {
                rewrittenUrl = getRelativeUrlTo(normalizedUrl);
            } else {
                boolean useSsl = this.ssl;
                if (!useSsl && this.policyManager != null) {
                    Policy policy = this.policyManager.getPolicy(this.accreditableManager,
                            normalizedUrl);
                    useSsl = policy.isSSLProtected();
                }

                URLInformation info = new URLInformation(normalizedUrl);
                String pubId = info.getPublicationId();

                Publication pub = null;
                if (pubId != null) {
                    pub = getPublication(pubId);
                }

                // link points to publication
                if (pub != null) {
                    rewrittenUrl = rewriteLink(normalizedUrl, pub, useSsl);
                }

                // link doesn't point to publication
                else {
                    Proxy proxy = getGlobalProxies().getProxy(ssl);
                    rewrittenUrl = proxy.getUrl() + normalizedUrl;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rewrittenUrl + suffix;
    }

    protected String normalizeUrl(final String url) throws URISyntaxException {
        String normalizedUrl;
        if (url.indexOf("..") > -1) {
            normalizedUrl = new URI(url).normalize().toString();
        } else {
            normalizedUrl = url;
        }
        return normalizedUrl;
    }

    private String requestUrl;

    private Map pubId2areaList = new HashMap();

    /**
     * Checks if a publication has an area by using a cache for performance reasons.
     * @param pub The publication.
     * @param area The area name.
     * @return if the publication contains the area.
     */
    protected boolean hasArea(Publication pub, String area) {
        String pubId = pub.getId();
        List areas = (List) this.pubId2areaList.get(pubId);
        if (areas == null) {
            areas = Arrays.asList(pub.getAreaNames());
            this.pubId2areaList.put(pubId, areas);
        }
        return areas.contains(area);
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
        if (areaName != null && hasArea(pub, areaName)) {
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
        String relativeUrl;
        if (this.requestUrl.equals(webappUrl)) {
            relativeUrl = getLastStep(webappUrl);
        }
        else {
            List sourceSteps = toList(this.requestUrl);
            List targetSteps = toList(webappUrl);
            
            String lastEqualStep = null;

            while (!sourceSteps.isEmpty() && !targetSteps.isEmpty()
                    && sourceSteps.get(0).equals(targetSteps.get(0))) {
                lastEqualStep = (String) sourceSteps.remove(0);
                targetSteps.remove(0);
            }

            String prefix = "";
            if (targetSteps.isEmpty()) {
                prefix = generateUpDots(sourceSteps.size());
            }
            else if (sourceSteps.isEmpty()) {
                prefix = getLastStep(this.requestUrl) + "/";
            }
            else if (sourceSteps.size() > 1) {
                prefix = generateUpDots(sourceSteps.size() - 1) + "/";
            }
            else if (sourceSteps.size() == 1 && targetSteps.get(0).equals("")) {
                prefix = generateUpDots(1) + "/" + lastEqualStep + "/";
            }

            String[] targetArray = (String[]) targetSteps.toArray(new String[targetSteps.size()]);
            String targetPath = StringUtil.join(targetArray, "/");
            relativeUrl = prefix + targetPath;
        }
        return relativeUrl;
    }

    protected String getLastStep(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    protected String generateUpDots(int length) {
        String upDots;
        String[] upDotsArray = new String[length];
        Arrays.fill(upDotsArray, "..");
        upDots = StringUtil.join(upDotsArray, "/");
        return upDots;
    }

    protected List toList(String url) {
        return new ArrayList(Arrays.asList(url.substring(1).split("/", -1)));
    }

}
