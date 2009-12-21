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

import java.net.MalformedURLException;
import java.util.StringTokenizer;

import org.apache.lenya.util.Query;

/**
 * A link to a document.
 */
public class Link {

    protected static final String PAIR_DELIMITER = ",";
    protected static final String KEY_VALUE_DELIMITER = "=";

    private String uuid;
    private String language;
    private String revision;
    private String area;
    private String pubId;
    
    /**
     * Ctor.
     */
    public Link() {
    }
    
    /**
     * Ctor.
     * @param linkUri The link URI.
     * @throws MalformedURLException if the URI doesn't represent a link.
     */
    public Link(String linkUri) throws MalformedURLException {
        
        if (!linkUri.startsWith(LinkResolver.SCHEME + ":")) {
            throw new MalformedURLException("The string [" + linkUri + "] is not a valid link URI!");
        }
        
        StringTokenizer schemeAndPath = new StringTokenizer(linkUri, ":");
        schemeAndPath.nextToken();
        if (schemeAndPath.hasMoreTokens()) {
            String path = schemeAndPath.nextToken();
    
            if (path.indexOf(PAIR_DELIMITER) > -1) {
                int firstDelimiterIndex = path.indexOf(PAIR_DELIMITER);
                this.uuid = path.substring(0, firstDelimiterIndex);
                String pathQueryString = path.substring(firstDelimiterIndex + 1);
                Query query = new Query(pathQueryString, PAIR_DELIMITER, KEY_VALUE_DELIMITER);
                this.pubId = query.getValue("pub");
                this.area = query.getValue("area");
                this.language = query.getValue("lang");
                this.revision = query.getValue("rev");
            } else {
                this.uuid = path;
            }
        }
    }
    
    /**
     * @return The area.
     */
    public String getArea() {
        return area;
    }

    /**
     * @param area The area.
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * @return The language.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language The language.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return The publication ID.
     */
    public String getPubId() {
        return pubId;
    }

    /**
     * @param pubId The publication ID.
     */
    public void setPubId(String pubId) {
        this.pubId = pubId;
    }

    /**
     * @return The revision.
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @param revision The revision.
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * @return The UUID.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid The UUID.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return The link URI.
     */
    public String getUri() {
        String uri = LinkResolver.SCHEME + ":";
        if (this.uuid != null) {
            uri = uri + this.uuid;
        }
        if (this.language != null) {
            uri = uri + ",lang=" + this.language;
        }
        if (this.area != null) {
            uri = uri + ",area=" + this.area;
        }
        if (this.pubId != null) {
            uri = uri + ",pub=" + this.pubId;
        }
        if (this.revision != null) {
            uri = uri + ",rev=" + this.revision;
        }
        return uri;
    }

    public boolean equals(Object obj) {
        if (!getClass().isInstance(obj)) {
            return false;
        }
        return ((Link) obj).getUri().equals(getUri());
    }

    public int hashCode() {
        return getUri().hashCode();
    }

    public String toString() {
        return getUri();
    }
    
}
