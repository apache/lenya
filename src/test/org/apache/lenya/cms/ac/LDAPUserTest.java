/*
 * $Id: LDAPUserTest.java,v 1.2 2003/06/24 14:37:35 felix Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2003 Wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment: "This product
 *    includes software developed by Wyona (http://www.wyona.com)"
 *
 * 4. The name "Lenya" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact contact@wyona.com
 *
 * 5. Products derived from this software may not be called "Lenya" nor
 *    may "Lenya" appear in their names without prior written permission
 *    of Wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment: "This product includes software developed by Wyona
 *    (http://www.wyona.com)"
 *
 * THIS SOFTWARE IS PROVIDED BY Wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS
 * OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * Wyona WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS A RESULT
 * OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF Wyona HAS BEEN
 * ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. Wyona WILL NOT BE LIABLE
 * FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */

package org.apache.lenya.cms.ac;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

import junit.framework.TestCase;

/**
 * @author egli
 * 
 * 
 */
public class LDAPUserTest extends TestCase {

    /**
     * Constructor for LDAPUserTest.
     * @param arg0
     */
    public LDAPUserTest(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LDAPUserTest.class);
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

    final public Publication getPublication() {
        String publicationId = "default";
        String servletContextPath =
            "/home/egli/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya/";
        return PublicationFactory.getPublication(
            publicationId,
            servletContextPath);
    }

    final public void createAndSaveUser(
        String userName,
        String email,
        String ldapId)
        throws AccessControlException {

        Publication publication = getPublication();
        String editorGroupName = "editorGroup";
        String adminGroupName = "adminGroup";
        String editorRoleName = "editorRole";
        String adminRoleName = "adminRole";

        FileRole editorRole = new FileRole(publication, editorRoleName);
        FileRole adminRole = new FileRole(publication, adminRoleName);

        FileGroup editorGroup = new FileGroup(publication, editorGroupName);
        FileGroup adminGroup = new FileGroup(publication, adminGroupName);

        LDAPUser user = new LDAPUser(publication, userName, email, ldapId);

        editorRole.save();
        adminRole.save();

        editorGroup.addRole(editorRole);
        user.addGroup(editorGroup);
        adminGroup.addRole(editorRole);
        adminGroup.addRole(adminRole);

        editorGroup.save();
        adminGroup.save();

        user.addGroup(adminGroup);
        user.save();

    }

    final public LDAPUser loadUser(String userName)
        throws AccessControlException {
        Publication publication = getPublication();
        UserManager manager = UserManager.instance(publication);
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

    final public void testGetLdapId() throws AccessControlException {
        String userName = "felix";
        String ldapId = "m400032";
        createAndSaveUser(userName, "felix@wyona.com", ldapId);
        LDAPUser user = null;
        user = loadUser(userName);
        assertNotNull(user);
        assertEquals(ldapId, user.getLdapId());
    }

    final public void testSetLdapId() throws AccessControlException {
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

    final public void testSave() throws AccessControlException {
        String userName = "felix";
        createAndSaveUser(userName, "felix@wyona.com", "m400032");
        User user = null;
        user = loadUser(userName);
        assertNotNull(user);
    }

    final public void testDelete() {
        //TODO Implement delete().
    }

}
