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

/**
 * Usecase to change a user's password. The old password is checked.
 */
public class UserPasswordWithCheck extends UserPassword {

    protected static final String OLD_PASSWORD = "oldPassword";

    protected void doCheckExecutionConditions() throws Exception {

        super.doCheckExecutionConditions();

        String oldPassword = getParameterAsString(OLD_PASSWORD);
        boolean authenticated = getUser().authenticate(oldPassword);
        if (!authenticated) {
            addErrorMessage("The old password is not correct.");
        }
    }

}
