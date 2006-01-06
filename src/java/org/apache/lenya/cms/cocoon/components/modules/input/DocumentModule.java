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
import org.apache.lenya.cms.publication.URLUtil;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.impl.RepositoryUtil;
import org.apache.lenya.util.ServletHelper;

/**
 * Document module.
 */
public class DocumentModule extends AbstractInputModule implements Serviceable {

    protected static final String NODE_ID = "nodeId";
    protected static final String AREA = "area";
    protected static final String LANGUAGE = "language";
    protected static final String LABEL = "label";
    private ServiceManager manager;

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object value;

        Request request = ObjectModelHelper.getRequest(objectModel);
        String webappUrl = ServletHelper.getWebappURI(request);
        Session session = RepositoryUtil.getSession(this.manager, request, getLogger());
        Document document;
        try {
            document = URLUtil.getDocument(session, webappUrl);

            if (name.equals(NODE_ID)) {
                value = document.getContentNode().getNodeId();
            } else if (name.equals(AREA)) {
                value = document.getContentNode().getContent().getArea().getAreaID();
            } else if (name.equals(LANGUAGE)) {
                value = document.getLanguage();
            } else if (name.equals(LABEL)) {
                value = document.getLabel();
            } else {
                throw new ConfigurationException("The attribute [" + name + "] is not supported.");
            }
        } catch (RepositoryException e) {
            throw new ConfigurationException("Error for attribute [" + name + "]:", e);
        }
        return value;
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
}
