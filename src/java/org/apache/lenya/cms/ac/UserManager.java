/*
 * $Id: UserManager.java,v 1.7 2003/06/24 16:50:10 egli Exp $
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lenya.cms.publication.Publication;
import org.apache.log4j.Category;

/**
 * Describe class <code>UserManager</code> here.
 *
 * @author egli
 * 
 *
 */
public class UserManager extends ItemManager {
    static private Category log = Category.getInstance(UserManager.class);

    protected static final String SUFFIX = ".iml";

    private static Map instances = new HashMap();

	/**
	 * Create a UserManager
	 * 
	 * @param publication for which the UserManager should be instanciated.
	 * @throws AccessControlException if the UserManager could not be 
	 * 	instantiated.
	 */
    protected UserManager(Publication publication)
        throws AccessControlException {

        super(publication);
    }

    /**
     * Describe <code>instance</code> method here.
     *
     * @param publication a <code>Publication</code> value
     * @return an <code>UserManager</code> value
     * @exception AccessControlException if an error occurs
     */
    public static UserManager instance(Publication publication)
        throws AccessControlException {

        assert publication != null;
        if (!instances.containsKey(publication))
            instances.put(publication, new UserManager(publication));
        return (UserManager) instances.get(publication);
    }

    /**
     * Get all users.
     *
     * @return an Iterator to iterate over all users
     */
    public Iterator getUsers() {
        return super.getItems();
    }

    /**
     * Add the given user
     * 
    * @param user User that is to be added
    */
    public void add(User user) {
        super.add(user);
    }

    /**
     * Remove the given user
     * 
    * @param user User that is to be removed
    */
    public void remove(User user) {
        super.remove(user);
    }

    /**
     * Get the user with the given user id.
     * 
     * @param userId user id of requested user
     * @return the requested user or null if there is 
     * no user with the given user id 
     */
    public User getUser(String userId) {
        User user = null;
        Iterator iter = getUsers();
        while (iter.hasNext()) {
            User element = (User) iter.next();
            if (element.getId().equals(userId)) {
                user = element;
                break;
            }
        }
        return user;
    }

    /**
     * Get a filefilter which filters for all user files.
     *
     * @return a <code>FileFilter</code> which filters for all user files.
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
