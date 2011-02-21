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
package org.apache.lenya.cms.repository.metadata.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
//florent : remove deprecated import, cause duplicate classes
/*import org.apache.lenya.cms.repository.metadata.Element;
import org.apache.lenya.cms.repository.metadata.ElementSet;
import org.apache.lenya.cms.repository.metadata.MetaDataException;
import org.apache.lenya.cms.repository.metadata.MetaDataRegistry;*/
import org.apache.lenya.cms.metadata.Element;
import org.apache.lenya.cms.metadata.ElementSet;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataRegistry;

/**
 * Avalon-based element set.
 */
public class ConfigurableElementSet implements ElementSet {

    private static final Log logger = LogFactory.getLog(ConfigurableElementSet.class);

    private String namespaceUri;
    private Map<String, Element> elements = new HashMap<String, Element>();
    private SourceResolver sourceResolver;
    private boolean loaded = false;

    private MetaDataRegistry registry;

    private String configUri;

    public void loadConfiguration() throws Exception {

        if (this.loaded) {
            return;
        }
        this.loaded = true; // fail only once

        Configuration config;
        Source source = null;
        try {
            source = this.sourceResolver.resolveURI(configUri);
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            config = builder.build(source.getInputStream());
        } finally {
            if (source != null) {
                this.sourceResolver.release(source);
            }
        }

        this.namespaceUri = config.getAttribute("namespace");

        Configuration[] attributeConfigs = config.getChildren("element");
        for (int i = 0; i < attributeConfigs.length; i++) {
            String name = attributeConfigs[i].getAttribute("name");
            boolean isMultiple = attributeConfigs[i].getAttributeAsBoolean("multiple", false);
            boolean isEditable = attributeConfigs[i].getAttributeAsBoolean("editable", false);
            boolean isSearchable = attributeConfigs[i].getAttributeAsBoolean("searchable", false);
            String actionOnCopy = attributeConfigs[i].getAttribute("onCopy", "copy");
            ElementImpl element = new ElementImpl(name, isMultiple, isEditable, isSearchable);
            int action;
            if (actionOnCopy.equalsIgnoreCase("copy")) {
                action = Element.ONCOPY_COPY;
            } else if (actionOnCopy.equalsIgnoreCase("ignore")) {
                action = Element.ONCOPY_IGNORE;
            } else if (actionOnCopy.equalsIgnoreCase("delete")) {
                action = Element.ONCOPY_DELETE;
            } else {
                throw new ConfigurationException("The action [" + actionOnCopy
                        + "] is not supported.");
            }
            try {
                element.setActionOnCopy(action);
            } catch (MetaDataException e) {
                throw new RuntimeException(e);
            }
            this.elements.put(name, element);
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

    public void register() throws Exception {
        loadConfiguration();
        this.registry.register(getNamespaceUri(), this);
    }

    public void setRegistry(MetaDataRegistry registry) throws MetaDataException {
        this.registry = registry;
    }

    public void setConfigUri(String configUri) throws Exception {
        this.configUri = configUri;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

}
