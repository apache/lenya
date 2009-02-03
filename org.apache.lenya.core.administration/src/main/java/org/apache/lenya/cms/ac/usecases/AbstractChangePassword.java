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

import org.apache.lenya.ac.User;


/**
 * Usecase to change a user's password.
 */
public abstract class AbstractChangePassword extends AccessControlUsecase {

    protected static final String NEW_PASSWORD = "newPassword";
    protected static final String CONFIRM_PASSWORD = "confirmPassword";

    protected abstract User getUser();

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        checkNewPassword(this);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        getUser().setPassword(getParameterAsString(NEW_PASSWORD));
        getUser().save();
    }

    /**
     * Checks a password and a confirmed password.
     * @param usecase The usecase.
     */
    protected static void checkNewPassword(AccessControlUsecase usecase) {
        String password = usecase.getParameterAsString(NEW_PASSWORD);
        String confirmPassword = usecase.getParameterAsString(CONFIRM_PASSWORD);

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
