/*
$Id: User.java,v 1.20 2003/07/17 11:01:18 egli Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.ac;

import org.apache.lenya.cms.ac2.*;

import org.apache.log4j.Category;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author  nobby
 */
public abstract class User extends AbstractGroupable implements Identifiable {

    private static Category log = Category.getInstance(User.class);
    private String email;
    private String encryptedPassword;
    private Set groups = new HashSet();

    /**
     * Creates a new User.
     */
    public User() {
    }

    /**
         * Create a User instance
         *
         * @param id the user id
         * @param fullName the full name of the user
         * @param email the users email address
         * @param password the users password
         */
    public User(String id, String fullName, String email, String password) {
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

    /**
     * @see org.apache.lenya.cms.ac.Accreditable#getAccreditables()
     */
    public Accreditable[] getAccreditables() {
        Set accreditables = new HashSet();
        accreditables.add(this);

        Group[] groups = getGroups();

        for (int i = 0; i < groups.length; i++) {
            Accreditable[] groupAccreditables = groups[i].getAccreditables();
            accreditables.addAll(Arrays.asList(groupAccreditables));
        }

        return (Accreditable[]) accreditables.toArray(new Accreditable[accreditables.size()]);
    }

}
