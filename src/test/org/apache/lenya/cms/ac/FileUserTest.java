/*
 * $Id: FileUserTest.java,v 1.6 2003/06/25 14:55:10 andreas Exp $
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.cms.PublicationHelper;

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

	final public Map getRoles() {
		return roles;
	}

	final public Map getGroups() {
		return groups;
	}

	final public FileUser createAndSaveUser(
		String userName,
		String fullName,
		String email,
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

		FileUser user =
			new FileUser(configDir, userName, fullName, email, password);
            
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

	final public FileUser loadUser(String userName)
		throws AccessControlException {
        File configDir = getConfigurationDirectory();
		UserManager manager = UserManager.instance(configDir);
		return (FileUser) manager.getUser(userName);
	}

	final public void testSave() throws AccessControlException {
		String userName = "alice";
		createAndSaveUser(
			userName,
			"Alice Wonderland",
			"alice@wonderland.org",
			"secret");
        File configDir = getConfigurationDirectory();
		File xmlFile =
			new File(configDir, userName + ".iml");
		assertTrue(xmlFile.exists());
	}

	final public void testGetEmail() throws AccessControlException {
		String userName = "alice";
		String email = "alice@wonderland.org";
		FileUser user =
			createAndSaveUser(userName, "Alice Wonderland", email, "secret");
		assertTrue(user.getEmail().equals(email));
		try {
			user = loadUser(userName);
		} catch (AccessControlException e) {
			e.printStackTrace();
		}
		assertTrue(user.getEmail().equals(email));
	}

	final public void testGetFullName() throws AccessControlException {
		String userName = "alice";
		String fullName = "Alice Wonderland";
		FileUser user =
			createAndSaveUser(
				userName,
				fullName,
				"alice@wonderland.org",
				"secret");
		assertTrue(user.getFullName().equals(fullName));
        user = loadUser(userName);
		assertTrue(user.getFullName().equals(fullName));
	}

	final public void testGetGroups() throws AccessControlException {
		FileUser user =
			createAndSaveUser(
				"alice",
				"Alice Wonderland",
				"alice@wonderland.org",
				"secret");
		int groupCount = 0;
        Group groups[] = user.getGroups();
        
		for (int i = 0; i < groups.length; i++) {
			groupCount += 1;
			assertTrue(getGroups().containsKey(groups[i].getName()));
		}
		assertEquals(groupCount, getGroups().size());
	}

	final public void testGetId() throws AccessControlException {
		String id = "alice";
		FileUser user =
			createAndSaveUser(
				id,
				"Alice Wonderland",
				"alice@wonderland.org",
				"secret");
		assertTrue(user.getId().equals(id));
	}

	final public void testDelete() throws AccessControlException {
		String id = "albert";
		FileUser user =
			createAndSaveUser(
				id,
				"Albert Einstein",
				"albert@physics.org",
				"secret");
        File configDir = getConfigurationDirectory();
		UserManager manager = null;
        manager = UserManager.instance(configDir);
		assertNotNull(manager);

		assertNotNull(manager.getUser(id));
        user.delete();
		assertNull(manager.getUser(id));
	}

	final public void testAuthenticate() throws AccessControlException {
		String password = "daisy";
		FileUser user =
			createAndSaveUser(
				"mickey",
				"Mickey Mouse",
				"mickey@mouse.com",
				password);
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
