/**
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
package org.apache.lenya.cms.ac2;

import junit.textui.TestRunner;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.User;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class IdentityTest extends AccessControlTest {

    /**
     * Ctor.
     * @param test The test.
     */
    public IdentityTest(String test) {
        super(test);
    }

    /**
     * The main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(IdentityTest.class);
    }
    
    public static final String USER_ID = "lenya";

    /**
     * Tests the identity.
     */
    public void testIdentity() throws AccessControlException {
        Identity identity = new Identity();
        User user = getAccreditableManager().getUserManager().getUser(USER_ID);
        System.out.println("Adding user to identity: [" + user + "]");
        identity.addIdentifiable(user);
        
        assertSame(user, identity.getUser());
    }

}
