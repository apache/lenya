/*
 * $Id: RoleManager.java,v 1.2 2003/06/24 16:50:10 egli Exp $
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
     * Create a RoleManager
     * 
     * @param publication for which the RoleManager is to be created
     * @throws AccessControlException if the RoleManager could not be instantiated
     */
    protected RoleManager(Publication publication)
        throws AccessControlException {
        super(publication);
    }

	/**
	 * Return the <code>RoleManager</code> for this publication. 
	 * The <code>RoleManager</code> is a singleton.
	 * 
	 * @param publication the <code>Publication</code> for which the RoleManager is requested.
	 * @return a <code>RoleManager</code>
	 * @throws AccessControlException if the <code>RoleManager<code> could not be instantiated
	 */
    public static RoleManager instance(Publication publication)
        throws AccessControlException {
        if (!instances.containsKey(publication))
            instances.put(publication, new RoleManager(publication));
        return (RoleManager) instances.get(publication);
    }

    /**
     * Get all roles
     * 
     * @return an <code>Iterator</code>
     */
    public Iterator getRoles() {
        return super.getItems();
    }

    /**
     * Add a role
     * 
     * @param role Role to add
     */
    public void add(Role role) {
        super.add(role);
    }

    /**
     * Remove a role
     * 
     * @param role Role to remove
     */
    public void remove(Role role) {
        super.remove(role);
    }

    /**
     * Get the role for the given roleName
     * 
     * @param roleName the name of the role requested
     * @return a <code>Role</code> or null if no role with the given name found
     */
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

    /**
     * Get a file filter for role files
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
