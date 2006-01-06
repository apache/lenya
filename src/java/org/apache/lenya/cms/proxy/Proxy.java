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
package org.apache.lenya.cms.proxy;

import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Site;
import org.apache.lenya.cms.repo.SiteNode;

/**
 * <p>
 * An object of this class represents a proxy configuration.
 * </p>
 * <p>
 * Configuration example:
 * </p>
 * 
 * <pre>
 *  &lt;proxy area=&quot;live&quot; ssl=&quot;true&quot; url=&quot;https://www.host.com/ssl/default&quot;/&gt;
 *  &lt;proxy area=&quot;live&quot; ssl=&quot;false&quot; url=&quot;http://www.host.com/default&quot;/&gt;
 *  &lt;proxy area=&quot;authoring&quot; ssl=&quot;true&quot; url=&quot;https://www.host.com/lenya/default/authoring&quot;/&gt;
 *  &lt;proxy area=&quot;authoring&quot; ssl=&quot;false&quot; url=&quot;http://www.host.com/lenya/default/authoring&quot;/&gt;
 * </pre>
 * 
 * @version $Id$
 */
public class Proxy {

    private String url;

    /**
     * Returns the absolute URL of a particular document.
     * @param document The document.
     * @return A string.
     */
    public String getURL(Document document) {
        try {
            Site site = document.getContentNode().getContent().getArea().getSite();
            SiteNode siteNode = site.getFirstReference(document);
            return getUrl() + siteNode.getPath();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the proxy URL.
     * @return A string.
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Sets the proxy URL.
     * @param _url The url to set.
     */
    public void setUrl(String _url) {
        this.url = _url;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Proxy URL=[" + getUrl() + "]";
    }
}