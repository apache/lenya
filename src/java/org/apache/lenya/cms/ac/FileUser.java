/*
 * $Id: FileUser.java,v 1.9 2003/06/06 13:58:49 egli Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
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
 *    includes software developed by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor
 *    may "lenya" appear in their names without prior written permission
 *    of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment: "This product includes software developed by lenya
 *    (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS
 * OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * lenya WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS A RESULT
 * OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS BEEN
 * ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE
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
import org.apache.log4j.Category;

/**
 * @author egli
 * 
 * 
 */
public class FileUser extends User {
	private Category log = Category.getInstance(FileUser.class);

	public static final String ID = "identity";
	public static final String FULL_NAME = "fullname";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final String GROUPS = "groups";
	public static final String GROUP = "group";
	public static final String PASSWORD_ATTRIBUTE = "type";
	public static final String ID_ATTRIBUTE = "id";
	public static final String CLASS_ATTRIBUTE = "class";

	private Publication publication;

	/**
	 * @param id
	 */
	public FileUser(Publication publication, String id) {
		super(id);
		this.publication = publication;
	}

	/**
	 * @param publication
	 * @param id
	 * @param fullName
	 * @param email
	 * @param password
	 */
	public FileUser(
		Publication publication,
		String id,
		String fullName,
		String email,
		String password) {
		super(id, fullName, email, password);
		this.publication = publication;
	}

	public FileUser(Publication publication, Configuration config)
		throws ConfigurationException {
		id = config.getAttribute(ID_ATTRIBUTE);
		setEmail(config.getChild(EMAIL).getValue());
		setFullName(config.getChild(FULL_NAME).getValue());
		setPassword(config.getChild(PASSWORD).getValue());
		Configuration[] groups = config.getChildren(GROUPS);
		if (groups.length == 1) {
			groups = groups[0].getChildren(GROUP);

			GroupManager manager = null;
			try {
				manager = GroupManager.instance(publication);
			} catch (AccessControlException e) {
				throw new ConfigurationException(
					"Exception when trying to fetch GroupManager for publication: "
						+ publication,
					e);
			}
			for (int i = 0; i < groups.length; i++) {
				String groupName = groups[i].getValue();
				Group group = manager.getGroup(groupName);
				if (group != null) {
					addGroup(group);
				} else {
					log.error(
						"Couldn't find Group for group name: " + groupName);
				}
			}
		} else {
			// strange, it should have groups
			log.error(
				"User "
					+ config.getAttribute(ID_ATTRIBUTE)
					+ " doesn't seem to have any groups");
		}
	}

	/**
	 * @return
	 */
	protected Configuration createConfiguration() {

		DefaultConfiguration config = new DefaultConfiguration(ID);
		config.setAttribute(ID_ATTRIBUTE, id);
		config.setAttribute(CLASS_ATTRIBUTE, this.getClass().getName());
		DefaultConfiguration child = null;
		// add fullname node
		child = new DefaultConfiguration(FULL_NAME);
		child.setValue(fullName);
		config.addChild(child);
		// add email node
		child = new DefaultConfiguration(EMAIL);
		child.setValue(email);
		config.addChild(child);
		// add email
		child = new DefaultConfiguration(PASSWORD);
		child.setValue(email);
		child.setAttribute(PASSWORD_ATTRIBUTE, "md5");
		config.addChild(child);
		// add group nodes
		child = new DefaultConfiguration(GROUPS);
		config.addChild(child);

		Iterator groupsIter = getGroups();
		while (groupsIter.hasNext()) {
			DefaultConfiguration groupNode = new DefaultConfiguration(GROUP);
			groupNode.setValue(((Group) groupsIter.next()).getName());
			child.addChild(groupNode);
		}
		return config;
	}

	/* (non-Javadoc)
	 * @see org.apache.lenya.cms.ac.User#save()
	 */
	public void save() throws AccessControlException {
		DefaultConfigurationSerializer serializer =
			new DefaultConfigurationSerializer();
		Configuration config = createConfiguration();
		File xmlPath = UserManager.instance(publication).getPath();
		File xmlfile = new File(xmlPath, getId() + UserManager.SUFFIX);
		try {
			serializer.serializeToFile(xmlfile, config);
		} catch (Exception e) {
			throw new AccessControlException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.lenya.cms.ac.User#delete()
	 */
	public void delete() throws AccessControlException {
		super.delete();

		UserManager manager = UserManager.instance(publication);

		manager.remove(this);
		File xmlPath = manager.getPath();
		File xmlfile = new File(xmlPath, getId() + UserManager.SUFFIX);
		xmlfile.delete();
	}

}
