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
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
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
    protected static final String URI_META = "http://apache.org/cocoon/lenya/metadata/1.0";

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

    protected String uuid = null;

    /**
     * Recycle this component. All instance variables are set to <code>null</code>.
     */
    public void recycle() {
        this.document = null;
        this.uuid = null;
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

        this.publicationId = par.getParameter("pubid", null);
        if (this.publicationId == null) {
            throw new ProcessingException("The pubid is not set! Please set like e.g. <map:parameter name='pubid' value='{request-param:pubid}'/>");
        }

        this.area = par.getParameter("area", null);
        if (this.area == null) {
            throw new ProcessingException("The area is not set! Please set like e.g. <map:parameter name='area' value='{request-param:area}'/>");
        }

        uuid = par.getParameter("uuid", null);
        if (this.uuid == null) {
            throw new ProcessingException("The uuid is not set! Please set like e.g. <map:parameter name='uuid' value='{request-param:uuid}'/>");
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
            pub = PublicationUtil.getPublication(this.manager, this.publicationId);
        } catch (Exception e) {
            throw new ProcessingException("Error geting publication id / area from page envelope",
                    e);
        }

        DocumentFactory map = DocumentUtil.createDocumentIdentityMap(this.manager, session);
        this.document = map.get(pub, area, uuid, language);
    }

    /**
     * Generate the unique key. This key must be unique inside the space of this component.
     * 
     * @return The generated key hashes the src
     */
    public Serializable getKey() {
        return language + "$$" + uuid;
    }

    /**
     * Generate the validity object.
     * 
     * @return The generated validity object or <code>null</code> if the component is currently
     *         not cacheable.
     */
    public SourceValidity getValidity() {
        long lastModified;
        try {
            lastModified = document.getLastModified().getTime();
        } catch (Exception e) {
            getLogger().error("Error determining last modification date", e);
            return null;
        }
        return new TimeStampValidity(lastModified);
    }

    /**
     * Generate XML data.
     */
    public void generate() throws IOException, SAXException, ProcessingException {
        startNodeRoot();
        performIncludesMeta();
        endNodeRoot();
    }

    private void performIncludesMeta() throws SAXException, ProcessingException {
        
        try {
            String[] namespaces = this.document.getMetaDataNamespaceUris();
            for (int i = 0; i < namespaces.length; i++) {
                this.contentHandler.startPrefixMapping("", namespaces[i]);
                startNodeMeta(namespaces[i]);
                parseMetaData(namespaces[i]);
                endNodeMeta(namespaces[i]);
                this.contentHandler.endPrefixMapping("");
            }
        } catch (MetaDataException e) {
            throw new ProcessingException(e);
        }
        
    }

    private void parseMetaData(String namespace) throws ProcessingException, SAXException {
        MetaData metaData = getMetaData(namespace);
        String[] names = metaData.getAvailableKeys();
        for (int i = 0; i < names.length; i++) {
            String[] values;
            try {
                values = metaData.getValues(names[i]);
            } catch (MetaDataException e) {
                throw new ProcessingException(e);
            }
            for (int j = 0; j < values.length; j++) {
                this.contentHandler.startElement(namespace, names[i], names[i], new AttributesImpl());
                char[] valueChars = values[j].toCharArray();
                this.contentHandler.characters(valueChars, 0, valueChars.length);
                this.contentHandler.endElement(namespace, names[i], names[i]);
            }
        }
    }

    private void endNodeRoot() throws SAXException {
        this.contentHandler.endElement(URI_META, "metadata", PREFIX_META + ":metadata");
        this.contentHandler.endPrefixMapping(PREFIX_META);
        this.contentHandler.endDocument();
    }

    private void startNodeRoot() throws SAXException {
        this.contentHandler.startDocument();
        this.contentHandler.startPrefixMapping(PREFIX_META, URI_META);
        this.contentHandler.startElement(URI_META,
                "metadata",
                PREFIX_META + ":metadata",
                attributes);
    }

    private void startNodeMeta(String namespace) throws SAXException {
        this.contentHandler.startElement(namespace,
                "elements",
                "elements",
                attributes);
    }

    private void endNodeMeta(String namespace) throws SAXException {
        this.contentHandler.endElement(namespace, "elements", "elements");
    }

    protected MetaData getMetaData(String namespaceUri) throws ProcessingException {
        MetaData metaData = null;
        try {
            metaData = this.document.getMetaData(namespaceUri);
        } catch (Exception e1) {
            throw new ProcessingException("Obtaining custom meta data value for ["
                    + document.getSourceURI() + "] failed: ", e1);
        }
        return metaData;
    }
}
