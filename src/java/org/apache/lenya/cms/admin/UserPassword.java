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
package org.apache.lenya.cms.admin;

import org.apache.lenya.ac.User;

/**
 * Usecase to change a user's password.
 */
public class UserPassword extends AccessControlUsecase {

    protected static final String OLD_PASSWORD = "oldPassword";
    protected static final String NEW_PASSWORD = "password";
    protected static final String CONFIRM_PASSWORD = "confirmPassword";
    
    protected static final String CHECK_PASSWORD = "checkPassword";

    /**
     * Ctor.
     */
    public UserPassword() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        
        String checkOldPassword = getParameter(CHECK_PASSWORD);
        if (checkOldPassword != null && checkOldPassword.equals(Boolean.toString(true))) {
            String oldPassword = getParameter(OLD_PASSWORD);
            boolean authenticated = this.user.authenticate(oldPassword);
            if (!authenticated) {
                addErrorMessage("The old password is not correct.");
            }
        }
        
        checkNewPassword(this);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        this.user.setPassword(getParameter(NEW_PASSWORD));
    }

    private User user;

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.String)
     */
    public void setParameter(String name, String value) {
        super.setParameter(name, value);

        if (name.equals(UserProfile.USER_ID)) {
            String userId = value;
            this.user = getUserManager().getUser(userId);
            if (this.user == null) {
                throw new RuntimeException("User [" + userId + "] not found.");
            }

        }
    }

    /**
     * Checks a password and a confirmed password.
     * @param usecase The usecase.
     */
    protected static void checkNewPassword(AccessControlUsecase usecase) {
        String password = usecase.getParameter(UserPassword.NEW_PASSWORD);
        String confirmPassword = usecase.getParameter(UserPassword.CONFIRM_PASSWORD);

        if (!password.equals(confirmPassword)) {
            usecase.addErrorMessage("Password and confirmed password are not equal.");
        }

        if (password.length() < 6) {
            usecase.addErrorMessage("The password must be at least six characters long.");
        }

        if (!password.matches(".*\\d.*")) {
            usecase.addErrorMessage("The password must contain at least one number.");
        }
    }


}