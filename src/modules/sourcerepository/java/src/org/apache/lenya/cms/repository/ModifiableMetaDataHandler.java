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

import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * Modifiable meta data handler.
 */
public class ModifiableMetaDataHandler extends SourceNodeMetaDataHandler implements Persistable {
    
    private MetaSourceWrapper sourceWrapper;
    private boolean changed = false;
    
    /**
     * @param manager The service manager.
     * @param sourceWrapper The source wrapper.
     */
    public ModifiableMetaDataHandler(ServiceManager manager, MetaSourceWrapper sourceWrapper) {
        super(manager, sourceWrapper.getRealSourceUri(), sourceWrapper.getNode());
        this.sourceWrapper = sourceWrapper;
        try {
            this.sourceWrapper.getNode().setPersistable(this);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }
    

    public void save() throws RepositoryException {
        if (!changed) {
            return;
        }
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
            OutputStream oStream = this.sourceWrapper.getOutputStream();
            DocumentHelper.writeDocument(helper.getDocument(), oStream);
            if (oStream != null) {
                oStream.flush();
                try {
                    oStream.close();
                } catch (Throwable t) {
                    throw new RuntimeException("Could not write meta XML: ", t);
                }
            }
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    protected void addValue(String namespaceUri, String key, String value) throws MetaDataException {
        List values = getValueList(namespaceUri, key);
        values.add(value);
        changed();
    }

    protected void removeAllValues(String namespaceUri, String key) throws MetaDataException {
        List values = getValueList(namespaceUri, key);
        values.clear();
        changed();
    }

    protected void setValue(String namespaceUri, String key, String value) throws MetaDataException {
        List values = getValueList(namespaceUri, key);
        values.clear();
        values.add(value);
        changed();
    }

    private void changed() {
        this.changed = true;
    }

    public boolean isModified() {
        return this.changed;
    }

}
