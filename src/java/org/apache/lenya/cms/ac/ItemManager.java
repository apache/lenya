/*
 * $Id: ItemManager.java,v 1.7 2003/06/25 14:37:07 andreas Exp $
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Category;

/**
 * Abstract superclass for classes that manage items loaded from configuration files.
 * @author egli
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public abstract class ItemManager {
	private static final Category log = Category.getInstance(ItemManager.class);

	public static final String PATH =
		"config" + File.separator + "ac" + File.separator + "passwd";

	private Set items = null;
    private File configurationDirectory;  

	/**
	 * Create a new ItemManager
	 * 
	 * @param configurationDirectory where the items are fetched from
	 * @throws AccessControlException if the item manager cannot be instantiated
	 */
	protected ItemManager(File configurationDirectory)
		throws AccessControlException {
            
        assert configurationDirectory != null;
            
        if (!configurationDirectory.exists()
            || !configurationDirectory.isDirectory()) {
            throw new AccessControlException(
                "The directory [" + configurationDirectory.getAbsolutePath()
                + "] does not exist!");
        }

		this.configurationDirectory = configurationDirectory;
		
		File[] itemFiles = configurationDirectory.listFiles(getFileFilter());
		items = new HashSet();
		Configuration config = null;
		for (int i = 0; i < itemFiles.length; i++) {
			DefaultConfigurationBuilder builder =
				new DefaultConfigurationBuilder();
			try {
                assert itemFiles[i].exists(); 
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
			Item item = null;
			try {
				item = (Item) Class.forName(klass).newInstance();
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
            item.setConfigurationDirectory(configurationDirectory);
            try {
                item.configure(config);
            } catch (ConfigurationException e) {
                String errorMsg =
                    "Exception when trying to configure: "
                        + klass;
                throw new AccessControlException(errorMsg, e);
            }
			items.add(item);
		}
	}

	/**
	 * get all items
	 * 
	 * @return an <code>Iterator</code>
	 */
	public Iterator getItems() {
		return items.iterator();
	}

	/**
	 * Add an Item to this manager
	 * 
	 * @param item to be added
	 */
	public void add(Object item) {
		items.add(item);
	}

	/**
	 * 
	 * Remove an item from this manager
	 * 
	 * @param item to be removed
	 */
	public void remove(Object item) {
		items.remove(item);
	}
	
    /**
     * Get the directory where the items are located.
     * 
     * @return a <code>File</code>
     */
	public File getConfigurationDirectory() {
		return configurationDirectory;
	}
	
	/**
	 * Get a file filter which filters for files containing items.
	 *  
	 * @return a <code>FileFilter</code>
	 */
	protected abstract FileFilter getFileFilter();
	

}
