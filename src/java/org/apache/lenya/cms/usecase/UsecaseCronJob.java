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
package org.apache.lenya.cms.usecase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.components.cron.ConfigurableCronJob;
import org.apache.cocoon.components.cron.ServiceableCronJob;

/**
 * Job to schedule usecase execution.
 * 
 * @version $Id:$
 */
public class UsecaseCronJob extends ServiceableCronJob implements ConfigurableCronJob {

    /**
     * Initializes the job.
     * @param usecase The usecase.
     */
    public void setup(Usecase usecase) {
        this.usecaseName = usecase.getName();
        String[] keys = usecase.getParameterNames();
        for (int i = 0; i < keys.length; i++) {
            this.parameters.put(keys[i], usecase.getParameter(keys[i]));
        }
    }

    private String usecaseName;
    private String sourceUrl;

    private Map parameters = new HashMap();

    protected static final String USECASE_NAME = "usecaseName";

    protected static final String SOURCE_URL = "sourceUrl";

    protected String getUsecaseName() {
        return this.usecaseName;
    }
    
    protected String getSourceURL() {
        return this.sourceUrl;
    }

    protected Map getParameters() {
        return Collections.unmodifiableMap(this.parameters);
    }

    /**
     * @see org.apache.cocoon.components.cron.CronJob#execute(java.lang.String)
     */
    public void execute(String jobname) {
        UsecaseResolver resolver = null;
        Usecase usecase = null;
        try {
            resolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);
            usecase = resolver.resolve(getSourceURL(), getUsecaseName());
            usecase.setName(getUsecaseName());

            passParameters(usecase);

            usecase.checkPreconditions();
            List errorMessages = usecase.getErrorMessages();
            if (!errorMessages.isEmpty()) {
                logErrorMessages("Pre condition messages:", errorMessages);
            } else {
                usecase.checkExecutionConditions();
                errorMessages = usecase.getErrorMessages();
                if (!errorMessages.isEmpty()) {
                    logErrorMessages("Execution condition messages:", errorMessages);
                } else {
                    usecase.execute();
                    logErrorMessages("Execution messages:", usecase.getErrorMessages());
                    usecase.checkPostconditions();
                    logErrorMessages("Post condition messages:", usecase.getErrorMessages());
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
     * @param headline The headline of the messages.
     * @param errorMessages The messages to log.
     */
    protected void logErrorMessages(String headline, List errorMessages) {
        getLogger().error("Usecase [" + getUsecaseName() + "] - " + headline);
        for (Iterator i = errorMessages.iterator(); i.hasNext();) {
            getLogger().error((String) i.next());
        }
    }

    /**
     * @param usecase The usecase to pass the parameters to.
     */
    protected void passParameters(Usecase usecase) {
        Map parameters = getParameters();
        for (Iterator i = parameters.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            Object value = parameters.get(key);
            usecase.setParameter(key, value);
        }
    }

    /**
     * @see org.apache.cocoon.components.cron.ConfigurableCronJob#setup(org.apache.avalon.framework.parameters.Parameters,
     *      java.util.Map)
     */
    public void setup(Parameters parameters, Map objects) {
        this.parameters.putAll(Parameters.toProperties(parameters));
        this.usecaseName = (String) objects.get(USECASE_NAME);
        this.sourceUrl = (String) objects.get(SOURCE_URL);
    }

}