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

/* $Id: RCMLTest.java,v 1.6 2004/03/04 15:41:10 egli Exp $  */

package org.apache.lenya.cms.rc;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.lenya.xml.DOMParserFactory;
import org.apache.lenya.xml.DOMWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RCMLTest extends TestCase {

	public static final short co = 0;
	public static final short ci = 1;
    
	private File rcmlFile;
	private Document document = null;
	private boolean dirty = false;
	private int maximalNumberOfEntries = 5;

    /**
     * Constructor.
     * @param test The test to execute.
     */
    public RCMLTest(String test) {
        super(test);
    }

    /**
     * The main method.
     * @param args The command-line arguments.
     */
    public void main(String[] args) {
//        TestRunner.run(getSuite());


		if (args.length != 1) {
			System.out.println("Usage: java RCML rcmlDirectory datafilename rootDirectory");

			return;
		}

		try {
			RCML rcml = new RCML(args[0], args[1], args[2]);
			rcml.checkOutIn(RCML.co, "michi", new Date().getTime(), false);

			new DOMWriter(new PrintWriter(System.out)).print(this.document);

			CheckOutEntry coe = rcml.getLatestCheckOutEntry();
			System.out.println("\n");

			if (coe == null) {
				System.out.println("Not checked out");
			} else {
				System.out.println("Checked out: " + coe.getIdentity() + " " + coe.getTime());
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
			DOMParserFactory dpf = new DOMParserFactory();
			document = dpf.getDocument();

			Element root = dpf.newElementNode(document, "XPSRevisionControl");
			document.appendChild(root);

    }
}
