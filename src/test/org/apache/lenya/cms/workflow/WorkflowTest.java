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

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.AccessControlTest;
import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class WorkflowTest extends AccessControlTest {
    /**
     * Constructor.
     * @param test The test to execute.
     */
    public WorkflowTest(String test) {
        super(test);
    }

    /**
     * The main program for the WorkflowTest class
     * 
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        args = PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(getSuite());
    }

    /**
     * Returns the test suite.
     * @return A test suite.
     */
    public static Test getSuite() {
        return new TestSuite(WorkflowTest.class);
    }

    private static final String variableName = "is-live";
    protected static final String URL = "/authoring/index.html";

    /**
     * Tests the workflow.
     * @throws Exception when something went wrong.
     */
    public void testWorkflow() throws Exception {
        Publication publication = PublicationHelper.getPublication();
        String url = "/" + publication.getId() + URL;
        DocumentIdentityMap map = new DocumentIdentityMap(publication);
        Document document = map.getFactory().getFromURL(url);

        File configDir = new File(publication.getDirectory(), "config" + File.separator + "ac"
                + File.separator + "passwd");
        assertTrue(configDir.exists());

        Policy policy = getPolicyManager().getPolicy(getAccreditableManager(), url);
        
        WorkflowResolver resolver = null;
        try {
            resolver = (WorkflowResolver) getManager().lookup(WorkflowResolver.ROLE);


            String[] emptyRoles = {};
            Situation situation = new CMSSituation(emptyRoles, "test", "127.0.0.1");

            WorkflowInstance instance = resolver.getWorkflowInstance(document);
            instance.getHistory().initialize(situation);

            for (int situationIndex = 0; situationIndex < situations.length; situationIndex++) {
                assertNotNull(instance);

                System.out.println("Current state: " + instance.getCurrentState());

                Identity identity = new Identity();
                User user = getAccreditableManager().getUserManager().getUser(
                        situations[situationIndex].getUser());
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

                situation = new CMSSituation(roleIds, identity.getUser().getId(), "");

                String[] events = instance.getExecutableEvents(situation);

                String event = null;
                System.out.print("Events:");

                for (int eventIndex = 0; eventIndex < events.length; eventIndex++) {
                    System.out.print(" " + events[eventIndex]);

                    if (events[eventIndex].equals(situations[situationIndex].getEvent())) {
                        event = events[eventIndex];
                    }
                }

                assertNotNull(event);
                System.out.println();

                System.out.println("Executing event: " + event);
                instance.invoke(situation, event);

                assertTrue(instance.getValue(variableName) == situations[situationIndex].getValue());

                System.out.println("Variable: " + variableName + " = "
                        + instance.getValue(variableName));
                System.out.println("------------------------------------------------------");
            }
        }
        finally {
//            this.manager.release(resolver);
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