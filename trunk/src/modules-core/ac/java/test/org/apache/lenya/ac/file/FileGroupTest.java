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
import java.io.IOException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Groupable;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.xml.sax.SAXException;

/**
 * Test for file-based groups
 */
public class FileGroupTest extends AbstractAccessControlTest {

    /**
     * <code>GROUP_ID</code> The group id
     */
    public static final String GROUP_ID = "testGroup";

    /**
     * Runs the test
     * 
     * @throws AccessControlException if an AC error occurs
     * @throws ConfigurationException if an error with the configuration occurs
     * @throws SAXException if a parsing error occurs
     * @throws IOException if an IO error occurs
     */
    final public void testFileGroup() throws AccessControlException, ConfigurationException,
            SAXException, IOException {

        FileGroup group = getGroup();
        group.save();

        File groupFile = new File(getAccreditablesDirectory(), GROUP_ID + FileGroupManager.SUFFIX);
        assertNotNull(groupFile);
        assertTrue(groupFile.exists());

        Configuration config = null;
        config = new DefaultConfigurationBuilder().buildFromFile(groupFile);
        assertNotNull(config);

        FileGroup newGroup = null;
        newGroup = new FileGroup(getAccreditableManager().getGroupManager(), getLogger());
        newGroup.setConfigurationDirectory(getAccreditablesDirectory());
        newGroup.configure(config);
        assertNotNull(newGroup);

        assertTrue(newGroup.getId().equals(GROUP_ID));

    }

    /**
     * Test getGroup
     * 
     * @return a <code>FileGroup</code>
     * @throws AccessControlException
     */
    protected FileGroup getGroup() throws AccessControlException {
        File configurationDirectory = getAccreditablesDirectory();
        getLogger().info("Configuration directory: " + configurationDirectory);
        FileGroup group = new FileGroup(getAccreditableManager().getGroupManager(), getLogger(),
                GROUP_ID);
        return group;
    }

    /**
     * Tests the removeAllMembers() method.
     * @throws AccessControlException
     */
    public void testRemoveAllMembers() throws AccessControlException {
        Group group = getGroup();
        Groupable members[] = group.getMembers();
        group.removeAllMembers();
        for (int i = 0; i < members.length; i++) {
            assertFalse(group.contains(members[i]));
        }
    }

}
