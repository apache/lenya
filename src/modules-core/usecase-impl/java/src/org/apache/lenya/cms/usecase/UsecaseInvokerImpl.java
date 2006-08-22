/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.usecase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Usecase invoker implementation.
 * 
 * @version $Id$
 */
public class UsecaseInvokerImpl extends AbstractLogEnabled implements UsecaseInvoker, Serviceable {

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

            resolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);
            usecase = resolver.resolve(webappUrl, usecaseName);

            usecase.setCommitEnabled(isCommitEnabled());
            usecase.setSourceURL(webappUrl);
            usecase.setName(usecaseName);

            passParameters(usecase, parameters);

            usecase.checkPreconditions();

            if (succeeded(PRECONDITIONS_FAILED, usecase)) {

                usecase.lockInvolvedObjects();
                usecase.checkExecutionConditions();

                if (succeeded(EXECUTION_CONDITIONS_FAILED, usecase)) {
                    usecase.execute();

                    if (succeeded(EXECUTION_FAILED, usecase)) {
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
    
    protected boolean isCommitEnabled() {
        return true;
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

}
