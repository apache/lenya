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

import org.apache.lenya.ac.User;

/**
 * Usecase to change a user's password.
 */
public class UserPassword extends AccessControlUsecase {

    protected static final String NEW_PASSWORD = "password";
    protected static final String CONFIRM_PASSWORD = "confirmPassword";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        
        if (this.user == null) {
            addErrorMessage("The user ID has to be provided when executing this usecase.");
            return;
        }

        checkNewPassword(this);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        this.user.setPassword(getParameterAsString(NEW_PASSWORD));
    }

    private User user;
    
    protected User getUser() {
        return this.user;
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {

        super.setParameter(name, value);

        if (name.equals(UserProfile.USER_ID)) {
            String userId = (String) value;
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
        String password = usecase.getParameterAsString(UserPassword.NEW_PASSWORD);
        String confirmPassword = usecase.getParameterAsString(UserPassword.CONFIRM_PASSWORD);

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