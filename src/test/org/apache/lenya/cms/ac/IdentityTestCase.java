package org.apache.lenya.cms.ac;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import java.io.FileInputStream;

import org.w3c.dom.Document;

import org.apache.lenya.cms.ac.Identity;

/**
 * A test case testing org.apache.lenya.cms.ac.Identity
 *
 * @author <a href="mailto:gregor@apache.org">Gregor J. Rothfuss</a>
 * @author <a href="mailto:michi@apache.org">Michael Wechner</a>
 * @version CVS $Id: IdentityTestCase.java,v 1.7 2003/05/30 17:58:20 andreas Exp $
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
        TestRunner.run(suite());
    }

    /**
     *
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new IdentityTestCase("testUsername"));
        suite.addTest(new IdentityTestCase("testEncryptedPassword"));
        return suite;
    }

    /**
     *
     */
     public IdentityTestCase(String test) {
         super(test);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new FileInputStream("/home/michi/src/lenya/src/webapp/lenya/pubs/oscom/config/ac/passwd/lenya.iml"));
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
    }

    /**
     * Test if encrypted password exists
     */
    public void testEncryptedPassword() throws Exception {
        assertTrue(Identity.getPassword(doc).equals("8e07dafd13495561db9063ebe4db4b27"));
    }
}

