/*
$Id: UserManagerTest.java,v 1.2 2004/02/02 02:51:51 stefano Exp $
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
package org.apache.lenya.ac.impl;

import java.io.File;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.file.FileGroup;
import org.apache.lenya.ac.file.FileGroupManager;
import org.apache.lenya.ac.file.FileRole;
import org.apache.lenya.ac.file.FileUser;
import org.apache.lenya.ac.file.FileUserManager;
import org.apache.lenya.cms.PublicationHelper;

/**
 * @author egli
 *
 *
 */
public class UserManagerTest extends AccessControlTest {

    /**
     * Constructor for UserManagerTest.
     * @param arg0 command line args
     */
    public UserManagerTest(String arg0) {
        super(arg0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        junit.textui.TestRunner.run(UserManagerTest.class);
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
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testInstance() throws AccessControlException {
        File configDir = getAccreditablesDirectory();
        FileUserManager manager = FileUserManager.instance(configDir);
        assertNotNull(manager);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testLoadConfig() throws AccessControlException {
        File configDir = getAccreditablesDirectory();

        String userName = "alice";
        String editorGroupId = "editorGroup";
        String adminGroupId = "adminGroup";
        String editorRoleId = "editorRole";
        String adminRoleId = "adminRole";

        FileRole editorRole = new FileRole(configDir, editorRoleId);
        FileRole adminRole = new FileRole(configDir, adminRoleId);

        User user =
            new FileUser(configDir, userName, "Alice in Wonderland", "alice@test.com", "secret");

        editorRole.save();
        adminRole.save();

        Group editorGroup = new FileGroup(configDir, editorGroupId);

        //		editorGroup.addRole(editorRole);
        editorGroup.add(user);

        FileGroup adminGroup = new FileGroup(configDir, adminGroupId);

        //		adminGroup.addRole(editorRole);
        //		adminGroup.addRole(adminRole);
        editorGroup.save();
        adminGroup.save();
        adminGroup.add(user);
        user.save();

        FileUserManager userManager = null;
        FileGroupManager groupManager = null;
        userManager = FileUserManager.instance(configDir);
        assertNotNull(userManager);

        groupManager = FileGroupManager.instance(configDir);
        assertNotNull(groupManager);

        Group fetchedGroup = groupManager.getGroup(editorGroupId);
        assertTrue(editorGroup.equals(fetchedGroup));

        fetchedGroup = groupManager.getGroup(adminGroupId);
        assertTrue(adminGroup.equals(fetchedGroup));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testGetUser() throws AccessControlException {
        File configDir = getAccreditablesDirectory();
        FileUserManager manager = null;
        String userName = "testuser";
        FileUser user =
            new FileUser(
                configDir,
                userName,
                "Alice in Wonderland",
                "alice@wonderland.com",
                "secret");
        manager = FileUserManager.instance(configDir);
        assertNotNull(manager);
        manager.add(user);

        User otherUser = manager.getUser(userName);
        assertEquals(user, otherUser);
        assertEquals(user.getDescription(), otherUser.getDescription());
        assertEquals(user.getEmail(), otherUser.getEmail());
        assertEquals(user.getEncryptedPassword(), ((AbstractUser) otherUser).getEncryptedPassword());
    }
}
