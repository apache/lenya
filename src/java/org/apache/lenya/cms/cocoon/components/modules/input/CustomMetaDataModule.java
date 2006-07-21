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

/* $Id: DublinCoreModule.java 169299 2005-05-09 12:00:43Z jwkaltz $  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.publication.Document;

/**
 * Input module to access custom meta data values.
 */
public class CustomMetaDataModule extends AbstractPageEnvelopeModule {

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

        // No pre-defined attributes. Return empty iterator.
        return new ArrayList().iterator();
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
                    + document.getSourceURI() + "] failed: ", e);
        }
        return metaData;
    }

    private String namespaceUri;
    
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
        this.namespaceUri = conf.getAttribute("namespace", null);
    }
    
    
    
}
