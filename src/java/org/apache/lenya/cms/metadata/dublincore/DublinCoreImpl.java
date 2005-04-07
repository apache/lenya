/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

/* $Id$  */

package org.apache.lenya.cms.metadata.dublincore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * Access dublin core meta data in documents. This class uses the dublin core specification from
 * 2003-03-04.
 */
public class DublinCoreImpl {
    private String sourceUri;

    private Map elements = new HashMap();
    private Map terms = new HashMap();

    private static final String META = "meta";

    private static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";
    private static final String DC_PREFIX = "dc";

    /**
     * The dublin core elements.
     */
    static final String[] ELEMENTS = { DublinCore.ELEMENT_TITLE, DublinCore.ELEMENT_CREATOR,
            DublinCore.ELEMENT_SUBJECT, DublinCore.ELEMENT_DESCRIPTION,
            DublinCore.ELEMENT_PUBLISHER, DublinCore.ELEMENT_CONTRIBUTOR, DublinCore.ELEMENT_DATE,
            DublinCore.ELEMENT_TYPE, DublinCore.ELEMENT_FORMAT, DublinCore.ELEMENT_IDENTIFIER,
            DublinCore.ELEMENT_SOURCE, DublinCore.ELEMENT_LANGUAGE, DublinCore.ELEMENT_RELATION,
            DublinCore.ELEMENT_COVERAGE, DublinCore.ELEMENT_RIGHTS };

    private static final String DCTERMS_NAMESPACE = "http://purl.org/dc/terms/";
    private static final String DCTERMS_PREFIX = "dcterms";

    /**
     * The dublin core terms.
     */
    static final String[] TERMS = { DublinCore.TERM_AUDIENCE, DublinCore.TERM_ALTERNATIVE,
            DublinCore.TERM_TABLEOFCONTENTS, DublinCore.TERM_ABSTRACT, DublinCore.TERM_CREATED,
            DublinCore.TERM_VALID, DublinCore.TERM_EXTENT, DublinCore.TERM_AVAILABLE,
            DublinCore.TERM_ISSUED, DublinCore.TERM_MODIFIED, DublinCore.TERM_EXTENT,
            DublinCore.TERM_LICENSE, DublinCore.TERM_MEDIUM, DublinCore.TERM_ISVERSIONOF,
            DublinCore.TERM_HASVERSION, DublinCore.TERM_ISREPLACEDBY, DublinCore.TERM_REPLACES,
            DublinCore.TERM_ISREQUIREDBY, DublinCore.TERM_REQUIRES, DublinCore.TERM_ISPARTOF,
            DublinCore.TERM_HASPART, DublinCore.TERM_ISREFERENCEDBY, DublinCore.TERM_REFERENCES,
            DublinCore.TERM_RIGHTSHOLDER, DublinCore.TERM_ISFORMATOF, DublinCore.TERM_HASFORMAT,
            DublinCore.TERM_CONFORMSTO, DublinCore.TERM_SPATIAL, DublinCore.TERM_TEMPORAL,
            DublinCore.TERM_MEDIATOR, DublinCore.TERM_DATEACCEPTED,
            DublinCore.TERM_DATECOPYRIGHTED, DublinCore.TERM_DATESUBMITTED,
            DublinCore.TERM_EDUCATIONLEVEL, DublinCore.TERM_ACCESSRIGHTS,
            DublinCore.TERM_BIBLIOGRAPHICCITATION };

    private ServiceManager manager;

    /**
     * Creates a new instance of Dublin Core
     * @param aDocument the document for which the Dublin Core instance is created.
     * @param manager The service manager.
     * @throws DocumentException if an error occurs
     */
    protected DublinCoreImpl(Document aDocument, ServiceManager manager) throws DocumentException {
        this.manager = manager;
        this.sourceUri = aDocument.getSourceURI();
        loadValues();
    }

    /**
     * Creates a new instance of Dublin Core
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @throws DocumentException if an error occurs
     */
    public DublinCoreImpl(String sourceUri, ServiceManager manager) throws DocumentException {
        this.sourceUri = sourceUri;
        this.manager = manager;
        loadValues();
    }

