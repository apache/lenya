/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.workflow;

import java.io.File;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Group;
import org.apache.lenya.cms.ac.ItemManager;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac.UserManager;
import org.apache.lenya.cms.ac2.Identity;
import org.apache.lenya.cms.ac2.Policy;
import org.apache.lenya.cms.ac2.file.FileAccessController;
import org.apache.lenya.cms.publication.DefaultDocument;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuildException;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.TestPageEnvelope;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkflowTest extends TestCase {

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

    /**
     * Tests the workflow.
     * @throws DocumentTypeBuildException when something went wrong.
     * @throws WorkflowException when something went wrong.
     * @throws AccessControlException when something went wrong.
     * @throws PageEnvelopeException when something went wrong.
     */
    public void testWorkflow()
        throws
            DocumentTypeBuildException,
            WorkflowException,
            AccessControlException,
            PageEnvelopeException {

        Publication publication = PublicationHelper.getPublication();
        Document document = new DefaultDocument(publication, "index");

        File configDir = new File(publication.getDirectory(), ItemManager.PATH);
        assertTrue(configDir.exists());

        File configurationDirectory = new File(publication.getDirectory(), "config/ac");
        FileAccessController accessController = new FileAccessController(configurationDirectory);
        PageEnvelope envelope = new TestPageEnvelope(publication, "index.html");
        Policy policy = ((FileAccessController) accessController).getPolicy(envelope);

        DocumentType type = DocumentTypeBuilder.buildDocumentType(documentTypeName, publication);
        String workflowId = type.getWorkflowFileName();
        WorkflowFactory.initHistory(document, workflowId);

        WorkflowFactory factory = WorkflowFactory.newInstance();
        for (int situationIndex = 0; situationIndex < situations.length; situationIndex++) {

            WorkflowInstance instance = null;
            instance = factory.buildInstance(document);
            assertNotNull(instance);

            System.out.println("Current state: " + instance.getCurrentState());

            User user =
                UserManager.instance(configDir).getUser(situations[situationIndex].getUser());

            Identity identity = new Identity();
            identity.addIdentifiable(user);
            Role roles[] = policy.getRoles(identity);
            System.out.print("Roles:");
            for (int roleIndex = 0; roleIndex < roles.length; roleIndex++) {
                System.out.print(" " + roles[roleIndex]);
            }
            System.out.println();

            Group group = new Group("test-group");
            //group.addRole(role);

            //user.addGroup(group);

            Situation situation = null;
            try {
                situation = factory.buildSituation(roles);
            } catch (WorkflowException e1) {
                e1.printStackTrace(System.err);
            }
            Event events[] = instance.getExecutableEvents(situation);

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

    private static final TestSituation situations[] =
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
            assert user != null;
            this.user = user;
            assert event != null;
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

    private static String documentTypeName;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        if (documentTypeName == null) {
            String args[] = { "D:\\Development\\build\\tomcat-4.1.24\\webapps\\lenya", "test" };
            PublicationHelper.extractPublicationArguments(args);
            documentTypeName = "simple";
        }
    }

}
