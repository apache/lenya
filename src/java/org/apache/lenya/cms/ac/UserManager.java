/*
$Id: UserManager.java,v 1.13 2003/07/23 13:21:15 gregor Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.ac;

import org.apache.log4j.Category;

import java.io.File;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
     * @param configurationDirectory for which the UserManager should be instanciated.
     * @throws AccessControlException if the UserManager could not be
     *         instantiated.
     */
    protected UserManager(File configurationDirectory) throws AccessControlException {
        super(configurationDirectory);
    }

    /**
     * Describe <code>instance</code> method here.
     *
     * @param configurationDirectory a directory
     * @return an <code>UserManager</code> value
     * @exception AccessControlException if an error occurs
     */
    public static UserManager instance(File configurationDirectory) throws AccessControlException {

        assert configurationDirectory != null;
        if (!configurationDirectory.isDirectory()) {
            throw new AccessControlException(
                "Configuration directory [" + configurationDirectory + "] does not exist!");
        }

        if (!instances.containsKey(configurationDirectory)) {
            instances.put(configurationDirectory, new UserManager(configurationDirectory));
        }

        return (UserManager) instances.get(configurationDirectory);
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
        return (User) getItem(userId);
    }

    /**
     * @see org.apache.lenya.cms.ac.ItemManager#getSuffix()
     */
    protected String getSuffix() {
        return SUFFIX;
    }
}
