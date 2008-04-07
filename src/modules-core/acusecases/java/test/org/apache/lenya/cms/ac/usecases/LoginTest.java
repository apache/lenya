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

import java.util.HashMap;
import java.util.Map;

import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.usecase.AbstractUsecaseTest;

/**
 * Login test.
 */
public class LoginTest extends AbstractUsecaseTest {

    protected static final String USER_ID = "lenya";
    protected static final String PASSWORD = "levi";

    protected Map getRequestParameters() {
        return getParameters();
    }
    
    protected Map getParameters() {
        Map params = new HashMap();
        params.put(Login.USERNAME, USER_ID);
        params.put(Login.PASSWORD, PASSWORD);
        return params;
    }

    protected String getUsecaseName() {
        return "ac.login";
    }

    protected void checkPostconditions() {
        Session session = getRequest().getSession();
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());
        User user = identity.getUser();
        assertNotNull(user);
        assertEquals(user.getId(), USER_ID);
    }
    
    protected void login() throws AccessControlException {
        getAccessController().setupIdentity(getRequest());
    }

}
