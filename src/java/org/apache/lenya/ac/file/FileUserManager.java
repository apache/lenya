/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: FileUserManager.java,v 1.3 2004/03/03 12:56:32 gregor Exp $  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;

/**
 * Describe class <code>UserManager</code> here.
 */
public class FileUserManager extends FileItemManager implements UserManager {
    
    private static Map instances = new HashMap();

    /**
     * Create a UserManager
     *
     * @param configurationDirectory for which the UserManager should be instanciated.
     * @throws AccessControlException if the UserManager could not be
     *         instantiated.
     */
    protected FileUserManager(File configurationDirectory) throws AccessControlException {
        super(configurationDirectory);
    }

    /**
     * Describe <code>instance</code> method here.
     *
     * @param configurationDirectory a directory
     * @return an <code>UserManager</code> value
     * @exception AccessControlException if an error occurs
     */
    public static FileUserManager instance(File configurationDirectory) throws AccessControlException {

        assert configurationDirectory != null;
        if (!configurationDirectory.isDirectory()) {
            throw new AccessControlException(
                "Configuration directory [" + configurationDirectory + "] does not exist!");
        }

        if (!instances.containsKey(configurationDirectory)) {
            instances.put(configurationDirectory, new FileUserManager(configurationDirectory));
        }

        return (FileUserManager) instances.get(configurationDirectory);
    }

    /**
     * Get all users.
     *
     * @return an Iterator to iterate over all users
     */
    public User[] getUsers() {
        Item[] items = super.getItems();
        User[] users = new User[items.length];
        for (int i = 0; i < users.length; i++) {
            users[i] = (User) items[i];
        }
        return users;
    }

    /**
     * Add the given user
     *
    * @param user User that is to be added
    */
    public void add(User user) throws AccessControlException {
        super.add(user);
    }

    /**
     * Remove the given user
     *
    * @param user User that is to be removed
    */
    public void remove(User user) throws AccessControlException {
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

    protected static final String SUFFIX = ".iml";

    /**
     * @see org.apache.lenya.cms.ac.ItemManager#getSuffix()
     */
    protected String getSuffix() {
        return SUFFIX;
    }
    
}
