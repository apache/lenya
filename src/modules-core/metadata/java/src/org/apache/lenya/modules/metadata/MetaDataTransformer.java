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
package org.apache.lenya.modules.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>Meta data transformer.</p>
 * <p>Usage example:</p>
 * <pre>
 * &lt;meta:value xmlns:meta="http://apache.org/lenya/meta/1.0/"
 *   element="title"
 *   ns="http://purl.org/dc/elements/1.1/"
 *   uuid="{$uuid}"
 *   lang="{$language}"
 *   default="default-title"
 *   i18n:attr="default" /&gt;
 * </pre>
 * <p>The attribute <em>default</em> is optional.</p>
 */
public class MetaDataTransformer extends AbstractSAXTransformer implements Disposable {
    /**
     * The namespace for the meta data is http://apache.org/lenya/meta/1.0
     */
    static public final String NAMESPACE_URI = "http://apache.org/lenya/meta/1.0/";

    /**
     * The namespace prefix for this namespace.
     */
    static public final String PREFIX = "meta";

    /**
     * The value element is getting the value for a specific ns and key. It is the only method
     * implemented so far.
     */
    static public final String VALUE_ELEMENT = "value";

    /**
     * ELEMENT_ATT - which meta data key do we want to look up
     */
    static public final String ELEMENT_ATT = "element";

    /**
     * NS_ATT - in which namespace should we look
     */
    static public final String NS_ATT = "ns";

    /**
     * UUID_ATT - for which uuid?
     */
    static public final String UUID_ATT = "uuid";

    /**
     * LANG_ATT - in which language this is optional (when not found use publication default)
     */
    static public final String LANG_ATT = "lang";

    /**
     * Use this attribute to provide a default value to be used if the document doesn't exist.
     */
    static public final String DEFAULT_ATT = "default";

    /** Helper for lenya document retrival */
    protected String publicationId = null;
    protected String area = null;
    protected String language = null;
    protected String uuid = null;
    protected Publication pub;
    private DocumentFactory factory;

    /**
     * Setup the MetaDataTransformer.
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
            throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, par);
        this.publicationId = par.getParameter("pubid", null);
        if (this.publicationId == null) {
            throw new ProcessingException(
                    "The pubid is not set! Please set like e.g. <map:parameter name='pubid' value='{request-param:pubid}'/>");
        }

        this.area = par.getParameter("area", null);
        if (this.area == null) {
            throw new ProcessingException(
                    "The area is not set! Please set like e.g. <map:parameter name='area' value='{request-param:area}'/>");
        }
        Request request = ObjectModelHelper.getRequest(objectModel);
        factory = DocumentUtil.getDocumentFactory(this.manager, request);
        try {
            pub = factory.getPublication(this.publicationId);
        } catch (PublicationException e) {
            throw new ProcessingException("Error geting publication id / area from page envelope",
                    e);
        }
    }

    public void startElement(String uri, String name, String raw, Attributes attr)
            throws SAXException {
        if (NAMESPACE_URI.equals(uri)) {
            if (VALUE_ELEMENT.equals(name)) {
                String lang = null, uuid = null, ns = null, key = null, defaultValue = null;
                for (int i = 0; i < attr.getLength(); i++) {
                    String localName = attr.getLocalName(i);
                    String value = attr.getValue(i);
                    if (ELEMENT_ATT.equals(localName))
                        key = value;
                    else if (NS_ATT.equals(localName))
                        ns = value;
                    else if (UUID_ATT.equals(localName))
                        uuid = value;
                    else if (LANG_ATT.equals(localName))
                        lang = value;
                    else if (DEFAULT_ATT.equals(localName))
                        defaultValue = value;
                }// end for
                if (uuid == null || ns == null || key == null)
                    throw new SAXException(
                            "Error by setting up the transformation. Please fix the calling code.");
                if (lang == null)
                    lang = pub.getDefaultLanguage();
                List returnValues = new ArrayList();
                try {
                    Area areaObj = pub.getArea(this.area);
                    if (areaObj.contains(uuid, lang)) {
                        Document document = areaObj.getDocument(uuid, lang);
                        MetaData metaData = document.getMetaData(ns);
                        returnValues.addAll(Arrays.asList(metaData.getValues(key)));
                    } else if (defaultValue != null) {
                        returnValues.add(defaultValue);
                    } else {
                        String doc = this.publicationId + ":" + this.area + ":" + uuid + ":" + lang;
                        throw new SAXException("The document [" + doc
                                + "] does not exist and no default value is provided.");
                    }
                } catch (Exception e) {
                    throw new SAXException(e);
                }
                if (returnValues.size() > 1) {
                    for (Iterator i = returnValues.iterator(); i.hasNext();) {
                        String value = (String) i.next();
                        AttributesImpl attributes = new AttributesImpl();
                        attributes.addAttribute("", VALUE_ELEMENT, VALUE_ELEMENT, "CDATA", value);
                        attributes.addAttribute("", ELEMENT_ATT, ELEMENT_ATT, "CDATA", key);
                        this.contentHandler.startElement(ns, VALUE_ELEMENT, PREFIX + ":"
                                + VALUE_ELEMENT, attributes);
                        this.contentHandler.endElement(ns, VALUE_ELEMENT, PREFIX + ":"
                                + VALUE_ELEMENT);
                    }
                } else if (returnValues.size() == 1) {
                    String value = (String) returnValues.get(0);
                    this.contentHandler.characters(value.toCharArray(), 0,
                            value.toCharArray().length);
                }

            } else {
                String warn = "Could not find method for " + name + ". Ignoring.";
                getLogger().warn(warn);
            }
        } else {
            super.startElement(uri, name, raw, attr);
        }
    }

    public void endElement(String uri, String name, String raw) throws SAXException {
        if (!NAMESPACE_URI.equals(uri)) {
            super.endElement(uri, name, raw);
        }
    }

    public void recycle() {
        super.recycle();
        this.publicationId = null;
        this.area = null;
        this.language = null;
        this.uuid = null;
        this.pub = null;
        this.factory = null;
    }

}
