/*
$Id
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

import org.apache.lenya.cms.PublicationHelper;

import java.io.File;

import java.util.HashMap;
import java.util.Map;


/**
 * @author egli
 *
 *
 */
public class FileUserTest extends AccessControlTest {
    private HashMap roles = new HashMap();
    private HashMap groups = new HashMap();

    /**
     * Constructor for FileUserTest.
     * @param arg0
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

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    final public Map getRoles() {
        return roles;
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
     * DOCUMENT ME!
     *
     * @param userName DOCUMENT ME!
     * @param fullName DOCUMENT ME!
     * @param email DOCUMENT ME!
     * @param password DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public FileUser createAndSaveUser(String userName, String fullName, String email,
        String password) throws AccessControlException {
        File configDir = getConfigurationDirectory();

        String editorGroupName = "editorGroup";
        String adminGroupName = "adminGroup";
        String editorRoleName = "editorRole";
        String adminRoleName = "adminRole";

        this.roles.clear();

        FileRole editorRole = new FileRole(configDir, editorRoleName);
        FileRole adminRole = new FileRole(configDir, adminRoleName);
        this.roles.put(editorRoleName, editorRole);
        this.roles.put(adminRoleName, adminRole);

        FileGroup editorGroup = new FileGroup(configDir, editorGroupName);
        FileGroup adminGroup = new FileGroup(configDir, adminGroupName);
        this.groups.put(editorGroupName, editorGroup);
        this.groups.put(adminGroupName, adminGroup);

        FileUser user = new FileUser(configDir, userName, fullName, email, password);

        editorGroup.add(user);
        adminGroup.add(user);

        editorRole.save();
        adminRole.save();

        //		editorGroup.addRole(editorRole);
        //		adminGroup.addRole(editorRole);
        //		adminGroup.addRole(adminRole);
        editorGroup.save();
        adminGroup.save();
        user.save();

        return user;
    }

    /**
     * DOCUMENT ME!
     *
     * @param userName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public FileUser loadUser(String userName) throws AccessControlException {
        File configDir = getConfigurationDirectory();
        UserManager manager = UserManager.instance(configDir);

        return (FileUser) manager.getUser(userName);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testSave() throws AccessControlException {
        String userName = "alice";
        createAndSaveUser(userName, "Alice Wonderland", "alice@wonderland.org", "secret");

        File configDir = getConfigurationDirectory();
        File xmlFile = new File(configDir, userName + ".iml");
        assertTrue(xmlFile.exists());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testGetEmail() throws AccessControlException {
        String userName = "alice";
        String email = "alice@wonderland.org";
        FileUser user = createAndSaveUser(userName, "Alice Wonderland", email, "secret");
        assertTrue(user.getEmail().equals(email));

        try {
            user = loadUser(userName);
        } catch (AccessControlException e) {
            e.printStackTrace();
        }

        assertTrue(user.getEmail().equals(email));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testGetFullName() throws AccessControlException {
        String userName = "alice";
        String fullName = "Alice Wonderland";
        FileUser user = createAndSaveUser(userName, fullName, "alice@wonderland.org", "secret");
        assertTrue(user.getFullName().equals(fullName));
        user = loadUser(userName);
        assertTrue(user.getFullName().equals(fullName));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testGetGroups() throws AccessControlException {
        FileUser user = createAndSaveUser("alice", "Alice Wonderland", "alice@wonderland.org",
                "secret");
        int groupCount = 0;
        Group[] groups = user.getGroups();

        for (int i = 0; i < groups.length; i++) {
            groupCount += 1;
            assertTrue(getGroups().containsKey(groups[i].getName()));
        }

        assertEquals(groupCount, getGroups().size());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testGetId() throws AccessControlException {
        String id = "alice";
        FileUser user = createAndSaveUser(id, "Alice Wonderland", "alice@wonderland.org", "secret");
        assertTrue(user.getId().equals(id));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testDelete() throws AccessControlException {
        String id = "albert";
        FileUser user = createAndSaveUser(id, "Albert Einstein", "albert@physics.org", "secret");
        File configDir = getConfigurationDirectory();
        UserManager manager = null;
        manager = UserManager.instance(configDir);
        assertNotNull(manager);

        assertNotNull(manager.getUser(id));
        user.delete();
        assertNull(manager.getUser(id));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws AccessControlException DOCUMENT ME!
     */
    final public void testAuthenticate() throws AccessControlException {
        String password = "daisy";
        FileUser user = createAndSaveUser("mickey", "Mickey Mouse", "mickey@mouse.com", password);
        assertTrue(user.authenticate(password));

        File configDir = getConfigurationDirectory();
        UserManager manager = null;
        manager = UserManager.instance(configDir);
        assertNotNull(manager);

        User lenya = manager.getUser("lenya");
        assertNotNull(lenya);
        assertTrue(lenya.authenticate("levi"));
    }
}
