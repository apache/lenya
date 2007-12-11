/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

package org.apache.lenya.ac.impl;

import java.io.File;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.file.FileRole;
import org.apache.lenya.ac.file.FileRoleManager;

/**
 * Role manager test.
 *
 * @version $Id$
 */
public class RoleManagerTest extends AbstractAccessControlTest {

    /**
     * Run the test
     * @throws AccessControlException if an error occurs
     */
    final public void testInstance() throws AccessControlException {
        File configDir = getAccreditablesDirectory();
        FileRoleManager _manager = FileRoleManager.instance(getAccreditableManager(), configDir, getLogger());
        assertNotNull(_manager);

        FileRoleManager anotherManager = FileRoleManager.instance(getAccreditableManager(), configDir, getLogger());
        assertNotNull(anotherManager);
        assertEquals(_manager, anotherManager);
    }

    /**
     * Test getRoles()
     */
    final public void testGetRoles() {
        // do nothing
    }

    /**
     * Test add(Role)
     * @throws AccessControlException if an error occurs
     */
    final public void testAddRole() throws AccessControlException {
        File configDir = getAccreditablesDirectory();
        String name = "test";
        FileRoleManager _manager = null;
        _manager = FileRoleManager.instance(getAccreditableManager(), configDir, getLogger());
        assertNotNull(_manager);
        Role role = new FileRole(getAccreditableManager().getRoleManager(), getLogger(), name);
        _manager.add(role);

        assertTrue(_manager.getRoles().length > 0);
    }

    /**
     * Test for void remove(Role)
     * @throws AccessControlException if an error occurs.
     */
    final public void testRemoveRole() throws AccessControlException {
        File configDir = getAccreditablesDirectory();
        String name = "test2";
        Role role = new FileRole(getAccreditableManager().getRoleManager(), getLogger(), name);
        FileRoleManager _manager = null;

        try {
            _manager = FileRoleManager.instance(getAccreditableManager(), configDir, getLogger());
        } catch (AccessControlException e) {
            e.printStackTrace();
        }

        assertNotNull(_manager);

        Role[] roles = _manager.getRoles();
        int roleCountBefore = roles.length;

        _manager.add(role);
        _manager.remove(role);

        roles = _manager.getRoles();
        int roleCountAfter = roles.length;

        assertEquals(roleCountBefore, roleCountAfter);
    }
}
