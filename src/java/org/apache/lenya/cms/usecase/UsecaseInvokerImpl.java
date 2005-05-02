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
 * @version $Id:$
 */
public class UsecaseInvokerImpl extends AbstractLogEnabled implements UsecaseInvoker, Serviceable {

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseInvoker#invoke(java.lang.String, java.lang.String, java.util.Map)
     */
    public void invoke(String webappUrl, String usecaseName, Map parameters) throws UsecaseException {
        UsecaseResolver resolver = null;
        Usecase usecase = null;
        try {

            resolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);
            usecase = resolver.resolve(webappUrl, usecaseName);

            usecase.setSourceURL(webappUrl);
            usecase.setName(usecaseName);

            passParameters(usecase, parameters);

            usecase.checkPreconditions();
            List errorMessages = usecase.getErrorMessages();
            if (!errorMessages.isEmpty()) {
                logErrorMessages(usecaseName, "Pre condition messages:", errorMessages);
            } else {
                usecase.lockInvolvedObjects();
                usecase.checkExecutionConditions();
                errorMessages = usecase.getErrorMessages();
                if (!errorMessages.isEmpty()) {
                    logErrorMessages(usecaseName, "Execution condition messages:", errorMessages);
                } else {
                    usecase.execute();
                    logErrorMessages(usecaseName, "Execution messages:", usecase.getErrorMessages());
                    usecase.checkPostconditions();
                    logErrorMessages(usecaseName, "Post condition messages:", usecase.getErrorMessages());
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
            getLogger().error((String) i.next());
        }
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
