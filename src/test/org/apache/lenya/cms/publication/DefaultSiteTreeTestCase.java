package org.apache.lenya.cms.publication;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.Label;

/**
 * A test case testing org.apache.lenya.cms.publishing.DefaultSiteTree
 *
 * @version CVS $Id: DefaultSiteTreeTestCase.java,v 1.1 2003/05/08 08:38:19 egli Exp $
 */
public final class DefaultSiteTreeTestCase extends TestCase {
    private DefaultSiteTree sitetree;
    
    /**
     * The main program for the DefaultSiteTreeTestCase class
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
        suite.addTest(new DefaultSiteTreeTestCase("testNodeAddition"));
        return suite;
    }

    /**
     *
     */
     public DefaultSiteTreeTestCase(String test) {
         super(test);
	 try {
	     sitetree = new DefaultSiteTree("");
	     
	 } catch (Exception e) {
	     System.err.println(e);
	 }
     }
    
    /**
     * Test if username exists
     */
    public void testNodeAddition() {
	Label label = new Label("Foo", null);
	Label[] labels = { label };
	sitetree.addNode("/tutorial", "foo", labels);
	// read the new sitetree and assert that it contains the new node

    }
}

