/*
 * Copyright  1999-2004 The Apache Software Foundation
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

/* $Id$  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreImpl;
import org.apache.lenya.cms.publication.Document;

/**
 * Input module to access the dublin core values.
 */
public class DublinCoreModule extends AbstractPageEnvelopeModule {

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        if (!DublinCoreImpl.isValidElement(name) && !DublinCoreImpl.isValidTerm(name)) {
            throw new ConfigurationException("The attribute [" + name + "] is not supported!");
        }

        Document document = getEnvelope(objectModel, name).getDocument();

        if (document == null) {
            throw new ConfigurationException("There is no document for this page envelope!");
        }
        Object value;
        try {
            DublinCore dc = (DublinCore) document.getMetaData();
            value = dc.getFirstValue(name);
        } catch (Exception e) {
            throw new ConfigurationException("Obtaining dublin core value for [" + name
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

        List names = new ArrayList();
        names.addAll(Arrays.asList(DublinCoreImpl.getElements()));
        names.addAll(Arrays.asList(DublinCoreImpl.getTerms()));
        return names.iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel) };
        return objects;
    }

}