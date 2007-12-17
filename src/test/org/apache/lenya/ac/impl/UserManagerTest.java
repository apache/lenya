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

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.components.ComponentContext.ComponentManagerWrapper;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserType;
import org.apache.lenya.ac.file.FileAccreditableManager;
import org.apache.lenya.ac.file.FileGroup;
import org.apache.lenya.ac.file.FileGroupManager;
import org.apache.lenya.ac.file.FileRole;
import org.apache.lenya.ac.file.FileUser;
import org.apache.lenya.ac.file.FileUserManager;
import org.apache.lenya.cms.PublicationHelper;

/**
 * User manager test.
 * 
 * @version $Id: UserManagerTest.java 473841 2006-11-12 00:46:38Z gregor $
 */
public class UserManagerTest extends AccessControlTest {

    /**
     * Constructor for UserManagerTest.
     * @param arg0 command line args
     */
    public UserManagerTest(String arg0) {
        super(arg0);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        junit.textui.TestRunner.run(UserManagerTest.class);
    }

    /**
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * DOCUMENT ME!
     * 
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testInstance() throws AccessControlException {
        FileUserManager manager = getUserManager();
        assertNotNull(manager);
    }

    protected FileUserManager getUserManager() throws AccessControlException {
        UserType[] userTypes = { FileAccreditableManager.getDefaultUserType() };
        ServiceManager wrapper = new ComponentManagerWrapper(this.manager);
        FileUserManager manager = FileUserManager.instance(wrapper,
                (FileAccreditableManager) getAccreditableManager(), userTypes,
                getLogEnabledLogger());
        return manager;
    }

    /**
     * DOCUMENT ME!
     * 
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testLoadConfig() throws AccessControlException {
        File configDir = getAccreditablesDirectory();

        String userName = "alice";
        String editorGroupId = "editorGroup";
        String adminGroupId = "adminGroup";
        String editorRoleId = "editorRole";
        String adminRoleId = "adminRole";

        FileRole editorRole = new FileRole(configDir, editorRoleId);
        FileRole adminRole = new FileRole(configDir, adminRoleId);

        FileUser user = new FileUser(configDir, userName, "Alice in Wonderland", "alice@test.com",
                "secret");

        editorRole.save();
        adminRole.save();

        Group editorGroup = new FileGroup(configDir, editorGroupId);

        // editorGroup.addRole(editorRole);
        editorGroup.add(user);

        FileGroup adminGroup = new FileGroup(configDir, adminGroupId);

        // adminGroup.addRole(editorRole);
        // adminGroup.addRole(adminRole);
        editorGroup.save();
        adminGroup.save();
        adminGroup.add(user);
        user.save();

        FileUserManager userManager = getUserManager();
        assertNotNull(userManager);

        ServiceManager wrapper = new ComponentManagerWrapper(this.manager);
        FileGroupManager groupManager = FileGroupManager.instance(wrapper,
                (FileAccreditableManager) getAccreditableManager(), getLogEnabledLogger());
        assertNotNull(groupManager);

        Group fetchedGroup = groupManager.getGroup(editorGroupId);
        assertTrue(editorGroup.equals(fetchedGroup));

        fetchedGroup = groupManager.getGroup(adminGroupId);
        assertTrue(adminGroup.equals(fetchedGroup));
    }

    /**
     * DOCUMENT ME!
     * 
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testGetUser() throws AccessControlException {
        File configDir = getAccreditablesDirectory();
        String userName = "testuser";
        FileUser user = new FileUser(configDir, userName, "Alice in Wonderland",
                "alice@wonderland.com", "secret");
        FileUserManager manager = getUserManager();
        assertNotNull(manager);
        manager.add(user);

        User otherUser = manager.getUser(userName);
        assertEquals(user, otherUser);
        assertEquals(user.getDescription(), otherUser.getDescription());
        assertEquals(user.getEmail(), otherUser.getEmail());
        assertEquals(user.getEncryptedPassword(), ((AbstractUser) otherUser).getEncryptedPassword());
    }
}
