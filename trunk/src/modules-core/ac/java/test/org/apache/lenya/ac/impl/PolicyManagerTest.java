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

import java.util.Arrays;
import java.util.List;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.cms.ac.PolicyUtil;

/**
 * Test for the Policy Manager
 */
public class PolicyManagerTest extends AbstractAccessControlTest {

    private static String[] URLS = { "/default/authoring/index.html" };

    /**
     * The test.
     * @throws AccessControlException when something went wrong.
     */
    public void testPolicyManager() throws AccessControlException {
        
        DefaultAccessController controller = getAccessController(); 
        PolicyManager policyManager = controller.getPolicyManager();
        assertNotNull(policyManager);
        
        for (int i = 0; i < URLS.length; i++) {
            Policy policy = policyManager.getPolicy(controller.getAccreditableManager(), URLS[i]);
            assertNotNull(policy);
            
            Role[] roles = policyManager.getGrantedRoles(controller.getAccreditableManager(), getIdentity(), URLS[i]);
            assertTrue(roles.length > 0);

            User[] users = PolicyUtil.getUsersWithRole(getManager(), URLS[i], "review", getLogger());
            
            UserManager userManager = controller.getAccreditableManager().getUserManager();
            User lenya = userManager.getUser("lenya");
            User alice = userManager.getUser("alice");
            
            List usersList = Arrays.asList(users);
            assertFalse(usersList.contains(lenya));
            assertTrue(usersList.contains(alice));
        }
    }

}
