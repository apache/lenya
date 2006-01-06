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
import org.apache.commons.lang.StringUtils;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.impl.RepositoryUtil;
import org.apache.lenya.util.ServletHelper;

/**
 * Publication module.
 */
public class PublicationModule extends AbstractInputModule implements Serviceable {

    protected static final String ID = "id";
    protected static final String DEFAULT_LANGUAGE = "defaultLanguage";
    protected static final String LANGUAGES_CSV = "languagesCsv";

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object value;

        Request request = ObjectModelHelper.getRequest(objectModel);
        String webappUrl = ServletHelper.getWebappURI(request);
        Session session = RepositoryUtil.getSession(this.manager, request, getLogger());

        String pubId = new URLInformation(webappUrl).getPublicationId();
        try {
            Publication pub = session.getPublication(pubId);

            if (name.equals(ID)) {
                value = pub.getPublicationId();
            } else if (name.equals(DEFAULT_LANGUAGE)) {
                return pub.getDefaultLanguage();
            } else if (name.equals(LANGUAGES_CSV)) {
                value = StringUtils.join(pub.getLanguages(), ',');
            } else {
                throw new ConfigurationException("The attribute [" + name + "] is not supported.");
            }

        } catch (RepositoryException e) {
            throw new ConfigurationException("Error for attribute [" + name + "]: ", e);
        }

        return value;
    }

    protected ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
