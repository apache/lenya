/*
 * $Id: FileRole.java,v 1.4 2003/06/25 08:56:32 egli Exp $
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
import org.apache.lenya.cms.publication.Publication;

/**
 * @author egli
 * 
 * 
 */
public class FileRole extends Role {

	public static final String GROUP = "role";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String CLASS_ATTRIBUTE = "class";

	private Publication publication;
	
	/**
	 * Create a new instance of <code>FileRole</code>
	 * 
	 * @param publication to which the role is to be attached
	 * @param name name of the role
	 */
	public FileRole(Publication publication, String name) {
		super(name);
		this.publication = publication;
	}

	/**
	 * Create a new instance of <code>FileRole</code>
	 * 
	 * @param publication to which the role is to be attached
	 * @param config containing the role details
	 * @throws ConfigurationException if the <code>FileRole</code> could not be instantiated
	 */
	public FileRole(Publication publication, Configuration config) throws ConfigurationException {
		super(config.getAttribute(NAME_ATTRIBUTE));
		this.publication = publication;
	}
	
	/**
	 * Save the role
	 * 
	 * @throws AccessControlException if the save fails
	 */
	public void save() throws AccessControlException {
		DefaultConfigurationSerializer serializer =
			new DefaultConfigurationSerializer();
		Configuration config = createConfiguration();
		File xmlPath = RoleManager.instance(publication).getPath();
		File xmlfile = new File(xmlPath, getName() + RoleManager.SUFFIX);
		try {
			serializer.serializeToFile(xmlfile, config);
		} catch (Exception e) {
			throw new AccessControlException(e);
		}
	}

	/**
	 * Create a configuration containing the role details
	 * 
	 * @return a <code>Configuration</code>
	 */
	private Configuration createConfiguration() {
		
		DefaultConfiguration config = new DefaultConfiguration(GROUP);
		config.setAttribute(NAME_ATTRIBUTE, getName());
		config.setAttribute(CLASS_ATTRIBUTE, this.getClass().getName());
		return config;
	}

}
