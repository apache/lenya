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

/* $Id: AccessControllerTest.java,v 1.2 2004/03/04 15:40:19 egli Exp $  */

package org.apache.lenya.ac.impl;

import junit.textui.TestRunner;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.cms.PublicationHelper;

public class AccessControllerTest extends AccessControlTest {

    /**
     * @param test A test.
     */
    public AccessControllerTest(String test) {
        super(test);
    }

    /**
     * The main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(AccessControllerTest.class);
    }

    /**
     * The test.
     * @throws AccessControlException when something went wrong.
     */
    public void testAccessController() throws AccessControlException {
        assertNotNull(getAccessController());
    }

}
