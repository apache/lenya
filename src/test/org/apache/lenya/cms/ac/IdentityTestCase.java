package org.apache.lenya.cms.ac;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import java.io.File;

import org.w3c.dom.Document;

import org.apache.lenya.cms.PublicationHelper;

/**
 * A test case testing org.apache.lenya.cms.ac.Identity
 *
 * @author <a href="mailto:gregor@apache.org">Gregor J. Rothfuss</a>
 * @author <a href="mailto:michi@apache.org">Michael Wechner</a>
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 * @version CVS $Id: IdentityTestCase.java,v 1.8 2003/06/12 10:16:31 andreas Exp $
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
     *
     */
    public static Test suite() {
        return new TestSuite(IdentityTestCase.class);
    }

    /**
     *
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
     */
    public void testEncryptedPassword() throws Exception {
        assertTrue(Identity.getPassword(doc).equals("8e07dafd13495561db9063ebe4db4b27"));
        System.out.println("Password OK.");
    }
}

