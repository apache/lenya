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
package org.apache.lenya.cms.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lenya.cms.observation.ObservationRegistry;
//florent import org.apache.lenya.transaction.Identity;
//import org.apache.lenya.ac.Identity;
//florent remove session
//import org.apache.lenya.cms.repository.SessionImpl;
//import org.apache.lenya.cms.repository.Session;
/**
 * Repository manager implementation.
 */
public class RepositoryManagerImpl implements RepositoryManager {
    
    private static final Log logger = LogFactory.getLog(RepositoryManagerImpl.class);
    
//    public Session createSession(Identity identity, boolean modifiable) throws RepositoryException {
//        SessionImpl session = new SessionImpl(identity, modifiable);
//        session.setObservationRegistry(getObservationRegistry());
//        session.setSharedItemStore(getSharedItemStore());
//        return session;
//    }

    private SharedItemStore sharedItemStore;
    private UUIDGenerator uuidGenerator;
    private ObservationRegistry observationRegistry;

    protected SharedItemStore getSharedItemStore() {
        return sharedItemStore;
    }

    public void setSharedItemStore(SharedItemStore sharedItemStore) {
        this.sharedItemStore = sharedItemStore;
    }

    protected UUIDGenerator getUuidGenerator() {
        return uuidGenerator;
    }

    public void setUuidGenerator(UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    protected ObservationRegistry getObservationRegistry() {
        return observationRegistry;
    }

    public void setObservationRegistry(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

}
