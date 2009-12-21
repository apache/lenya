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

package org.apache.lenya.ac.file;

import java.io.File;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;

/**
 * File role test.
 *
 * @version $Id$
 */
public class FileRoleTest extends AbstractAccessControlTest {
    
    /**
     * DOCUMENT ME!
     * 
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testFileRole() throws AccessControlException {
        String name = "test";
        File configDir = getAccreditablesDirectory();
        FileRole role = new FileRole(getAccreditableManager().getRoleManager(), getLogger(), name);
        role.save();

        File path = null;
        path = FileRoleManager.instance(getAccreditableManager(), configDir, getLogger())
                .getConfigurationDirectory();

        File roleFile = new File(path, name + FileRoleManager.SUFFIX);
        assertNotNull(roleFile);
        assertTrue(roleFile.exists());
    }

    /**
     * DOCUMENT ME!
     * 
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testSave() throws AccessControlException {
        File configDir = getAccreditablesDirectory();
        String name = "test";
        FileRole role = new FileRole(getAccreditableManager().getRoleManager(), getLogger(), name);
        role.save();

        File path = null;
        path = FileRoleManager.instance(getAccreditableManager(), configDir, getLogger())
                .getConfigurationDirectory();

        File roleFile = new File(path, name + FileRoleManager.SUFFIX);
        assertNotNull(roleFile);
        assertTrue(roleFile.exists());
    }

    /**
     * DOCUMENT ME!
     * @throws AccessControlException 
     */
    final public void testGetId() throws AccessControlException {
        String id = "test";
        FileRole role = new FileRole(getAccreditableManager().getRoleManager(), getLogger(), id);
        assertTrue(role.getId().equals(id));
    }

    /**
     * Test for boolean equals(Object)
     * @throws AccessControlException 
     */
    final public void testEqualsObject() throws AccessControlException {
        String name = "test";
        FileRole role1 = new FileRole(getAccreditableManager().getRoleManager(), getLogger(), name);
        FileRole role2 = new FileRole(getAccreditableManager().getRoleManager(), getLogger(), name);
        assertEquals(role1, role2);
    }
}
