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

/* $Id$  */

package org.apache.lenya.cms.workflow;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.Workflowable;

/**
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class WorkflowTest extends AbstractAccessControlTest {

    private static final String variableName = "is_live";
    protected static final String URL = "/authoring/index.html";

    protected String getWebappUrl() {
        return "/test" + URL;
    }

    /**
     * Tests the workflow.
     * @throws Exception when something went wrong.
     */
    public void testWorkflow() throws Exception {
        Publication publication = getPublication("test");
        String url = "/" + publication.getId() + URL;
        DocumentFactory map = getFactory();
        Document document = map.getFromURL(url);

        document.getRepositoryNode().lock();

        Session session = getSession(submitSituation);
        Workflowable workflowable = WorkflowUtil.getWorkflowable(getManager(),
                session,
                getLogger(),
                document);
        if (workflowable.getVersions().length > 0) {
            Version version = workflowable.getLatestVersion();
            if (version.getValue(variableName) == true) {
                invoke(document, deactivateSituation);
            } else if (version.getState().equals("review")) {
                invoke(document, rejectSituation);
            }
        }

        for (int situationIndex = 0; situationIndex < situations.length; situationIndex++) {
            TestSituation situation = situations[situationIndex];
            invoke(document, situation);
        }

        document.getRepositoryNode().unlock();

        getLogger().info("Test completed.");
    }

    protected void invoke(Document document, TestSituation situation)
            throws AccessControlException, RepositoryException, WorkflowException {
        Session session = getSession(situation);
        Workflowable instance = new DocumentWorkflowable(getManager(),
                session,
                document,
                getLogger());
        assertNotNull(instance);

        String event = situation.getEvent();

        getLogger().info("Event: " + event);

        WorkflowUtil.invoke(getManager(), session, getLogger(), document, event);

        boolean value = instance.getLatestVersion().getValue(variableName);

        getLogger().info("Variable: " + variableName + " = " + value);
        getLogger().info("------------------------------------------------------");

        assertEquals(value, situation.getValue());
    }

    protected Session getSession(TestSituation situation) throws AccessControlException,
            RepositoryException {
        Session session = login(situation.getUser());
        getLogger().info("User: [" + session.getIdentity().getUser() + "]");
        return session;
    }

    private static final TestSituation submitSituation = new TestSituation("lenya", "submit", false);
    private static final TestSituation rejectSituation = new TestSituation("alice", "reject", false);
    private static final TestSituation deactivateSituation = new TestSituation("alice",
            "deactivate",
            false);
    private static final TestSituation publishSituation = new TestSituation("alice",
            "publish",
            true);

    private static final TestSituation[] situations = { submitSituation, rejectSituation,
            submitSituation, publishSituation, deactivateSituation };

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