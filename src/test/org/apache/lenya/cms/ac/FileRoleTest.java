/*
 * $Id: FileRoleTest.java,v 1.1 2003/06/03 13:52:12 egli Exp $
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

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

import junit.framework.TestCase;

/**
 * @author egli
 * 
 * 
 */
public class FileRoleTest extends TestCase {

	/**
	 * Constructor for FileRoleTest.
	 * @param arg0
	 */
	public FileRoleTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(FileRoleTest.class);
	}

	final public Publication getPublication() {
		String publicationId = "default";
		String servletContext = "/home/egli/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya/";
		Publication pub = PublicationFactory.getPublication(publicationId, servletContext);
		return pub;		
	}
	
	final public void testFileRole() {
		String name = "test";
		Publication pub = getPublication();
		FileRole role = new FileRole(pub, name);
		try {
			role.save();
		} catch (AccessControlException e) {
			e.printStackTrace();
		}
		File path = null;
		try {
			path = RoleManager.instance(pub).getPath();
		} catch (AccessControlException e1) {
			e1.printStackTrace();
		}
		File roleFile = new File(path, name + RoleManager.SUFFIX);
		assertNotNull(roleFile);
		assertTrue(roleFile.exists());	
	}

	final public void testSave() {
		String name = "test";
		String publicationId = "default";
		String servletContext = "/home/egli/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya/";
		Publication pub = PublicationFactory.getPublication(publicationId, servletContext);
		FileRole role = new FileRole(pub, name);
		try {
			role.save();
		} catch (AccessControlException e) {
			e.printStackTrace();
		}
		File path = null;
		try {
			path = RoleManager.instance(pub).getPath();
		} catch (AccessControlException e1) {
			e1.printStackTrace();
		}
		File roleFile = new File(path, name + RoleManager.SUFFIX);
		assertNotNull(roleFile);
		assertTrue(roleFile.exists());
	}

	final public void testGetName() {
		String name = "test";
		Publication pub = getPublication();
		FileRole role = new FileRole(pub, name);
		assertTrue(role.getName().equals(name));
	}

	/*
	 * Test for boolean equals(Object)
	 */
	final public void testEqualsObject() {
		String name = "test";
		Publication pub = getPublication();
		FileRole role1 = new FileRole(pub, name);
		FileRole role2 = new FileRole(pub, name);
		assertEquals(role1, role2);
	}
}
