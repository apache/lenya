/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.lenya.ac.impl;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;

/**
 * Tests the identity
 */
public class IdentityTest extends AbstractAccessControlTest {

    /**
     * <code>USER_ID</code> The user id to test
     */
    public static final String USER_ID = "lenya";

    /**
     * Tests the identity.
     * 
     * @throws AccessControlException if an error occurs
     */
    public void testIdentity() throws AccessControlException {
        Identity identity = new Identity(getLogger());
        User user = getAccessController().getAccreditableManager().getUserManager().getUser(USER_ID);
        getLogger().info("Adding user to identity: [" + user + "]");
        identity.addIdentifiable(user);
        
        assertSame(user, identity.getUser());
    }
    
    /**
     * Test the {@link Identity#belongsTo(org.apache.lenya.ac.AccreditableManager)} method.
     * @throws Exception if an error occurs.
     */
    public void testBelongsTo() throws Exception {
        AccreditableManager testMgr = getAccessController(getSession(), "test").getAccreditableManager();
        AccreditableManager defaultMgr = getAccessController(getSession(), "default").getAccreditableManager();
        
        String userId = "lenya";
        User testUser = testMgr.getUserManager().getUser(userId);
        User defaultUser = defaultMgr.getUserManager().getUser(userId);
        
        Identity testIdentity = new Identity(getLogger());
        testIdentity.addIdentifiable(testUser);
        
        Identity defaultIdentity = new Identity(getLogger());
        defaultIdentity.addIdentifiable(defaultUser);
        
        assertTrue(testIdentity.belongsTo(testMgr));
        assertTrue(defaultIdentity.belongsTo(defaultMgr));
        
        assertTrue(testIdentity.belongsTo(defaultMgr));
        assertTrue(defaultIdentity.belongsTo(testMgr));
    }

}