    /**
     * Loads the dublin core values from the XML file.
     * @throws DocumentException when something went wrong.
     */
    protected void loadValues() throws DocumentException {

        try {
            org.w3c.dom.Document doc = SourceUtil.readDOM(this.sourceUri, this.manager);
            if (doc != null) {

                // FIXME: what if "lenya:meta" element doesn't exist yet?
                // Currently the element is inserted.
                Element metaElement = getMetaElement(doc);

                String[] namespaces = { DC_NAMESPACE, DCTERMS_NAMESPACE };
                String[] prefixes = { DC_PREFIX, DCTERMS_PREFIX };
                String[][] arrays = { ELEMENTS, TERMS };
                Map[] maps = { this.elements, this.terms };

                for (int type = 0; type < 2; type++) {
                    NamespaceHelper helper = new NamespaceHelper(namespaces[type], prefixes[type],
                            doc);
                    String[] elementNames = arrays[type];
                    for (int i = 0; i < elementNames.length; i++) {
                        Element[] children = helper.getChildren(metaElement, elementNames[i]);
                        String[] values = new String[children.length];
                        for (int valueIndex = 0; valueIndex < children.length; valueIndex++) {
                            values[valueIndex] = DocumentHelper
                                    .getSimpleElementText(children[valueIndex]);
                        }
                        maps[type].put(elementNames[i], values);
                    }
                }
            }
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    /**
     * Save the meta data.
     * @throws DocumentException if the meta data could not be made persistent.
     */
    public void save() throws DocumentException {

        try {

            org.w3c.dom.Document doc = SourceUtil.readDOM(this.sourceUri, this.manager);

            Element metaElement = getMetaElement(doc);

            String[] namespaces = { DC_NAMESPACE, DCTERMS_NAMESPACE };
            String[] prefixes = { DC_PREFIX, DCTERMS_PREFIX };
            String[][] arrays = { ELEMENTS, TERMS };
            Map[] maps = { this.elements, this.terms };

            for (int type = 0; type < 2; type++) {
                NamespaceHelper helper = new NamespaceHelper(namespaces[type], prefixes[type], doc);
                String[] elementNames = arrays[type];
                for (int i = 0; i < elementNames.length; i++) {
                    Element[] children = helper.getChildren(metaElement, elementNames[i]);
                    for (int valueIndex = 0; valueIndex < children.length; valueIndex++) {
                        metaElement.removeChild(children[valueIndex]);
                    }
                    String[] values = (String[]) maps[type].get(elementNames[i]);
                    for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
                        Element valueElement = helper.createElement(elementNames[i],
                                values[valueIndex]);
                        metaElement.appendChild(valueElement);
                    }
                }
            }
            SourceUtil.writeDOM(doc, this.sourceUri, this.manager);

        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    /**
     * Returns the Lenya meta data element.
     * @param doc The XML document.
     * @return A DOM element.
     * @throws DocumentException if an error occurs.
     */
    protected Element getMetaElement(org.w3c.dom.Document doc) throws DocumentException {
        Element metaElement;
        try {
            NamespaceHelper namespaceHelper = new NamespaceHelper(PageEnvelope.NAMESPACE,
                    PageEnvelope.DEFAULT_PREFIX, doc);
            Element documentElement = doc.getDocumentElement();
            metaElement = namespaceHelper.getFirstChild(documentElement, META);

            if (metaElement == null) {
                metaElement = namespaceHelper.createElement(META);
                Element[] children = DocumentHelper.getChildren(documentElement);
                if (children.length == 0) {
                    documentElement.appendChild(metaElement);
                } else {
                    documentElement.insertBefore(metaElement, children[0]);
                }
            }
        } catch (final DOMException e) {
            throw new DocumentException(e);
        }
        return metaElement;

    }

    /**
     * Returns the first value for a certain key.
     * @param key The key.
     * @return A string.
     * @throws DocumentException if an error occurs.
     */
    public String getFirstValue(String key) throws DocumentException {
        String value = null;
        String[] values = getElementOrTerm(key);
        if (values.length > 0) {
            value = values[0];
        }
        return value;
    }

    /**
     * Returns the element or term values, resp., for a certain key.
     * @param key The key.
     * @return An array of strings.
     * @throws DocumentException if an error occurs.
     */
    protected String[] getElementOrTerm(String key) throws DocumentException {
        String[] values;

        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            values = (String[]) this.elements.get(key);
        } else if (termList.contains(key)) {
            values = (String[]) this.terms.get(key);
        } else {
            throw new DocumentException("The key [" + key
                    + "] does not refer to a dublin core element or term!");
        }
        if (values == null) {
            values = new String[0];
        }
        return values;
    }

    /**
     * Returns all values for a certain key.
     * @param key The key.
     * @return An array of strings.
     * @throws DocumentException if an error occurs.
     * @see DublinCore#getValues(String)
     */
    public String[] getValues(String key) throws DocumentException {
        return getElementOrTerm(key);
    }

    /**
     * Adds a value for a certain key.
     * @param key The key.
     * @param value The value.
     * @throws DocumentException if an error occurs.
     * @see DublinCore#addValue(String, String)
     */
    public void addValue(String key, String value) throws DocumentException {
        String[] existingValues = getElementOrTerm(key);
        List list = new ArrayList(Arrays.asList(existingValues));
        list.add(value);
        String[] newValues = (String[]) list.toArray(new String[list.size()]);

        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            this.elements.put(key, newValues);
        } else if (termList.contains(key)) {
            this.terms.put(key, newValues);
        } else {
            throw new DocumentException("The key [" + key
                    + "] does not refer to a dublin core element or term!");
        }
    }

    /**
     * @param key The key.
     * @param value The value.
     * @throws DocumentException if an error occurs.
     * @see org.apache.lenya.cms.metadata.dublincore.DublinCore#setValue(java.lang.String,
     *      java.lang.String)
     */
    public void setValue(String key, String value) throws DocumentException {
        String[] newValues = { value };

        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            this.elements.put(key, newValues);
        } else if (termList.contains(key)) {
            this.terms.put(key, newValues);
        } else {
            throw new DocumentException("The key [" + key
                    + "] does not refer to a dublin core element or term!");
        }
    }

