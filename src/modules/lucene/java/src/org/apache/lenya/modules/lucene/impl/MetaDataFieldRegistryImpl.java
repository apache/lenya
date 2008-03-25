/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.modules.lucene.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.cms.metadata.Element;
import org.apache.lenya.cms.metadata.ElementSet;
import org.apache.lenya.cms.metadata.MetaDataRegistry;
import org.apache.lenya.modules.lucene.MetaDataFieldRegistry;
import org.apache.lenya.util.Assert;

public class MetaDataFieldRegistryImpl extends AbstractLogEnabled implements MetaDataFieldRegistry,
        ThreadSafe, Serviceable {

    private MetaDataRegistry registry;
    private ServiceManager manager;
    private Map namespace2prefix;

    public String getFieldName(String namespace, String elementName) {
        Assert.notNull("namespace", namespace);
        Assert.notNull("element name", elementName);
        initPrefixes();
        Assert.isTrue("namespace [" + namespace + "] exists", this.namespace2prefix
                .containsKey(namespace));
        String prefix = (String) this.namespace2prefix.get(namespace);
        return prefix + elementName;
    }

    protected MetaDataRegistry getRegistry() {
        if (this.registry == null) {
            try {
                this.registry = (MetaDataRegistry) this.manager.lookup(MetaDataRegistry.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.registry;
    }

    protected void initPrefixes() {
        if (this.namespace2prefix == null) {
            this.namespace2prefix = new HashMap();
            MetaDataRegistry registry = getRegistry();
            try {
                String[] namespaces = registry.getNamespaceUris();
                for (int n = 0; n < namespaces.length; n++) {
                    String prefix = "{" + namespaces[n] + "}";
                    this.namespace2prefix.put(namespaces[n], prefix);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public String[] getFieldNames() {
        MetaDataRegistry registry = getRegistry();
        Set fieldNames = new HashSet();
        try {
            String[] namespaces = registry.getNamespaceUris();
            for (int n = 0; n < namespaces.length; n++) {
                ElementSet elementSet = registry.getElementSet(namespaces[n]);
                Element[] elements = elementSet.getElements();
                for (int e = 0; e < elements.length; e++) {
                    String fieldName = getFieldName(namespaces[n], elements[e].getName());
                    fieldNames.add(fieldName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (String[]) fieldNames.toArray(new String[fieldNames.size()]);
    }

}
