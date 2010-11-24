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

/* $Id: AccessControlTest.java 408702 2006-05-22 16:03:49Z andreas $  */

package org.apache.lenya.cms;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Repository;
//import org.apache.lenya.cms.publication.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.publication.Session;

/**
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
//TODO : florent : see how to rewrite this tests

//public class AbstractAccessControlTest extends AbstractLenyaTestCase {
//
//    private static final Log logger = LogFactory.getLog(AbstractAccessControlTest.class);
//
//    protected static final String TEST_PUB_ID = "test";
//    private AccessControllerResolver accessControllerResolver;
//    private Repository repository;
//
//    protected Session login(String userId) throws AccessControlException {
//        return login(userId, TEST_PUB_ID);
//    }
//
//    protected Session login(String userId, String pubId) throws AccessControlException {
//        
//        final Session anonymousSession = getRepository().startSession(null, false);
//        AccessController ac = getAccessController(anonymousSession, pubId);
//        AccreditableManager acMgr = ac.getAccreditableManager();
//        User user = acMgr.getUserManager().getUser(userId);
//
//        if (user == null) {
//            throw new AccessControlException("The user [" + userId + "] does not exist!");
//        }
//        getRequest().setRequestURI("/" + pubId + "/");
//        ac.setupIdentity(getRequest());
//
//        HttpSession cocoonSession = getRequest().getSession();
//        Identity identity = (Identity) cocoonSession.getAttribute(Identity.class.getName());
//
//        if (!identity.contains(user)) {
//            User oldUser = identity.getUser();
//            if (oldUser != null) {
//                if (logger.isDebugEnabled()) {
//                    logger.debug("Removing user [" + oldUser + "] from identity.");
//                }
//                identity.removeIdentifiable(oldUser);
//            }
//            identity.addIdentifiable(user);
//        }
//
//        ac.authorize(getRequest());
//
//        Accreditable[] accrs = identity.getAccreditables();
//        for (int i = 0; i < accrs.length; i++) {
//            logger.info("Accreditable: " + accrs[i]);
//        }
//
//        final Session userSession = getRepository().startSession(identity, true);
//        getRequest().setAttribute(Session.class.getName(), userSession);
//        return userSession;
//    }
//
//    protected AccessController getAccessController() throws AccessControlException {
//        return getAccessController(getSession(), TEST_PUB_ID);
//    }
//
//    protected AccessController getAccessController(Session session, String pubId)
//            throws AccessControlException {
//        Validate.notNull(session, "session");
//        AccessController controller;
//        logger.info("Using access controller resolver: ["
//                + getAccessControllerResolver().getClass() + "]");
//
//        try {
//            Publication pub = session.existsPublication(pubId) ? session.getPublication(pubId)
//                    : session.addPublication(pubId);
//            logger.info("Resolve access controller");
//            logger.info("Publication directory: [" + pub.getSourceUri() + "]");
//        } catch (RepositoryException e) {
//            throw new AccessControlException(e);
//        }
//
//        String url = "/" + pubId + "/authoring/index.html";
//        controller = this.getAccessControllerResolver().resolveAccessController(url);
//
//        assertNotNull(controller);
//        logger.info("Resolved access controller: [" + controller.getClass() + "]");
//        return controller;
//    }
//
//    protected static final String USER_ID = "lenya";
//
//    /**
//     * Returns the policy manager.
//     * @return A policy manager.
//     * @throws AccessControlException
//     */
//    protected PolicyManager getPolicyManager() throws AccessControlException {
//        return getAccessController().getPolicyManager();
//    }
//
//    /**
//     * Returns the accreditable manager.
//     * @return An accreditable manager.
//     * @throws AccessControlException
//     */
//    protected AccreditableManager getAccreditableManager() throws AccessControlException {
//        return getAccessController().getAccreditableManager();
//    }
//
//    private Session session;
//
//    protected Session getSession() {
//        if (this.session == null) {
//            try {
//                this.session = login(getUserId());
//            } catch (AccessControlException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return this.session;
//    }
//
//    protected String getUserId() {
//        return USER_ID;
//    }
//
//    protected Identity getIdentity() {
//        return getSession().getIdentity();
//    }
//
//    public void setRepository(Repository repository) {
//        this.repository = repository;
//    }
//
//    public Repository getRepository() {
//        if (this.repository == null) {
//            this.repository = (Repository) getBeanFactory().getBean(Repository.class.getName());
//        }
//        return repository;
//    }
//
//    public void setAccessControllerResolver(AccessControllerResolver accessControllerResolver) {
//        this.accessControllerResolver = accessControllerResolver;
//    }
//
//    protected AccessControllerResolver getAccessControllerResolver() {
//        if (this.accessControllerResolver == null) {
//            this.accessControllerResolver = (AccessControllerResolver) getBeanFactory().getBean(
//                    AccessControllerResolver.ROLE);
//        }
//        return this.accessControllerResolver;
//    }
//
//}
