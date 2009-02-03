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
package org.apache.lenya.cms.linking.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.linking.GlobalProxies;
import org.apache.lenya.cms.publication.Proxy;

/**
 * GlobalProxy service implementation.
 * The class is implemented as a singleton.
 */
public class GlobalProxiesImpl extends AbstractLogEnabled implements GlobalProxies, Serviceable,
        ThreadSafe, Configurable {
    
    private Map ssl2proxy = new HashMap();
    private ServiceManager manager;

    public Proxy getProxy(boolean ssl) {
        Object key = Boolean.valueOf(ssl);
        Proxy proxy = (Proxy) this.ssl2proxy.get(key);
        if (proxy == null) {
            proxy = initializeProxy(key);
        }
        return proxy;
    }

    protected synchronized Proxy initializeProxy(Object key) {
        Proxy proxy;
        proxy = new Proxy();
        ContextUtility context = null;
        try {
            context = (ContextUtility) manager.lookup(ContextUtility.ROLE);
            proxy.setUrl(context.getRequest().getContextPath());
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (context != null) {
                this.manager.release(context);
            }
        }
        this.ssl2proxy.put(key, proxy);
        return proxy;
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public void configure(Configuration config) throws ConfigurationException {
        Configuration[] proxyConfigs = config.getChildren("proxy");
        for (int p = 0; p < proxyConfigs.length; p++) {
            boolean ssl = proxyConfigs[p].getAttributeAsBoolean("ssl");
            String url = proxyConfigs[p].getAttribute("url");
            Proxy proxy = new Proxy();
            proxy.setUrl(url);
            setProxy(ssl, proxy);
        }
    }

    /**
     * @param ssl If the proxy is responsible for SSL requests.
     * @param proxy A proxy.
     */
    public void setProxy(boolean ssl, Proxy proxy) {
        this.ssl2proxy.put(Boolean.valueOf(ssl), proxy);
    }

}
