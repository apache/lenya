/*
$Id: IdentityTestCase.java,v 1.10 2003/07/08 12:13:40 egli Exp $
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
package org.apache.lenya.cms.ac;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

import org.apache.lenya.cms.PublicationHelper;

import org.w3c.dom.Document;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * A test case testing org.apache.lenya.cms.ac.Identity
 *
 * @author <a href="mailto:gregor@apache.org">Gregor J. Rothfuss</a>
 * @author <a href="mailto:michi@apache.org">Michael Wechner</a>
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 * @version CVS $Id: IdentityTestCase.java,v 1.10 2003/07/08 12:13:40 egli Exp $
 */
public final class IdentityTestCase extends TestCase {
    private Identity identity = null;
    private Document doc = null;

    /**
     * The main program for the IdentityTestCase class
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        args = PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(suite());
    }

	/**
	 * Get the test
	 * 
	 * @return a <code>Test</code>
	 */
    public static Test suite() {
        return new TestSuite(IdentityTestCase.class);
    }

	/**
	 * Create a Identity test case
	 * 
	 * @param test the test
	 */
    public IdentityTestCase(String test) {
        super(test);

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File publicationDirectory = PublicationHelper.getPublication().getDirectory();
            File identityFile = new File(publicationDirectory, "config/ac/passwd/lenya.iml");
            doc = db.parse(identityFile);
            identity = new Identity(doc);

            //new org.apache.lenya.xml.DOMWriter(System.out).printWithoutFormatting(identity.createDocument());
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Test if username exists
     */
    public void testUsername() {
        assertTrue(identity.getUsername().equals("lenya"));
        System.out.println("Username OK.");
    }

    /**
     * Test if encrypted password exists
     * 
     * @throws Exception if an error occurs
     */
    public void testEncryptedPassword() throws Exception {
        assertTrue(Identity.getPassword(doc).equals("8e07dafd13495561db9063ebe4db4b27"));
        System.out.println("Password OK.");
    }
}
