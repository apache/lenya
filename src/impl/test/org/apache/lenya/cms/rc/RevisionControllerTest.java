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

package org.apache.lenya.cms.rc;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Document;

/**
 * Revision Controller test
 */
public class RevisionControllerTest extends AbstractAccessControlTest {

    public void testRevisionController() {
        
        String[] args = { "", "", "", "" };
        
        //       TestRunner.run(getSuite());

        if (args.length != 4) {
            System.out.println(
                "Usage: "
                    + RevisionController.class.getName()
                    + " username(user who checkout) source(filename without the rootDirectory of the document to checkout) username(user who checkin) destination(filename without the rootDirectory of document to checkin)");

            return;
        }

        Document doc1 = null;
        Document doc2 = null;
        
        String identityS = args[0];
        String source = args[1];
        String identityD = args[2];
        String destination = args[3];
        RevisionController rc = new RevisionController();
        try {
            rc.reservedCheckOut(doc1.getRepositoryNode(), identityS);
        } catch (FileNotFoundException e) // No such source file
            {
            System.out.println(e.toString());
        } catch (FileReservedCheckOutException e) // Source has been checked out already
            {
            System.out.println(e.toString());
            //	System.out.println(error(e.source + "is already check out by " + e.checkOutUsername + " since " +						e.checkOutDate));
            return;

        } catch (IOException e) { // Cannot create rcml file
            System.out.println(e.toString());
            return;

        } catch (Exception e) {
            System.out.println(e.toString());
            return;
        }

        try {
            rc.reservedCheckIn(doc2.getRepositoryNode(), identityD, true, true);
        } catch (FileReservedCheckInException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /*   protected static final Class[] classes = {
       };
    
       /**
        * Creates a test suite.
        * @return a test suite.
        */
    /*    public static Test getSuite() {
            TestSuite suite = new TestSuite();
    
            for (int i = 0; i < classes.length; i++) {
                suite.addTestSuite(classes[i]);
            }
    
            return suite;
        }
    */
    /** @see junit.framework.TestCase#setUp() */
    protected void setUp() throws Exception {
        // do nothing
    }
}
