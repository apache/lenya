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

/* $Id: UserManagerTest.java,v 1.3 2004/03/04 15:40:19 egli Exp $  */

package org.apache.lenya.ac.impl;

import java.io.File;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.file.FileGroup;
import org.apache.lenya.ac.file.FileGroupManager;
import org.apache.lenya.ac.file.FileRole;
import org.apache.lenya.ac.file.FileUser;
import org.apache.lenya.ac.file.FileUserManager;
import org.apache.lenya.cms.PublicationHelper;

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
     *  (non-Javadoc)
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
        File configDir = getAccreditablesDirectory();
        FileUserManager manager = FileUserManager.instance(configDir);
        assertNotNull(manager);
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

        User user =
            new FileUser(configDir, userName, "Alice in Wonderland", "alice@test.com", "secret");

        editorRole.save();
        adminRole.save();

        Group editorGroup = new FileGroup(configDir, editorGroupId);

        //		editorGroup.addRole(editorRole);
        editorGroup.add(user);

        FileGroup adminGroup = new FileGroup(configDir, adminGroupId);

        //		adminGroup.addRole(editorRole);
        //		adminGroup.addRole(adminRole);
        editorGroup.save();
        adminGroup.save();
        adminGroup.add(user);
        user.save();

        FileUserManager userManager = null;
        FileGroupManager groupManager = null;
        userManager = FileUserManager.instance(configDir);
        assertNotNull(userManager);

        groupManager = FileGroupManager.instance(configDir);
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
        FileUserManager manager = null;
        String userName = "testuser";
        FileUser user =
            new FileUser(
                configDir,
                userName,
                "Alice in Wonderland",
                "alice@wonderland.com",
                "secret");
        manager = FileUserManager.instance(configDir);
        assertNotNull(manager);
        manager.add(user);

        User otherUser = manager.getUser(userName);
        assertEquals(user, otherUser);
        assertEquals(user.getDescription(), otherUser.getDescription());
        assertEquals(user.getEmail(), otherUser.getEmail());
        assertEquals(user.getEncryptedPassword(), ((AbstractUser) otherUser).getEncryptedPassword());
    }
}
