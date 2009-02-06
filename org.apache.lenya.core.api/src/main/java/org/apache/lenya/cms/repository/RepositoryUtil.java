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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.lenya.ac.Identity;

/**
 * Repository utility class.
 */
public class RepositoryUtil {

    /**
     * Returns the session attached to the request or creates a session.
     * @param repoManager The repository manager.
     * @param request The request.
     * @return A session.
     * @throws RepositoryException if an error occurs.
     */
    public static Session getSession(RepositoryManager repoManager, HttpServletRequest request)
            throws RepositoryException {
        Session session = (Session) request.getAttribute(Session.class.getName());
        if (session == null) {
            Identity identity = getIdentity(request);
            // attach a read-only repository session to the HTTP request
            session = repoManager.createSession(identity, false);
            request.setAttribute(Session.class.getName(), session);
        } else if (session.getIdentity() == null) {
            Identity identity = getIdentity(request);
            if (identity != null) {
                session.setIdentity(identity);
            }
        }
        return session;
    }

    protected static Identity getIdentity(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Identity) session.getAttribute(Identity.class.getName());
    }

    /**
     * Removes the repository session from the servlet session.
     * @param request The current request.
     */
    public static void removeSession(HttpServletRequest request) {
        request.removeAttribute(Session.class.getName());
    }

}
