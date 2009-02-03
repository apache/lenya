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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserType;

/**
 * Manage users.
 * 
 * @version $Id: Users.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class Users extends AccessControlUsecase {

    protected static final String USERS = "users";
    protected static final String CURRENT_USER = "currentUser";
    protected static final String USER_TYPES = "userTypes";

    protected void prepareView() throws Exception {
        
        User[] users = getUserManager().getUsers();
        List userList = new ArrayList();
        userList.addAll(Arrays.asList(users));
        Collections.sort(userList);
        setParameter(USERS, userList);
        
        Request request = ContextHelper.getRequest(getContext());
        Session session = request.getSession(false);
        if (session != null) {
            Identity identity = (Identity) session.getAttribute(Identity.class.getName());
            if (identity != null) {
                setParameter(CURRENT_USER, identity.getUser());
            }
        }
        
        UserType[] types = getUserManager().getUserTypes();
        setParameter(USER_TYPES, Arrays.asList(types));
    }
}