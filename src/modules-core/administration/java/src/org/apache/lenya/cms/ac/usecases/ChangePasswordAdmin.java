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
public class ChangePasswordAdmin extends AbstractChangePassword {

    private User user;

    protected User getUser() {
        if (this.user == null) {
            String userId = getParameterAsString(UserProfile.USER_ID);
            this.user = getUserManager().getUser(userId);
        }
        return this.user;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        if (getUser() == null) {
            addErrorMessage("The " + UserProfile.USER_ID 
                + " parameter has to be provided when executing this usecase.");
            return;
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        setExitParameter(UserProfile.USER_ID, getParameterAsString(UserProfile.USER_ID));
    }

}
