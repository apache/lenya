/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.lenya.ac.impl;

import junit.textui.TestRunner;

import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.cms.ExcaliburTest;
import org.apache.lenya.cms.PublicationHelper;

public class AccreditableManagerTest extends ExcaliburTest {

    /**
     * @param test The test to execute.
     */
    public AccreditableManagerTest(String test) {
        super(test);
    }

    /**
     * The main program.
     * The parameters are set from the command line arguments.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        args = PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(AccreditableManagerTest.class);
    }
    
    private AccreditableManager accreditableManager;
    private ComponentSelector selector;
    
    protected static final String HINT = "file";

    /**
     * The JUnit setup method. Lookup the resolver role.
     *
     * @exception  Exception  Description of Exception
     * @since
     */
    public void setUp() throws Exception {
        super.setUp();

        String role = AccreditableManager.ROLE + "Selector";
        selector = (ComponentSelector) manager.lookup(role);
        
        accreditableManager = (AccreditableManager) selector.select(HINT);
        assertNotNull("AccreditableManager is null", accreditableManager);
    }

    /**
     * The test.
     * @throws AccessControlException when something went wrong.
     */
    public void testAccreditableManager() throws AccessControlException {
        assertNotNull(accreditableManager.getUserManager());
        assertNotNull(accreditableManager.getGroupManager());
        assertNotNull(accreditableManager.getRoleManager());
        
    }

}
