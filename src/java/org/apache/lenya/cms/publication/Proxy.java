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
package org.apache.lenya.cms.publication;

import org.apache.lenya.util.Assert;

/**
 * <p>
 * An object of this class represents a proxy configuration.
 * </p>
 * <p>
 * Configuration example (<code>$PUB_HOME/config/publication.xml</code>):
 * </p>
 * <pre>
 * &lt;proxies&gt;
 *   &lt;proxy area="live" ssl="true" url="https://www.host.com/ssl/default"/&gt;
 *   &lt;proxy area="live" ssl="false" url="http://www.host.com/default"/&gt;
 *   &lt;proxy area="authoring" ssl="true" url="https://www.host.com/lenya/default/authoring"/&gt;
 *   &lt;proxy area="authoring" ssl="false" url="http://www.host.com/lenya/default/authoring"/&gt;
 * &lt;proxies;&gt;
 * </pre>
 * 
 * @version $Id$
 */
public class Proxy {
    
    private String defaultUrl;
    private String url;

    /**
     * @param defaultUrl The default proxy URL.
     */
    public Proxy(String defaultUrl) {
        Assert.notNull("default URL", defaultUrl);
        this.defaultUrl = defaultUrl;
    }

    /**
     */
    public Proxy() {
    }

    /**
     * @param area The area.
     * @return The proxy URL if no proxy is declared in {@link PublicationConfiguration#CONFIGURATION_FILE}.
     */
    public String getDefaultUrl() {
        return this.defaultUrl;
    }

    /**
     * Returns the absolute URL of a particular document.
     * @param document The document.
     * @return A string.
     */
    public String getURL(Document document) {
        return getUrl() + document.getCanonicalDocumentURL();
    }

    /**
     * Returns the proxy URL.
     * @return A string.
     */
    public String getUrl() {
        if (this.url != null) {
            return this.url;
        }
        else if (this.defaultUrl != null) {
            return this.defaultUrl;
        }
        else {
            throw new IllegalStateException("This proxy has no URL.");
        }
    }

    /**
     * Sets the proxy URL.
     * @param _url The url to set.
     */
    public void setUrl(String _url) {
        Assert.notNull("url", _url);
        this.url = _url;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Proxy URL=[" + getUrl() + "]";
    }
    
}