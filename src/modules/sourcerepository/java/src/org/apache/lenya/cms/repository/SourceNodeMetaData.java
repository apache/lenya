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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.metadata.Element;
import org.apache.lenya.cms.metadata.ElementSet;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataRegistry;

/**
 * Source-node-based meta data.
 */
public class SourceNodeMetaData extends AbstractLogEnabled implements MetaData {

    private String namespaceUri;
    private ServiceManager manager;
    private SourceNodeMetaDataHandler handler;

    /**
     * Ctor.
     * @param namespaceUri The namespace URI.
     * @param handler The meta data handler.
     * @param manager The service manager.
     */
    public SourceNodeMetaData(String namespaceUri, SourceNodeMetaDataHandler handler,
            ServiceManager manager) {
        this.namespaceUri = namespaceUri;
        this.handler = handler;
        this.manager = manager;
    }

    protected String getNamespaceUri() {
        return this.namespaceUri;
    }

    protected SourceNodeMetaDataHandler getHandler() {
        return this.handler;
    }

    private ElementSet elementSet;

    public ElementSet getElementSet() {
        if (this.elementSet == null) {
            try {
                MetaDataRegistry registry = (MetaDataRegistry) this.manager
                        .lookup(MetaDataRegistry.ROLE);
                this.elementSet = registry.getElementSet(this.namespaceUri);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return this.elementSet;
    }

    public String[] getValues(String key) throws MetaDataException {
        String[] values = getHandler().getValues(this.namespaceUri, key);
        if (values.length == 0) {
            checkKey(key);
        }
        return values;
    }

    public String getFirstValue(String key) throws MetaDataException {
        String[] values = getValues(key);
        if (values.length == 0) {
            checkKey(key);
            return null;
        } else {
            return values[0];
        }
    }
    
    /**
     * Cache for better performance.
     */
    private Set availableKeys;
    
    protected Set availableKeys() {
        if (this.availableKeys == null) {
            this.availableKeys = new HashSet(Arrays.asList(getAvailableKeys()));
        }
        return this.availableKeys;
    }

    public String[] getAvailableKeys() {
        Element[] elements;
        elements = getElementSet().getElements();
        String[] keys = new String[elements.length];
        for (int i = 0; i < elements.length; i++) {
            keys[i] = elements[i].getName();
        }
        return keys;
    }

    protected void checkKey(String key) throws MetaDataException {
        if (!isValidAttribute(key)) {
            throw new MetaDataException("The meta data element set ["
                    + getElementSet().getNamespaceUri() + "] does not support the key [" + key
                    + "]!");
        }
    }

    public void setValue(String key, String value) throws MetaDataException {
        checkKey(key);
        if (value == null) {
            throw new MetaDataException("The value for key [" + key + "] must not be null.");
        }
        getHandler().setValue(this.namespaceUri, key, value);
    }

    public void addValue(String key, String value) throws MetaDataException {
        checkKey(key);
        if (!getElementSet().getElement(key).isMultiple() && getValues(key).length > 0) {
            throw new MetaDataException("The element [" + key
                    + "] doesn't support multiple values!");
        }
        getHandler().addValue(this.namespaceUri, key, value);
    }

    public void replaceBy(MetaData other) throws MetaDataException {
        Element[] elements = getElementSet().getElements();
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].getActionOnCopy() == Element.ONCOPY_COPY) {
                replaceBy(other, elements[i]);
            } else if (elements[i].getActionOnCopy() == Element.ONCOPY_DELETE) {
                String key = elements[i].getName();
                removeAllValues(key);
            }
        }
    }

    protected void replaceBy(MetaData other, Element element) throws MetaDataException {
        String key = element.getName();
        removeAllValues(key);
        String[] values = other.getValues(key);
        for (int j = 0; j < values.length; j++) {
            addValue(key, values[j]);
        }
    }

    public void forcedReplaceBy(MetaData other) throws MetaDataException {
        Element[] elements = getElementSet().getElements();
        for (int i = 0; i < elements.length; i++) {
            replaceBy(other, elements[i]);
        }
    }

    public String[] getPossibleKeys() {
        return getAvailableKeys();
    }

    public boolean isValidAttribute(String key) {
        return availableKeys().contains(key);
    }

    public long getLastModified() throws MetaDataException {
        try {
            return getHandler().getLastModified();
        } catch (RepositoryException e) {
            throw new MetaDataException(e);
        }
    }

    public void removeAllValues(String key) throws MetaDataException {
        checkKey(key);
        getHandler().removeAllValues(this.namespaceUri, key);
    }

}
