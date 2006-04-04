/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Generic meta-data implementation.
 * 
 * Meta-data is represented as XML. For compatibility with the Dublin Core meta-data requirements, a
 * set of meta-data may contain up to two namespaces, one for "elements" and one for "terms". A
 * metadata implementation requiring only one namespace can ignore the "terms".
 * 
 * @version $Id$
 */
public abstract class MetaDataImpl extends AbstractLogEnabled implements MetaData {

    private String sourceUri;

    private ServiceManager manager;

    private Map elements = new HashMap();

    private Map terms = new HashMap();

    private List elementList = Arrays.asList(getElements());

    private List termList = Arrays.asList(getTerms());

    private static final String META_ROOT = "meta";

    /**
     * Creates a new instance of metadata, reading any existing values from the source URI.
     * 
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @param _logger A logger
     * @throws DocumentException if an error occurs
     */
    public MetaDataImpl(String sourceUri, ServiceManager manager, Logger _logger)
            throws DocumentException {
        ContainerUtil.enableLogging(this, _logger);
        this.sourceUri = sourceUri;
        this.manager = manager;
        this.elementList = Arrays.asList(getElements());
        this.termList = Arrays.asList(getTerms());
        loadValues();
    }

    /**
     * Determine under which element name this meta-data is specified.
     * 
     * @return the name of the element containing this meta-data
     */
    protected abstract String getLocalElementName();

    /**
     * The namespaces under which this meta-data is specified.
     * 
     * @return an array of strings, containing the names of the namespaces
     */
    protected abstract String[] getNamespaces();

    /**
     * The prefixes under which this meta-data is specified.
     * 
     * @return an array of strings, containing the names of the prefixes
     */
    protected abstract String[] getPrefixes();

    /**
     * Elements to be used in this meta-data. A meta-data implementation which does not have a fixed
     * list of elements may return an empty array, and override useFixedElements()
     * 
     * @see #useFixedElements()
     * @return an array of string representing the elements
     */
    protected abstract String[] getElements();

    /**
     * Terms to be used in this meta-data. A meta-data implementation requiring only one namespace
     * may ignore terms and return an empty array.
     * 
     * @return an array of string representing the terms
     */
    protected abstract String[] getTerms();

    /**
     * Determine if the meta-data should consist of a fixed list of known attributes, or whether
     * arbitrary names can be used.
     * 
     * @return true if meta-data consists of fixed elements
     */
    protected boolean useFixedElements() {
        return true;
    }

    /**
     * @see org.apache.lenya.cms.metadata.MetaData#getAvailableKeys()
     */
    public String[] getAvailableKeys() {
        String[] availableKeys = new String[elements.size()];
        availableKeys = (String[]) elements.keySet().toArray(availableKeys);
        return availableKeys;
    }

    /**
     * @return All keys that values exist for.
     */
    public HashMap getAvailableKey2Value() {
        HashMap key2value = new HashMap();
        Iterator iteratorMap = this.elements.entrySet().iterator();
        while (iteratorMap.hasNext()) {
            Map.Entry entry = (Map.Entry) iteratorMap.next();
            String[] value = (String[]) entry.getValue();
            String key = (String) entry.getKey();
            // remove namespace prefix
            if (key.indexOf(":") > -1) {
                key = key.substring(key.indexOf(":") + 1, key.length());
            }
            if (value.length > 0) {
                String nodeValue = "";
                for (int i = 0; i < value.length; i++) {
                    nodeValue = nodeValue + value[i];
                }
                /**
                 * FIXME: if we do not want empty elements then uncomment and delete this note
                 */
                // if (!"".equals(nodeValue)){
                key2value.put(key, nodeValue);
                /**
                 * FIXME: if we do not want empty elements then uncomment and delete this note
                 */
                // }
            }
        }
        return key2value;
    }

