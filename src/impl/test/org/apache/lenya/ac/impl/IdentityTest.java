/*
* Copyright 1999-2004 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
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

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;

/**
 * Tests the identity
 */
public class IdentityTest extends AccessControlTest {

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
        Identity identity = new Identity();
        ContainerUtil.enableLogging(identity, getLogger());
        User user = getAccessController().getAccreditableManager().getUserManager().getUser(USER_ID);
        getLogger().info("Adding user to identity: [" + user + "]");
        identity.addIdentifiable(user);
        
        assertSame(user, identity.getUser());
    }

}
