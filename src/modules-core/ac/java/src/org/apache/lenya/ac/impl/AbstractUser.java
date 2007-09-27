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

package org.apache.lenya.ac.impl;

import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.ItemManager;
import org.apache.lenya.ac.Password;
import org.apache.lenya.ac.User;

/**
 * Abstract user implementation.
 * @version $Id$
 */
public abstract class AbstractUser extends AbstractGroupable implements User {

    private String email;
    private String encryptedPassword;
    private String defaultMenuLocale;
    private String defaultDocumentLocale;

    /**
     * Creates a new User.
     * @param itemManager The item manager.
     * @param logger The logger.
     */
    public AbstractUser(ItemManager itemManager, Logger logger) {
        super(itemManager, logger);
    }

    /**
     * Create a User instance
     * @param itemManager The item manager.
     * @param logger The logger.
     * @param id the user id
     * @param fullName the full name of the user
     * @param _email the users email address
     * @param password the users password
     */
    public AbstractUser(ItemManager itemManager, Logger logger, String id, String fullName,
            String _email, String password) {
        this(itemManager, logger);
        setId(id);
        setName(fullName);
        this.email = _email;
        setPassword(password);
    }

    /**
     * Get the email address
     * @return a <code>String</code>
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Set the email address
     * @param _email the new email address
     */
    public void setEmail(String _email) {
        this.email = _email;
    }

    /**
     * Sets the password.
     * @param plainTextPassword The plain text passwrod.
     */
    public void setPassword(String plainTextPassword) {
        this.encryptedPassword = Password.encrypt(plainTextPassword);
    }

    /**
     * This method can be used for subclasses to set the password without it
     * being encrypted again. Some subclass might have knowledge of the
     * encrypted password and needs to be able to set it.
     * @param _encryptedPassword the encrypted password
     */
    protected void setEncryptedPassword(String _encryptedPassword) {
        this.encryptedPassword = _encryptedPassword;
    }

    /**
     * Get the encrypted password
     * @return the encrypted password
     */
    protected String getEncryptedPassword() {
        return this.encryptedPassword;
    }

    /**
     * Checks support for changing password
     * @return true if password change is supported
     */
    public boolean canChangePassword() {
        return true;
    }

    /**
     * @return Returns the defaultDocumentLocale.
     */
    public String getDefaultDocumentLocale() {
        return defaultDocumentLocale;
    }

    /**
     * @param defaultDocumentLocale The defaultDocumentLocale to set.
     */
    public void setDefaultDocumentLocale(String defaultDocumentLocale) {
        this.defaultDocumentLocale = defaultDocumentLocale;
    }

    /**
     * @return Returns the defaultMenuLocale.
     */
    public String getDefaultMenuLocale() {
        return defaultMenuLocale;
    }

    /**
     * @param defaultMenuLocale The defaultMenuLocale to set.
     */
    public void setDefaultMenuLocale(String defaultMenuLocale) {
        this.defaultMenuLocale = defaultMenuLocale;
    }

    /**
     * Save the user
     * @throws AccessControlException if the save failed
     */
    public abstract void save() throws AccessControlException;

    /**
     * Delete a user
     * @throws AccessControlException if the delete failed
     */
    public void delete() throws AccessControlException {
        removeFromAllGroups();
    }

    /**
     * Authenticate a user. This is done by encrypting the given password and
     * comparing this to the encryptedPassword.
     * @param password to authenticate with
     * @return true if the given password matches the password for this user
     */
    public boolean authenticate(String password) {
        getLogger().debug("LDAP Password: " + password);
        getLogger().debug("LDAP pw encypted: " + Password.encrypt(password));
        getLogger().debug("LDAP orig encrypted pw: " + this.encryptedPassword);

        return this.encryptedPassword.equals(Password.encrypt(password));
    }

}
