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

/* $Id: UserManager.java,v 1.2 2004/03/03 12:56:31 gregor Exp $  */

package org.apache.lenya.ac;

public interface UserManager extends ItemManager {
    
    /**
     * Get all users.
     *
     * @return an array of users
     */
    User[] getUsers();
    
    /**
     * Add the given user
     *
     * @param user User that is to be added
     * @throws AccessControlException when the user is already contained.
     */
    void add(User user) throws AccessControlException;
    
    /**
     * Remove the given user
     *
     * @param user User that is to be removed
     * @throws AccessControlException when the user is not contained.
     */
    void remove(User user) throws AccessControlException;
    
    /**
     * Get the user with the given user id.
     *
     * @param userId user id of requested user
     * @return the requested user or null if there is
     * no user with the given user id
     */
    User getUser(String userId);
    
}