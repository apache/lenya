/*
 * $Id: RoleManager.java,v 1.1 2003/06/02 17:17:37 egli Exp $
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

import org.apache.lenya.cms.publication.Publication;
import org.apache.log4j.Category;

/**
 * @author egli
 * 
 * 
 */
public class RoleManager extends ItemManager {
	private static Category log = Category.getInstance(RoleManager.class);

	protected static final String SUFFIX = ".rml";

	private static Map instances = new HashMap();

	/**
	 * @param publication
	 * @throws AccessControlException
	 */
	private RoleManager(Publication publication)
		throws AccessControlException {
		super(publication);
	}


	public static RoleManager instance(Publication publication)
		throws AccessControlException {
		if (!instances.containsKey(publication))
			instances.put(publication, new RoleManager(publication));
		return (RoleManager) instances.get(publication);
	}

	public Iterator getRoles() {
		return super.getItems();
	}

	public void add(Role role) {
		super.add(role);
	}

	public void remove(Role role) {
		super.remove(role);
	}

	public Role getRole(String roleName) {
		Role role = null;
		Iterator iter = getRoles();
		while (iter.hasNext()) {
			Role element = (Role) iter.next();
			if (element.getName().equals(roleName)) {
				role = element;
			}
		}
		return role;
	}
	

   /* (non-Javadoc)
	 * @see org.apache.lenya.cms.ac.ItemManager#getFileFilter()
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
