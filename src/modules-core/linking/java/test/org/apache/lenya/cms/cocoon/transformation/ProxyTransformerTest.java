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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.components.serializers.XHTMLSerializer;
import org.apache.cocoon.components.source.SourceResolverAdapter;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.templating.Instantiator;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ProxyTransformerTest extends AbstractAccessControlTest {

    protected static final String PUBCONF_NAMESPACE = "http://apache.org/cocoon/lenya/publication/1.1";
    
    protected static final String NAMESPACE = XHTMLSerializer.XHTML1_NAMESPACE;
    protected static final String ELEMENT = "a";
    protected static final String ATTRIBUTE = "href";
    
    protected String getWebappUrl() {
        return "/default/authoring/index.html";
    }

    public void testProxyTransformer() throws Exception {

        ProxyTransformer transformer = new ProxyTransformer();
        transformer.enableLogging(getLogger());
        transformer.service(getManager());
        
        String pubId = "mock";
        String area = "authoring";
        String proxyUrl = "http://www.apache-lenya-proxy-test.org";

        createMockPublication(pubId, area, proxyUrl);

        Context context = this.context;
        Map objectModel = (Map) context.get(ContextHelper.CONTEXT_OBJECT_MODEL);

        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
            transformer.setup(new SourceResolverAdapter(resolver), objectModel, "",
                    new Parameters());

            String documentUrl = "/index.html";
            String linkUrl = "/" + pubId + "/" + area + documentUrl;
            String targetUrl = proxyUrl + documentUrl;
            rewriteLink(transformer, linkUrl, targetUrl);
            
            String cssUrl = "/lenya/foo.css";
            rewriteLink(transformer, cssUrl, cssUrl);

            String moduleUrl = "/modules/foo/bar.html?x=y";
            rewriteLink(transformer, moduleUrl, moduleUrl);
            
        } finally {
            if (resolver != null) {
                getManager().release(resolver);
            }
        }
    }

    protected void rewriteLink(ProxyTransformer transformer, String linkUrl, String targetUrl) throws Exception {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", ATTRIBUTE, ATTRIBUTE, "string", linkUrl);

        AbstractLinkTransformer.AttributeConfiguration config = new AbstractLinkTransformer.AttributeConfiguration(
                NAMESPACE, ELEMENT, ATTRIBUTE);

        transformer.handleLink(linkUrl, config, attrs);

        String rewrittenUrl = attrs.getValue(ATTRIBUTE);
        assertEquals(rewrittenUrl, targetUrl);
    }

    protected void createMockPublication(String pubId, String area, String proxyUrl)
            throws PublicationException, ServiceException, Exception {
        if (!existsPublication(pubId)) {

            Publication defaultPub = getPublication("default");
            Instantiator instantiator = null;
            ServiceSelector selector = null;
            try {
                selector = (ServiceSelector) getManager().lookup(Instantiator.ROLE + "Selector");
                instantiator = (Instantiator) selector.select(defaultPub.getInstantiatorHint());
                instantiator.instantiate(defaultPub, pubId, "Mock");
                configureProxy(area, proxyUrl);
            } finally {
                if (selector != null) {
                    if (instantiator != null) {
                        selector.release(instantiator);
                    }
                    getManager().release(selector);
                }
            }
        }
    }

    protected void configureProxy(String area, String proxyUrl)
            throws ServiceException, SourceNotFoundException, ParserConfigurationException,
            SAXException, IOException, TransformerConfigurationException, TransformerException,
            MalformedURLException {
        String configUri = "context://lenya/pubs/mock/config/publication.xml";
        Document dom = SourceUtil.readDOM(configUri, getManager());
        NamespaceHelper helper = new NamespaceHelper(PUBCONF_NAMESPACE, "", dom);
        
        Element docElem = dom.getDocumentElement();
        Element instantiatorElem = helper.getFirstChild(docElem, "template-instantiator");
        if (instantiatorElem != null) {
            docElem.removeChild(instantiatorElem);
        }

        Element proxies = helper.getFirstChild(docElem, "proxies");
        if (proxies == null) {
            proxies = helper.createElement("proxies");
            docElem.appendChild(proxies);
        }

        addProxyElement(helper, proxies, area, proxyUrl, false);
        addProxyElement(helper, proxies, area, proxyUrl, true);

        SourceUtil.writeDOM(dom, configUri, getManager());
    }

    protected void addProxyElement(NamespaceHelper helper, Element proxies, String area,
            String proxyUrl, boolean ssl) {
        Element proxyElement = helper.createElement("proxy");
        proxyElement.setAttribute("ssl", Boolean.toString(ssl));
        proxyElement.setAttribute("area", area);
        proxyElement.setAttribute("url", proxyUrl);
        proxies.appendChild(proxyElement);
    }

    protected boolean existsPublication(String pubId) {
        Publication[] pubs = getFactory().getPublications();
        List pubIds = new ArrayList();
        for (int i = 0; i < pubs.length; i++) {
            pubIds.add(pubs[i].getId());
        }
        return pubIds.contains(pubId);
    }
}
