/*
 * $Id: FileGroup.java,v 1.6 2003/06/25 14:38:29 andreas Exp $
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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.log4j.Category;

/**
 * @author egli
 * 
 * 
 */
public class FileGroup extends Group implements Item {
	private static Category log = Category.getInstance(FileGroup.class);
	
	public static final String GROUP = "group";
	public static final String ROLES = "roles";
	public static final String ROLE = "role";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String CLASS_ATTRIBUTE = "class";
    
    /**
     * Creates a new FileGroup object.
     */
    public FileGroup() {
    }

    /**	
     * Create a new instance of <code>FileGroup</code>
     * @param configurationDirectory to which the group will be attached to
     * @param name the name of the group
	 */
	public FileGroup(File configurationDirectory, String name) {
		super(name);
        setConfigurationDirectory(configurationDirectory);
	}

    /**
     * Configures this file group.
     * @param config The configuration.
     * @throws ConfigurationException when something went wrong.
     */
	public void configure(Configuration config)
		throws ConfigurationException {
            
		setName(config.getAttribute(NAME_ATTRIBUTE));

		Configuration[] rolesConfig = config.getChildren(ROLES);
		if (rolesConfig.length == 1) {
			Configuration[] roles = rolesConfig[0].getChildren(ROLE);
			for (int i = 0; i < roles.length; i++) {
				String roleName = roles[i].getValue();
				RoleManager manager = null;
				try {
					manager = RoleManager.instance(getConfigurationDirectory());
				} catch (AccessControlException e) {
					throw new ConfigurationException(
						"Exception when trying to fetch RoleManager for publication: "
							+ getConfigurationDirectory(),
						e);
				}
			}

		} else {
			// the Group should have a Roles node
			log.error(
				"The groups "
					+ config.getAttribute(NAME_ATTRIBUTE)
					+ "doesn't appear to have the mandatory roles node");
		}
	}
	
	/**
	 * Save this group
	 * 
	 * @throws AccessControlException if the save failed
	 */
	public void save() throws AccessControlException {
		DefaultConfigurationSerializer serializer =
			new DefaultConfigurationSerializer();
		Configuration config = createConfiguration();
		File xmlPath = getConfigurationDirectory();
		File xmlfile = new File(xmlPath, getName() + GroupManager.SUFFIX);
		try {
			serializer.serializeToFile(xmlfile, config);
		} catch (Exception e) {
			throw new AccessControlException(e);
		}
	}

	/**
	 * Create a configuration containing the group details
	 * 
	 * @return a <code>Configuration</code>
	 */
	private Configuration createConfiguration() {
		
		DefaultConfiguration config = new DefaultConfiguration(GROUP);
		config.setAttribute(NAME_ATTRIBUTE, getName());
		config.setAttribute(CLASS_ATTRIBUTE, this.getClass().getName());
		DefaultConfiguration child = null;
		// add roles node
		child = new DefaultConfiguration(ROLES);
		config.addChild(child);		

		return config;
	}
    
    private File configurationDirectory;
    
    /**
     * Returns the configuration directory.
     * @return A file object.
     */
    protected File getConfigurationDirectory() {
        return configurationDirectory;
    }

    /**
     * @see org.apache.lenya.cms.ac.Item#setConfigurationDirectory(java.io.File)
     */
    public void setConfigurationDirectory(File configurationDirectory) {
        assert configurationDirectory != null
            && configurationDirectory.isDirectory();
        this.configurationDirectory = configurationDirectory;
    }

}
