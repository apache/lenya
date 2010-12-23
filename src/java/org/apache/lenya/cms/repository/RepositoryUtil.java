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

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.Identity;

/**
 * Repository utility class.
 */
public class RepositoryUtil {

    /**
     * Returns the session attached to the request or creates a session.
     * @param manager The service manager.
     * @param request The request.
     * @return A session.
     * @throws RepositoryException if an error occurs.
     */
    public static Session getSession(ServiceManager manager, Request request)
            throws RepositoryException {
        Session session = (Session) request.getAttribute(Session.class.getName());
        if (session == null) {
            Identity identity = getIdentity(request);
            if (identity == null)
                identity = Identity.ANONYMOUS;
            // attach a read-only repository session to the HTTP request
            session = createSession(manager, identity, false);
            request.setAttribute(Session.class.getName(), session);
        } else if (session.getIdentity() == null) {
            Identity identity = getIdentity(request);
            if (identity != null) {
                session.setIdentity(identity);
            }
        }
        return session;
    }

    protected static Identity getIdentity(Request request) {
        org.apache.cocoon.environment.Session cocoonSession = request.getSession();
        return Identity.getIdentity(cocoonSession);
    }

    /**
     * Creates a session.
     * @param manager The service manager.
     * @param identity The identity.
     * @param modifiable Determines if the repository items in this session should be modifiable.
     * @return a session.
     * @throws RepositoryException if an error occurs.
     */
    public static Session createSession(ServiceManager manager, Identity identity, boolean modifiable)
            throws RepositoryException {
        RepositoryManager repoMgr = null;
        Session session;
        try {
            repoMgr = (RepositoryManager) manager.lookup(RepositoryManager.ROLE);
            session = repoMgr.createSession(identity, modifiable);
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {
            manager.release(repoMgr);
        }
        return session;
    }

    /**
     * Removes the repository session from the servlet session.
     * @param manager The service manager.
     * @param request The current request.
     */
    public static void removeSession(ServiceManager manager, Request request) {
        request.removeAttribute(Session.class.getName());
        /*
        org.apache.cocoon.environment.Session session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(Session.class.getName());
        }
        */
    }

}
