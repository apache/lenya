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

package org.apache.lenya.ac.impl;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Credential;
import org.apache.lenya.ac.InheritingPolicyManager;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;

/**
 * Policy Test
 * 
 */
public class PolicyTest extends AbstractAccessControlTest {
    protected static final String URL = "/test/authoring/index.html";

    protected static final String SAVE_URL = "/test/authoring/tutorial.html";

    /**
     * A test.
     * 
     * @throws AccessControlException when something went wrong.
     * @throws PublicationException
     */
    public void testLoadPolicy() throws AccessControlException, PublicationException {
        Publication pub = getPublication("test");
        String url = "/" + pub.getId() + URL;
        Policy policy = getPolicy(url);
        
        Role[] allRoles = getAccreditableManager().getRoleManager().getRoles();
        
        getLogger().info("Roles: ");
        for (int i = 0; i < allRoles.length; i++) {
            int result = policy.check(getIdentity(), allRoles[i]);
            if (result == Policy.RESULT_GRANTED) {
                getLogger().info(allRoles[i].getId() + ": granted");
            }
            else {
                getLogger().info(allRoles[i].getId() + ": denied");
            }
        }
    }

    /**
     * Returns the policy for a URL.
     * 
     * @param url The URL.
     * @return The policy.
     * @throws AccessControlException when something went wrong.
     */
    protected Policy getPolicy(String url) throws AccessControlException {
        Policy policy = getPolicyManager().getPolicy(
                getAccessController().getAccreditableManager(), url);

        return policy;
    }

    /**
     * A test.
     * 
     * @throws AccessControlException when something went wrong.
     */
    public void testSavePolicy() throws AccessControlException {
        InheritingPolicyManager policyManager = (InheritingPolicyManager) getPolicyManager();
        DefaultPolicy subtreePolicy = (DefaultPolicy) policyManager.buildSubtreePolicy(
                getAccessController().getAccreditableManager(), URL);
        DefaultPolicy newPolicy = new DefaultPolicy();

        Credential[] credentials = subtreePolicy.getCredentials();

        for (int i = 0; i < credentials.length; i++) {
            Role role = credentials[i].getRole();
            CredentialImpl credential = new CredentialImpl(credentials[i].getAccreditable(), role);
            credential.setMethod(credentials[i].getMethod());
            newPolicy.addCredential(credential);
        }

        assertEquals(subtreePolicy.getCredentials().length, newPolicy.getCredentials().length);

        policyManager.saveSubtreePolicy(SAVE_URL, newPolicy);

        newPolicy = (DefaultPolicy) policyManager.buildSubtreePolicy(getAccessController()
                .getAccreditableManager(), SAVE_URL);
        assertEquals(subtreePolicy.getCredentials().length, newPolicy.getCredentials().length);

        Credential[] newCredentials = newPolicy.getCredentials();

        for (int i = 0; i < credentials.length; i++) {
            Role role = credentials[i].getRole();
            CredentialImpl credential = new CredentialImpl(credentials[i].getAccreditable(), role);
            credential.setMethod(credential.getMethod());
            Credential newCredential = null;

            for (int k = 0; k < newCredentials.length; k++) {
                if (newCredentials[k].getAccreditable().equals(credential.getAccreditable())) {
                    newCredential = newCredentials[k];
                }
            }

            getLogger().info("Accreditable: [" + credential.getAccreditable() + "]");
            assertNotNull(newCredential);

            Role oldRole = credential.getRole();
            Role newRole = newCredential.getRole();
            assertEquals(oldRole, newRole);

            /*
             * for (int j = 0; j < roles.length; j++) { assertEquals(roles[j],
             * newRoles[j]); getLogger().info(" Role: [" + roles[j] + "]"); }
             */
        }
    }
}
