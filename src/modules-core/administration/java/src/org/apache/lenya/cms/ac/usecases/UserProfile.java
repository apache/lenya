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
import org.apache.lenya.ac.ItemUtil;

/**
 * Usecase to edit a user's profile.
 */
public class UserProfile extends AccessControlUsecase {

    protected static final String USER_ID = "userId";
    protected static final String FULL_NAME = "fullName";
    protected static final String EMAIL = "email";
    protected static final String DESCRIPTION = "description";
    protected static final String MENU_LOCALE = "defaultMenuLocale";
    protected static final String DOCUMENT_LOCALE = "defaultDocumentLocale";
    
    /**
     * Ctor.
     */
    public UserProfile() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        
        String email = getParameterAsString(UserProfile.EMAIL);
        if (!ItemUtil.isValidEmail(email)) {
            addErrorMessage("Please enter a valid e-mail address.");
        }
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        
        String fullName = getParameterAsString(UserProfile.FULL_NAME);
        String description = getParameterAsString(UserProfile.DESCRIPTION);
        String email = getParameterAsString(UserProfile.EMAIL);
        String defaultMenuLocale = getParameterAsString(UserProfile.MENU_LOCALE);
        String defaultDocumentLocale = getParameterAsString(UserProfile.DOCUMENT_LOCALE);
        
        getUser().setEmail(email);
        getUser().setName(fullName);
        getUser().setDescription(description);
        getUser().setDefaultMenuLocale(defaultMenuLocale);
        getUser().setDefaultDocumentLocale(defaultDocumentLocale);
        getUser().save();
        
    }
    
    private User user;
    
    /**
     * Returns the currently edited user.
     * @return A user.
     */
    protected User getUser() {
        return this.user;
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
        
        if (name.equals(USER_ID)) {
            String userId = (String) value;
            this.user = getUserManager().getUser(userId);
            if (this.user == null) {
                throw new RuntimeException("User [" + userId + "] not found.");
            }
            
            setParameter(EMAIL, this.user.getEmail());
            setParameter(DESCRIPTION, this.user.getDescription());
            setParameter(MENU_LOCALE, this.user.getDefaultMenuLocale());
            setParameter(DOCUMENT_LOCALE, this.user.getDefaultDocumentLocale());
            setParameter(FULL_NAME, this.user.getName());
        }
    }

}
