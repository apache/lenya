/*
 * $Id: FileGroupTest.java,v 1.2 2003/06/25 14:55:10 andreas Exp $
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
import java.io.IOException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.cms.PublicationHelper;
import org.xml.sax.SAXException;

/**
 * @author egli
 * 
 * 
 */
public class FileGroupTest extends AccessControlTest {

	/**
	 * Constructor for FileGroupTest.
	 * @param arg0
	 */
	public FileGroupTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
		junit.textui.TestRunner.run(FileGroupTest.class);
	}

	final public void testFileGroup() throws AccessControlException, ConfigurationException, SAXException, IOException {
		String groupName = "testGroup";
		String roleName = "testRole";
        File configurationDirectory = getConfigurationDirectory();
        
        System.out.println("Configuration directory: " + configurationDirectory);
        
		FileGroup group = new FileGroup(configurationDirectory, groupName);
		FileRole role = new FileRole(configurationDirectory, roleName);
//		group.addRole(role);

        role.save();
        group.save();
        
        File path = null;
        path = RoleManager.instance(configurationDirectory).getConfigurationDirectory();
        
		File groupFile = new File(path, groupName + GroupManager.SUFFIX);
		assertNotNull(groupFile);
		assertTrue(groupFile.exists());
		Configuration config = null;
        config = new DefaultConfigurationBuilder().buildFromFile(groupFile);
		assertNotNull(config);
        
		FileGroup newGroup = null;
    	newGroup = new FileGroup();
        newGroup.setConfigurationDirectory(configurationDirectory);
        newGroup.configure(config);
		assertNotNull(newGroup);
        
		assertTrue(newGroup.getName().equals(groupName));
        
        /*
		int roleCount = 0;
		for (Iterator roles = newGroup.getRoles(); roles.hasNext();) {
			Role newRole = (Role) roles.next();
			roleCount = roleCount + 1;
			assertTrue(newRole.getName().equals(roleName));	
		}
		assertEquals(1, roleCount);
        */
	}

}
