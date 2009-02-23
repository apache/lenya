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

import java.util.Map;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to login a user.
 * 
 * @version $Id: Login.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class Login extends AccessControlUsecase {

    protected static final String HISTORY_SESSION_ATTRIBUTE = "org.apache.lenya.cms.cocoon.acting.History";
    protected static final String PASSWORD = "password";
    protected static final String USERNAME = "username";
    protected static final String REFERRER_QUERY_STRING = "referrerQueryString";
    protected static final String PUBLICATION = "publication";
    protected static final String CURRENT_USER = "currentUser";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Publication publication;

        try {
            publication = PublicationUtil.getPublicationFromUrl(this.manager, getDocumentFactory(),
                    getSourceURL());
            if (publication.exists()) {
                setParameter(PUBLICATION, publication);
            }
            Identity identity = this.getSession().getIdentity();
            if (identity != null && identity.getUser() != null) {
                setParameter(CURRENT_USER, this.getSession().getIdentity().getUser());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

        String userId = getParameterAsString(USERNAME);
        String password = getParameterAsString(PASSWORD);

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
        
        if (!hasErrors()) {
            Map objectModel = ContextHelper.getObjectModel(getContext());
            Request request = ObjectModelHelper.getRequest(objectModel);
            request.getSession(true);
            if (getAccessController().authenticate(request)) {
                request.getSession(false).removeAttribute(HISTORY_SESSION_ATTRIBUTE);
                setDefaultTargetURL(request.getPathInfo());
            } else {
                addErrorMessage("Authentication failed");
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getExitQueryString()
     */
    protected String getExitQueryString() {
        String queryString = getParameterAsString(REFERRER_QUERY_STRING);
        if (queryString != null && !queryString.equals("")) {
            queryString = "?" + queryString;
        }
        return queryString;
    }
}