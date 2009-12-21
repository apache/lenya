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

import org.apache.lenya.cms.publication.Document;

/**
 * Observation registry.
 */
public interface ObservationRegistry extends RepositoryListener {

    /**
     * The Avalon service role.
     */
    String ROLE = ObservationRegistry.class.getName();

    /**
     * @param listener The listener.
     * @param document The document to listen to.
     * @throws ObservationException if the listener is already registered for this document.
     */
    void registerListener(RepositoryListener listener, Document document) throws ObservationException;
    
    /**
     * Registers a listener which is notified for all events.
     * @param listener The listener.
     * @throws ObservationException if the listener is already registered.
     */
    void registerListener(RepositoryListener listener) throws ObservationException;

}
