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

/**
 * Value object to identify documents.
 */
public class DocumentIdentifier {

    private String publicationId;
    private String area;
    private String language;
    private String uuid;

    /**
     * Ctor.
     * @param pubId The publication ID.
     * @param area The area.
     * @param uuid The document UUID.
     * @param language The language.
     */
    public DocumentIdentifier(String pubId, String area, String uuid, String language) {

        if (uuid.startsWith("/") && uuid.split("-").length == 4) {
            throw new IllegalArgumentException("The UUID [" + uuid + "] must not begin with a '/'!");
        }
        if (uuid.indexOf("/") > 0) {
            throw new IllegalArgumentException("The UUID [" + uuid
                    + "] must not contain a '/' after the first position!");
        }

        this.publicationId = pubId;
        this.area = area;
        this.language = language;
        this.uuid = uuid;
    }

    /**
     * @return The UUID.
     */
    public String getUUID() {
        return this.uuid;
    }

    /**
     * @return The area.
     */
    public String getArea() {
        return area;
    }

    /**
     * @return The language.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @return The publication ID.
     */
    public String getPublicationId() {
        return publicationId;
    }

    public boolean equals(Object obj) {
        return (obj instanceof DocumentIdentifier) && obj.hashCode() == hashCode();
    }

    public int hashCode() {
        return getKey().hashCode();
    }

    protected String getKey() {
        return this.publicationId + ":" + this.area + ":" + this.uuid + ":" + this.language;
    }

    public String toString() {
        return getKey();
    }

}
