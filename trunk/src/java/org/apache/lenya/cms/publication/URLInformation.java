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

/* $Id$  */

package org.apache.lenya.cms.publication;

/**
 * This class resolves all Lenya-specific information from a webapp URL.
 */
public class URLInformation {

    private String publicationId = null;
    private String area = null;
    private String completeArea = null;
    private String documentUrl = null;

    private String url;

    /**
     * Returns the area (without the "webdav" prefix).
     * @return A string.
     */
    public String getArea() {
        if (this.area == null) {
            String completeArea = getCompleteArea();
            if (Publication.DAV_AREA.equals(completeArea)) {
                this.area = Publication.AUTHORING_AREA;
            } else {
                this.area = completeArea;
            }
        }
        return this.area;
    }

    /**
     * Returns the complete area (including the "webdav" prefix).
     * @return A string.
     */
    public String getCompleteArea() {
        String pubId = getPublicationId();
        if (this.completeArea == null && pubId != null) {
            String pubUrl = this.url.substring(pubId.length());
            if (pubUrl.startsWith("/")) {
                this.completeArea = extractBeforeSlash(pubUrl.substring(1));
            } else {
                this.completeArea = null;
            }
        }
        return this.completeArea;
    }

    /**
     * Returns the document URL.
     * @return A string.
     */
    public String getDocumentUrl() {
        if (this.documentUrl == null) {
            String pubId = getPublicationId();
            String area = getCompleteArea();
            if (pubId != null && area != null) {
                String prefix = pubId + "/" + area;
                this.documentUrl = this.url.substring(prefix.length());
            }
        }
        return this.documentUrl;
    }

    /**
     * Returns the publication ID.
     * @return A string.
     */
    public String getPublicationId() {
        if (this.publicationId == null) {
            this.publicationId = extractBeforeSlash(this.url);
        }
        return this.publicationId;
    }

    protected String extractBeforeSlash(String remaining) {

        if (remaining.length() == 0) {
            return null;
        }

        String step;
        int slashIndex = remaining.indexOf('/');
        if (slashIndex == -1) {
            step = remaining;
        } else {
            step = remaining.substring(0, slashIndex);
        }
        return step;
    }

    /**
     * Ctor.
     * @param webappUrl A webapp URL (without context prefix).
     */
    public URLInformation(String webappUrl) {

        if (!webappUrl.startsWith("/")) {
            throw new RuntimeException("The URL [" + webappUrl + "] doesn't start with a slash!");
        }

        this.url = webappUrl.substring(1);
    }
}
