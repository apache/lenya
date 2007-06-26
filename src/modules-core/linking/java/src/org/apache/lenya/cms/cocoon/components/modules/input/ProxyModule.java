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
import org.apache.lenya.cms.linking.LinkRewriter;
import org.apache.lenya.cms.linking.OutgoingLinkRewriter;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

/**
 * Input module for getting the base URL which may be prepended to internal URLs
 * to construct links. The functionality corresponds to the
 * {@link org.apache.lenya.cms.cocoon.transformation.ProxyTransformer} with one
 * exception: If the <em>webappUrl</em> parameter is an empty string, the root
 * proxy URL (or the context prefix, resp.) is returned.
 * 
 * <p>
 * Usage: <code>{proxy:{webappUrl}}</code>
 * </p>
 */
public class ProxyModule extends AbstractInputModule implements Serviceable {

    private ServiceManager manager;

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        final String webappUrl = name;
        Request request = ObjectModelHelper.getRequest(objectModel);

        String value = null;
        try {
            if (webappUrl.equals("")) {
                value = rewrite(request, "/");
                if (value.endsWith("/")) {
                    value = value.substring(0, value.length() - 1);
                }
            } else {
                value = rewrite(request, webappUrl);
            }
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Obtaining value for [" + name + "] failed: ", e);
        }
        return value;
    }

    protected String rewrite(Request request, String url) throws RepositoryException,
            ConfigurationException {
        Session session = RepositoryUtil.getSession(this.manager, request);
        LinkRewriter rewriter = new OutgoingLinkRewriter(this.manager, session, request
                .getRequestURI(), request.isSecure(), false);
        if (!rewriter.matches(url)) {
            throw new ConfigurationException("The URL [" + url + "] can't be rewritten!");
        }
        return rewriter.rewrite(url);
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
     *      org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
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