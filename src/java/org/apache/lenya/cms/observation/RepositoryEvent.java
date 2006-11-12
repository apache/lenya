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
package org.apache.lenya.cms.observation;

import org.apache.lenya.cms.publication.ResourceType;

/**
 * Repository event.
 */
public class RepositoryEvent {

    private String pubId;
    private String area;
    private String uuid;
    private String documentUrl;
    private String language;
    private ResourceType resourceType;

    public RepositoryEvent(String pubId, String area, String uuid, String language,
            ResourceType resourceType, String documentUrl) {
        this.pubId = pubId;
        this.area = area;
        this.uuid = uuid;
        this.language = language;
        this.documentUrl = documentUrl;
        this.resourceType = resourceType;
    }

    public String getArea() {
        return area;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public String getPublicationId() {
        return pubId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getLanguage() {
        return language;
    }
    
    public ResourceType getResourceType() {
        return this.resourceType;
    }

}
