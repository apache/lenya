/*
 * $Id: ItemManager.java,v 1.2 2003/06/02 17:17:37 egli Exp $
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
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.log4j.Category;

/**
 * @author egli
 * 
 * 
 */
public abstract class ItemManager {
	static Category log = Category.getInstance(ItemManager.class);

	private static final String PATH =
		"config" + File.separator + "ac" + File.separator + "passwd";

	private Set items = null;
	private Publication publication = null;

	protected ItemManager(Publication publication)
		throws AccessControlException {

		this.publication = publication;
		
		File groupDir = new File(publication.getDirectory(), PATH);
		if (!groupDir.exists() || !groupDir.isDirectory()) {
			//			throw new Execption();
		}
		File[] itemFiles = groupDir.listFiles(getFileFilter());
		items = new HashSet();
		Configuration config = null;
		for (int i = 0; i < itemFiles.length; i++) {
			DefaultConfigurationBuilder builder =
				new DefaultConfigurationBuilder();
			try {
				config = builder.buildFromFile(itemFiles[i]);
			} catch (Exception e) {
				String errorMsg =
					"Exception when reading the configuration from file: "
						+ itemFiles[i].getName();
				// an exception occured when trying to read the configuration
				// from the identity file.
				log.error(errorMsg);
				throw new AccessControlException(errorMsg, e);
			}
			String klass = null;
			try {
				klass = config.getAttribute(FileUser.CLASS_ATTRIBUTE);
			} catch (ConfigurationException e) {
				String errorMsg = "Exception when extracting class name from identity file: " + klass + config.getAttributeNames();
				log.error(errorMsg);
				throw new AccessControlException(errorMsg, e);
			}
			Object item = null;
			try {
				Class[] constructorClasses = { Configuration.class };
				Constructor constructor = Class.forName(klass).getConstructor(constructorClasses);
				Object[] arguments = { config };
				item = constructor.newInstance(arguments);
			} catch (Exception e) {
				String errorMsg =
					"Exception when trying to instanciate: "
						+ klass
						+ " with exception: "
						+ e.fillInStackTrace();
				// an exception occured when trying to instanciate
				// a user.
				log.error(errorMsg);
				throw new AccessControlException(errorMsg, e);
			}
			items.add(item);
		}
	}


	public Iterator getItems() {
		return items.iterator();
	}

	public void add(Object item) {
		items.add(item);
	}

	public void remove(Object item) {
		items.remove(item);
	}
	
	public File getPath() {
		return new File(publication.getDirectory(), PATH);
	}
	
	protected abstract FileFilter getFileFilter();
	
	/**
	 * @return
	 */
	public Publication getPublication() {
		return publication;
	}

}
