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
package org.apache.lenya.cms.linking;

import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;

/**
 * <p>
 * Converts browser-based links to web application links by using the
 * publication's proxy settings.
 * </p>
 * <p>
 * Objects of this class are not thread-safe.
 * </p>
 */
public class IncomingLinkRewriter implements LinkRewriter {

    private Publication pub;

    /**
     * @param pub The current publication.
     */
    public IncomingLinkRewriter(Publication pub) {
        this.pub = pub;
    }

    public boolean matches(String url) {
        return getMatchingProxyConfiguration(url) != null;
    }

    protected ProxyConfiguration getMatchingProxyConfiguration(String url) {
        ProxyConfiguration config = null;
        String[] areas = this.pub.getAreaNames();
        Boolean[] sslValues = { Boolean.FALSE, Boolean.TRUE };
        for (int a = 0; a < areas.length; a++) {
            for (int s = 0; s < sslValues.length; s++) {
                Proxy proxy = this.pub.getProxy(areas[a], sslValues[s].booleanValue());
                if (config == null && url.startsWith(proxy.getUrl())) {
                    config = new ProxyConfiguration(areas[a], sslValues[s].booleanValue());
                }
            }
        }
        return config;
    }

    public String rewrite(String url) {
        ProxyConfiguration config = getMatchingProxyConfiguration(url);
        if (config == null) {
            throw new RuntimeException("No matching proxy config for URL [" + url + "]");
        }
        Proxy proxy = this.pub.getProxy(config.area, config.ssl);
        String suffix = url.substring(proxy.getUrl().length());
        return "/" + this.pub.getId() + "/" + config.area + suffix;
    }

    protected static class ProxyConfiguration {
        protected String area;
        protected boolean ssl;
        protected ProxyConfiguration(String area, boolean ssl) {
            this.area = area;
            this.ssl = ssl;
        }
    }

}
