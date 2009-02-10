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
import org.apache.lenya.cms.repository.Session;

/**
 * Document-related event.
 */
public class DocumentEvent extends RepositoryEvent {

    private String pubId;
    private String area;
    private String uuid;
    private String language;
    private ResourceType resourceType;


    /**
     * The change action.
     */
    public static final Object CHANGED = "changed";
    /**
     * The removal action.
     */
    public static final Object REMOVED = "removed";

    /**
     * Ctor.
     * @param session The session.
     * @param pubId The publication ID.
     * @param area The area.
     * @param uuid The UUID.
     * @param language The language.
     * @param resourceType The resource type.
     * @param descriptor More information about the event, for example
     *        {@link #CHANGED} or {@link #REMOVED}.
     */
    public DocumentEvent(Session session, String pubId, String area, String uuid, String language,
            ResourceType resourceType, Object descriptor) {
        super(session, descriptor);
        this.pubId = pubId;
        this.area = area;
        this.uuid = uuid;
        this.language = language;
        this.resourceType = resourceType;
    }

    /**
     * @return The area.
     */
    public String getArea() {
        return area;
    }

    /**
     * @return The publication ID.
     */
    public String getPublicationId() {
        return pubId;
    }

    /**
     * @return The UUID.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @return The language.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @return The resource type.
     */
    public ResourceType getResourceType() {
        return this.resourceType;
    }

}
