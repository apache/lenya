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
            org.apache.cocoon.environment.Session cocoonSession = request.getSession();
            Identity identity = (Identity) cocoonSession.getAttribute(Identity.class.getName());
            session = createSession(manager, identity);
            request.setAttribute(Session.class.getName(), session);
        }
        return session;
    }

    /**
     * Creates a session.
     * @param manager The service manager.
     * @param identity The identity.
     * @return a session.
     * @throws RepositoryException if an error occurs.
     */
    public static Session createSession(ServiceManager manager, Identity identity)
            throws RepositoryException {
        RepositoryManager repoMgr = null;
        Session session;
        try {
            repoMgr = (RepositoryManager) manager.lookup(RepositoryManager.ROLE);
            session = repoMgr.createSession(identity);
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {
            manager.release(repoMgr);
        }
        return session;
    }

}