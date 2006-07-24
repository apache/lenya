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

package org.apache.lenya.ac.impl;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;

/**
 * Test for the Policy Manager
 */
public class PolicyManagerTest extends AbstractAccessControlTest {

    private static String[] URLS = { "/test/authoring/index.html" };

    /**
     * The test.
     * @throws AccessControlException when something went wrong.
     */
    public void testAccessController() throws AccessControlException {
        
        DefaultAccessController controller = getAccessController(); 
        PolicyManager policyManager = controller.getPolicyManager();
        assertNotNull(policyManager);
        
        for (int i = 0; i < URLS.length; i++) {
            Policy policy = policyManager.getPolicy(controller.getAccreditableManager(), URLS[i]);
            assertNotNull(policy);
            assertTrue(policy.getRoles(getIdentity()).length > 0);
        }
    }

}
