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
package org.apache.lenya.ac.shibboleth;

import java.util.Arrays;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.cocoon.components.modules.input.AbstractPageEnvelopeModule;
import org.apache.lenya.cms.publication.util.OutgoingLinkRewriter;
import org.apache.lenya.util.ServletHelper;

/**
 * <p>
 * Shibboleth input module.
 * </p>
 * <p>
 * Supported attributes:
 * </p>
 * <ul>
 * <li><em>wayfServer</em> - the URL of the WAYF server</li>
 * <li><em>shire</em> - the value of the shire request parameter for the WAYF server</li>
 * <li><em>target</em> - the value of the target request parameter for the WAYF server</li>
 * <li><em>providerId</em> - the value of the providerId request parameter for the WAYF server</li>
 * </ul>
 */
public class ShibbolethModule extends AbstractPageEnvelopeModule implements Serviceable {

    protected static final String ATTR_TARGET = "target";
    protected static final String ATTR_SHIRE = "shire";
    protected static final String ATTR_WAYF_SERVER = "wayfServer";
    protected static final String ATTR_PROVIDER_ID = "providerId";
    private ServiceManager manager;

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        
        ShibbolethUtil util = new ShibbolethUtil(this.manager);
        String host = util.getHostUrl();

        // attributes to get from the org.apache.shibboleth.ShibbolethModule
        String[] shibModuleAttrs = { ATTR_WAYF_SERVER, ATTR_PROVIDER_ID, ATTR_SHIRE };
        if (Arrays.asList(shibModuleAttrs).contains(name)) {
            org.apache.shibboleth.ShibbolethModule shibModule = null;
            try {
                shibModule = (org.apache.shibboleth.ShibbolethModule) this.manager
                        .lookup(org.apache.shibboleth.ShibbolethModule.ROLE);

                if (name.equals(ATTR_WAYF_SERVER)) {
                    return shibModule.getWayfServerUrl();
                } else if (name.equals(ATTR_PROVIDER_ID)) {
                    return shibModule.getProviderId();
                } else if (name.equals(ATTR_SHIRE)) {
                        return shibModule.getShireUrl(host);
                } else {
                    throw new ConfigurationException("Attribute [" + name + "] not supported!");
                }

            } catch (ServiceException e) {
                throw new ConfigurationException("Error looking up shibboleth module: ", e);
            } finally {
                if (shibModule != null) {
                    this.manager.release(shibModule);
                }
            }
        } else {

            if (name.equals(ATTR_TARGET)) {
                Request req = ObjectModelHelper.getRequest(objectModel);
                String webappUrl = ServletHelper.getWebappURI(req);
                OutgoingLinkRewriter rewriter = new OutgoingLinkRewriter(this.manager, getLogger());
        
                String outgoingUrl = rewriter.rewrite(webappUrl);
                if (outgoingUrl.startsWith("/")) {
                    int port = req.getServerPort();
                    String portSuffix = ShibbolethUtil.getPortSuffix(port);
                    String serverUrl = req.getScheme() + "://" + req.getServerName() + portSuffix;
                    outgoingUrl = serverUrl + outgoingUrl;
                }
                return outgoingUrl;
            } else {
                throw new ConfigurationException("Attribute [" + name + "] not supported!");
            }
        }

    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
