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

package org.apache.lenya.ac.file;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;

/**
 * File-based user manager implementation.
 * @version $Id$
 */
public class FileUserManager extends FileItemManager implements UserManager {
    
    private static Map instances = new HashMap();
    private Collection userTypes;

    /**
     * Create a UserManager
     *
     * @param configurationDirectory for which the UserManager should be instanciated.
     * @throws AccessControlException if the UserManager could not be
     *         instantiated.
     */
    protected FileUserManager(File configurationDirectory, Collection userTypes) throws AccessControlException {
        super(configurationDirectory);
	    this.userTypes = userTypes;
    }

    /**
     * Describe <code>instance</code> method here.
     *
     * @param configurationDirectory a directory
     * @return an <code>UserManager</code> value
     * @exception AccessControlException if an error occurs
     */
    public static FileUserManager instance(File configurationDirectory, Collection userTypes) throws AccessControlException {

        assert configurationDirectory != null;
        if (!configurationDirectory.isDirectory()) {
            throw new AccessControlException(
                "Configuration directory [" + configurationDirectory + "] does not exist!");
        }

        if (!instances.containsKey(configurationDirectory)) {
            instances.put(configurationDirectory, new FileUserManager(configurationDirectory, userTypes));
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
     * @see org.apache.lenya.ac.UserManager#add(org.apache.lenya.ac.User)
     */
    public void add(User user) throws AccessControlException {
        super.add(user);
    }

    /**
     * @see org.apache.lenya.ac.UserManager#remove(org.apache.lenya.ac.User)
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

    public Collection getUserTypes() {
	    return userTypes;
    }

    protected static final String SUFFIX = ".iml";

    /**
     * @see org.apache.lenya.ac.file.FileItemManager#getSuffix()
     */
    protected String getSuffix() {
        return SUFFIX;
    }
    
}
