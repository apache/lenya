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
    private SourceNode node;

    /**
     * Ctor.
     * @param namespaceUri The namespace URI.
     * @param node The node.
     * @param manager The service manager.
     */
    public SourceNodeMetaData(String namespaceUri, SourceNode node, ServiceManager manager) {
        this.namespaceUri = namespaceUri;
        this.node = node;
        this.manager = manager;
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
        checkKey(key);
        return this.node.getValues(this.namespaceUri, key);
    }

    public String getFirstValue(String key) throws MetaDataException {
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
        this.node.removeAllValues(this.namespaceUri, key);
        addValue(key, value);
    }

    public void addValue(String key, String value) throws MetaDataException {
        checkKey(key);
        if (!getElementSet().getElement(key).isMultiple() && getValues(key).length > 0) {
            throw new MetaDataException("The element [" + key
                    + "] doesn't support multiple values!");
        }
        this.node.addValue(this.namespaceUri, key, value);
    }

    public void replaceBy(MetaData other) throws MetaDataException {
        Element[] elements = getElementSet().getElements();
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].getActionOnCopy() == Element.ONCOPY_COPY) {
                String key = elements[i].getName();
                removeAllValues(key);
                String[] values = other.getValues(key);
                for (int j = 0; j < values.length; j++) {
                    addValue(key, values[j]);
                }
            }
            else if (elements[i].getActionOnCopy() == Element.ONCOPY_DELETE) {
                String key = elements[i].getName();
                removeAllValues(key);
            }
        }
    }

    public String[] getPossibleKeys() {
        return getAvailableKeys();
    }

    public boolean isValidAttribute(String key) {
        return Arrays.asList(getAvailableKeys()).contains(key);
    }

    public long getLastModified() throws MetaDataException {
        try {
            return this.node.getMetaLastModified();
        } catch (RepositoryException e) {
            throw new MetaDataException(e);
        }
    }

    public void removeAllValues(String key) throws MetaDataException {
        checkKey(key);
        this.node.removeAllValues(this.namespaceUri, key);
    }

}
