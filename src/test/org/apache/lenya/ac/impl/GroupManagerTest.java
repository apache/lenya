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

/* $Id: GroupManagerTest.java,v 1.3 2004/03/04 15:40:19 egli Exp $  */

package org.apache.lenya.ac.impl;

import java.io.File;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.file.FileGroupManager;
import org.apache.lenya.cms.PublicationHelper;

public class GroupManagerTest extends AccessControlTest {

    /**
     * Constructor for GroupManagerTest.
     * @param arg0 command line args
     */
    public GroupManagerTest(String arg0) {
        super(arg0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        junit.textui.TestRunner.run(GroupManagerTest.class);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    public final void testInstance() throws AccessControlException {
        FileGroupManager manager = null;
        File configDir = getAccreditablesDirectory();
        manager = FileGroupManager.instance(configDir);
        assertNotNull(manager);

        FileGroupManager anotherManager = null;
        anotherManager = FileGroupManager.instance(configDir);
        assertNotNull(anotherManager);
        assertEquals(manager, anotherManager);
    }
}
