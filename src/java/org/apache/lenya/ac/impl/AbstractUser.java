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

/* $Id: AbstractUser.java,v 1.3 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.ac.impl;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Password;
import org.apache.lenya.ac.User;
import org.apache.log4j.Category;

public abstract class AbstractUser extends AbstractGroupable implements User {

    private static Category log = Category.getInstance(AbstractUser.class);
    private String email;
    private String encryptedPassword;
    
    /**
     * Creates a new User.
     */
    public AbstractUser() {
    }

    /**
         * Create a User instance
         *
         * @param id the user id
         * @param fullName the full name of the user
         * @param email the users email address
         * @param password the users password
         */
    public AbstractUser(String id, String fullName, String email, String password) {
        setId(id);
        setName(fullName);
        this.email = email;
        setPassword(password);
    }

    /**
     * Get the email address
     *
     * @return a <code>String</code>
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the full name
     *
     * @return a <code>String</code>
     * @deprecated has been superceded by getName()
     */
    public String getFullName() {
        return getName();
    }

    /**
     * Set the email address
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set the full name
     *
     * @param name the new full name
     * @deprecated has been superceded by setName(String)
     */
    public void setFullName(String name) {
        setName(name);
    }

    /**
    * Sets the password.
     * @param plainTextPassword The plain text passwrod.
     */
    public void setPassword(String plainTextPassword) {
        encryptedPassword = Password.encrypt(plainTextPassword);
    }

    /**
     * This method can be used for subclasses to set the password without it
     * being encrypted again. Some subclass might have knowledge of the encrypted
     * password and needs to be able to set it.
     *
     * @param encryptedPassword the encrypted password
     */
    protected void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    /**
     * Get the encrypted password
     *
     * @return the encrypted password
     */
    protected String getEncryptedPassword() {
        return encryptedPassword;
    }

    /**
     * Save the user
     *
     * @throws AccessControlException if the save failed
     */
    public abstract void save() throws AccessControlException;

    /**
     * Delete a user
     *
     * @throws AccessControlException if the delete failed
     */
    public void delete() throws AccessControlException {
        removeFromAllGroups();
    }

    /**
     * Authenticate a user. This is done by encrypting
     * the given password and comparing this to the
     * encryptedPassword.
     *
     * @param password to authenticate with
     * @return true if the given password matches the password for this user
     */
    public boolean authenticate(String password) {
        log.debug("Password: " + password);
        log.debug("pw encypted: " + Password.encrypt(password));
        log.debug("orig encrypted pw: " + this.encryptedPassword);

        return this.encryptedPassword.equals(Password.encrypt(password));
    }
    
}
