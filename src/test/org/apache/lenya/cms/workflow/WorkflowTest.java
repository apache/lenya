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

/* $Id: WorkflowTest.java,v 1.23 2004/03/04 15:41:10 egli Exp $  */

package org.apache.lenya.cms.workflow;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.file.FileItemManager;
import org.apache.lenya.ac.impl.AccessControlTest;
import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuildException;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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
        documentTypeName = args[0];
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
     * @throws DocumentTypeBuildException when something went wrong.
     * @throws WorkflowException when something went wrong.
     * @throws AccessControlException when something went wrong.
     * @throws PageEnvelopeException when something went wrong.
     * @throws DocumentBuildException when something went wrong.
     */
    public void testWorkflow()
        throws
            DocumentTypeBuildException,
            WorkflowException,
            AccessControlException,
            PageEnvelopeException,
            DocumentBuildException {
        Publication publication = PublicationHelper.getPublication();
        String url = "/" + publication.getId() + URL;
        Document document = publication.getDocumentBuilder().buildDocument(publication, url);

        File configDir = new File(publication.getDirectory(), FileItemManager.PATH);
        assertTrue(configDir.exists());

        Policy policy = getPolicyManager().getPolicy(getAccreditableManager(), url);

        DocumentType type = DocumentTypeBuilder.buildDocumentType(documentTypeName, publication);
        String workflowId = type.getWorkflowFileName();

        WorkflowFactory factory = WorkflowFactory.newInstance();

        String[] emptyRoles = {
        };
        Situation situation = factory.buildSituation(emptyRoles, "test", "127.0.0.1");

        WorkflowFactory.initHistory(document, workflowId, situation);

        for (int situationIndex = 0; situationIndex < situations.length; situationIndex++) {
            WorkflowInstance instance = null;
            instance = factory.buildInstance(document);
            assertNotNull(instance);

            System.out.println("Current state: " + instance.getCurrentState());

            Identity identity = new Identity();
            User user =
                getAccreditableManager().getUserManager().getUser(
                    situations[situationIndex].getUser());
            identity.addIdentifiable(user);

            Role[] roles = policy.getRoles(identity);
            System.out.print("Roles:");

            for (int roleIndex = 0; roleIndex < roles.length; roleIndex++) {
                System.out.print(" " + roles[roleIndex]);
            }

            System.out.println();

            situation = null;

            try {
                situation = factory.buildSituation(roles, identity);
            } catch (WorkflowException e1) {
                e1.printStackTrace(System.err);
            }

            Event[] events = instance.getExecutableEvents(situation);

            Event event = null;
            System.out.print("Events:");

            for (int eventIndex = 0; eventIndex < events.length; eventIndex++) {
                System.out.print(" " + events[eventIndex]);

                if (events[eventIndex].getName().equals(situations[situationIndex].getEvent())) {
                    event = events[eventIndex];
                }
            }

            assertNotNull(event);
            System.out.println();

            System.out.println("Executing event: " + event);
            instance.invoke(situation, event);

            assertTrue(instance.getValue(variableName) == situations[situationIndex].getValue());

            System.out.println(
                "Variable: " + variableName + " = " + instance.getValue(variableName));
            System.out.println("------------------------------------------------------");
        }

        System.out.println("Test completed.");
    }

    private static final TestSituation[] situations =
        {
            new TestSituation("lenya", "submit", false),
            new TestSituation("roger", "reject", false),
            new TestSituation("lenya", "submit", false),
            new TestSituation("roger", "publish", true),
            new TestSituation("roger", "deactivate", false)};

    /**
     * A test situation.
     */
    public static class TestSituation {
        private String user;
        private String event;
        private boolean value;

        /**
         * Creates a new test situation.
         * @param user The user.
         * @param event The event.
         * @param value The variable value.
         */
        public TestSituation(String user, String event, boolean value) {
            this.user = user;
            this.event = event;
            this.value = value;
        }

        /**
         * Returns the event.
         * @return An event.
         */
        public String getEvent() {
            return event;
        }

        /**
         * Returns the user.
         * @return A string.
         */
        public String getUser() {
            return user;
        }

        /**
         * Returns the value.
         * @return A value.
         */
        public boolean getValue() {
            return value;
        }
    }

    private static String documentTypeName = "simple";

}
