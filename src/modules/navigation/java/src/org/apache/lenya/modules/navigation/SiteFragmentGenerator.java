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
package org.apache.lenya.modules.navigation;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceValidity;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Generate a fragment of the site.
 */
public class SiteFragmentGenerator extends ServiceableGenerator implements
        CacheableProcessingComponent, Parameterizable, NodeGenerator {

    protected static final String PARAM_PUB = "pub";
    protected static final String PARAM_AREA = "area";
    protected static final String PARAM_LANG = "lang";
    protected static final String PARAM_PATH = "path";
    protected static final String PARAM_SELECTOR_PATH = "selectorPath";
    protected static final String PARAM_SELECTOR = "selector";

    protected static final String PREFIX = "site";
    protected static final String NAMESPACE = "http://apache.org/lenya/site/1.0";
    protected static final String ATTR_PUB = "pub";
    protected static final String ATTR_AREA = "area";
    protected static final String ELEM_FRAGMENT = "fragment";
    protected static final String ELEM_NODE = "node";
    protected static final String ELEM_LINK = "link";
    protected static final String ATTR_UUID = "uuid";
    protected static final String ATTR_NAME = "name";
    protected static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
    protected static final String XML_PREFIX = "xml";
    protected static final String ATTR_LANG = "lang";
    protected static final String ATTR_LABEL = "label";
    protected static final String ATTR_CURRENT = "current";
    protected static final String ATTR_ANCESTOR_OF_CURRENT = "ancestorOfCurrent";
    protected static final String ATTR_HREF = "href";

    private SiteStructure site;
    private String cacheKey;
    private SourceValidity validity;
    private String language;
    private String selectorClass;
    private String path;
    private String selectorPath;

    public void setup(org.apache.cocoon.environment.SourceResolver resolver, Map objectModel,
            String src, Parameters params) throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, params);

        Request request = ObjectModelHelper.getRequest(objectModel);

        Source source = null;
        try {
            String pubId = params.getParameter(PARAM_PUB);
            String area = params.getParameter(PARAM_AREA);
            this.language = params.getParameter(PARAM_LANG);
            this.path = params.getParameter(PARAM_PATH);
            this.selectorPath = params.getParameter(PARAM_SELECTOR_PATH, "");

            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            Publication pub = factory.getPublication(pubId);
            this.site = pub.getArea(area).getSite();

            this.cacheKey = pubId + "/" + area;
            source = resolver.resolveURI(this.site.getRepositoryNode().getSourceURI());
            this.validity = source.getValidity();

        } catch (Exception e) {
            throw new ProcessingException("Could not setup transformer: ", e);
        } finally {
            if (source != null) {
                resolver.release(source);
            }
        }
    }

    /**
     * @return The site structure to generate from.
     */
    protected SiteStructure getSite() {
        return this.site;
    }

    /**
     * @return The language to generate the fragment for.
     */
    protected String getLanguage() {
        return this.language;
    }

    public Serializable getKey() {
        if (this.cacheKey == null) {
            throw new IllegalStateException("setup() has not been called.");
        }
        return this.cacheKey;
    }

    public SourceValidity getValidity() {
        if (this.validity == null) {
            throw new IllegalStateException("setup() has not been called.");
        }
        return this.validity;
    }

    protected String getPath() {
        return this.path;
    }

    public void generate() throws IOException, SAXException, ProcessingException {

        this.contentHandler.startDocument();
        this.contentHandler.startPrefixMapping(PREFIX, NAMESPACE);

        AttributesImpl attrs = new AttributesImpl();
        addAttribute(attrs, ATTR_PUB, this.site.getPublication().getId());
        addAttribute(attrs, ATTR_AREA, this.site.getArea());
        this.contentHandler.startElement(NAMESPACE, ELEM_FRAGMENT, PREFIX + ':' + ELEM_FRAGMENT,
                attrs);

        generateFragment();

        this.contentHandler.endElement(NAMESPACE, ELEM_FRAGMENT, PREFIX + ':' + ELEM_FRAGMENT);

        this.contentHandler.endPrefixMapping(PREFIX);
        this.contentHandler.endDocument();

    }

    protected void generateFragment() throws ProcessingException {
        try {
            FragmentSelector selector = (FragmentSelector) Class.forName(this.selectorClass)
                    .newInstance();
            selector.selectFragment(this, getSite(), this.selectorPath, getLanguage());
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

    public void startNode(SiteNode node) throws SAXException {
        AttributesImpl attrs = new AttributesImpl();
        addAttribute(attrs, ATTR_UUID, node.getUuid());
        addAttribute(attrs, ATTR_NAME, node.getName());

        if (getPath().startsWith(node.getPath() + "/")) {
            addAttribute(attrs, ATTR_ANCESTOR_OF_CURRENT, Boolean.toString(true));
        }

        this.contentHandler.startElement(NAMESPACE, ELEM_NODE, PREFIX + ':' + ELEM_NODE, attrs);

    }

    public void endNode(SiteNode node) throws SAXException {
        this.contentHandler.endElement(NAMESPACE, ELEM_NODE, PREFIX + ':' + ELEM_NODE);
    }

    public void generateLink(SiteNode node, String language) throws SAXException {
        if (node.isVisible() && node.hasLink(language)) {
            Link link = getLink(node, language);
            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute(XML_NAMESPACE, ATTR_LANG, XML_PREFIX + ":" + ATTR_LANG, "CDATA",
                    link.getLanguage());
            addAttribute(attrs, ATTR_LABEL, link.getLabel());
            addAttribute(attrs, ATTR_HREF, getHref(link).getUri());
            
            if (node.getPath().equals(getPath()) && language.equals(getLanguage())) {
                addAttribute(attrs, ATTR_CURRENT, Boolean.toString(true));
            }

            this.contentHandler.startElement(NAMESPACE, ELEM_LINK, PREFIX + ':' + ELEM_LINK, attrs);
            this.contentHandler.endElement(NAMESPACE, ELEM_LINK, PREFIX + ':' + ELEM_LINK);
        }
    }

    protected Link getLink(SiteNode node, String language) throws SAXException {
        try {
            return node.getLink(language);
        } catch (SiteException e) {
            throw new SAXException(e);
        }
    }

    protected org.apache.lenya.cms.linking.Link getHref(Link link) {
        org.apache.lenya.cms.linking.Link href = new org.apache.lenya.cms.linking.Link();
        href.setPubId(getSite().getPublication().getId());
        href.setArea(getSite().getArea());
        href.setUuid(link.getNode().getUuid());
        href.setLanguage(link.getLanguage());
        return href;
    }

    protected void addAttribute(AttributesImpl attrs, String name, String value) {
        attrs.addAttribute("", name, name, "CDATA", value);
    }

    public void parameterize(Parameters params) throws ParameterException {
        this.selectorClass = params.getParameter(PARAM_SELECTOR);
    }

}
