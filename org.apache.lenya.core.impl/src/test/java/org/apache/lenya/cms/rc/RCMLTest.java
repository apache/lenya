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

import java.io.PrintWriter;

import org.w3c.dom.Document;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.xml.DocumentHelper;

/**
 * RCML Test
 */
public class RCMLTest extends AbstractAccessControlTest {

	/**
	 * <code>co</code> Checkout
	 */
	public static final short co = 0;
	/**
	 * <code>ci</code> Checkin
	 */
	public static final short ci = 1;
    
	private Document document = null;

    /**
     * Constructor.
     * @param test The test to execute.
     */
    public RCMLTest() {
        super();
    }
    
    public void testRCML() {
        String[] args = { "", "", "" };
        testRCML(args);
    }

    public void testRCML(String[] args) {

		if (args.length != 1) {
			System.out.println("Usage: java RCML rcmlDirectory datafilename rootDirectory");

			return;
		}

		try {
            org.apache.lenya.cms.publication.Document doc = null;
			doc.getRepositoryNode().checkout();

			(new PrintWriter(System.out)).print(this.document);

			System.out.println("\n");

			if (doc.getRepositoryNode().isCheckedOut()) {
			    System.out.println("Checked out");
			} else {
			    System.out.println("Not checked out");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	    }

/*    protected static final Class[] classes = {
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
		/**
		 * initialise the RCML-document. Delete all entries
		 */
        this.document = DocumentHelper.createDocument(null, "XPSRevisionControl", null);
    }
}
