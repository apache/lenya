/*
 * $Id: GroupManager.java,v 1.4 2003/06/25 14:37:07 andreas Exp $
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Category;

/**
 * @author egli
 * 
 * 
 */
public final class GroupManager extends ItemManager {
	private static Category log = Category.getInstance(GroupManager.class);

	protected static final String SUFFIX = ".gml";

	private static Map instances = new HashMap();
    

	/**
	 * Create a GroupManager
	 * 
	 * @param configurationDirectory for which the GroupManager is to be created
	 * @throws AccessControlException if no GroupManager could be instanciated
	 */
	private GroupManager(File configurationDirectory)
		throws AccessControlException {
		super(configurationDirectory);
	}

	/**
	 * Return the <code>GroupManager</code> for the given publication.
	 * The <code>GroupManager</code> is a singleton.
	 * 
	 * @param configurationDirectory for which the GroupManager is requested
	 * @return a <code>GroupManager</code>
	 * @throws AccessControlException if no GroupManager could be instanciated
	 */
	public static GroupManager instance(File configurationDirectory)
		throws AccessControlException {
        assert configurationDirectory != null;
		if (!instances.containsKey(configurationDirectory))
			instances.put(configurationDirectory, new GroupManager(configurationDirectory));
		return (GroupManager) instances.get(configurationDirectory);
	}

	/**
	 * Get all groups
	 * 
	 * @return an <code>Iterator</code>
	 */
	public Iterator getGroups() {
		return super.getItems();
	}

	/**
	 * Add a group to this manager
	 * 
	 * @param group the group to be added
	 */
	public void add(Group group) {
		super.add(group);
	}
	
	/**
	 * Remove a group from this manager
	 * 
	 * @param group the group to be removed
	 */
	public void remove(Group group) {
		super.remove(group);
	}
	
	/**
	 * Get the group with the given group name.
	 *  
	 * @param groupName the name of the requested group
	 * @return a <code>Group</code> or null if there is no group with the given name
	 */
	public Group getGroup(String groupName) {
		Group group = null;
		Iterator iter = getGroups();
		while (iter.hasNext()) {
			Group element = (Group) iter.next();
			if (element.getName().equals(groupName)) {
				group = element;
			}
		}
		return group;
	}
	
	/**
	 * Get a Filefilter that returns only group files
	 * 
	 * @return a <code>FileFilter</code>
	 */
	protected FileFilter getFileFilter() {
		FileFilter filter = new FileFilter() {

			public boolean accept(File pathname) {
				return (pathname.getName().endsWith(SUFFIX));
			}
		};
		return filter;
	}

}
