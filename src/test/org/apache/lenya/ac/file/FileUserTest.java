/*
$Id: FileUserTest.java,v 1.2 2004/02/02 02:51:51 stefano Exp $
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
package org.apache.lenya.ac.file;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.AccessControlTest;
import org.apache.lenya.cms.PublicationHelper;


/**
 * @author egli
 *
 *
 */
public class FileUserTest extends AccessControlTest {
    private HashMap groups = new HashMap();

    /**
     * Constructor for FileUserTest.
     * @param arg0 command line args
     */
    public FileUserTest(String arg0) {
        super(arg0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        junit.textui.TestRunner.run(FileUserTest.class);
    }

	/**
	 *  (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    final public Map getGroups() {
        return groups;
    }

    /**
     * Create and save a user
     *
     * @param userName DOCUMENT ME!
     * @param fullName DOCUMENT ME!
     * @param email DOCUMENT ME!
     * @param password DOCUMENT ME!
     *
     * @return a <code>FileUser</code>
     *
     * @throws AccessControlException if an error occurs
     */
    final public FileUser createAndSaveUser(String userName, String fullName, String email,
        String password) throws AccessControlException {
        File configDir = getAccreditablesDirectory();

        String editorGroupName = "editorGroup";
        String adminGroupName = "adminGroup";

        FileGroup editorGroup = new FileGroup(configDir, editorGroupName);
        FileGroup adminGroup = new FileGroup(configDir, adminGroupName);
        this.groups.put(editorGroupName, editorGroup);
        this.groups.put(adminGroupName, adminGroup);

        FileUser user = new FileUser(configDir, userName, fullName, email, password);

        editorGroup.add(user);
        adminGroup.add(user);

        editorGroup.save();
        adminGroup.save();
        user.save();
        FileUserManager manager = FileUserManager.instance(configDir);
        manager.add(user);

        return user;
    }

    /**
     * Load a user.
     *
     * @param userName the name of the user
     *
     * @return a <code>FileUser</code>
     *
     * @throws AccessControlException if an error occurs
     */
    final public FileUser loadUser(String userName) throws AccessControlException {
        File configDir = getAccreditablesDirectory();
        FileUserManager manager = FileUserManager.instance(configDir);

        return (FileUser) manager.getUser(userName);
    }

    /**
     * Test save
     *
     * @throws AccessControlException if an error occurs
     */
    final public void testSave() throws AccessControlException {
        String userName = "alice";
        createAndSaveUser(userName, "Alice Wonderland", "alice@wonderland.org", "secret");

        File configDir = getAccreditablesDirectory();
        File xmlFile = new File(configDir, userName + ".iml");
        assertTrue(xmlFile.exists());
    }

    /**
     * Test getEmail
     *
     * @throws AccessControlException if an error occurs
     */
    final public void testGetEmail() throws AccessControlException {
        String userID = "alice";
        String email = "alice@wonderland.org";
        User user = createAndSaveUser(userID, "Alice Wonderland", email, "secret");
        assertTrue(user.getEmail().equals(email));
        user = loadUser(userID);
        assertTrue(user.getEmail().equals(email));
    }

    /**
     * Test getFullName
     *
     * @throws AccessControlException if an error occurs
     */
    final public void testGetFullName() throws AccessControlException {
        String userID = "alice";
        String userName = "Alice Wonderland";
        FileUser user = createAndSaveUser(userID, userName, "alice@wonderland.org", "secret");
        assertTrue(user.getName().equals(userName));
        user = loadUser(userID);
        assertTrue(user.getName().equals(userName));
    }

    /**
     * Test getGroups
     *
     * @throws AccessControlException if an error occurs
     */
    final public void testGetGroups() throws AccessControlException {
        FileUser user = createAndSaveUser("alice", "Alice Wonderland", "alice@wonderland.org",
                "secret");
                
        for (Iterator i = getGroups().values().iterator(); i.hasNext(); ) {
            Group group = (Group) i.next();
            assertTrue(group.contains(user));
        }
    }

    /**
     * Test getId
     *
     * @throws AccessControlException if an error occurs
     */
    final public void testGetId() throws AccessControlException {
        String id = "alice";
        FileUser user = createAndSaveUser(id, "Alice Wonderland", "alice@wonderland.org", "secret");
        assertTrue(user.getId().equals(id));
    }

    /**
     * Test delete
     *
     * @throws AccessControlException if an error occurs
     */
    final public void testDelete() throws AccessControlException {
        String id = "albert";
        FileUser user = createAndSaveUser(id, "Albert Einstein", "albert@physics.org", "secret");
        File configDir = getAccreditablesDirectory();
        FileUserManager manager = null;
        manager = FileUserManager.instance(configDir);
        assertNotNull(manager);

        assertNotNull(manager.getUser(id));
        user.delete();
        manager.remove(user);
        assertNull(manager.getUser(id));
    }

    /**
     * Test authenticate
     *
     * @throws AccessControlException if an error occurs
     */
    final public void testAuthenticate() throws AccessControlException {
        String password = "daisy";
        FileUser user = createAndSaveUser("mickey", "Mickey Mouse", "mickey@mouse.com", password);
        assertTrue(user.authenticate(password));

        File configDir = getAccreditablesDirectory();
        FileUserManager manager = null;
        manager = FileUserManager.instance(configDir);
        assertNotNull(manager);

        User lenya = manager.getUser("lenya");
        assertNotNull(lenya);
        assertTrue(lenya.authenticate("levi"));
    }
}
