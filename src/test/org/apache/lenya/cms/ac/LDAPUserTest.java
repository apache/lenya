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

import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

import java.io.File;


/**
 * @author egli
 *
 *
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
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * get a publication
     *
     * @return a <code>Publication</code>
     */
    final public Publication getPublication() {
        String publicationId = "default";
        String servletContextPath = "/home/egli/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya/";

        return PublicationFactory.getPublication(publicationId, servletContextPath);
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
    final public void createAndSaveUser(String userName, String email, String ldapId)
        throws AccessControlException, ConfigurationException {
        String editorGroupName = "editorGroup";
        String adminGroupName = "adminGroup";
        String editorRoleName = "editorRole";
        String adminRoleName = "adminRole";

        File configDir = getConfigurationDirectory();
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
    final public LDAPUser loadUser(String userName) throws AccessControlException {
        UserManager manager = UserManager.instance(getConfigurationDirectory());

        return (LDAPUser) manager.getUser(userName);
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
    final public void testGetLdapId() throws ConfigurationException, AccessControlException {
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
    final public void testSetLdapId() throws ConfigurationException, AccessControlException {
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
    final public void testSave() throws ConfigurationException, AccessControlException {
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
