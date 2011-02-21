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

import javax.servlet.http.HttpServletRequest;

import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.utils.ServletHelper;
import org.apache.lenya.utils.URLInformation;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to login a user.
 * 
 */
public class Login extends AccessControlUsecase {

    protected static final String HISTORY_SESSION_ATTRIBUTE = "org.apache.lenya.cms.cocoon.acting.History";
    protected static final String PASSWORD = "password";
    protected static final String USERNAME = "username";
    protected static final String REFERRER_QUERY_STRING = "referrerQueryString";
    protected static final String PUBLICATION = "publication";
    protected static final String CURRENT_USER = "currentUser";
    
    //private Repository repository;
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        
        try {
        	String pubId = new URLInformation().getPublicationId();
            if (getSession().existsPublication(pubId)) {
                Publication publication = getSession().getPublication(pubId);
                setParameter(PUBLICATION, publication);
            }
            //florent : Identity identity = this.getSession().getIdentity();
            Identity identity = Identity.getIdentity(this.request.getSession(false));
            if (identity != null && identity.getUser() != null) {
                //florent : use the just define identity, move when ok
            	//setParameter(CURRENT_USER, this.getSession().getIdentity().getUser());
            	setParameter(CURRENT_USER, identity.getUser());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        	//TODO : remove this part for retrive the request and use the other technique for identity as the request is in the abstract
        	//usecase now
        	HttpServletRequest request = ServletHelper.getRequest();
            request.getSession(true);
            
            if (getAccessController().authenticate(request)) {
            	//we have an authenticated user, so we create a modifiable repository session
            	Identity identity = (Identity) request.getSession().getAttribute(Identity.class.getName());
            	//florent : see if ok, startsession remove from repository
            	//Session s = this.repository.startSession(identity, true);
            	Session s = this.repository.getSession(this.request);
            	this.setSession(s);
            	//TODO : see if this remove attribute is still valid
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