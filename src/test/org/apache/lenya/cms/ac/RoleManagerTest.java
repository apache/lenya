/*
 * $Id: RoleManagerTest.java,v 1.3 2003/06/25 14:55:10 andreas Exp $
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
import java.util.Iterator;

import org.apache.lenya.cms.PublicationHelper;

/**
 * @author egli
 * 
 * 
 */
public class RoleManagerTest extends AccessControlTest {

	/**
	 * Constructor for RoleManagerTest.
	 * @param arg0
	 */
	public RoleManagerTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
		junit.textui.TestRunner.run(RoleManagerTest.class);
	}
	
	final public void testInstance() throws AccessControlException {
		File configDir = getConfigurationDirectory();
		RoleManager manager = RoleManager.instance(configDir);
		assertNotNull(manager);
		RoleManager anotherManager = RoleManager.instance(configDir);
		assertNotNull(anotherManager);
		assertEquals(manager, anotherManager);
	}

	final public void testGetRoles() {
	}

	/*
	 * Test for void add(Role)
	 */
	final public void testAddRole() throws AccessControlException {
        File configDir = getConfigurationDirectory();
		String name = "test";
		Role role = new Role(name);
		RoleManager manager = null;
		manager = RoleManager.instance(configDir);
		assertNotNull(manager);
		manager.add(role);

		assertTrue(manager.getRoles().hasNext());	
	}

	/*
	 * Test for void remove(Role)
	 */
	final public void testRemoveRole() {
        File configDir = getConfigurationDirectory();
		String name = "test2";
		Role role = new Role(name);
		RoleManager manager = null;
		try {
			manager = RoleManager.instance(configDir);
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
