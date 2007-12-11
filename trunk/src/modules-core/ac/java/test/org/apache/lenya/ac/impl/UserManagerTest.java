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

package org.apache.lenya.ac.impl;

import java.io.File;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.ac.UserType;
import org.apache.lenya.ac.file.FileAccreditableManager;
import org.apache.lenya.ac.file.FileGroup;
import org.apache.lenya.ac.file.FileGroupManager;
import org.apache.lenya.ac.file.FileRole;
import org.apache.lenya.ac.file.FileUser;
import org.apache.lenya.ac.file.FileUserManager;

/**
 * User manager test.
 * 
 * @version $Id$
 */
public class UserManagerTest extends AbstractAccessControlTest {

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Run the test
     * @throws AccessControlException if an error occurs
     */
    final public void testInstance() throws AccessControlException {
        UserManager _manager = getAccreditableManager().getUserManager();
        assertNotNull(_manager);
    }

    /**
     * Load the configuration for the test
     * @throws AccessControlException if an error occurs
     */
    final public void testLoadConfig() throws AccessControlException {
        FileAccreditableManager accreditableManager = (FileAccreditableManager) getAccreditableManager();
        File configDir = accreditableManager.getConfigurationDirectory();

        String userName = "aliceTest";
        String editorGroupId = "editorGroup";
        String adminGroupId = "adminGroup";
        String editorRoleId = "editorRole";
        String adminRoleId = "adminRole";

        FileRole editorRole = new FileRole(getAccreditableManager().getRoleManager(), getLogger(),
                editorRoleId);
        FileRole adminRole = new FileRole(getAccreditableManager().getRoleManager(), getLogger(),
                adminRoleId);

        User user = new FileUser(getAccreditableManager().getUserManager(), getLogger(), userName,
                "Alice in Wonderland", "alice@test.com", "secret");

        editorRole.save();
        adminRole.save();

        Group editorGroup = new FileGroup(getAccreditableManager().getGroupManager(), getLogger(),
                editorGroupId);

        // editorGroup.addRole(editorRole);
        editorGroup.add(user);

        FileGroup adminGroup = new FileGroup(getAccreditableManager().getGroupManager(),
                getLogger(), adminGroupId);

        // adminGroup.addRole(editorRole);
        // adminGroup.addRole(adminRole);
        editorGroup.save();
        adminGroup.save();
        adminGroup.add(user);
        user.save();

        FileGroupManager groupManager = null;
        UserManager userManager = getAccreditableManager().getUserManager();
        assertNotNull(userManager);

        groupManager = FileGroupManager.instance(getAccreditableManager(), configDir, getLogger());
        assertNotNull(groupManager);

        Group fetchedGroup = groupManager.getGroup(editorGroupId);
        assertTrue(editorGroup.equals(fetchedGroup));

        fetchedGroup = groupManager.getGroup(adminGroupId);
        assertTrue(adminGroup.equals(fetchedGroup));
    }

    /**
     * Test getUser()
     * @throws AccessControlException if an error occurs
     */
    final public void testGetUser() throws AccessControlException {
        FileAccreditableManager accrMgr = (FileAccreditableManager) getAccreditableManager();
        File configDir = accrMgr.getConfigurationDirectory();
        String userName = "aliceTest";
        FileUser user = new FileUser(getAccreditableManager().getUserManager(), getLogger(),
                userName, "Alice in Wonderland", "alice@wonderland.com", "secret");
        UserType[] userTypes = { FileAccreditableManager.getDefaultUserType() };
        FileUserManager _manager = FileUserManager.instance(getAccreditableManager(), configDir,
                userTypes, getLogger());
        assertNotNull(_manager);
        _manager.add(user);

        User otherUser = _manager.getUser(userName);
        assertEquals(user, otherUser);
        assertEquals(user.getDescription(), otherUser.getDescription());
        assertEquals(user.getEmail(), otherUser.getEmail());
        assertEquals(user.getEncryptedPassword(), ((AbstractUser) otherUser).getEncryptedPassword());
    }
}