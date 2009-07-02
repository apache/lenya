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
package org.apache.lenya.cms.usecase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.ac.usecase.UsecaseAuthorizer;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.usecase.impl.TestUsecaseInvoker;

/**
 * Usecase test base class.
 */
public abstract class AbstractUsecaseTest extends AbstractAccessControlTest {

    /**
     * The test.
     * @throws Exception
     */
    public void testUsecase() throws Exception {

        Session session = getSession();
        prepareUsecase();

        UsecaseInvoker invoker = null;
        try {
            invoker = (UsecaseInvoker) getManager().lookup(TestUsecaseInvoker.ROLE);
            invoker.setTestSession(session);
            invoker.invoke(getRequest().getPathInfo(), getUsecaseName(), getParameters());

            int result = invoker.getResult();
            if (result != UsecaseInvoker.PRECONDITIONS_FAILED
                    && result != UsecaseInvoker.EXECUTION_CONDITIONS_FAILED
                    && result != UsecaseInvoker.AUTHORIZATION_FAILED) {
                this.targetUrl = invoker.getTargetUrl();
            }

            List errorMessages = invoker.getErrorMessages();
            for (Iterator i = errorMessages.iterator(); i.hasNext();) {
                UsecaseMessage message = (UsecaseMessage) i.next();
                String m = message.getMessage();
                String[] params = message.getParameters();
                if (params != null) {
                    for (int j = 0; j < params.length; j++) {
                        m += " [" + params[j] + "]";
                    }
                }
                getLogger().error("Usecase error: " + m);
            }

            assertEquals(invoker.getResult(), UsecaseInvoker.SUCCESS);
            assertEquals(invoker.getErrorMessages().size(), 0);
        } finally {
            if (invoker != null) {
                getManager().release(invoker);
            }
        }

        checkPostconditions();

    }

    private String targetUrl;

    protected String getTargetUrl() {
        if (this.targetUrl == null) {
            throw new IllegalStateException("The usecase has not yet been executed.");
        }
        return this.targetUrl;
    }

    protected void prepareUsecase() throws Exception {
    }

    protected Map getParameters() {
        return new HashMap();
    }

    protected abstract String getUsecaseName();

    protected void checkPostconditions() throws Exception {
    }

    private UsecaseAuthorizer authorizer;

    protected UsecaseAuthorizer getUsecaseAuthorizer() {
        if (this.authorizer == null) {
            Authorizer[] authorizers = getAccessController().getAuthorizers();
            for (int i = 0; i < authorizers.length; i++) {
                if (authorizers[i] instanceof UsecaseAuthorizer) {
                    this.authorizer = (UsecaseAuthorizer) authorizers[i];
                }
            }
        }
        return this.authorizer;
    }

    protected void setUsecasePermission(String usecase, Publication pub, String roleName,
            boolean value) throws AccessControlException {
        Role role = getAccessController().getAccreditableManager().getRoleManager().getRole(
                roleName);
        getUsecaseAuthorizer().setPermission(usecase, pub, role, value);
    }

}
