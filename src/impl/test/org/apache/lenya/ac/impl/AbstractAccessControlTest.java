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

package org.apache.lenya.ac.impl;

import java.io.File;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.file.FileAccreditableManager;
import org.apache.lenya.cms.LenyaTestCase;
import org.apache.lenya.cms.ac.PublicationAccessControllerResolver;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.repository.SessionImpl;

/**
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class AbstractAccessControlTest extends LenyaTestCase {

    protected static final String TEST_PUB_ID = "test";
    private ServiceSelector accessControllerResolverSelector;
    private AccessControllerResolver accessControllerResolver;
    private DefaultAccessController accessController;

    protected org.apache.lenya.cms.repository.Session login(String userId)
            throws AccessControlException {
        return login(userId, TEST_PUB_ID);
    }

    protected Session login(String userId, String pubId) throws AccessControlException {
        Session session = new SessionImpl(null, true, getManager(), getLogger());
        getRequest().setAttribute(Session.class.getName(), session);

        DefaultAccessController ac = getAccessController(session, pubId);
        AccreditableManager acMgr = ac.getAccreditableManager();
        User user = acMgr.getUserManager().getUser(userId);

        if (user == null) {
            throw new AccessControlException("The user [" + userId
                    + "] does not exist in the accreditable manager [" + acMgr.getId() + "]!");
        }

        ac.setupIdentity(getRequest());

        org.apache.cocoon.environment.Session cocoonSession = getRequest().getSession();
        Identity identity = (Identity) cocoonSession.getAttribute(Identity.class.getName());

        if (!identity.contains(user)) {
            User oldUser = identity.getUser();
            if (oldUser != null) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Removing user [" + oldUser + "] from identity.");
                }
                identity.removeIdentifiable(oldUser);
            }
            identity.addIdentifiable(user);
        }

        ac.authorize(getRequest());

        Accreditable[] accrs = identity.getAccreditables();
        for (int i = 0; i < accrs.length; i++) {
            getLogger().info("Accreditable: " + accrs[i]);
        }

        session.setIdentity(identity);
        return session;
    }

    protected DefaultAccessController getAccessController() {
        return getAccessController(getSession(), TEST_PUB_ID);
    }

    protected DefaultAccessController getAccessController(Session session, String pubId) {
        DefaultAccessController controller;
        try {
            this.accessControllerResolverSelector = (ServiceSelector) getManager().lookup(
                    AccessControllerResolver.ROLE + "Selector");
            assertNotNull(this.accessControllerResolverSelector);

            this.accessControllerResolver = (AccessControllerResolver) this.accessControllerResolverSelector
                    .select(AccessControllerResolver.DEFAULT_RESOLVER);

            assertNotNull(this.accessControllerResolver);
            getLogger().info(
                    "Using access controller resolver: ["
                            + this.accessControllerResolver.getClass() + "]");

            Publication pub = getPublication(session, pubId);
            getLogger().info("Resolve access controller");
            getLogger().info(
                    "Publication directory: [" + pub.getDirectory().getAbsolutePath() + "]");

            String url = "/" + pubId + "/authoring/index.html";
            controller = (DefaultAccessController) ((PublicationAccessControllerResolver) this.accessControllerResolver)
                    .resolveAccessController(url);

            assertNotNull(controller);
            getLogger().info("Resolved access controller: [" + controller.getClass() + "]");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return controller;
    }

    /**
     * The teardown method for JUnit
     * @exception Exception if an error occurs
     */
    public void tearDown() throws Exception {

        if (this.accessControllerResolverSelector != null) {
            if (this.accessControllerResolver != null) {
                if (this.accessController != null) {
                    this.accessControllerResolver.release(this.accessController);
                }
                this.accessControllerResolverSelector.release(this.accessControllerResolver);
            }
            getManager().release(this.accessControllerResolverSelector);
        }
        super.tearDown();
    }

    protected static final String USER_ID = "lenya";

    /**
     * Returns the policy manager.
     * @return A policy manager.
     */
    protected PolicyManager getPolicyManager() {
        return getAccessController().getPolicyManager();
    }

    /**
     * Returns the accreditable manager.
     * @return An accreditable manager.
     */
    protected AccreditableManager getAccreditableManager() {
        return getAccessController().getAccreditableManager();
    }

    protected File getAccreditablesDirectory() throws AccessControlException {
        FileAccreditableManager accrMgr = (FileAccreditableManager) getAccreditableManager();
        return accrMgr.getConfigurationDirectory();
    }

    protected DocumentFactory getFactory() {
        return DocumentUtil.createDocumentFactory(getManager(), getSession());
    }

    protected DocumentFactory getFactory(Session session) {
        return DocumentUtil.createDocumentFactory(getManager(), session);
    }

    private Session session;

    protected Session getSession() {
        if (this.session == null) {
            try {
                this.session = login(getUserId());
            } catch (AccessControlException e) {
                throw new RuntimeException(e);
            }
        }
        return this.session;
    }

    protected String getUserId() {
        return USER_ID;
    }

    protected Publication getPublication(Session session, String pubId) throws PublicationException {
        return getFactory(session).getPublication(pubId);
    }

    protected Publication getPublication(String id) throws PublicationException {
        return getFactory().getPublication(id);
    }

    protected Identity getIdentity() {
        return getSession().getIdentity();
    }
}
