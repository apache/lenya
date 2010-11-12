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
 * @version $Id$
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
     * @param plainTextPassword The plain text password.
     */
    void setPassword(String plainTextPassword);
   
    /**
     * Checks support for changing password
     * @return true if password change is supported
     */
    public abstract boolean canChangePassword();
 
    /**
     * Returns the user's preferred locale for the Lenya
     * CMS menus and CMS screens.
     * 
     * The locale can either be a 2 letter country code 
     * (de, en) or a locale code according to RFC 1766 /
     * ISO 639 / ISO 3166 (de_DE, de_CH, de_AT, en_US, ...)  
     * @return The locale.
     */
    String getDefaultMenuLocale();
    
    /**
     * Sets the user's preferred locale for the Lenya
     * menu and CMS screens.
     * 
     * The locale can either be a 2 letter country code 
     * (de, en) or a locale code according to RFC 1766 /
     * ISO 639 / ISO 3166 (de_DE, de_CH, de_AT, en_US, ...)
     * @param menuLocale The locale.
     */
    void setDefaultMenuLocale(String menuLocale);
    
    /**
     * Returns the user's default locale of documents
     * to be created or edited.
     * 
     * The locale can either be a 2 letter country code 
     * (de, en) or a locale code according to RFC 1766 /
     * ISO 639 / ISO 3166 (de_DE, de_CH, de_AT, en_US, ...)
     * @return The locale.
     */
    String getDefaultDocumentLocale();
    
    /**
     * Sets the user's default locale of documents
     * to be created or edited.
     * 
     * The locale can either be a 2 letter country code 
     * (de, en) or a locale code according to RFC 1766 /
     * ISO 639 / ISO 3166 (de_DE, de_CH, de_AT, en_US, ...)
     * @param documentLocale The locale.
     */
    void setDefaultDocumentLocale(String documentLocale); 
    
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
    
    /**
     * @return The accreditable manager this accreditable belongs to.
     */
    AccreditableManager getAccreditableManager();
}
