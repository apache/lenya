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

    /**
     * Tests the workflow.
     * @throws Exception when something went wrong.
     */
    public void testWorkflow() throws Exception {
        Publication publication = PublicationUtil.getPublication(getManager(), "test");
        String url = "/" + publication.getId() + URL;
        DocumentIdentityMap map = getIdentityMap();
        Document document = map.getFromURL(url);
        Session session = new Session(map.getIdentityMap(), getIdentity(), getLogger());

        File configDir = new File(publication.getDirectory(), "config" + File.separator + "ac"
                + File.separator + "passwd");
        assertTrue(configDir.exists());

        Policy policy = getPolicyManager().getPolicy(getAccreditableManager(), url);

        WorkflowManager resolver = null;
        try {
            resolver = (WorkflowManager) getManager().lookup(WorkflowManager.ROLE);

            String[] emptyRoles = {};

            Workflowable instance = new DocumentWorkflowable(getManager(),
                    session,
                    document,
                    getLogger());

            for (int situationIndex = 0; situationIndex < situations.length; situationIndex++) {
                assertNotNull(instance);

                System.out.println("Current state: " + instance.getLatestVersion().getState());

                Identity identity = new Identity();
                User user = getAccreditableManager().getUserManager()
                        .getUser(situations[situationIndex].getUser());
                identity.addIdentifiable(user);

                Role[] roles = policy.getRoles(identity);
                System.out.print("Roles:");

                for (int roleIndex = 0; roleIndex < roles.length; roleIndex++) {
                    System.out.print(" " + roles[roleIndex]);
                }

                System.out.println();

                String[] roleIds = new String[roles.length];
                for (int i = 0; i < roles.length; i++) {
                    roleIds[i] = roles[i].getId();
                }


                assertTrue(instance.getLatestVersion().getValue(variableName) == situations[situationIndex].getValue());

                System.out.println("Variable: " + variableName + " = "
                        + instance.getLatestVersion().getValue(variableName));
                System.out.println("------------------------------------------------------");
            }
        } finally {
            // this.manager.release(resolver);
        }

        System.out.println("Test completed.");
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