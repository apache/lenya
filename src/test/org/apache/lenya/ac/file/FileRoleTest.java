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

/* $Id: FileRoleTest.java,v 1.3 2004/03/04 15:37:59 egli Exp $  */

package org.apache.lenya.ac.file;

import java.io.File;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.impl.AccessControlTest;
import org.apache.lenya.cms.PublicationHelper;

public class FileRoleTest extends AccessControlTest {
    /**
     * Constructor for FileRoleTest.
     * @param arg0 The test to execute.
     */
    public FileRoleTest(String arg0) {
        super(arg0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        junit.textui.TestRunner.run(FileRoleTest.class);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testFileRole() throws AccessControlException {
        String name = "test";
        File configDir = getAccreditablesDirectory();
        FileRole role = new FileRole(configDir, name);
        role.save();

        File path = null;
        path = FileRoleManager.instance(configDir).getConfigurationDirectory();

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
        FileRole role = new FileRole(configDir, name);
        role.save();

        File path = null;
        path = FileRoleManager.instance(configDir).getConfigurationDirectory();

        File roleFile = new File(path, name + FileRoleManager.SUFFIX);
        assertNotNull(roleFile);
        assertTrue(roleFile.exists());
    }

    /**
     * DOCUMENT ME!
     */
    final public void testGetId() {
        String id = "test";
        File configDir = getAccreditablesDirectory();
        FileRole role = new FileRole(configDir, id);
        assertTrue(role.getId().equals(id));
    }

    /**
     * Test for boolean equals(Object)
     */
    final public void testEqualsObject() {
        String name = "test";
        File configDir = getAccreditablesDirectory();
        FileRole role1 = new FileRole(configDir, name);
        FileRole role2 = new FileRole(configDir, name);
        assertEquals(role1, role2);
    }
}
