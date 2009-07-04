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
package org.apache.lenya.cms.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.cms.metadata.Element.IndexType;

/**
 * Avalon-based element set.
 */
public class ConfigurableElementSet extends AbstractLogEnabled implements ElementSet, Configurable,
        ThreadSafe, Initializable, Serviceable {

    private String namespaceUri;
    private Map<String, Element> elements = new HashMap<String, Element>();

    public void configure(Configuration config) throws ConfigurationException {

        this.namespaceUri = config.getAttribute("name");

        for (Configuration elemConfig : config.getChildren("element")) {
            String name = elemConfig.getAttribute("name");
            boolean isMultiple = elemConfig.getAttributeAsBoolean("multiple", false);
            boolean isEditable = elemConfig.getAttributeAsBoolean("editable", false);
            boolean isSearchable = elemConfig.getAttributeAsBoolean("searchable", false);
            String actionOnCopy = elemConfig.getAttribute("onCopy", "copy");
            ElementImpl element = new ElementImpl(name, isMultiple, isEditable, isSearchable);
            
            String indexTypeString = elemConfig.getAttribute("indexType", "text");
            element.setIndexType(getIndexType(indexTypeString));
            
            int action;
            if (actionOnCopy.equalsIgnoreCase("copy")) {
                action = Element.ONCOPY_COPY;
            }
            else if (actionOnCopy.equalsIgnoreCase("ignore")) {
                action = Element.ONCOPY_IGNORE;
            }
            else if (actionOnCopy.equalsIgnoreCase("delete")) {
                action = Element.ONCOPY_DELETE;
            }
            else {
                throw new ConfigurationException("The action [" + actionOnCopy + "] is not supported.");
            }
            try {
                element.setActionOnCopy(action);
            } catch (MetaDataException e) {
                throw new RuntimeException(e);
            }
            this.elements.put(name, element);
        }

    }

    protected IndexType getIndexType(String indexTypeString) throws ConfigurationException {
        if (indexTypeString.equals("text")) {
            return IndexType.TEXT;
        }
        else if (indexTypeString.equals("keyword")) {
            return IndexType.KEYWORD;
        }
        else if (indexTypeString.equals("date")) {
            return IndexType.DATE;
        }
        else {
            throw new ConfigurationException("Unsupported index type: '" + indexTypeString + "'");
        }
    }

    public Element[] getElements() {
        Collection<Element> values = this.elements.values();
        return (Element[]) values.toArray(new Element[values.size()]);
    }

    public Element getElement(String name) {
        return (Element) this.elements.get(name);
    }

    public String getNamespaceUri() {
        return this.namespaceUri;
    }

    public boolean containsElement(String name) {
        return this.elements.keySet().contains(name);
    }

    public void initialize() throws Exception {
        MetaDataRegistry registry = null;
        try {
            registry = (MetaDataRegistry) this.manager.lookup(MetaDataRegistry.ROLE);
            registry.register(getNamespaceUri(), this);
        }
        finally {
            if (registry != null) {
                this.manager.release(registry);
            }
        }
    }
    
    private ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
