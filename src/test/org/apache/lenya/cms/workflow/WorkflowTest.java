/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.workflow;

import junit.framework.Test;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.ItemManager;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac2.AccessControlTest;
import org.apache.lenya.cms.ac2.Identity;
import org.apache.lenya.cms.ac2.Policy;
import org.apache.lenya.cms.publication.DefaultDocumentBuilder;
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

import java.io.File;

/**
 * @author andreas
 *
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
        Document document = DefaultDocumentBuilder.getInstance().buildDocument(publication, url);

        File configDir = new File(publication.getDirectory(), ItemManager.PATH);
        assertTrue(configDir.exists());

        Policy policy = getPolicyManager().getPolicy(getAccreditableManager(), url);

        DocumentType type = DocumentTypeBuilder.buildDocumentType(documentTypeName, publication);
        String workflowId = type.getWorkflowFileName();
        
        WorkflowFactory factory = WorkflowFactory.newInstance();

        String[] emptyRoles = {};
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

    private static String documentTypeName = "simple";

}
