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

/* $Id: RoleManagerTest.java,v 1.3 2004/03/04 15:40:19 egli Exp $  */

package org.apache.lenya.ac.impl;

import java.io.File;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.file.FileRole;
import org.apache.lenya.ac.file.FileRoleManager;
import org.apache.lenya.cms.PublicationHelper;

public class RoleManagerTest extends AccessControlTest {
    /**
     * Constructor for RoleManagerTest.
     * @param arg0 command line args
     */
    public RoleManagerTest(String arg0) {
        super(arg0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        junit.textui.TestRunner.run(RoleManagerTest.class);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testInstance() throws AccessControlException {
        File configDir = getAccreditablesDirectory();
        FileRoleManager manager = FileRoleManager.instance(configDir);
        assertNotNull(manager);

        FileRoleManager anotherManager = FileRoleManager.instance(configDir);
        assertNotNull(anotherManager);
        assertEquals(manager, anotherManager);
    }

    /**
     * DOCUMENT ME!
     */
    final public void testGetRoles() {
    }

	/**
	 * Test add(Role)
	 * 
	 * @throws AccessControlException if an error occurs
	 */
    final public void testAddRole() throws AccessControlException {
        File configDir = getAccreditablesDirectory();
        String name = "test";
        FileRoleManager manager = null;
        manager = FileRoleManager.instance(configDir);
        assertNotNull(manager);
        Role role = new FileRole(manager.getConfigurationDirectory(), name);
        manager.add(role);

        assertTrue(manager.getRoles().length > 0);
    }

	/**
     * Test for void remove(Role)
	 *
	 */
    final public void testRemoveRole() throws AccessControlException {
        File configDir = getAccreditablesDirectory();
        String name = "test2";
        Role role = new FileRole(configDir, name);
        FileRoleManager manager = null;

        try {
            manager = FileRoleManager.instance(configDir);
        } catch (AccessControlException e) {
            e.printStackTrace();
        }

        assertNotNull(manager);

        Role[] roles = manager.getRoles();
        int roleCountBefore = roles.length;

        manager.add(role);
        manager.remove(role);

        roles = manager.getRoles();
        int roleCountAfter = roles.length;

        assertEquals(roleCountBefore, roleCountAfter);
    }
}
