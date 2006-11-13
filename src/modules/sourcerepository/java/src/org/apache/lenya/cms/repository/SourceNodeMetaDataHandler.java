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
package org.apache.lenya.cms.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.ElementSet;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataOwner;
import org.apache.lenya.cms.metadata.MetaDataRegistry;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handles the meta data of source nodes.
 */
public class SourceNodeMetaDataHandler implements MetaDataOwner {
    
    private ServiceManager manager;
    private String metaSourceUri;
    
    /**
     * @param manager The service manager.
     * @param sourceUri The soure uri.
     */
    public SourceNodeMetaDataHandler(ServiceManager manager, String sourceUri) {
        this.manager = manager;
        this.metaSourceUri = sourceUri;
    }
    
    protected String getMetaSourceUri() {
        return this.metaSourceUri;
    }

    private Map namespace2metadata = new HashMap();

    public MetaData getMetaData(String namespaceUri) throws MetaDataException {

        MetaDataRegistry registry = null;
        try {
            registry = (MetaDataRegistry) this.manager.lookup(MetaDataRegistry.ROLE);
            if (!registry.isRegistered(namespaceUri)) {
                throw new MetaDataException("The namespace [" + namespaceUri
                        + "] is not registered!");
            }
        } catch (ServiceException e) {
            throw new MetaDataException(e);
        } finally {
            if (registry != null) {
                this.manager.release(registry);
            }
        }

        MetaData meta = (MetaData) this.namespace2metadata.get(namespaceUri);
        if (meta == null) {
            meta = new SourceNodeMetaData(namespaceUri, this, this.manager);
            this.namespace2metadata.put(namespaceUri, meta);
        }
        return meta;
    }

    private Map namespace2metamap = null;

    protected Map getMetaDataMap(String namespaceUri) throws MetaDataException {
        if (this.namespace2metamap == null) {
            loadMetaData();
        }
        Map map = (Map) this.namespace2metamap.get(namespaceUri);
        if (map == null) {
            map = new HashMap();
            this.namespace2metamap.put(namespaceUri, map);
        }
        return map;
    }

    protected static final String META_DATA_NAMESPACE = "http://apache.org/lenya/metadata/1.0";
    protected static final String ELEMENT_METADATA = "metadata";
    protected static final String ELEMENT_SET = "element-set";
    protected static final String ELEMENT_ELEMENT = "element";
    protected static final String ELEMENT_VALUE = "value";
    protected static final String ATTRIBUTE_NAMESPACE = "namespace";
    protected static final String ATTRIBUTE_KEY = "key";

