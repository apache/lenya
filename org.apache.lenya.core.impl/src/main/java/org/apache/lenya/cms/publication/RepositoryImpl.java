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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.Validate;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.observation.ObservationRegistry;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.SharedItemStore;
import org.apache.lenya.cms.repository.UUIDGenerator;

public class RepositoryImpl implements Repository {

    public Session getSession(HttpServletRequest request) {
        Validate.notNull(request);
        SessionImpl session = (SessionImpl) request.getAttribute(Session.class.getName());
        if (session == null) {
            Identity identity = getIdentity(request);
            // attach a read-only repository session to the HTTP request
            session = (SessionImpl) startSession(identity, false);
            request.setAttribute(Session.class.getName(), session);
        } else if (session.getIdentity() == null) {
            Identity identity = getIdentity(request);
            if (identity != null) {
                session.setIdentity(identity);
            }
        }
        return session;
    }

    public Session startSession(Identity identity, boolean modifiable) {
        SessionImpl session = new SessionImpl(identity, modifiable);
        try {
            session.setObservationRegistry(getObservationRegistry());
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        session.setUuidGenerator(getUuidGenerator());
        session.setSharedItemStore(getSharedItemStore());
        return session;
    }

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

    protected static Identity getIdentity(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Identity) session.getAttribute(Identity.class.getName());
    }

    /**
     * Removes the repository session from the servlet session.
     * @param request The current request.
     */
    public void removeSession(HttpServletRequest request) {
        request.removeAttribute(Session.class.getName());
    }

}
