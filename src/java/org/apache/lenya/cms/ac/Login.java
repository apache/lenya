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
package org.apache.lenya.cms.ac;

import java.util.Map;

import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.admin.AccessControlUsecase;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

/**
 * Usecase to login a user.
 * 
 * @version $Id$
 */
public class Login extends AccessControlUsecase {

    /**
     * Ctor.
     */
    public Login() {
        super();
    }

    /**
     * Validates the request parameters.
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {

        String userId = getParameterAsString("username");
        String password = getParameterAsString("password");

        if (userId.length() == 0) {
            addErrorMessage("Please enter a user name.");
        }
        if (password.length() == 0) {
            addErrorMessage("Please enter a password.");
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
        Map objectModel = ContextHelper.getObjectModel(getContext());
        Request request = ObjectModelHelper.getRequest(objectModel);
        if (getAccessController().authenticate(request)) {
        	setTargetURL(request.getRequestURI());
		} else {
        	addErrorMessage("Authentication failed.");
        }
   }

}