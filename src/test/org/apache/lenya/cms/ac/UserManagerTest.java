/*
 * $Id: UserManagerTest.java,v 1.5 2003/06/11 14:59:11 egli Exp $
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
public class UserManagerTest extends TestCase {

	/**
	 * Constructor for UserManagerTest.
	 * @param arg0
	 */
	public UserManagerTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(UserManagerTest.class);
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

	final public void testInstance() {

		Publication publication = getPublication();
		UserManager manager = null;
		try {
			manager = (UserManager) UserManager.instance(publication);
		} catch (AccessControlException e) {
		}
		assertNotNull(manager);
	}

	final public void testLoadConfig() {
		Publication publication = getPublication();
		String userName = "alice";
		String editorGroupName = "editorGroup";
		String adminGroupName = "adminGroup";
		String editorRoleName = "editorRole";
		String adminRoleName = "adminRole";

		FileRole editorRole = new FileRole(publication, editorRoleName);
		FileRole adminRole = new FileRole(publication, adminRoleName);

		User user =
			new FileUser(
				publication,
				userName,
				"Alice in Wonderland",
				"alice@test.com",
				"secret");

		try {
			editorRole.save();
			adminRole.save();
		} catch (AccessControlException e5) {
			e5.printStackTrace();
		}
		FileGroup editorGroup = new FileGroup(publication, editorGroupName);
		editorGroup.addRole(editorRole);
		user.addGroup(editorGroup);
		FileGroup adminGroup = new FileGroup(publication, adminGroupName);
		adminGroup.addRole(editorRole);
		adminGroup.addRole(adminRole);
		try {
			editorGroup.save();
			adminGroup.save();
		} catch (AccessControlException e2) {
			e2.printStackTrace();
		}
		user.addGroup(adminGroup);
		try {
			user.save();
		} catch (AccessControlException e3) {
			e3.printStackTrace();
		}

		UserManager userManager = null;
		GroupManager groupManager = null;
		try {
			userManager = UserManager.instance(publication);
		} catch (AccessControlException e) {
		}
		assertNotNull(userManager);

		try {
			groupManager = GroupManager.instance(publication);
		} catch (AccessControlException e4) {
			e4.printStackTrace();
		}
		assertNotNull(groupManager);

		Group fetchedGroup = groupManager.getGroup(editorGroupName);
		assertTrue(editorGroup.equals(fetchedGroup));

		fetchedGroup = groupManager.getGroup(adminGroupName);
		assertTrue(adminGroup.equals(fetchedGroup));
	}

	final public void testGetUser() {
		Publication publication = getPublication();
		UserManager manager = null;
		String userName = "test-user";
		FileUser user =
			new FileUser(
				publication,
				userName,
				"Alice in Wonderland",
				"alice@wonderland.com",
				"secret");
		try {
			manager = (UserManager) UserManager.instance(publication);
		} catch (AccessControlException e) {
		}
		assertNotNull(manager);
		manager.add(user);

		User otherUser = manager.getUser(userName);
		assertEquals(user, otherUser);
	}

}
