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
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
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
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.util.StringUtil;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>
 * Proxy transformer.
 * </p>
 * <p>
 * The resulting URLs can either be absolute (default) or relative. You can
 * either configure this when declaring the transformer:
 * </p>
 * <code><pre>
 *   &lt;map:transformer ... &gt;
 *     &lt;urls type=&quot;relative&quot;/&gt;
 *     ...
 *   &lt;/map:transformer&gt;
 * </pre></code>
 * <p>
 * or pass a parameter:
 * </p>
 * <code><pre>
 *   &lt;map:parameter name=&quot;urls&quot; value=&quot;relative&quot;/&gt;
 * </pre></code>
 */
public class ProxyTransformer extends AbstractLinkTransformer implements Disposable {

    protected static final String URL_TYPE_ABSOLUTE = "absolute";
    protected static final String URL_TYPE_RELATIVE = "relative";
    protected static final String PARAMETER_URLS = "urls";
    
    private DocumentFactory factory;
    private String url;
    private ServiceSelector serviceSelector;
    private AccessControllerResolver acResolver;
    private AccreditableManager accreditableManager;
    private PolicyManager policyManager;
    private Publication publication;
    private boolean relativeUrls = false;

    protected static final String PARAMETER_FACTORY = "private.factory";
    private static final String ATTRIBUTE_ROOT = "root";

    public void setup(SourceResolver _resolver, Map _objectModel, String _source,
            Parameters _parameters) throws ProcessingException, SAXException, IOException {
        super.setup(_resolver, _objectModel, _source, _parameters);
        Request _request = ObjectModelHelper.getRequest(_objectModel);

        try {
            if (_parameters.isParameter(PARAMETER_URLS)) {
                setUrlType(_parameters.getParameter(PARAMETER_URLS));
            }

            Session session = RepositoryUtil.getSession(this.manager, _request);
            this.factory = DocumentUtil.createDocumentFactory(this.manager, session);
            this.url = ServletHelper.getWebappURI(_request);
            URLInformation info = new URLInformation(this.url);
            String pubId = info.getPublicationId();
            if (pubId != null && isPublication(pubId)) {
                this.publication = this.factory.getPublication(pubId);
            }
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

    protected void handleLink(String linkUrl, AttributeConfiguration config, AttributesImpl newAttrs)
            throws Exception {
        if (linkUrl.startsWith("/")) {
            rewriteLink(newAttrs, config.attribute, linkUrl);
        }
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

    protected void rewriteLink(AttributesImpl newAttrs, String attributeName, String linkUrl)
            throws Exception {
        String rewrittenUrl = "";

        if (this.relativeUrls) {
            rewrittenUrl = getRelativeUrlTo(linkUrl);
        } else {
            boolean ssl = false;
            if (this.policyManager != null) {
                Policy policy = this.policyManager.getPolicy(this.accreditableManager, linkUrl);
                ssl = policy.isSSLProtected();
            }

            URLInformation info = new URLInformation(linkUrl);
            String pubId = info.getPublicationId();

            // link points to publication
            if (pubId != null && isPublication(pubId)) {
                Publication pub = this.factory.getPublication(pubId);
                rewrittenUrl = rewriteLink(linkUrl, pub, ssl);
            }

            // link doesn't point to publication -> use own publication if
            // exists
            else if (this.publication != null) {
                rewrittenUrl = rewriteLink(linkUrl, this.publication, ssl);
            }

            // link doesn't point to publication, no own publication
            else {
                rewrittenUrl = this.request.getContextPath() + linkUrl;
            }
        }

        setAttribute(newAttrs, attributeName, rewrittenUrl);
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
            String proxiedUrl = info.getDocumentUrl();
            rewrittenUrl = getProxyUrl(linkUrl, proxy, proxiedUrl);
        }

        // invalid area
        else {
            Proxy proxy = pub.getProxy(ATTRIBUTE_ROOT, ssl);
            rewrittenUrl = getProxyUrl(linkUrl, proxy, linkUrl);
        }
        return rewrittenUrl;
    }

    /**
     * @param linkUrl The complete link URL.
     * @param proxy The proxy (may be null).
     * @param proxiedUrl The URL to append to the proxy URL.
     * @return Either {proxy.url}{proxiedUrl} (if proxy != null) or
     *         {contextPath}{linkUrl} (if proxy == null).
     */
    protected String getProxyUrl(String linkUrl, Proxy proxy, String proxiedUrl) {
        String rewrittenUrl;
        if (proxy == null) {
            rewrittenUrl = this.request.getContextPath() + linkUrl;
        } else {
            rewrittenUrl = proxy.getUrl() + proxiedUrl;
        }
        return rewrittenUrl;
    }

    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        Configuration urlConfig = config.getChild(PARAMETER_URLS, false);
        if (urlConfig != null) {
            String value = urlConfig.getAttribute("type");
            setUrlType(value);
        }
    }

    protected void setUrlType(String value) throws ConfigurationException {
        if (value.equals(URL_TYPE_RELATIVE)) {
            this.relativeUrls = true;
        } else if (value.equals(URL_TYPE_ABSOLUTE)) {
            this.relativeUrls = false;
        } else {
            throw new ConfigurationException("Invalid URL type [" + value
                    + "], must be relative or absolute.");
        }
    }

    protected String getRelativeUrlTo(String webappUrl) {
        List sourceSteps = toList(this.url);
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
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (this.serviceSelector != null) {
            if (this.acResolver != null) {
                this.serviceSelector.release(this.acResolver);
            }
            this.manager.release(this.serviceSelector);
        }
    }

}
