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

/* $Id$  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.impl.RepositoryUtil;
import org.apache.lenya.cms.repo.metadata.Element;
import org.apache.lenya.cms.repo.metadata.ElementSet;
import org.apache.lenya.cms.repo.metadata.MetaData;
import org.apache.lenya.cms.repo.metadata.impl.DublinCoreElements;

/**
 * Input module to access the dublin core values.
 */
public class DublinCoreModule extends AbstractServiceableInputModule {

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        Object value;
        try {
            Request request = ObjectModelHelper.getRequest(objectModel);
            Document document = RepositoryUtil.getDocument(this.manager, request, getLogger());
            if (document == null) {
                throw new ConfigurationException("There is no document for this page envelope!");
            }
            MetaData dc = document.getMetaData(DublinCoreElements.NAME);

            if (!dc.getElementSet().contains(name)) {
                throw new ConfigurationException("The attribute [" + name + "] is not supported!");
            }

            value = dc.getValue(name);
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

        // calling static method on DublinCoreImpl is a work-around
        // because we do not have access to an instance here, because
        // the page envelope cannot be read here.

        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = RepositoryUtil.getSession(this.manager, request, getLogger());
        ElementSet dcElements;
        try {
            dcElements = session.getRepository()
                    .getMetaDataRegistry()
                    .getElementSet(DublinCoreElements.NAME);
        } catch (RepositoryException e) {
            throw new ConfigurationException("Obtaining attribute names failed: ", e);
        }

        Element[] elements = dcElements.getElements();
        List names = new ArrayList();
        for (int i = 0; i < elements.length; i++) {
            names.add(elements[i].getName());
        }

        return names.iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel) };
        return objects;
    }

}
