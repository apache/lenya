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

package org.apache.lenya.ac.file;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserType;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;

/**
 * File user test.
 * 
 * @version $Id$
 */
public class FileUserTest extends AbstractAccessControlTest {
    private HashMap groups = new HashMap();

    /**
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Get all Groups
     * 
     * @return A map of the groups
     */
    final public Map getGroups() {
        return this.groups;
    }

    /**
     * Create and save a user
     * 
     * @param userName The user name
     * @param fullName The full name
     * @param email The email
     * @param password The password
     * 
     * @return a <code>FileUser</code>
     * 
     * @throws AccessControlException if an error occurs
     */
    final public FileUser createAndSaveUser(String userName, String fullName, String email,
            String password) throws AccessControlException {

        String editorGroupName = "editorGroup";
        String adminGroupName = "adminGroup";

        FileGroup editorGroup = new FileGroup(getAccreditableManager().getGroupManager(),
                getLogger(), editorGroupName);
        ContainerUtil.enableLogging(editorGroup, getLogger());
        FileGroup adminGroup = new FileGroup(getAccreditableManager().getGroupManager(),
                getLogger(), adminGroupName);
        ContainerUtil.enableLogging(adminGroup, getLogger());
        this.groups.put(editorGroupName, editorGroup);
        this.groups.put(adminGroupName, adminGroup);

        FileUser user = new FileUser(getAccreditableManager().getUserManager(), getLogger(),
                userName, fullName, email, password);
        ContainerUtil.enableLogging(user, getLogger());

        editorGroup.add(user);
        adminGroup.add(user);

        editorGroup.save();
        adminGroup.save();
        user.save();

        FileUserManager _manager = getUserManager();
        _manager.add(user);

        return user;
    }

    /**
     * Returns the file user manager.
     * @return A file user manager.
     * @throws AccessControlException if an error occurs.
     */
    protected FileUserManager getUserManager() throws AccessControlException {
        UserType[] userTypes = { FileAccreditableManager.getDefaultUserType() };
        FileUserManager _manager = FileUserManager.instance(getAccreditableManager(),
                getAccreditablesDirectory(), userTypes, getLogger());
        return _manager;
    }

    /**
     * Load a user.
     * 
     * @param userName the name of the user
     * 
     * @return a <code>FileUser</code>
     * 
     * @throws AccessControlException if an error occurs
     */
    final public FileUser loadUser(String userName) throws AccessControlException {
        FileUserManager _manager = getUserManager();
        return (FileUser) _manager.getUser(userName);
    }

    /**
     * Test save
     * 
     * @throws AccessControlException if an error occurs
     */
    final public void testSave() throws AccessControlException {
        String userName = "aliceTest";
        createAndSaveUser(userName, "Alice Wonderland", "alice@wonderland.org", "secret");

        File configDir = getAccreditablesDirectory();
        File xmlFile = new File(configDir, userName + ".iml");
        assertTrue(xmlFile.exists());
    }

    /**
     * Test getEmail
     * 
     * @throws AccessControlException if an error occurs
     */
    final public void testGetEmail() throws AccessControlException {
        String userID = "aliceTest";
        String email = "alice@wonderland.org";
        User user = createAndSaveUser(userID, "Alice Wonderland", email, "secret");
        assertTrue(user.getEmail().equals(email));
        user = loadUser(userID);
        assertTrue(user.getEmail().equals(email));
    }

    /**
     * Test getName
     * 
     * @throws AccessControlException if an error occurs
     */
    final public void testGetName() throws AccessControlException {
        String userID = "aliceTest";
        String userName = "Alice Wonderland";
        FileUser user = createAndSaveUser(userID, userName, "alice@wonderland.org", "secret");
        assertTrue(user.getName().equals(userName));
        user = loadUser(userID);
        assertTrue(user.getName().equals(userName));
    }

    /**
     * Test getGroups
     * 
     * @throws AccessControlException if an error occurs
     */
    final public void testGetGroups() throws AccessControlException {
        FileUser user = createAndSaveUser("aliceTest", "Alice Wonderland", "alice@wonderland.org",
                "secret");

        for (Iterator i = getGroups().values().iterator(); i.hasNext();) {
            Group group = (Group) i.next();
            assertTrue(group.contains(user));
        }
    }

    /**
     * Test getId
     * 
     * @throws AccessControlException if an error occurs
     */
    final public void testGetId() throws AccessControlException {
        String id = "aliceTest";
        FileUser user = createAndSaveUser(id, "Alice Wonderland", "alice@wonderland.org", "secret");
        assertTrue(user.getId().equals(id));
    }

    /**
     * Test delete
     * 
     * @throws AccessControlException if an error occurs
     */
    final public void testDelete() throws AccessControlException {
        String id = "albert";
        FileUser user = createAndSaveUser(id, "Albert Einstein", "albert@physics.org", "secret");
        FileUserManager _manager = getUserManager();
        assertNotNull(_manager);

        assertNotNull(_manager.getUser(id));
        user.delete();
        _manager.remove(user);
        assertNull(_manager.getUser(id));
    }

    /**
     * Test authenticate
     * 
     * @throws AccessControlException if an error occurs
     */
    final public void testAuthenticate() throws AccessControlException {
        String password = "daisy";
        FileUser user = createAndSaveUser("mickey", "Mickey Mouse", "mickey@mouse.com", password);
        assertTrue(user.authenticate(password));

        FileUserManager _manager = getUserManager();
        assertNotNull(_manager);

        User lenya = _manager.getUser("lenya");
        assertNotNull(lenya);
        assertTrue(lenya.authenticate("levi"));
    }
}