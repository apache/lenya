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
 * Usecase to change a user's password. The old password is checked.
 */
public class ChangePassword extends AbstractChangePassword {

    protected static final String OLD_PASSWORD = "oldPassword";

    /**
     * @return Always returns the currently logged in user.
     */
    protected User getUser() {
        return getSession().getIdentity().getUser();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        checkOldPassword();
    }

    /**
     * verifies that the user knows the current password before s/he is allowed to change it.
     */
    private void checkOldPassword() {
        String oldPassword = getParameterAsString(OLD_PASSWORD);
        boolean authenticated = getUser().authenticate(oldPassword);
        if (!authenticated) {
            addErrorMessage("The old password is not correct.");
        }
    }
}
