/*
 * $Id: FileUserTest.java,v 1.2 2003/06/05 11:59:21 egli Exp $
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
import java.util.Iterator;
import java.util.Map;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

import junit.framework.TestCase;

/**
 * @author egli
 * 
 * 
 */
public class FileUserTest extends TestCase {

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

	final public Publication getPublication() {
		String publicationId = "default";
		String servletContextPath = "/home/egli/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya/";
		return PublicationFactory.getPublication(publicationId, servletContextPath);
	}
	
	final public Map getRoles() {
		return roles;
	}
	
	final public Map getGroups() {
		return groups;
	}
	
	final public FileUser createAndSaveUser(
		String name,
		String fullName,
		String email,
		String password) {

		Publication publication = getPublication();
		String userName = "alice";
		String editorGroupName = "editorGroup";
		String adminGroupName = "adminGroup";
		String editorRoleName = "editorRole";
		String adminRoleName = "adminRole";

		FileRole editorRole = new FileRole(publication, editorRoleName);
		FileRole adminRole = new FileRole(publication, adminRoleName);
		this.roles.put(editorRoleName, editorRole);
		this.roles.put(adminRoleName, adminRole);
		
		FileGroup editorGroup = new FileGroup(publication, editorGroupName);
		FileGroup adminGroup = new FileGroup(publication, adminGroupName);
		this.groups.put(editorGroupName, editorGroup);
		this.groups.put(adminGroupName, adminGroup);		
		
		FileUser user = new FileUser(publication, userName, fullName, email, password);
		
		try {
			editorRole.save();
			adminRole.save();
		} catch (AccessControlException e5) {
			e5.printStackTrace();
		}
		editorGroup.addRole(editorRole);
		user.addGroup(editorGroup);
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
		return user;
	}
	
	final public FileUser loadUser(String userName) throws AccessControlException {
		Publication publication = getPublication();
		UserManager manager = UserManager.instance(publication);
		return (FileUser) manager.getUser(userName);
	}
	
	final public void testSave() {
		Publication publication = getPublication();
		String userName = "alice";
		createAndSaveUser(
			userName,
			"Alice Wonderland",
			"alice@wonderland.org",
			"secret");
		File xmlFile =
			new File(
				publication.getDirectory(),
				ItemManager.PATH + File.separator + userName + ".iml");
		assertTrue(xmlFile.exists());
	}

	/*
	 * Test for void FileUser(Publication, Configuration)
	 */
	final public void testFileUserPublicationConfiguration() {
		//TODO Implement FileUser().
	}

	final public void testGetEmail() {
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

	final public void testGetFullName() {
		String userName = "alice";
		String fullName = "Alice Wonderland";
		FileUser user =
			createAndSaveUser(
				userName,
				fullName,
				"alice@wonderland.org",
				"secret");
		assertTrue(user.getFullName().equals(fullName));
		try {
			user = loadUser(userName);
		} catch (AccessControlException e) {
			e.printStackTrace();
		}
		assertTrue(user.getFullName().equals(fullName));
	}

	final public void testGetGroups() {
		FileUser user =
			createAndSaveUser(
				"alice",
				"Alice Wonderland",
				"alice@wonderland.org",
				"secret");
		int groupCount = 0;
		for (Iterator groups = user.getGroups(); groups.hasNext();) {
			Group group = (Group) groups.next();
			groupCount += 1;
			assertTrue(getGroups().containsKey(group.getName()));
		}
		assertEquals(groupCount, getGroups().size());
	}

	final public void testGetId() {
		String id = "alice";
		FileUser user =
			createAndSaveUser(
				id,
				"Alice Wonderland",
				"alice@wonderland.org",
				"secret");
		assertTrue(user.getId().equals(id));
	}
}
