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

/* $Id: User.java,v 1.2 2004/03/03 12:56:31 gregor Exp $  */

package org.apache.lenya.ac;

public interface User extends Identifiable, Item, Groupable {
    
    /**
     * Get the email address
     *
     * @return a <code>String</code>
     */
    String getEmail();
    
    /**
     * Set the email address
     *
     * @param email the new email address
     */
    void setEmail(String email);
    
    /**
     * Sets the password.
     * @param plainTextPassword The plain text passwrod.
     */
    void setPassword(String plainTextPassword);
    
    /**
     * Save the user
     *
     * @throws AccessControlException if the save failed
     */
    void save() throws AccessControlException;
    
    /**
     * Delete a user
     *
     * @throws AccessControlException if the delete failed
     */
    void delete() throws AccessControlException;
    
    /**
     * Authenticate a user. This is done by encrypting
     * the given password and comparing this to the
     * encryptedPassword.
     *
     * @param password to authenticate with
     * @return true if the given password matches the password for this user
     */
    boolean authenticate(String password);
    
}