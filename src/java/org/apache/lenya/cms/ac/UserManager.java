/*
 * $Id: UserManager.java,v 1.1 2003/05/30 09:38:59 egli Exp $
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
 *    includes software developed by Wyona (http://www.wyona.org)"
 *
 * 4. The name "Lenya" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "Lenya" nor
 *    may "Lenya" appear in their names without prior written permission
 *    of Wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment: "This product includes software developed by Wyona
 *    (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY Wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS
 * OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * Wyona WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS A RESULT
 * OF USING THIS SOFTWARE. IN NO EVENT WILL Wyona BE LIABLE FOR ANY SPECIAL,
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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.log4j.Category;

/**
 * @author egli
 * 
 * 
 */
public class UserManager {
	static Category log = Category.getInstance(UserManager.class);

	private static final String IDENTITY_PATH =
		"config" + File.separator + "ac" + File.separator + "passwd";

	private static HashMap instances = new HashMap();
	
	protected List users = null;

	private UserManager(Publication publication) {

		File identityDir = new File(publication.getDirectory(), IDENTITY_PATH);
		if (!identityDir.exists() || !identityDir.isDirectory()) {
			//			throw new Execption();
		}
		File[] userFiles = identityDir.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				return (pathname.getName().endsWith(".iml"));
			}
		});
		users = new ArrayList();
		Configuration config = null;
		for (int i = 0; i < userFiles.length; i++) {
			DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
			try {
				config = builder.buildFromFile(userFiles[i]);
			} catch (Exception e) {
				log.error(
					// an exception occured when trying to read the configuration
					// from the identity file. Log and continue without
					// this user.
					"Exception when reading the configuration from file: "
						+ userFiles[i].getName());
				continue;
			}
			String klass = config.getValue(FileUser.CLASS_ATTRIBUTE);
			User user = null;
			try {
				user = (User) Class.forName(klass).newInstance();
			} catch (Exception e) {
				// an exception occured when trying to instanciate
				// a user. Log an continue without this user.
				log.error(
					"Exception when trying to instanciate: "
						+ klass
						+ " with exception: "
						+ e.getMessage());
				continue;
			}
			user.configure(config);
			users.add(user);
		}
	}

	public UserManager instance(Publication publication) {
		if (!instances.containsKey(publication))
			instances.put(publication, new UserManager(publication));
		return (UserManager) instances.get(publication);
	}
	
	public Iterator getUsers() {
		return users.iterator();
	}
}
