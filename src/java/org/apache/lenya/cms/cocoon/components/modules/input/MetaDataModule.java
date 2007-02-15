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

/* $Id: DublinCoreModule.java 169299 2005-05-09 12:00:43Z jwkaltz $  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.metadata.Element;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.MetaDataRegistry;
import org.apache.lenya.cms.publication.Document;

/**
 * <p>
 * Input module to access meta data values. Use the name of the element as
 * input module parameter.
 * </p>
 * <p>Configuration:</p>
 * <pre>
 *  &lt;component-instance logger="sitemap.modules.input.dublincore" name="[...]"
 *    class="org.apache.lenya.cms.cocoon.components.modules.input.MetaDataModule"
 *    namespace="[namespace URI of the element set]"/&gt;
 * </pre>
 * <p>Usage examples:</p>
 * <ul>
 * <li><code>{dublincore:title}</code></li>
 * <li><code>{myMetData:myElementName}</code></li>
 * </ul>
 */
public class MetaDataModule extends AbstractPageEnvelopeModule {

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object value;

        MetaData metaData = getCustomMetaData(objectModel);

        if (!metaData.isValidAttribute(name)) {
            throw new ConfigurationException("The attribute [" + name + "] is not supported!");
        }

        try {
            value = metaData.getFirstValue(name);
        } catch (MetaDataException e) {
            throw new ConfigurationException("Obtaining custom meta data value for [" + name
                    + "] failed: ", e);
        }

        return value;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        MetaDataRegistry registry = null;
        try {
            registry = (MetaDataRegistry) this.manager.lookup(MetaDataRegistry.ROLE);
            Element[] elements = registry.getElementSet(this.namespaceUri).getElements();
            String[] keys = new String[elements.length];
            for (int i = 0; i < keys.length; i++) {
                keys[i] = elements[i].getName();
            }
            return Arrays.asList(keys).iterator();
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
        finally {
            this.manager.release(registry);
        }
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object[] values;
        MetaData metaData = getCustomMetaData(objectModel);

        if (!metaData.isValidAttribute(name)) {
            throw new ConfigurationException("The attribute [" + name + "] is not supported!");
        }

        try {
            values = metaData.getValues(name);
        } catch (MetaDataException e) {
            throw new ConfigurationException("Obtaining custom meta data value for [" + name
                    + "] failed: ", e);
        }

        return values;
    }

    protected MetaData getCustomMetaData(Map objectModel) throws ConfigurationException {
        // FIXME: There seems to be no reason to pass the attribute name to get the page envelope.
        Document document = getEnvelope(objectModel, "").getDocument();
        if (document == null) {
            throw new ConfigurationException("There is no document for this page envelope!");
        }
        MetaData metaData = null;
        try {
            metaData = document.getMetaData(this.namespaceUri);
        } catch (MetaDataException e) {
            throw new ConfigurationException("Obtaining custom meta data value for ["
                    + document + "] failed: ", e);
        }
        return metaData;
    }

    private String namespaceUri;
    
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
        this.namespaceUri = conf.getAttribute("namespace");
    }
    
    
    
}
