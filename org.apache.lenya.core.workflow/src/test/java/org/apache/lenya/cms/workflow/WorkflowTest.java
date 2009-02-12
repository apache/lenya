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

import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.workflow.Version;
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
        
        User lenya = getAccreditableManager().getUserManager().getUser("lenya");
        Group reviewers = getAccreditableManager().getGroupManager().getGroup("reviewer");
        reviewers.add(lenya);
        
        
        Session session = getSession();
        Publication publication = session.getPublication("test");
        String url = "/" + publication.getId() + URL;
        Document document = session.getUriHandler().getDocument(url);

        document.lock();

        Workflowable workflowable = WorkflowUtil.getWorkflowable(document);
        if (workflowable.getVersions().length > 0) {
            Version version = workflowable.getLatestVersion();
            if (version.getValue(variableName) == true) {
                invoke(document, deactivateSituation);
            } else if (version.getState().equals("review")) {
                invoke(document, rejectSituation);
            }
        }

        for (TestSituation situation : situations) {
            invoke(document, situation);
        }

        document.unlock();

        getLogger().info("Test completed.");
    }

    protected void invoke(Document document, TestSituation situation) throws Exception {
        Workflowable instance = new DocumentWorkflowable(document);
        assertNotNull(instance);

        String event = situation.getEvent();

        getLogger().info("Event: " + event);

        WorkflowUtil.invoke(document, event);

        boolean value = instance.getLatestVersion().getValue(variableName);

        getLogger().info("Variable: " + variableName + " = " + value);
        getLogger().info("------------------------------------------------------");

        assertEquals(value, situation.getValue());
    }

    protected Session getSession(TestSituation situation) throws Exception {
        Session session = login(situation.getUser());
        getLogger().info("User: [" + session.getIdentity().getUser() + "]");
        return session;
    }

    private static final TestSituation submitSituation = new TestSituation("lenya", "submit", false);
    private static final TestSituation rejectSituation = new TestSituation("alice", "reject", false);
    private static final TestSituation deactivateSituation = new TestSituation("alice",
            "deactivate", false);
    private static final TestSituation publishSituation = new TestSituation("alice", "publish",
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