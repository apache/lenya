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
package org.apache.lenya.cms.lucene;

import org.apache.cocoon.components.search.IndexException;
import org.apache.lenya.cms.observation.RepositoryListener;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.Session;

/**
 * Index updater which updates the index when a document changes.
 */
public interface IndexUpdater extends RepositoryListener {
    
    /**
     * The service role.
     */
    String ROLE = IndexUpdater.class.getName();

    /**
     * Adds a document to the index.
     * @param session The session.
     * @param resourceType The resource type.
     * @param publicationId The publication ID.
     * @param area The area.
     * @param uuid The UUID.
     * @param language The language.
     * @throws IndexException if an error occurs.
     */
    void index(Session session, ResourceType resourceType, String publicationId, String area, String uuid,
            String language) throws IndexException;

    /**
     * Deletes a document from the index.
     * @param session The session.
     * @param resourceType The resource type.
     * @param publicationId The publication ID.
     * @param area The area.
     * @param uuid The UUID.
     * @param language The language.
     * @throws IndexException if an error occurs.
     */
    void delete(Session session, ResourceType resourceType, String publicationId, String area,
            String uuid, String language) throws IndexException;

}
