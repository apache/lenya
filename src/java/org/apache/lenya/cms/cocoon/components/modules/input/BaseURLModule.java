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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;

/**
 * Input module for getting the base URL which may be prepended to internal 
 * URLs to construct links.
 * The base-url contains no trailing slash.
 * 
 * <p>
 * Usage: <code>{base-url:{pubid}:{area}:{ssl}}</code>
 * </p>
 * 
 * <p>
 * If the publication uses proxying, the base URL is the proxy URL defined in 
 * the file conf/publication.xconf. If no proxying is used, 
 * the result will be {context-path}/{pub-id}/{area}.
 * The ssl parameter is optional, if omitted the protocol (http or https) of the current request will be used.
 * </p>
 * 
 */
public class BaseURLModule extends AbstractInputModule implements Serviceable {

    private ServiceManager manager;

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        // Get parameters
        final String[] attributes = name.split(":");

        if (attributes.length < 2) {
            throw new ConfigurationException("Invalid number of parameters: " + attributes.length
                    + ". Expected pubid, area, [ssl].");
        }

        Request request = ObjectModelHelper.getRequest(objectModel);
        
        final String pubid = attributes[0];
        final String area = attributes[1];
        boolean ssl = false;
        
        if (attributes.length == 3) {
            ssl = Boolean.valueOf(attributes[2]).booleanValue();
        } else if (request.getScheme().equals("https")) {
            ssl = true;
        }
        
        String value = null;
        try {
            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            Publication publication = factory.getPublication(pubid);

            Proxy proxy = publication.getProxy(area, ssl);
            
            if (proxy != null) {
                value = proxy.getUrl();
            } else {
                value = request.getContextPath() + "/" + pubid + "/" + area;
            }
            
        } catch (Exception e) {
            throw new ConfigurationException("Obtaining value for [" + name + "] failed: ", e);
        }
        return value;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        return Collections.EMPTY_SET.iterator();
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

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
}