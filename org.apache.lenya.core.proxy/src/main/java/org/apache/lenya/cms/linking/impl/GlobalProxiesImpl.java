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

import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.lenya.cms.linking.GlobalProxies;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.ProxyImpl;

/**
 * GlobalProxy service implementation.
 * The class is implemented as a singleton.
 */
public class GlobalProxiesImpl extends AbstractLogEnabled implements GlobalProxies {
    
    private Map ssl2proxy = new HashMap();

    public Proxy getProxy(boolean ssl) {
        Object key = Boolean.valueOf(ssl);
        Proxy proxy = (Proxy) this.ssl2proxy.get(key);
        if (proxy == null) {
            proxy = initializeProxy(key);
        }
        return proxy;
    }

    protected synchronized Proxy initializeProxy(Object key) {
        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        Proxy proxy = new ProxyImpl();
        proxy.setUrl(process.getRequest().getContextPath());
        this.ssl2proxy.put(key, proxy);
        return proxy;
    }
    
    public void setNonSslProxyUrl(String url) {
        Proxy proxy = new ProxyImpl();
        proxy.setUrl(url);
        setProxy(false, proxy);
    }

    public void setSslProxyUrl(String url) {
        Proxy proxy = new ProxyImpl();
        proxy.setUrl(url);
        setProxy(true, proxy);
    }

    /**
     * @param ssl If the proxy is responsible for SSL requests.
     * @param proxy A proxy.
     */
    public void setProxy(boolean ssl, Proxy proxy) {
        this.ssl2proxy.put(Boolean.valueOf(ssl), proxy);
    }

}
