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

/* $Id: PolicyTest.java,v 1.3 2004/03/04 15:40:19 egli Exp $  */

package org.apache.lenya.ac.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.textui.TestRunner;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.cms.PublicationHelper;

/**
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PolicyTest extends AccessControlTest {
    /**
     * Executes this test.
     * @param test The test.
     */
    public PolicyTest(String test) {
        super(test);
    }

    /**
     * The main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(PolicyTest.class);
    }

    protected static final String URL = "/test/authoring/index.html";
    protected static final String SAVE_URL = "/test/authoring/tutorial.html";

    /**
     * A test.
     * @throws AccessControlException when something went wrong.
     */
    public void testLoadPolicy() throws AccessControlException {
        String url = "/" + PublicationHelper.getPublication().getId() + URL;
        Policy policy = getPolicy(url);
        Role[] roles = policy.getRoles(getIdentity());
        System.out.print("Roles: ");

        for (int i = 0; i < roles.length; i++) {
            System.out.print(roles[i]);
        }

        System.out.println();
    }

    /**
     * Returns the policy for a URL.
     * @param url The URL.
     * @return The policy.
     * @throws AccessControlException when something went wrong.
     */
    protected Policy getPolicy(String url) throws AccessControlException {
        Policy policy =
            getPolicyManager().getPolicy(getAccessController().getAccreditableManager(), url);

        return policy;
    }

    /**
     * A test.
     * @throws AccessControlException when something went wrong.
     */
    public void testSavePolicy() throws AccessControlException {
        DefaultPolicy urlPolicy =
            getPolicyManager().buildURLPolicy(getAccessController().getAccreditableManager(), URL);
        DefaultPolicy newPolicy = new DefaultPolicy();

        Credential[] credentials = urlPolicy.getCredentials();

        for (int i = 0; i < credentials.length; i++) {
            Credential credential = new Credential(credentials[i].getAccreditable());
            Role[] roles = credentials[i].getRoles();

            for (int j = 0; j < roles.length; j++) {
                credential.addRole(roles[j]);
            }

            newPolicy.addCredential(credential);
        }

        assertEquals(urlPolicy.getCredentials().length, newPolicy.getCredentials().length);

        getPolicyManager().saveURLPolicy(SAVE_URL, newPolicy);

        newPolicy =
            getPolicyManager().buildURLPolicy(
                getAccessController().getAccreditableManager(),
                SAVE_URL);
        assertEquals(urlPolicy.getCredentials().length, newPolicy.getCredentials().length);

        Credential[] newCredentials = newPolicy.getCredentials();

        for (int i = 0; i < credentials.length; i++) {
            Credential credential = new Credential(credentials[i].getAccreditable());

            Credential newCredential = null;

            for (int k = 0; k < newCredentials.length; k++) {
                if (newCredentials[k].getAccreditable().equals(credential.getAccreditable())) {
                    newCredential = newCredentials[k];
                }
            }

            System.out.println("Accreditable: [" + credential.getAccreditable() + "]");
            assertNotNull(newCredential);

            Set oldRoles = new HashSet(Arrays.asList(credential.getRoles()));
            Set newRoles = new HashSet(Arrays.asList(newCredential.getRoles()));
            assertEquals(oldRoles, newRoles);

            /*
            for (int j = 0; j < roles.length; j++) {
                assertEquals(roles[j], newRoles[j]);
                System.out.println("  Role: [" + roles[j] + "]");
            }
            */
        }
    }
}
