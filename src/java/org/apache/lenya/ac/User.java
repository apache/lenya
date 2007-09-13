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
 * A user.
 * @version $Id: User.java 473841 2006-11-12 00:46:38Z gregor $
 */
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
     * Authenticate a user. This is done by encrypting the given password and
     * comparing this to the encryptedPassword.
     * 
     * @param password to authenticate with
     * @return true if the given password matches the password for this user
     */
    boolean authenticate(String password);

    /**
     * @return The names of all possible attributes.
     */
    String[] getAttributeNames();

    /**
     * @param name The attribute name.
     * @return The attribute values or <code>null</code> if no value is
     *         available for the attribute.
     * @throws AccessControlException if the attribute is not supported.
     */
    String[] getAttributeValues(String name) throws AccessControlException;

    /**
     * @return If this user object is persistent, i.e. stored in the Lenya user
     *         database (not transient).
     */
    boolean isPersistent();

}