    /**
     * Loads the meta values from the XML source.
     * 
     * @throws DocumentException when something went wrong.
     */
    protected void loadValues() throws DocumentException {

        try {
            Document doc = getDocument();
            if (doc != null) {

                Element metaElement = getLocalMetaElement(doc);

                String[] namespaces = getNamespaces();
                String[] prefixes = getPrefixes();
                String[][] arrays = { getElements(), getTerms() };
                Map[] maps = { this.elements, this.terms };

                for (int type = 0; type < namespaces.length; type++) {
                    NamespaceHelper helper = new NamespaceHelper(namespaces[type],
                            prefixes[type],
                            doc);

                    if (useFixedElements()) {
                        String[] elementNames = arrays[type];
                        for (int i = 0; i < elementNames.length; i++) {
                            Element[] children = helper.getChildren(metaElement, elementNames[i]);
                            loadElementValues(maps[type], elementNames[i], children);
                        }
                    } else {
                        Element[] elements = helper.getChildren(metaElement);
                        for (int i = 0; i < elements.length; i++) {
                            loadElementValues(maps[type],
                                    elements[i].getTagName(),
                                    helper.getChildren(metaElement, elements[i].getLocalName()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    /**
     * Retrieve an element (and its values) from the DOM and add the element to the map
     */
    private void loadElementValues(Map map, String elementName, Element[] children) {
        String[] values = new String[children.length];
        for (int valueIndex = 0; valueIndex < children.length; valueIndex++) {
            values[valueIndex] = DocumentHelper.getSimpleElementText(children[valueIndex]);
        }
        map.put(elementName, values);
    }

    /**
     * Save the meta data.
     * 
     * @throws DocumentException if the meta data could not be made persistent.
     */
    public void save() throws DocumentException {

        if (getLogger().isDebugEnabled())
            getLogger().debug("MetaDataImpl::save() called, sourceUri [" + sourceUri + "]");

        try {
            Document doc = getDocument();
            Element metaElement = getLocalMetaElement(doc);
            String[] namespaces = getNamespaces();
            String[] prefixes = getPrefixes();
            String[][] arrays = { getElements(), getTerms() };
            Map[] maps = { this.elements, this.terms };

            List childNodes = new ArrayList();
            NodeList nodes = metaElement.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i).getParentNode() == metaElement) {
                    childNodes.add(nodes.item(i));
                }
            }
            Node[] children = (Node[]) childNodes.toArray(new Node[childNodes.size()]);
            for (int i = 0; i < children.length; i++) {
                metaElement.removeChild(children[i]);
            }

            for (int type = 0; type < namespaces.length; type++) {
                NamespaceHelper helper = new NamespaceHelper(namespaces[type], prefixes[type], doc);
                if (useFixedElements()) {
                    String[] elementNames = arrays[type];
                    for (int i = 0; i < elementNames.length; i++) {
                        writeElementValues(helper, metaElement, maps[type], elementNames[i]);
                    }
                } else {
                    Iterator elementNames = maps[type].keySet().iterator();
                    while (elementNames.hasNext()) {
                        /*
                         * removing prefixed elements to prevent that lenya:custom/* is inserted
                         * twice. Further prefixed metadata is causing an exception since
                         * writeElementValues(...) is prefixing all elements by default.
                         */
                        String elementName = (String) elementNames.next();
                        if (!(elementName.indexOf(":") > -1)) {
                            writeElementValues(helper, metaElement, maps[type], elementName);
                        }
                    }
                }
            }

            SourceUtil.writeDOM(doc, this.sourceUri, this.manager);
        } catch (final Exception e) {
            throw new DocumentException(e);
        }
    }

    /**
     * Add a new element (and its values) to an existing element in the DOM
     */
    private void writeElementValues(NamespaceHelper helper, Element father, Map elementMap,
            String elementName) {
        String[] values = (String[]) elementMap.get(elementName);
        for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
            Element valueElement = helper.createElement(elementName, values[valueIndex]);
            father.appendChild(valueElement);
        }
    }

    /**
     * Create a DOM representation of this meta-data.
     * 
     * @return A DOM document.
     * @throws ServiceException if a general error occurs.
     * @throws SourceNotFoundException if the meta-data's source can not be found
     * @throws ParserConfigurationException if an error occurs during XML parsing.
     * @throws SAXException if an error occurs during XML parsing.
     * @throws IOException if an error occurs during source access..
     */
    protected Document getDocument() throws ServiceException, SourceNotFoundException,
            ParserConfigurationException, SAXException, IOException {
        org.w3c.dom.Document doc = SourceUtil.readDOM(this.sourceUri, this.manager);
        NamespaceHelper namespaceHelper;
        if (doc == null) {
            namespaceHelper = new NamespaceHelper(PageEnvelope.NAMESPACE,
                    PageEnvelope.DEFAULT_PREFIX,
                    "document");
        } else {
            namespaceHelper = new NamespaceHelper(PageEnvelope.NAMESPACE,
                    PageEnvelope.DEFAULT_PREFIX,
                    doc);
        }
        return namespaceHelper.getDocument();
    }

    /**
     * Returns the Lenya meta data element.
     * 
     * @param document The XML document.
     * @return A DOM element.
     * @throws DocumentException if an error occurs.
     */
    protected Element getMetaElement(Document document) throws DocumentException {
        Element metaElement;
        try {
            NamespaceHelper namespaceHelper = new NamespaceHelper(PageEnvelope.NAMESPACE,
                    PageEnvelope.DEFAULT_PREFIX,
                    document);
            Element documentElement = namespaceHelper.getDocument().getDocumentElement();
            metaElement = namespaceHelper.getFirstChild(documentElement, META_ROOT);

            if (metaElement == null) {
                metaElement = namespaceHelper.createElement(META_ROOT);
                Element[] children = DocumentHelper.getChildren(documentElement);
                if (children.length == 0) {
                    documentElement.appendChild(metaElement);
                } else {
                    documentElement.insertBefore(metaElement, children[0]);
                }
            }
        } catch (final Exception e) {
            throw new DocumentException(e);
        }
        return metaElement;
    }

    /**
     * Returns the meta data element for this type of meta-data
     * 
     * @param document The XML document.
     * @return A DOM element.
     * @throws DocumentException if an error occurs.
     */
    protected Element getLocalMetaElement(Document document) throws DocumentException {
        Element topMetaElement = getMetaElement(document);

        Element metaElement;
        try {
            NamespaceHelper namespaceHelper = new NamespaceHelper(PageEnvelope.NAMESPACE,
                    PageEnvelope.DEFAULT_PREFIX,
                    document);
            metaElement = namespaceHelper.getFirstChild(topMetaElement, getLocalElementName());

            if (metaElement == null) {
                metaElement = namespaceHelper.createElement(getLocalElementName());
                Element[] children = DocumentHelper.getChildren(topMetaElement);
                if (children.length == 0) {
                    topMetaElement.appendChild(metaElement);
                } else {
                    topMetaElement.insertBefore(metaElement, children[0]);
                }
            }
        } catch (final Exception e) {
            throw new DocumentException(e);
        }
        return metaElement;
    }

    /**
     * Returns the first value for a certain key.
     * 
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
     * 
     * @param key The key.
     * @return An array of strings.
     * @throws DocumentException if an error occurs.
     */
    protected String[] getElementOrTerm(String key) throws DocumentException {
        String[] values;

        if (useFixedElements()) {
            if (elementList.contains(key)) {
                values = (String[]) this.elements.get(key);
            } else if (termList.contains(key)) {
                values = (String[]) this.terms.get(key);
            } else {
                throw new DocumentException("The key [" + key
                        + "] does not refer to a metadata element or term!");
            }
        } else {
            values = (String[]) this.elements.get(key);
        }
        if (values == null) {
            values = new String[0];
        }
        return values;
    }

    /**
     * Returns all values for a certain key.
     * 
     * @param key The key.
     * @return An array of strings.
     * @throws DocumentException if an error occurs.
     * @see MetaData#getValues(String)
     */
    public String[] getValues(String key) throws DocumentException {
        return getElementOrTerm(key);
    }

    /**
     * @param key The key.
     * @param value The value.
     * @throws DocumentException if an error occurs.
     * @see org.apache.lenya.cms.metadata.MetaData#setValue(java.lang.String, java.lang.String)
     */
    public void setValue(String key, String value) throws DocumentException {
        String[] newValues = { value };

        if (useFixedElements()) {
            if (elementList.contains(key)) {
                this.elements.put(key, newValues);
            } else if (termList.contains(key)) {
                this.terms.put(key, newValues);
            } else {
                throw new DocumentException("The key [" + key
                        + "] does not refer to a dublin core element or term!");
            }
        } else
            this.elements.put(key, newValues);
        save();
    }

    /**
     * @param other The other meta-data
     * @throws DocumentException if an error occurs.
     * @see org.apache.lenya.cms.metadata.MetaData#replaceBy(org.apache.lenya.cms.metadata.MetaData)
     */
    public void replaceBy(MetaData other) throws DocumentException {
        if (useFixedElements()) {
            String[] elements = getElements();
            String[] terms = getTerms();
            for (int i = 0; i < elements.length; i++) {
                String key = elements[i];
                removeAllValues(key);
                addValues(key, other.getValues(key));
            }
            for (int i = 0; i < terms.length; i++) {
                String key = terms[i];
                removeAllValues(key);
                addValues(key, other.getValues(key));
            }
        } else {
            // elements not fixed: clear old elements and write all from other
            HashMap elementMap = other.getAvailableKey2Value();
            Iterator iteratorMap = elementMap.entrySet().iterator();
            while (iteratorMap.hasNext()) {
                Map.Entry entry = (Map.Entry) iteratorMap.next();
                String key =(String) entry.getKey();
                String[] valueNode = {(String) entry.getValue()};
                addValues(key, valueNode);
            }
        }
        save();
    }

    /**
     * @return All possible keys.
     */
    public String[] getPossibleKeys() {
        List keys = new ArrayList();
        keys.addAll(this.elementList);
        keys.addAll(this.termList);
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    /**
     * Checks if a key represents a valid metadata attribute.
     * 
     * @param key The key.
     * @return A boolean value.
     */
    public boolean isValidAttribute(String key) {
        if (useFixedElements()) {
            return termList.contains(key) || elementList.contains(key);
        } else {
            return this.elements.containsKey(key);
        }
    }

    /**
     * @see org.apache.lenya.cms.metadata.MetaData#addValue(java.lang.String, java.lang.String)
     */
    public void addValue(String key, String value) throws DocumentException {
        String[] existingValues = getElementOrTerm(key);
        List list = new ArrayList(Arrays.asList(existingValues));
        list.add(value);
        String[] newValues = (String[]) list.toArray(new String[list.size()]);

        if (useFixedElements()) {
            if (elementList.contains(key)) {
                this.elements.put(key, newValues);
            } else if (termList.contains(key)) {
                this.terms.put(key, newValues);
            } else {
                throw new DocumentException("The key [" + key
                        + "] does not refer to a metadata element or term!");
            }
        } else {
            this.elements.put(key, newValues);
        }
        save();
    }

    /**
     * @param key The key.
     * @param values The values.
     * @throws DocumentException if an error occurs.
     */
    private void addValues(String key, String[] values) throws DocumentException {
        for (int i = 0; i < values.length; i++) {
            addValue(key, values[i]);
        }
    }

    /**
     * @param key The key.
     * @throws DocumentException if an error occurs.
     */
    private void removeAllValues(String key) throws DocumentException {
        if (useFixedElements()) {
            if (elementList.contains(key)) {
                this.elements.put(key, new String[0]);
            } else if (termList.contains(key)) {
                this.terms.put(key, new String[0]);
            } else {
                throw new DocumentException("The key [" + key
                        + "] does not refer to a dublin core element or term!");
            }
        } else {
            this.elements.remove(key);
        }
    }

    public long getLastModified() throws DocumentException {

        Source source = null;
        try {
            SourceResolver resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(sourceUri);
        } catch (Exception e) {
            throw new DocumentException("Unable to resolve meta data source", e);
        }
        return source.getLastModified();
    }

}
