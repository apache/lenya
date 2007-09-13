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

/* $Id: GroupManagerTest.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.ac.impl;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.components.ComponentContext.ComponentManagerWrapper;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.file.FileAccreditableManager;
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
        ServiceManager wrapper = new ComponentManagerWrapper(this.manager);
        FileGroupManager manager = FileGroupManager.instance(wrapper,
                (FileAccreditableManager) getAccreditableManager(), getLogEnabledLogger());
        assertNotNull(manager);

        FileGroupManager anotherManager = FileGroupManager.instance(wrapper,
                (FileAccreditableManager) getAccreditableManager(), getLogEnabledLogger());
        assertNotNull(anotherManager);
        assertEquals(manager, anotherManager);
    }
}
