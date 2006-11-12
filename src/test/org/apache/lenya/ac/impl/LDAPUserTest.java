/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.lenya.ac.impl;

import java.io.File;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserType;
import org.apache.lenya.ac.file.FileAccreditableManager;
import org.apache.lenya.ac.file.FileGroup;
import org.apache.lenya.ac.file.FileRole;
import org.apache.lenya.ac.file.FileUserManager;
import org.apache.lenya.ac.ldap.LDAPUser;
import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;

/**
 * LDAP user test.
 * 
 * @version $Id$
 */
public class LDAPUserTest extends AccessControlTest {
    /**
     * Constructor for LDAPUserTest.
     * @param arg0 a <code>String</code>
     */
    public LDAPUserTest(String arg0) {
        super(arg0);
    }

    /**
     *
     * @param args an array of <code>String</code>
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        junit.textui.TestRunner.run(LDAPUserTest.class);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * get a publication
     *
     * @return a <code>Publication</code>
     * 
     * @throws PublicationException if an error occurs
     */
    final public Publication getPublication() throws PublicationException {
        String publicationId = "default";
        String servletContextPath =
            "/home/egli/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya/";

        return PublicationFactory.getPublication(
            publicationId,
            servletContextPath);
    }

    /**
     * Create and save an ldap user
     *
     * @param userName name of the user
     * @param email of the user
     * @param ldapId ldap id of the user
     * @throws AccessControlException if the creating or the saving fails
    * @throws ConfigurationException if the creating or the saving fails
     */
    final public void createAndSaveUser(
        String userName,
        String email,
        String ldapId)
        throws AccessControlException, ConfigurationException {
        String editorGroupName = "editorGroup";
        String adminGroupName = "adminGroup";
        String editorRoleName = "editorRole";
        String adminRoleName = "adminRole";

        File configDir = getAccreditablesDirectory();
        FileRole editorRole = new FileRole();
        editorRole.setName(editorRoleName);
        editorRole.setConfigurationDirectory(configDir);

        FileRole adminRole = new FileRole();
        adminRole.setName(adminRoleName);
        adminRole.setConfigurationDirectory(configDir);

        FileGroup editorGroup = new FileGroup(configDir, editorGroupName);
        FileGroup adminGroup = new FileGroup(configDir, adminGroupName);

        LDAPUser user = new LDAPUser(configDir, userName, email, ldapId);

        editorRole.save();
        adminRole.save();

        /*
                editorGroup.addRole(editorRole);
                user.addGroup(editorGroup);
                adminGroup.addRole(editorRole);
                adminGroup.addRole(adminRole);
        */
        editorGroup.save();
        adminGroup.save();

        adminGroup.add(user);
        user.save();
    }

    /**
     * Test loading an LDAPUser
     *
     * @param userName the name of the user
     * @return an <code>LDAPUser</code>
     * @throws AccessControlException of the loading fails
     */
    final public LDAPUser loadUser(String userName)
        throws AccessControlException {
        UserType[] userTypes = { FileAccreditableManager.getDefaultUserType() };
        FileUserManager manager = FileUserManager.instance(getAccreditablesDirectory(), userTypes);

        return (LDAPUser)manager.getUser(userName);
    }

    //    final public void testGetFullName() throws AccessControlException {
    //		String userName = "felix";
    //		createAndSaveUser(userName, "felix@wyona.com", "m400032");
    //		LDAPUser user = null;
    //		user = loadUser(userName);
    //		assertNotNull(user);
    //		String fullName = user.getFullName();
    //		assertTrue(fullName.equals(" Felix Maeder - Wayona"));
    //    }

    /**
     * Test the setter of the full name
     */
    final public void testSetFullName() {
        // the setFullName method is supposed to do nothing
    }

    //    final public void testAuthenticate() throws AccessControlException {
    //        String userName = "felix";
    //        createAndSaveUser(userName, "felix@wyona.com", "m400032");
    //        User user = null;
    //        user = loadUser(userName);
    //        assertNotNull(user);
    //        assertTrue(user.authenticate("sekret"));
    //    }

    /**
     * Test the ldap id getter
     *
     * @throws AccessControlException if the test fails
    * @throws ConfigurationException if the creating or the saving fails
     */
    final public void testGetLdapId()
        throws ConfigurationException, AccessControlException {
        String userName = "felix";
        String ldapId = "m400032";
        createAndSaveUser(userName, "felix@wyona.com", ldapId);

        LDAPUser user = null;
        user = loadUser(userName);
        assertNotNull(user);
        assertEquals(ldapId, user.getLdapId());
    }

    /**
     * Test settinf the ldap id
     *
     * @throws AccessControlException if the test fails
    * @throws ConfigurationException if the creating or the saving fails
     */
    final public void testSetLdapId()
        throws ConfigurationException, AccessControlException {
        String userName = "felix";
        String newLdapId = "foo";
        createAndSaveUser(userName, "felix@wyona.com", "bar");

        LDAPUser user = null;
        user = loadUser(userName);
        assertNotNull(user);
        user.setLdapId(newLdapId);
        user.save();
        user = null;
        user = loadUser(userName);
        assertNotNull(user);
        assertEquals(newLdapId, user.getLdapId());
    }

    /**
     * Test save
     *
     * @throws AccessControlException if the test fails
    * @throws ConfigurationException if the creating or the saving fails
     */
    final public void testSave()
        throws ConfigurationException, AccessControlException {
        String userName = "felix";
        createAndSaveUser(userName, "felix@wyona.com", "m400032");

        User user = null;
        user = loadUser(userName);
        assertNotNull(user);
    }

    /**
     * Test the deletion of a ldap user
     *
     */
    final public void testDelete() {
        //TODO Implement delete().
    }
}
