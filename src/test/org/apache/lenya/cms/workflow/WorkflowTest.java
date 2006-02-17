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

/* $Id$  */

package org.apache.lenya.cms.workflow;

import java.io.File;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.commandline.CommandLineRequest;
import org.apache.cocoon.environment.mock.MockEnvironment;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.AccessControlTest;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.workflow.WorkflowManager;
import org.apache.lenya.workflow.Workflowable;

/**
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class WorkflowTest extends AccessControlTest {

    private static final String variableName = "is-live";
    protected static final String URL = "/authoring/index.html";

    protected String getWebappUrl() {
        return "/test" + URL;
    }

    /**
     * Tests the workflow.
     * @throws Exception when something went wrong.
     */
    public void testWorkflow() throws Exception {
        Publication publication = PublicationUtil.getPublication(getManager(), "test");
        String url = "/" + publication.getId() + URL;
        DocumentIdentityMap map = getIdentityMap();
        Document document = map.getFromURL(url);

        File configDir = new File(publication.getDirectory(), "config" + File.separator + "ac"
                + File.separator + "passwd");
        assertTrue(configDir.exists());

        Policy policy = getPolicyManager().getPolicy(getAccreditableManager(), url);

        WorkflowManager resolver = null;
        try {
            resolver = (WorkflowManager) getManager().lookup(WorkflowManager.ROLE);

            String[] emptyRoles = {};

            for (int situationIndex = 0; situationIndex < situations.length; situationIndex++) {

                Identity identity = new Identity();
                ContainerUtil.enableLogging(identity, getLogger());
                User user = getAccreditableManager().getUserManager()
                        .getUser(situations[situationIndex].getUser());
                ContainerUtil.enableLogging(user, getLogger());
                identity.addIdentifiable(user);

                Role[] roles = policy.getRoles(identity);
                getLogger().info("Roles:");

                for (int roleIndex = 0; roleIndex < roles.length; roleIndex++) {
                    getLogger().info(" " + roles[roleIndex]);
                }

                String[] roleIds = new String[roles.length];
                for (int i = 0; i < roles.length; i++) {
                    roleIds[i] = roles[i].getId();
                }
                Session session = new Session(map.getIdentityMap(), identity, getLogger());
                Workflowable instance = new DocumentWorkflowable(getManager(),
                        session,
                        document,
                        getLogger());
                assertNotNull(instance);
                
                if (situationIndex > 0) {
                    getLogger().info("Current state: " + instance.getLatestVersion().getState());
                }

                WorkflowUtil.invoke(getManager(),
                        session,
                        getLogger(),
                        document,
                        situations[situationIndex].getEvent());

                assertTrue(instance.getLatestVersion().getValue(variableName) == situations[situationIndex].getValue());

                getLogger().info("Variable: " + variableName + " = "
                        + instance.getLatestVersion().getValue(variableName));
                getLogger().info("------------------------------------------------------");
            }
        } finally {
            // this.manager.release(resolver);
        }

        getLogger().info("Test completed.");
    }

    private static final TestSituation[] situations = {
            new TestSituation("lenya", "submit", false),
            new TestSituation("roger", "reject", false),
            new TestSituation("lenya", "submit", false),
            new TestSituation("roger", "publish", true),
            new TestSituation("roger", "deactivate", false) };

    /**
     * A test situation.
     */
    public static class TestSituation {
        private String user;
        private String event;
        private boolean value;

        /**
         * Creates a new test situation.
         * @param _user The user.
         * @param _event The event.
         * @param _value The variable value.
         */
        public TestSituation(String _user, String _event, boolean _value) {
            this.user = _user;
            this.event = _event;
            this.value = _value;
        }

        /**
         * Returns the event.
         * @return An event.
         */
        public String getEvent() {
            return this.event;
        }

        /**
         * Returns the user.
         * @return A string.
         */
        public String getUser() {
            return this.user;
        }

        /**
         * Returns the value.
         * @return A value.
         */
        public boolean getValue() {
            return this.value;
        }
    }

}