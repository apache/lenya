/*
$Id: RCMLTest.java,v 1.2 2003/07/31 11:56:42 andreas Exp $
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

import java.io.PrintWriter;
import java.io.File;

import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Gregor J. Rothfuss gregor@apache.org
 *
 */
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
			rcml.checkOutIn(RCML.co, "michi", new Date().getTime());

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