    /**
     * @param key The key.
     * @param values The values.
     * @throws DocumentException if an error occurs.
     * @see org.apache.lenya.cms.metadata.dublincore.DublinCore#addValues(java.lang.String,
     *      java.lang.String[])
     */
    public void addValues(String key, String[] values) throws DocumentException {
        for (int i = 0; i < values.length; i++) {
            addValue(key, values[i]);
        }
    }

    /**
     * @param key The key.
     * @param value The value.
     * @throws DocumentException if an error occurs.
     * @see org.apache.lenya.cms.metadata.dublincore.DublinCore#removeValue(java.lang.String,
     *      java.lang.String)
     */
    public void removeValue(String key, String value) throws DocumentException {
        String[] existingValues = getElementOrTerm(key);
        List list = new ArrayList(Arrays.asList(existingValues));

        if (!list.contains(value)) {
            throw new DocumentException("The key [" + key + "] does not contain the value ["
                    + value + "]!");
        }

        list.remove(value);
        String[] newValues = (String[]) list.toArray(new String[list.size()]);

        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            this.elements.put(key, newValues);
        } else if (termList.contains(key)) {
            this.terms.put(key, newValues);
        } else {
            throw new DocumentException("The key [" + key
                    + "] does not refer to a dublin core element or term!");
        }
    }

    /**
     * @param key The key.
     * @throws DocumentException if an error occurs.
     * @see org.apache.lenya.cms.metadata.dublincore.DublinCore#removeAllValues(java.lang.String)
     */
    public void removeAllValues(String key) throws DocumentException {
        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            this.elements.put(key, new String[0]);
        } else if (termList.contains(key)) {
            this.terms.put(key, new String[0]);
        } else {
            throw new DocumentException("The key [" + key
                    + "] does not refer to a dublin core element or term!");
        }
    }

    /**
     * @param other The other dublin core.
     * @throws DocumentException if an error occurs.
     * @see org.apache.lenya.cms.metadata.dublincore.DublinCore#replaceBy(org.apache.lenya.cms.metadata.dublincore.DublinCore)
     */
    public void replaceBy(DublinCore other) throws DocumentException {
        for (int i = 0; i < ELEMENTS.length; i++) {
            String key = ELEMENTS[i];
            removeAllValues(key);
            addValues(key, other.getValues(key));
        }
        for (int i = 0; i < TERMS.length; i++) {
            String key = TERMS[i];
            removeAllValues(key);
            addValues(key, other.getValues(key));
        }
    }

    /**
     * Returns the term keys.
     * @return An array of strings.
     */
    public static String[] getTerms() {
        return (String[]) Arrays.asList(TERMS).toArray(new String[TERMS.length]);
    }

    /**
     * Returns the element keys.
     * @return An array of strings.
     */
    public static String[] getElements() {
        return (String[]) Arrays.asList(ELEMENTS).toArray(new String[ELEMENTS.length]);
    }

    /**
     * Checks if a key represents a valid dublin core term.
     * @param key The key.
     * @return A boolean value.
     */
    public static boolean isValidTerm(String key) {
        return Arrays.asList(DublinCoreImpl.TERMS).contains(key);
    }

    /**
     * Checks if a key represents a valid dublin core element.
     * @param key The key.
     * @return A boolean value.
     */
    public static boolean isValidElement(String key) {
        return Arrays.asList(DublinCoreImpl.ELEMENTS).contains(key);
    }

}