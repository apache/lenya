/*
 * Copyright 1999-20045 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.cms.cocoon.generation;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;
import org.apache.excalibur.xml.dom.DOMParser;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataManager;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Meta data generator.
 */
public class LenyaMetaDataGenerator extends ServiceableGenerator implements
        CacheableProcessingComponent {

    /** The corresponding lenya document */
    protected Document document;

    /** Node and attribute names */
    protected AttributesImpl attributes;

    /** Metadata */
    protected static final String ROOT_META_NODE_NAME = "meta";

    /** The URI of the namespace of the metadata */
    protected static final String URI_META = "http://apache.org/cocoon/lenya/page-envelope/1.0";

    /** The namespace prefix for this namespace */
    protected static final String PREFIX_META = "lenya";

    /** Custom metadata */
    protected static final String ROOT_CUSTOM_META_NODE_NAME = "custom";

    /** Internal metadata */
    protected static final String ROOT_INTERNAL_META_NODE_NAME = "internal";

    /** Dublin Core metadata */
    protected static final String ROOT_DC_META_NODE_NAME = "dc";

    /** Namespace prefix for the dublin core */
    protected static final String PREFIX_META_DC = "dc";

    /** The URI of the namespace of the dublin core metadata */
    protected static final String URI_META_DC = "http://purl.org/dc/elements/1.1/";

    /** The parser for the XML snippets to be included. */
    protected DOMParser parser = null;

    /** The document that should be parsed and (partly) included. */
    protected org.w3c.dom.Document doc = null;

    /** Helper for lenya document retrival */
    protected String publicationId = null;

    protected String area = null;

    protected String language = null;

    protected String docId = null;

    /**
     * Recycle this component. All instance variables are set to <code>null</code>.
     */
    public void recycle() {
        this.document = null;
        this.docId = null;
        this.language = null;
        this.area = null;
        this.publicationId = null;
        this.doc = null;
        this.parser = null;
        super.recycle();
    }

    /**
     * Serviceable
     * 
     * @param manager the ComponentManager
     * 
     * @throws ServiceException in case a component could not be found
     */
    public void service(ServiceManager manager) throws ServiceException {
        super.service(manager);
        this.parser = (DOMParser) manager.lookup(DOMParser.ROLE);
        this.attributes = new AttributesImpl();
    }

    /**
     * Setup the file generator. Try to get the last modification date of the source for caching.
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
            throws ProcessingException, SAXException, IOException {

        super.setup(resolver, objectModel, src, par);

        docId = par.getParameter("docid", null);
        if (this.docId == null) {
            throw new ProcessingException("The docid is not set! Please set like e.g. <map:parameter name='docid' value='{request-param:docid}'/>");
        }

        language = par.getParameter("lang", null);
        if (language == null)
            throw new ProcessingException("The 'lang' parameter is not set.");

        try {
            prepareLenyaDoc(objectModel);
        } catch (DocumentBuildException e) {
            throw new ProcessingException(src + " threw DocumentBuildException: " + e);
        }

    }

    protected void prepareLenyaDoc(Map objectModel) throws DocumentBuildException,
            ProcessingException {

        Request request = ObjectModelHelper.getRequest(objectModel);
        Publication pub;
        Session session;
        try {
            session = RepositoryUtil.getSession(this.manager, request);
            pub = PublicationUtil.getPublication(this.manager, objectModel);
        } catch (Exception e) {
            throw new ProcessingException("Error geting publication id / area from page envelope",
                    e);
        }
        if (pub != null && pub.exists()) {
            this.publicationId = pub.getId();
            String url = ServletHelper.getWebappURI(request);
            this.area = new URLInformation(url).getArea();
            if (this.language == null) {
                this.language = pub.getDefaultLanguage();
            }
        }

        DocumentFactory map = DocumentUtil.createDocumentIdentityMap(this.manager, session);
        this.document = map.get(pub, area, docId, language);
    }

    /**
     * Generate the unique key. This key must be unique inside the space of this component.
     * 
     * @return The generated key hashes the src
     */
    public Serializable getKey() {
        return language + "$$" + docId;
    }

    /**
     * Generate the validity object.
     * 
     * @return The generated validity object or <code>null</code> if the component is currently
     *         not cacheable.
     */
    public SourceValidity getValidity() {
        long lastModified = 0;
        try {
            MetaDataManager metaMgr = document.getMetaDataManager();
            if (lastModified < metaMgr.getCustomMetaData().getLastModified())
                lastModified = metaMgr.getCustomMetaData().getLastModified();
            if (lastModified < metaMgr.getDublinCoreMetaData().getLastModified())
                lastModified = metaMgr.getDublinCoreMetaData().getLastModified();
            if (lastModified < metaMgr.getLenyaMetaData().getLastModified())
                lastModified = metaMgr.getLenyaMetaData().getLastModified();
        } catch (DocumentException e) {
            getLogger().error("Error determining last modification date", e);
            return null;
        }
        return new TimeStampValidity(lastModified);
    }

    /**
     * Generate XML data.
     */
    public void generate() throws IOException, SAXException, ProcessingException {
        // START metadata
        startNodeRoot(ROOT_META_NODE_NAME);
        // lenya document meta
        performIncludesMeta();
        // END metadata
        endNodeRoot(ROOT_META_NODE_NAME);
    }

    private void performIncludesMeta() throws SAXException, ProcessingException {
        // custom metadata
        startNodeMeta(ROOT_CUSTOM_META_NODE_NAME);
        parseMetaData("custom");
        endNodeMeta(ROOT_CUSTOM_META_NODE_NAME);
        // internal metadata
        startNodeMeta(ROOT_INTERNAL_META_NODE_NAME);
        parseMetaData("internal");
        endNodeMeta(ROOT_INTERNAL_META_NODE_NAME);
        // dc metadata
        startNodeMeta(ROOT_DC_META_NODE_NAME);
        parseMetaData("dc");
        endNodeMeta(ROOT_DC_META_NODE_NAME);
    }

    private void parseMetaData(String type) throws ProcessingException, SAXException {
        MetaData metaData = getMetaData(type);
        HashMap elementMap;
        elementMap = metaData.getAvailableKey2Value();
        if ("dc".equals(type)) {
            Iterator iteratorMap = elementMap.entrySet().iterator();
            while (iteratorMap.hasNext()) {
                Map.Entry entry = (Map.Entry) iteratorMap.next();
                this.contentHandler.startPrefixMapping(PREFIX_META_DC, URI_META_DC);
                startNodeMetaDC((String) entry.getKey());
                String valueNode = (String) entry.getValue();
                char[] textNode = valueNode.toCharArray();
                this.contentHandler.characters(textNode, 0, textNode.length);
                endNodeMetaDC((String) entry.getKey());
                this.contentHandler.endPrefixMapping(PREFIX_META_DC);
            }
        } else if ("custom".equals(type) | "internal".equals(type)) {
            Iterator iteratorMap = elementMap.entrySet().iterator();
            while (iteratorMap.hasNext()) {
                Map.Entry entry = (Map.Entry) iteratorMap.next();
                startNodeMeta((String) entry.getKey());
                String valueNode = (String) entry.getValue();
                char[] textNode = valueNode.toCharArray();
                this.contentHandler.characters(textNode, 0, textNode.length);
                endNodeMeta((String) entry.getKey());
            }
        }
    }

    private void endNodeRoot(String nodeName) throws SAXException {
        endNodeMeta(nodeName);
        this.contentHandler.endPrefixMapping(PREFIX_META);
        this.contentHandler.endDocument();
    }

    private void startNodeRoot(String nodeName) throws SAXException {
        this.contentHandler.startDocument();
        this.contentHandler.startPrefixMapping(PREFIX_META, URI_META);
        startNodeMeta(nodeName);
    }

    private void startNodeMeta(String nodeName) throws SAXException {
        this.contentHandler.startElement(URI_META,
                nodeName,
                PREFIX_META + ":" + nodeName,
                attributes);
    }

    private void endNodeMeta(String nodeName) throws SAXException {
        this.contentHandler.endElement(URI_META, nodeName, PREFIX_META + ":" + nodeName);
    }

    private void startNodeMetaDC(String nodeName) throws SAXException {
        this.contentHandler.startElement(URI_META_DC,
                nodeName,
                PREFIX_META_DC + ":" + nodeName,
                attributes);
    }

    private void endNodeMetaDC(String nodeName) throws SAXException {
        this.contentHandler.endElement(URI_META_DC, nodeName, PREFIX_META_DC + ":" + nodeName);
    }

    protected MetaData getMetaData(String type) throws ProcessingException {
        MetaData metaData = null;
        try {
            if ("custom".equals(type)) {
                metaData = this.document.getMetaDataManager().getCustomMetaData();
            } else if ("internal".equals(type)) {
                metaData = this.document.getMetaDataManager().getLenyaMetaData();
            } else if ("dc".equals(type)) {
                metaData = this.document.getMetaDataManager().getDublinCoreMetaData();
            }
        } catch (DocumentException e1) {
            throw new ProcessingException("Obtaining custom meta data value for ["
                    + document.getSourceURI() + "] failed: ", e1);
        }
        return metaData;
    }
}
