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
package org.apache.lenya.cms.ac.usecases;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.ItemUtil;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.ac.file.FileUser;
import org.apache.lenya.ac.file.FileUserManager;
import org.apache.lenya.ac.ldap.LDAPUser;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to add a user.
 * 
 * @version $Id: AddUser.java 407248 2006-05-17 13:20:01Z andreas $
 */
public class AddUser extends AccessControlUsecase {

    protected static final String CLASS_NAME = "className";
    protected static final String LDAP_ID = "ldapId";

    /**
     * Validates the request parameters.
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {

        String userId = getParameterAsString(UserProfile.USER_ID).toLowerCase();
        String email = getParameterAsString(UserProfile.EMAIL);
        String className = getParameterAsString(CLASS_NAME);
        String ldapId = getParameterAsString(LDAP_ID);

        User existingUser = getUserManager().getUser(userId);

        if (existingUser != null) {
            addErrorMessage("This user already exists.");
        }

        if (!ItemUtil.isValidId(userId)) {
            addErrorMessage("This is not a valid user ID.");
        }

        if (!ItemUtil.isValidEmail(email)) {
            addErrorMessage("Please enter a valid e-mail address.");
        }
        
        if (className == null) {
            addErrorMessage("Internal error - the user class name has not been provided. " + 
                    "Please consult your system administrator.");
        }

        if (className.equals(LDAPUser.class.getName())) {
            LDAPUser ldapUser = new LDAPUser(getUserManager(), getLogger());
            ContainerUtil.enableLogging(ldapUser, getLogger());

            try {
                if (!ldapUser.existsUser(ldapId)) {
                    addErrorMessage("ldap_no_such_user", new String[]{ldapId});
                }
            } catch (AccessControlException e) {
                throw new UsecaseException(e);
            }
        }

        else {
            ChangePassword.checkNewPassword(this);
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        if (getLogger().isDebugEnabled())
            getLogger().debug("AddUser.doCheckExecutionConditions() called");

        validate();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        UserManager userManager = (FileUserManager) getUserManager();

        String userId = getParameterAsString(UserProfile.USER_ID).toLowerCase();
        String fullName = getParameterAsString(UserProfile.FULL_NAME);
        String description = getParameterAsString(UserProfile.DESCRIPTION);
        String email = getParameterAsString(UserProfile.EMAIL);
        String className = getParameterAsString(CLASS_NAME);

        User user;
        if (className.equals(LDAPUser.class.getName())) {
            String ldapId = getParameterAsString(LDAP_ID);
            user = new LDAPUser(userManager, getLogger(), userId, email, ldapId, getLogger());
        } else {
            String password = getParameterAsString(AbstractChangePassword.NEW_PASSWORD);
            user = new FileUser(userManager, getLogger(), userId, fullName, email, "");
            user.setName(fullName);
            user.setPassword(password);
        }
        ContainerUtil.enableLogging(user, getLogger());
        user.setDescription(description);
        user.save();
        getUserManager().add(user);
        
        setExitParameter(UserProfile.USER_ID, userId);
    }
}
