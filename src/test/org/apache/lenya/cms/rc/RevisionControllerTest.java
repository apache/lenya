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

/* $Id: RevisionControllerTest.java,v 1.5 2004/03/04 15:41:10 egli Exp $  */

package org.apache.lenya.cms.rc;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

public class RevisionControllerTest extends TestCase {
    /**
     * Constructor.
     * @param test The test to execute.
     */
    public RevisionControllerTest(String test) {
        super(test);
    }

    /**
     * The main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        //       TestRunner.run(getSuite());

        if (args.length != 4) {
            System.out.println(
                "Usage: "
                    + new RevisionController().getClass().getName()
                    + " username(user who checkout) source(filename without the rootDirectory of the document to checkout) username(user who checkin) destination(filename without the rootDirectory of document to checkin)");

            return;
        }

        String identityS = args[0];
        String source = args[1];
        String identityD = args[2];
        String destination = args[3];
        RevisionController rc = new RevisionController();
        try {
            rc.reservedCheckOut(source, identityS);
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
            rc.reservedCheckIn(destination, identityD, true);
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
    protected void setUp() throws Exception {}
}
