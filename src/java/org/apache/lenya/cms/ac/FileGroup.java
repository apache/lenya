/*
 * $Id: FileGroup.java,v 1.2 2003/06/02 17:15:00 egli Exp $
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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.lenya.cms.publication.Publication;

/**
 * @author egli
 * 
 * 
 */
public class FileGroup extends Group {
	
	public static final String GROUP = "group";
	public static final String ROLES = "roles";
	public static final String ROLE = "role";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String CLASS_ATTRIBUTE = "class";

	public FileGroup(Configuration config) throws ConfigurationException {
		name = config.getAttribute(name);
		Configuration[] groups = config.getChildren(ROLES);
		//		for (int i = 0; i < groups.length; i++) {
		//			Configuration group = groups[i];
		//		}		
	}
	
	public void save(Publication publication) throws AccessControlException {
		DefaultConfigurationSerializer serializer =
			new DefaultConfigurationSerializer();
		Configuration config = createConfiguration();
		File xmlPath = GroupManager.instance(publication).getPath();
		File xmlfile = new File(xmlPath, name + GroupManager.SUFFIX);
		try {
			serializer.serializeToFile(xmlfile, config);
		} catch (Exception e) {
			throw new AccessControlException(e);
		}
	}

	/**
	 * @return
	 */
	private Configuration createConfiguration() {
		
		DefaultConfiguration config = new DefaultConfiguration(GROUP);
		config.setAttribute(NAME_ATTRIBUTE, name);
		config.setAttribute(CLASS_ATTRIBUTE, this.getClass().getName());
		DefaultConfiguration child = null;
		// add roles node
		child = new DefaultConfiguration(GROUP);
		config.addChild(child);		

		Iterator groupsIter = getRoles();
		while (groupsIter.hasNext()) {
			DefaultConfiguration groupNode = new DefaultConfiguration(ROLE);
			groupNode.setValue(((Group) groupsIter.next()).getName());
			child.addChild(groupNode);
		}
		return config;
	}

}
