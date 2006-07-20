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
package org.apache.lenya.cms.repository;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.metadata.Element;
import org.apache.lenya.cms.metadata.ElementSet;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataRegistry;
import org.apache.lenya.cms.publication.DocumentException;

/**
 * Source-node-based meta data.
 */
public class SourceNodeMetaData extends AbstractLogEnabled implements MetaData {

    private String namespaceUri;
    private ServiceManager manager;
    private SourceNode node;

    public SourceNodeMetaData(String namespaceUri, SourceNode node, ServiceManager manager) {
        this.namespaceUri = namespaceUri;
        this.node = node;
        this.manager = manager;
    }

    private ElementSet elementSet;

    protected ElementSet getElementSet() throws DocumentException {
        if (this.elementSet == null) {
            try {
                MetaDataRegistry registry = (MetaDataRegistry) this.manager.lookup(MetaDataRegistry.ROLE);
                this.elementSet = registry.getElementSet(this.namespaceUri);
            } catch (ServiceException e) {
                throw new DocumentException(e);
            }

        }
        return this.elementSet;
    }

    public String[] getValues(String key) throws DocumentException {
        checkKey(key);
        try {
            return this.node.getValues(this.namespaceUri, key);
        } catch (RepositoryException e) {
            throw new DocumentException(e);
        }
    }

    public String getFirstValue(String key) throws DocumentException {
        checkKey(key);
        String[] values = getValues(key);
        if (values.length == 0) {
            return null;
        } else {
            return values[0];
        }
    }

    public String[] getAvailableKeys() {
        Element[] elements;
        try {
            elements = getElementSet().getElements();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        String[] keys = new String[elements.length];
        for (int i = 0; i < elements.length; i++) {
            keys[i] = elements[i].getName();
        }
        return keys;
    }

    protected void checkKey(String key) throws DocumentException {
        if (!isValidAttribute(key)) {
            throw new DocumentException("The meta data element set ["
                    + getElementSet().getNamespaceUri() + "] does not support the key [" + key
                    + "]!");
        }
    }

    public void setValue(String key, String value) throws DocumentException {
        checkKey(key);
        try {
            this.node.removeAllValues(this.namespaceUri, key);
            addValue(key, value);
        } catch (RepositoryException e) {
            throw new DocumentException(e);
        }
    }

    public void addValue(String key, String value) throws DocumentException {
        checkKey(key);
        try {
            this.node.addValue(this.namespaceUri, key, value);
        } catch (RepositoryException e) {
            throw new DocumentException(e);
        }
    }

    public void replaceBy(MetaData other) throws DocumentException {
        Element[] elements = getElementSet().getElements();
        for (int i = 0; i < elements.length; i++) {
            String key = elements[i].getName();
            String[] values = other.getValues(key);
            removeAllValues(key);
            for (int j = 0; j < values.length; j++) {
                addValue(key, values[j]);
            }
        }
    }

    public String[] getPossibleKeys() {
        return getAvailableKeys();
    }

    public HashMap getAvailableKey2Value() {
        HashMap map = new HashMap();
        try {
            Element[] elements = getElementSet().getElements();
            for (int i = 0; i < elements.length; i++) {
                String key = elements[i].getName();
                String[] values = getValues(key);
                if (values.length == 1) {
                    map.put(key, values[0]);
                }
                else if (values.length > 1) {
                    map.put(key, Arrays.asList(values));
                }
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public boolean isValidAttribute(String key) {
        return Arrays.asList(getAvailableKeys()).contains(key);
    }

    public long getLastModified() throws DocumentException {
        try {
            return this.node.getLastModified();
        } catch (RepositoryException e) {
            throw new DocumentException(e);
        }
    }

    public void removeAllValues(String key) throws DocumentException {
        checkKey(key);
        try {
            this.node.removeAllValues(this.namespaceUri, key);
        } catch (RepositoryException e) {
            throw new DocumentException(e);
        }
    }

}
