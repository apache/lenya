/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

package org.apache.lenya.ac;

/**
 * User manager.
 * @version $Id$
 */
public interface UserManager extends ItemManager {
    
    /**
     * Get all users.
     *
     * @return an array of users
     */
    User[] getUsers();
    
    /**
     * Get all supported user types
     *
     * @return a collection of user types
     */
    UserType[] getUserTypes();

    /**
     * Add the given user
     *
     * @param user A user.
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