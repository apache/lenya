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
package org.apache.lenya.cms.usecase.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Role;
import org.apache.lenya.cms.ac.PolicyUtil;
import org.apache.lenya.cms.ac.usecase.UsecaseAuthorizer;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.usecase.Usecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.usecase.UsecaseInvoker;
import org.apache.lenya.cms.usecase.UsecaseMessage;
import org.apache.lenya.cms.usecase.UsecaseResolver;

/**
 * Usecase invoker implementation.
 * 
 * @version $Id$
 */
public class UsecaseInvokerImpl extends AbstractLogEnabled implements UsecaseInvoker, Serviceable {

    private String targetUrl;

    /**
     * @return if the identity is authorized to invoke the usecase.
     * @throws Exception if an error occurs.
     */
    protected boolean isUsecaseAuthorized(String webappUrl, String usecaseName) throws Exception {
        boolean authorized = false;
        ServiceSelector selector = null;
        AccessControllerResolver acResolver = null;
        AccessController accessController = null;
        ContextUtility contextUtil = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(AccessControllerResolver.ROLE
                    + "Selector");
            acResolver = (AccessControllerResolver) selector
                    .select(AccessControllerResolver.DEFAULT_RESOLVER);
            accessController = acResolver.resolveAccessController(webappUrl);

            contextUtil = (ContextUtility) this.manager.lookup(ContextUtility.ROLE);
            Request request = contextUtil.getRequest();
            DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);
            URLInformation info = new URLInformation(webappUrl);
            Publication pub = factory.getPublication(info.getPublicationId());
            Role[] roles = PolicyUtil.getRoles(request);

            Authorizer[] authorizers = accessController.getAuthorizers();
            for (int i = 0; i < authorizers.length; i++) {
                if (authorizers[i] instanceof UsecaseAuthorizer) {
                    UsecaseAuthorizer authorizer = (UsecaseAuthorizer) authorizers[i];
                    authorized = authorizer.authorizeUsecase(usecaseName, roles, pub);
                }
            }
        } finally {
            if (selector != null) {
                if (acResolver != null) {
                    if (accessController != null) {
                        acResolver.release(accessController);
                    }
                    selector.release(acResolver);
                }
                this.manager.release(selector);
            }
            if (contextUtil != null) {
                this.manager.release(contextUtil);
            }
        }
        return authorized;
    }

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseInvoker#invoke(java.lang.String, java.lang.String,
     *      java.util.Map)
     */
    public void invoke(String webappUrl, String usecaseName, Map parameters)
            throws UsecaseException {

        this.errorMessages.clear();
        this.infoMessages.clear();

        UsecaseResolver resolver = null;
        Usecase usecase = null;
        this.result = SUCCESS;
        try {

            if (!isUsecaseAuthorized(webappUrl, usecaseName)) {
                this.errorMessages.add(new UsecaseMessage("Authorization of usecase " + usecaseName
                        + " failed."));
                this.result = AUTHORIZATION_FAILED;
                return;
            }

            resolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);
            usecase = resolver.resolve(webappUrl, usecaseName);

            Session testSession = getTestSession();
            if (testSession != null) {
                usecase.setTestSession(testSession);
            }

            usecase.getParameterNames(); // initialize usecase parameters to enable overriding
            passParameters(usecase, parameters);

            usecase.checkPreconditions();

            if (succeeded(PRECONDITIONS_FAILED, usecase)) {

                usecase.lockInvolvedObjects();
                usecase.checkExecutionConditions();

                if (succeeded(EXECUTION_CONDITIONS_FAILED, usecase)) {
                    usecase.execute();

                    boolean success = succeeded(EXECUTION_FAILED, usecase);
                    this.targetUrl = usecase.getTargetURL(success);
                    if (success) {
                        usecase.checkPostconditions();
                        succeeded(POSTCONDITIONS_FAILED, usecase);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (usecase != null) {
                    try {
                        resolver.release(usecase);
                    } catch (ServiceException e) {
                        throw new RuntimeException(e);
                    }
                }
                this.manager.release(resolver);
            }
        }
    }

    private Session testSession = null;

    protected Session getTestSession() {
        return this.testSession;
    }

    protected boolean succeeded(int result, Usecase usecase) {

        this.errorMessages.addAll(usecase.getErrorMessages());
        this.infoMessages.addAll(usecase.getInfoMessages());

        if (usecase.getErrorMessages().isEmpty()) {
            return true;
        } else {
            this.result = result;
            String message = null;
            switch (result) {
            case PRECONDITIONS_FAILED:
                message = "Precondition messages:";
                break;
            case EXECUTION_CONDITIONS_FAILED:
                message = "Execution condition messages:";
                break;
            case EXECUTION_FAILED:
                message = "Execution messages:";
                break;
            case POSTCONDITIONS_FAILED:
                message = "Postcondition messages:";
                break;
            }
            logErrorMessages(usecase.getName(), message, usecase.getErrorMessages());
            return false;
        }
    }

    /**
     * @param usecase The usecase to pass the parameters to.
     * @param parameters The parameters.
     */
    protected void passParameters(Usecase usecase, Map parameters) {
        for (Iterator i = parameters.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            Object value = parameters.get(key);
            usecase.setParameter(key, value);
        }
    }

    /**
     * @param usecaseName The name of the usecase.
     * @param headline The headline of the messages.
     * @param errorMessages The messages to log.
     */
    protected void logErrorMessages(String usecaseName, String headline, List errorMessages) {
        getLogger().error("Usecase [" + usecaseName + "] - " + headline);
        for (Iterator i = errorMessages.iterator(); i.hasNext();) {
            getLogger().error("" + (UsecaseMessage) i.next());
        }
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    private List errorMessages = new ArrayList();
    private List infoMessages = new ArrayList();

    public List getErrorMessages() {
        return Collections.unmodifiableList(this.errorMessages);
    }

    public List getInfoMessages() {
        return Collections.unmodifiableList(this.infoMessages);
    }

    private int result = SUCCESS;

    public int getResult() {
        return this.result;
    }

    public String getTargetUrl() {
        if (this.targetUrl == null) {
            throw new IllegalStateException("The usecase has not been executed yet.");
        }
        return this.targetUrl;
    }

    public void setTestSession(Session session) {
        this.testSession = session;
    }

}
