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
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryManager;

public class RepositoryImpl implements Repository {

    private RepositoryManager repositoryManager;

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

        IdentityWrapper wrapper = new IdentityWrapper(identity);
        org.apache.lenya.cms.repository.Session repoSession;
        try {
            repoSession = this.repositoryManager
                    .createSession(wrapper, modifiable);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        SessionImpl session = new SessionImpl(this, repoSession);
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
    public void removeSession(HttpServletRequest request) {
        request.removeAttribute(Session.class.getName());
    }

    public void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public RepositoryManager getRepositoryManager() {
        return repositoryManager;
    }

}
