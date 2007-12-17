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
 * A user which is managed by the Lenya CMS itself, i.e. it belongs to a {@link UserManager}.
 */
public interface ManagedUser extends User, Accreditable {

    /**
     * Delete this user.
     * @throws AccessControlException if the delete failed
     */
    void delete() throws AccessControlException;

    /**
     * Authenticate this user. This is done by encrypting the given password and comparing this to
     * the encrypted password.
     * @param password The plain text password.
     * @return true if the given password matches the password of this user.
     */
    boolean authenticate(String password);

    /**
     * Sets the password.
     * @param plainTextPassword The plain text password.
     */
    void setPassword(String plainTextPassword);

    /**
     * Saves this user.
     * @throws AccessControlException if the user could not be saved.
     */
    void save() throws AccessControlException;

}
