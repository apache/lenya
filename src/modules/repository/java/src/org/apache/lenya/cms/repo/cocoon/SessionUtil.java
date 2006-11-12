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
package org.apache.lenya.cms.repo.cocoon;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.repo.Repository;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.avalon.RepositoryFactory;

/**
 * Session utility class.
 */
public class SessionUtil {

    /**
     * @param manager The service manager.
     * @return The current session.
     * @throws RepositoryException if an error occurs.
     */
    public static Session getSession(ServiceManager manager) throws RepositoryException {
        ContextUtility context = null;
        try {
            context = (ContextUtility) manager.lookup(ContextUtility.ROLE);
            Request request = context.getRequest();
            org.apache.cocoon.environment.Session cocoonSession = request.getSession(true);

            Session session = (Session) cocoonSession.getAttribute(Session.class.getName());
            if (session == null) {
                session = createSession(manager, request);
                cocoonSession.setAttribute(Session.class.getName(), session);
            }
            return session;
        } catch (RepositoryException e) {
            throw e;
        } catch (ServiceException e) {
            throw new RepositoryException(e);
        } finally {
            if (context != null) {
                manager.release(context);
            }
        }
    }

    protected static Session createSession(ServiceManager manager, Request request)
            throws ServiceException, RepositoryException {
        Session session;
        RepositoryFactory factory = null;
        try {
            factory = (RepositoryFactory) manager.lookup(RepositoryFactory.ROLE);
            Repository repo = factory.getRepository();

            User user = getUserId(request);

            session = repo.login(user.getId());
        } finally {
            if (factory != null) {
                manager.release(factory);
            }
        }
        return session;
    }

    protected static User getUserId(Request request) throws RepositoryException {
        Identity identity = (Identity) request.getSession(false)
                .getAttribute(Identity.class.getName());
        if (identity == null) {
            throw new RepositoryException("The session doesn't contain an identity.");
        }
        User user = identity.getUser();
        if (user == null) {
            throw new RepositoryException("The identity doesn't contain a user.");
        }
        return user;
    }

}
