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
package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.site.usecases.CreateUsecaseDocument;
import org.apache.lenya.xml.DocumentHelper;

/**
 * Module to retrieve information from a usecase resource type document.
 */
public class UsecaseDocumentModule extends AbstractInputModule implements Serviceable {

    protected static final String USECASE = "usecase";

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        Object value = null;
        try {
            if (name.equals(USECASE)) {
                Request request = ObjectModelHelper.getRequest(objectModel);
                Document doc = DocumentUtil.getCurrentDocument(this.manager, request);
                org.w3c.dom.Document xmlDoc = DocumentHelper.readDocument(doc.getInputStream());
                String usecaseName = xmlDoc.getDocumentElement().getAttribute(CreateUsecaseDocument.ATTRIBUTE_NAME);
                value = usecaseName;
            }
        } catch (Exception e) {
            throw new ConfigurationException("Could not obtain value: ", e);
        }
        return value;
    }

    protected ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
