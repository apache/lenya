/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.modules.metadata;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.TimeStampValidity;
import org.apache.excalibur.xml.dom.DOMParser;
import org.apache.lenya.cms.cocoon.source.RepositorySource;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.repository.ContentHolder;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>
 * Generates the meta data of a document. The <code>src</code> attribute must be
 * a {@link RepositorySource} URI (e.g., <code>lenya-document:...</code>).
 * </p>
 * <p>
 * For multi-value elements, multiple &lt;prefix:key&gt; elements are generated.
 * </p>
 * <p>
 * Example output:
 * </p>
 * 
 * <pre>
 *  &lt;meta:metadata xmlns:meta=&quot;http://apache.org/cocoon/lenya/metadata/1.0&quot;&gt;
 *  &lt;elements xmlns=&quot;http://purl.org/dc/elements/1.1/&quot;&gt;
 *  &lt;title&gt;Search&lt;/title&gt;
 *  &lt;date&gt;2006-06-12 13:43:14&lt;/date&gt;
 *  &lt;language&gt;en&lt;/language&gt;
 *  &lt;creator&gt;lenya&lt;/creator&gt;
 *  &lt;/elements&gt;
 *  &lt;elements xmlns=&quot;http://apache.org/lenya/metadata/document/1.0&quot;&gt;
 *  &lt;extension&gt;xml&lt;/extension&gt;
 *  &lt;resourceType&gt;usecase&lt;/resourceType&gt;
 *  &lt;contentType&gt;xml&lt;/contentType&gt;
 *  &lt;/elements&gt;
 *  &lt;/meta:metadata&gt;
 * </pre>
 */
public class LenyaMetaDataGenerator extends ServiceableGenerator implements
        CacheableProcessingComponent {

    /** Node and attribute names */
    protected AttributesImpl attributes;

    /** Metadata */
    protected static final String ROOT_META_NODE_NAME = "meta";

    /** The URI of the namespace of the metadata */
    protected static final String URI_META = "http://apache.org/cocoon/lenya/metadata/1.0";

    /** The namespace prefix for this namespace */
    protected static final String PREFIX_META = "lenya";

    /** The parser for the XML snippets to be included. */
    protected DOMParser parser = null;
    
    private String src;
    
    /** The repository content holder to generate the meta data for */
    private ContentHolder content;
    
    private SourceValidity validity;

    /**
     * Recycle this component. All instance variables are set to <code>null</code>.
     */
    public void recycle() {
        this.content = null;
        this.src = null;
        this.parser = null;
        this.validity = null;
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
        this.src = src;
        
        RepositorySource source = null;
        try {
            source = (RepositorySource) resolver.resolveURI(src);
            this.content = source.getContent();
            this.validity = source.getValidity();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (source != null) {
                resolver.release(source);
            }
        }
    }

    /**
     * Generate the unique key. This key must be unique inside the space of this component.
     * 
     * @return The generated key hashes the src
     */
    public Serializable getKey() {
        return this.src;
    }

    /**
     * Generate the validity object.
     * 
     * @return The generated validity object or <code>null</code> if the component is currently
     *         not cacheable.
     */
    public SourceValidity getValidity() {
        return this.validity;
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
            String[] namespaces = this.content.getMetaDataNamespaceUris();
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
                this.contentHandler.startElement(namespace, names[i], names[i],
                        new AttributesImpl());
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
        this.contentHandler.startElement(URI_META, "metadata", PREFIX_META + ":metadata",
                attributes);
    }

    private void startNodeMeta(String namespace) throws SAXException {
        this.contentHandler.startElement(namespace, "elements", "elements", attributes);
    }

    private void endNodeMeta(String namespace) throws SAXException {
        this.contentHandler.endElement(namespace, "elements", "elements");
    }

    protected MetaData getMetaData(String namespaceUri) throws ProcessingException {
        try {
            return this.content.getMetaData(namespaceUri);
        } catch (Exception e1) {
            throw new ProcessingException("Obtaining meta data value for [" + this.content
                    + "] failed: ", e1);
        }
    }
}
