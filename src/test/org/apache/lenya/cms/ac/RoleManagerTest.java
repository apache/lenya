/*
 * $Id: RoleManagerTest.java,v 1.2 2003/06/11 12:02:39 egli Exp $
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

import java.util.Iterator;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

import junit.framework.TestCase;

/**
 * @author egli
 * 
 * 
 */
public class RoleManagerTest extends TestCase {

	/**
	 * Constructor for RoleManagerTest.
	 * @param arg0
	 */
	public RoleManagerTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(RoleManagerTest.class);
	}
	
	final public Publication getPublication() {
		String publicationId = "default";
		String servletContext = "/home/egli/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya/";
		Publication pub = PublicationFactory.getPublication(publicationId, servletContext);
		return pub;
	}

	final public void testInstance() {
		Publication pub = getPublication();
		RoleManager manager = null;
		try {
			manager = RoleManager.instance(pub);
		} catch (AccessControlException e) {
			e.printStackTrace();
		}
		assertNotNull(manager);
		RoleManager anotherManager = null;
		try {
			anotherManager = RoleManager.instance(pub);
		} catch (AccessControlException e1) {
			e1.printStackTrace();
		}
		assertNotNull(anotherManager);
		assertEquals(manager, anotherManager);
	}

	final public void testGetRoles() {
	}

	/*
	 * Test for void add(Role)
	 */
	final public void testAddRole() {
		Publication pub = getPublication();
		String name = "test";
		Role role = new Role(name);
		RoleManager manager = null;
		try {
			manager = RoleManager.instance(pub);
		} catch (AccessControlException e) {
			e.printStackTrace();
		}
		assertNotNull(manager);
		manager.add(role);

		assertTrue(manager.getRoles().hasNext());	
	}

	/*
	 * Test for void remove(Role)
	 */
	final public void testRemoveRole() {
		Publication pub = getPublication();
		String name = "test2";
		Role role = new Role(name);
		RoleManager manager = null;
		try {
			manager = RoleManager.instance(pub);
		} catch (AccessControlException e) {
			e.printStackTrace();
		}
		assertNotNull(manager);
		int roleCountBefore = 0;
		for (Iterator iter = manager.getRoles(); iter.hasNext();) {
			roleCountBefore += 1;
			iter.next();
		}
		manager.add(role);
		manager.remove(role);
		int roleCountAfter = 0;
		for (Iterator iter = manager.getRoles(); iter.hasNext();) {
			roleCountAfter += 1;	
			iter.next();
		}
		assertEquals(roleCountBefore, roleCountAfter);
	}

}
