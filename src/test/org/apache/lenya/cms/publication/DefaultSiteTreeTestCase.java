package org.apache.lenya.cms.publication;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * A test case testing org.apache.lenya.cms.publishing.DefaultSiteTree
 *
 * @version CVS $Id: DefaultSiteTreeTestCase.java,v 1.2 2003/05/30 15:00:02 andreas Exp $
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
        try {
            sitetree.addNode("/tutorial", "foo", labels);
        } catch (SiteTreeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace(System.err);
        }
        // read the new sitetree and assert that it contains the new node

    }
}
