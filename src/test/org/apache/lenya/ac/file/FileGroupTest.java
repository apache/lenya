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

/* $Id: FileGroupTest.java,v 1.3 2004/03/04 15:37:59 egli Exp $  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.io.IOException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Groupable;
import org.apache.lenya.ac.impl.AccessControlTest;
import org.apache.lenya.cms.PublicationHelper;
import org.xml.sax.SAXException;

/**
 * @author egli
 *
 *
 */
public class FileGroupTest extends AccessControlTest {

    /**
     * Constructor for FileGroupTest.
     * @param arg0 command line args
     */
    public FileGroupTest(String arg0) {
        super(arg0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        junit.textui.TestRunner.run(FileGroupTest.class);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     * @throws ConfigurationException DOCUMENT ME!
     * @throws SAXException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    final public void testFileGroup()
        throws AccessControlException, ConfigurationException, SAXException, IOException {

        FileGroup group = getGroup();
        group.save();

        File groupFile = new File(getAccreditablesDirectory(), GROUP_ID + FileGroupManager.SUFFIX);
        assertNotNull(groupFile);
        assertTrue(groupFile.exists());

        Configuration config = null;
        config = new DefaultConfigurationBuilder().buildFromFile(groupFile);
        assertNotNull(config);

        FileGroup newGroup = null;
        newGroup = new FileGroup();
        newGroup.setConfigurationDirectory(getAccreditablesDirectory());
        newGroup.configure(config);
        assertNotNull(newGroup);

        assertTrue(newGroup.getId().equals(GROUP_ID));

    }

    public static final String GROUP_ID = "testGroup";

    /**
     * Test getGroup
     * 
     * @return a <code>FileGroup</code>
     */
    protected FileGroup getGroup() {
        File configurationDirectory = getAccreditablesDirectory();
        System.out.println("Configuration directory: " + configurationDirectory);
        FileGroup group = new FileGroup(configurationDirectory, GROUP_ID);
        return group;
    }

    /**
     * Tests the removeAllMembers() method.
     */
    public void testRemoveAllMembers() {
        Group group = getGroup();
        Groupable members[] = group.getMembers();
        group.removeAllMembers();
        for (int i = 0; i < members.length; i++) {
            assertFalse(group.contains(members[i]));
        }
    }

}