    protected void loadMetaData() throws MetaDataException {

        if (this.namespace2metamap != null) {
            throw new IllegalStateException("The meta data have already been loaded!");
        }

        try {
            this.namespace2metamap = new HashMap();
            if (SourceUtil.exists(getMetaSourceUri(), this.manager)) {
                Document xml = SourceUtil.readDOM(getMetaSourceUri(), this.manager);
                if (!xml.getDocumentElement().getNamespaceURI().equals(META_DATA_NAMESPACE)) {
                    loadLegacyMetaData(xml);
                } else {
                    NamespaceHelper helper = new NamespaceHelper(META_DATA_NAMESPACE, "", xml);
                    Element[] setElements = helper.getChildren(xml.getDocumentElement(),
                            ELEMENT_SET);
                    for (int setIndex = 0; setIndex < setElements.length; setIndex++) {
                        String namespace = setElements[setIndex].getAttribute(ATTRIBUTE_NAMESPACE);
                        Element[] elementElements = helper.getChildren(setElements[setIndex],
                                ELEMENT_ELEMENT);
                        for (int elemIndex = 0; elemIndex < elementElements.length; elemIndex++) {
                            String key = elementElements[elemIndex].getAttribute(ATTRIBUTE_KEY);
                            Element[] valueElements = helper.getChildren(
                                    elementElements[elemIndex], ELEMENT_VALUE);
                            for (int valueIndex = 0; valueIndex < valueElements.length; valueIndex++) {
                                String value = DocumentHelper
                                        .getSimpleElementText(valueElements[valueIndex]);
                                List values = getValueList(namespace, key);
                                values.add(value);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new MetaDataException(e);
        }
    }

    protected void loadLegacyMetaData(Document xml) throws MetaDataException {
        NamespaceHelper helper = new NamespaceHelper(PageEnvelope.NAMESPACE, "", xml);

        Element metaElement = helper.getFirstChild(xml.getDocumentElement(), "meta");

        Element internalElement = helper.getFirstChild(metaElement, "internal");

        Element[] internalElements = helper.getChildren(internalElement);
        for (int i = 0; i < internalElements.length; i++) {
            String value = DocumentHelper.getSimpleElementText(internalElements[i]);
            String key = internalElements[i].getLocalName();

            if (key.equals("workflowVersion")) {
                List values = getValueList("http://apache.org/lenya/metadata/workflow/1.0", key);
                values.add(value);
            } else {
                List values = getValueList("http://apache.org/lenya/metadata/document/1.0", key);
                values.add(value);
            }
        }

        NamespaceHelper dcHelper = new NamespaceHelper(DublinCore.DC_NAMESPACE, "", xml);
        Element dcElement = helper.getFirstChild(metaElement, "dc");

        if (dcElement != null) {
            MetaDataRegistry registry = null;
            try {
                registry = (MetaDataRegistry) this.manager.lookup(MetaDataRegistry.ROLE);
                ElementSet dcElementSet = registry.getElementSet(DublinCore.DC_NAMESPACE);
                ElementSet dcTermSet = registry.getElementSet(DublinCore.DCTERMS_NAMESPACE);

                Element[] dcElements = dcHelper.getChildren(dcElement);
                for (int i = 0; i < dcElements.length; i++) {
                    String value = DocumentHelper.getSimpleElementText(dcElements[i]);

                    String key = dcElements[i].getLocalName();

                    if (dcElementSet.containsElement(key)) {
                        List values = getValueList(DublinCore.DC_NAMESPACE, key);
                        values.add(value);
                    } else if (dcTermSet.containsElement(key)) {
                        List values = getValueList(DublinCore.DCTERMS_NAMESPACE, key);
                        values.add(value);
                    } else {
                        throw new RepositoryException("The dublin core key [" + key
                                + "] is not supported.");
                    }
                }
            } catch (MetaDataException e) {
                throw e;
            } catch (Exception e) {
                throw new MetaDataException(e);
            } finally {
                if (registry != null) {
                    this.manager.release(registry);
                }
            }
        }

    }

    protected void saveMetaData() throws MetaDataException {
        try {
            NamespaceHelper helper = new NamespaceHelper(META_DATA_NAMESPACE, "", ELEMENT_METADATA);
            Collection namespaces = this.namespace2metamap.keySet();
            for (Iterator i = namespaces.iterator(); i.hasNext();) {
                String namespace = (String) i.next();

                Element setElement = helper.createElement(ELEMENT_SET);
                setElement.setAttribute(ATTRIBUTE_NAMESPACE, namespace);
                helper.getDocument().getDocumentElement().appendChild(setElement);

                Map map = getMetaDataMap(namespace);
                Collection keys = map.keySet();
                for (Iterator keyIterator = keys.iterator(); keyIterator.hasNext();) {
                    String key = (String) keyIterator.next();

                    Element elementElement = helper.createElement(ELEMENT_ELEMENT);
                    elementElement.setAttribute(ATTRIBUTE_KEY, key);

                    List values = (List) map.get(key);
                    for (Iterator valueIterator = values.iterator(); valueIterator.hasNext();) {
                        String value = (String) valueIterator.next();
                        if (!value.equals("")) {
                            Element valueElement = helper.createElement(ELEMENT_VALUE, value);
                            elementElement.appendChild(valueElement);
                        }
                    }
                    if (elementElement.hasChildNodes()) {
                        setElement.appendChild(elementElement);
                    }
                }
            }
            SourceUtil.writeDOM(helper.getDocument(), getMetaSourceUri(), this.manager);
        } catch (Exception e) {
            throw new MetaDataException(e);
        }
    }

    protected String[] getValues(String namespaceUri, String key, int revisionNumber) throws MetaDataException {
        List values = getValueList(namespaceUri, key);
        return (String[]) values.toArray(new String[values.size()]);
    }

    protected String[] getValues(String namespaceUri, String key) throws MetaDataException {
        List values = getValueList(namespaceUri, key);
        return (String[]) values.toArray(new String[values.size()]);
    }

    protected List getValueList(String namespaceUri, String key) throws MetaDataException {
        Map map = getMetaDataMap(namespaceUri);
        List values = (List) map.get(key);
        if (values == null) {
            values = new ArrayList();
            map.put(key, values);
        }
        return values;
    }

    protected void addValue(String namespaceUri, String key, String value) throws MetaDataException {
        List values = getValueList(namespaceUri, key);
        values.add(value);
        saveMetaData();
    }

    protected void removeAllValues(String namespaceUri, String key) throws MetaDataException {
        List values = getValueList(namespaceUri, key);
        values.clear();
        saveMetaData();
    }

    public String[] getMetaDataNamespaceUris() throws MetaDataException {
        MetaDataRegistry registry = null;
        try {
            registry = (MetaDataRegistry) this.manager.lookup(MetaDataRegistry.ROLE);
            return registry.getNamespaceUris();
        } catch (ServiceException e) {
            throw new MetaDataException(e);
        } finally {
            if (registry != null) {
                this.manager.release(registry);
            }
        }
    }
    
    private long lastModified;

    /**
     * @return the last modified date of the meta data
     * @throws RepositoryException if an error occurs.
     */
    public long getMetaLastModified() throws RepositoryException {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(getMetaSourceUri());

            long sourceLastModified;

            if (source.exists()) {
                sourceLastModified = source.getLastModified();
                if (sourceLastModified > this.lastModified) {
                    this.lastModified = sourceLastModified;
                }
            } else if (this.lastModified == -1) {
                throw new RepositoryException("The source [" + getMetaSourceUri()
                        + "] does not exist!");
            }

            return this.lastModified;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

}
