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

/**
 * Display user information.
 * 
 * @version $Id: User.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class User extends AccessControlUsecase {

    protected static final String USER_ID = "userId";
    protected static final String USER = "user";

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);

        if (name.equals(USER_ID)) {
            String userId = (String) value;
            org.apache.lenya.ac.User user = getUserManager().getUser(userId);
            if (user == null) {
                addErrorMessage("user_no_such_user", new String[]{userId});
            } else {
                setParameter(USER, user);
            }
        }
    }
}
