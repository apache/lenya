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
package org.apache.lenya.cms.ac.usecases;

import java.io.File;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.file.FileUser;
import org.apache.lenya.ac.file.FileUserManager;
import org.apache.lenya.ac.impl.AbstractItem;
import org.apache.lenya.ac.ldap.LDAPUser;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to add a user.
 * 
 * @version $Id: AddUser.java 123348 2004-12-25 22:49:57Z gregor $
 */
public class AddUser extends AccessControlUsecase {

    /**
     * Ctor.
     */
    public AddUser() {
        super();
    }

    protected static final String CLASS_NAME = "className";
    protected static final String LDAP_ID = "ldapId";

    /**
     * Validates the request parameters.
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {

        String userId = getParameterAsString(UserProfile.USER_ID);
        String email = getParameterAsString(UserProfile.EMAIL);
        String className = getParameterAsString(CLASS_NAME);
        String ldapId = getParameterAsString(LDAP_ID);

        User existingUser = getUserManager().getUser(userId);

        if (existingUser != null) {
            addErrorMessage("This user already exists.");
        }

        if (!AbstractItem.isValidId(userId)) {
            addErrorMessage("This is not a valid user ID.");
        }

        if (email.length() == 0) {
            addErrorMessage("Please enter an e-mail address.");
        }

        if (className.equals(LDAPUser.class.getName())) {
            LDAPUser ldapUser = new LDAPUser(((FileUserManager) getUserManager())
                    .getConfigurationDirectory());
            try {
                if (!ldapUser.existsUser(ldapId)) {
                    addErrorMessage("This LDAP user ID does not exist.");
                }
            } catch (AccessControlException e) {
                throw new UsecaseException(e);
            }
        }

        else {
            UserPassword.checkNewPassword(this);
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        validate();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        File configDir = ((FileUserManager) getUserManager()).getConfigurationDirectory();

        String userId = getParameterAsString(UserProfile.USER_ID);
        String fullName = getParameterAsString(UserProfile.FULL_NAME);
        String description = getParameterAsString(UserProfile.DESCRIPTION);
        String email = getParameterAsString(UserProfile.EMAIL);
        String className = getParameterAsString(CLASS_NAME);

        User user;
        if (className.equals(LDAPUser.class.getName())) {
            String ldapId = getParameterAsString(LDAP_ID);
            user = new LDAPUser(configDir, userId, email, ldapId);
        } else {
            String password = getParameterAsString(UserPassword.NEW_PASSWORD);
            user = new FileUser(configDir, userId, fullName, email, "");
            user.setName(fullName);
            user.setPassword(password);
        }

        user.setDescription(description);
        user.save();
        getUserManager().add(user);
    }
}