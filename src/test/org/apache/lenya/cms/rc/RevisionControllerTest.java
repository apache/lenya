/*
$Id: RevisionControllerTest.java,v 1.2 2003/07/31 11:56:42 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.rc;

import junit.framework.TestCase;

import java.io.FileNotFoundException;

import java.io.File;
import java.io.IOException;

/**
 * @author Gregor J. Rothfuss gregor@apache.org
 *
 */
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
					System.out.println("Usage: " + new RevisionController().getClass().getName() +
						" username(user who checkout) source(filename without the rootDirectory of the document to checkout) username(user who checkin) destination(filename without the rootDirectory of document to checkin)");

					return;
				}

				String identityS = args[0];
				String source = args[1];
				String identityD = args[2];
				String destination = args[3];
				RevisionController rc = new RevisionController();
				File in = null;

				try {
					in = rc.reservedCheckOut(source, identityS);
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
    protected void setUp() throws Exception {
    }
}
