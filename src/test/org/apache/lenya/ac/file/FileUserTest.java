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

/* $Id: FileUserTest.java,v 1.3 2004/03/04 15:37:59 egli Exp $  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.AccessControlTest;
import org.apache.lenya.cms.PublicationHelper;

public class FileUserTest extends AccessControlTest {
    private HashMap groups = new HashMap();

    /**
     * Constructor for FileUserTest.
     * @param arg0 command line args
     */
    public FileUserTest(String arg0) {
        super(arg0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        junit.textui.TestRunner.run(FileUserTest.class);
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
     * @return DOCUMENT ME!
     */
    final public Map getGroups() {
        return groups;
    }

    /**
     * Create and save a user
     *
     * @param userName DOCUMENT ME!
     * @param fullName DOCUMENT ME!
     * @param email DOCUMENT ME!
     * @param password DOCUMENT ME!
     *
     * @return a <code>FileUser</code>
     *
     * @throws AccessControlException if an error occurs
     */
    final public FileUser createAndSaveUser(String userName, String fullName, String email,
        String password) throws AccessControlException {
        File configDir = getAccreditablesDirectory();

        String editorGroupName = "editorGroup";
        String adminGroupName = "adminGroup";

        FileGroup editorGroup = new FileGroup(configDir, editorGroupName);
        FileGroup adminGroup = new FileGroup(configDir, adminGroupName);
        this.groups.put(editorGroupName, editorGroup);
        this.groups.put(adminGroupName, adminGroup);

        FileUser user = new FileUser(configDir, userName, fullName, email, password);

        editorGroup.add(user);
        adminGroup.add(user);

        editorGroup.save();
        adminGroup.save();
        user.save();
        FileUserManager manager = FileUserManager.instance(configDir);
        manager.add(user);

        return user;
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
        File configDir = getAccreditablesDirectory();
        FileUserManager manager = FileUserManager.instance(configDir);

        return (FileUser) manager.getUser(userName);
    }

    /**
     * Test save
     *
     * @throws AccessControlException if an error occurs
     */
    final public void testSave() throws AccessControlException {
        String userName = "alice";
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
        String userID = "alice";
        String email = "alice@wonderland.org";
        User user = createAndSaveUser(userID, "Alice Wonderland", email, "secret");
        assertTrue(user.getEmail().equals(email));
        user = loadUser(userID);
        assertTrue(user.getEmail().equals(email));
    }

    /**
     * Test getFullName
     *
     * @throws AccessControlException if an error occurs
     */
    final public void testGetFullName() throws AccessControlException {
        String userID = "alice";
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
        FileUser user = createAndSaveUser("alice", "Alice Wonderland", "alice@wonderland.org",
                "secret");
                
        for (Iterator i = getGroups().values().iterator(); i.hasNext(); ) {
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
        String id = "alice";
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
        File configDir = getAccreditablesDirectory();
        FileUserManager manager = null;
        manager = FileUserManager.instance(configDir);
        assertNotNull(manager);

        assertNotNull(manager.getUser(id));
        user.delete();
        manager.remove(user);
        assertNull(manager.getUser(id));
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

        File configDir = getAccreditablesDirectory();
        FileUserManager manager = null;
        manager = FileUserManager.instance(configDir);
        assertNotNull(manager);

        User lenya = manager.getUser("lenya");
        assertNotNull(lenya);
        assertTrue(lenya.authenticate("levi"));
    }
}
