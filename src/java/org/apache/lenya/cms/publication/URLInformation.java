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

    /**
     * Returns the area (without the "info-" prefix).
     * @return A string.
     */
    public String getArea() {
        return this.area;
    }

    /**
     * Returns the complete area (including the "info-" prefix).
     * @return A string.
     */
    public String getCompleteArea() {
        return this.completeArea;
    }

    /**
     * Returns the document URL.
     * @return A string.
     */
    public String getDocumentUrl() {
        return this.documentUrl;
    }

    /**
     * Returns the publication ID.
     * @return A string.
     */
    public String getPublicationId() {
        return this.publicationId;
    }

    /**
     * Ctor.
     * @param webappUrl A webapp URL (without context prefix).
     */
    public URLInformation(String webappUrl) {
        assert webappUrl.startsWith("/");

        String url = webappUrl.substring(1);

        String[] fragments = url.split("/");
        if (fragments.length > 0) {
            this.publicationId = fragments[0];

            if (fragments.length > 1) {
                this.completeArea = fragments[1];

                if (url.length() > (this.publicationId + "/" + this.completeArea).length()) {
                    this.documentUrl = url.substring((this.publicationId + "/" + this.completeArea).length());
                } else {
                    this.documentUrl = "";
                }

                if (this.completeArea.startsWith(Publication.SEARCH_AREA_PREFIX)) {
                    this.area = this.completeArea.substring(Publication.SEARCH_AREA_PREFIX.length());
                } else {
                    this.area = this.completeArea;
                }
            }
        }
    }

}
