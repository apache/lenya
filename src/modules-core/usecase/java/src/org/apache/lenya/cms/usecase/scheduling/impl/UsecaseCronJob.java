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
package org.apache.lenya.cms.usecase.scheduling.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.components.CocoonComponentManager;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.components.cron.ConfigurableCronJob;
import org.apache.cocoon.components.cron.ServiceableCronJob;
import org.apache.cocoon.environment.Environment;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.commandline.CommandLineRequest;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.cms.usecase.Usecase;
import org.apache.lenya.cms.usecase.UsecaseInvoker;

/**
 * Job to schedule usecase execution.
 * 
 * @version $Id$
 */
public class UsecaseCronJob extends ServiceableCronJob implements ConfigurableCronJob,
        Contextualizable {

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
    private String userId;
    private String machineIp;

    private Map parameters = new HashMap();

    protected static final String USECASE_NAME = "usecaseName";
    protected static final String SOURCE_URL = "sourceUrl";
    protected static final String USER_ID = "userId";
    protected static final String MACHINE_IP = "machineIp";

    /**
     * @return The name of the usecase to execute.
     */
    public String getUsecaseName() {
        return this.usecaseName;
    }
    
    /**
     * @return The ID of the user who scheduled the job.
     */
    public String getUserId() {
        return this.userId;
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
        UsecaseInvoker invoker = null;
        try {
            setupOriginalRequest();
            authorizeRequest();

            invoker = (UsecaseInvoker) this.manager.lookup(UsecaseInvoker.ROLE);
            invoker.invoke(getSourceURL(), getUsecaseName(), getParameters());

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (invoker != null) {
                this.manager.release(invoker);
            }
        }
    }

    /**
     * Creates a new request object based on the information from the original request which
     * triggered the usecase.
     */
    protected void setupOriginalRequest() {
        Environment env = CocoonComponentManager.getCurrentEnvironment();

        Request request = ContextHelper.getRequest(this.context);
        Map attributes = new HashMap();
        for (Enumeration e = request.getAttributeNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            attributes.put(key, request.getAttribute(key));
        }

        Map requestParameters = new HashMap();
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            requestParameters.put(key, request.getParameter(key));
        }

        Map objectModel = ContextHelper.getObjectModel(this.context);
        objectModel.put(ObjectModelHelper.REQUEST_OBJECT, new CommandLineRequest(env,
                request.getContextPath(),
                request.getServletPath(),
                getSourceURL(),
                attributes,
                requestParameters));
    }

    /**
     * Initializes the session with the access control information.
     * @throws AccessControlException if an error occurs.
     * @throws ServiceException if the access controller resolver could not be created.
     */
    protected void authorizeRequest() throws AccessControlException, ServiceException {

        ServiceSelector selector = null;
        AccessControllerResolver acResolver = null;
        AccessController controller = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(AccessControllerResolver.ROLE
                    + "Selector");
            acResolver = (AccessControllerResolver) selector.select(AccessControllerResolver.DEFAULT_RESOLVER);
            controller = acResolver.resolveAccessController(getSourceURL());

            getLogger().debug("Add identity to session");
            getLogger().debug("User ID: [" + this.userId + "]");
            getLogger().debug("Machine: [" + this.machineIp + "]");

            Request request = ContextHelper.getRequest(this.context);
            controller.setupIdentity(request);
            Session session = request.getSession(false);
            Identity identity = (Identity) session.getAttribute(Identity.class.getName());
            Identifiable[] identifiables = identity.getIdentifiables();
            for (int i = 0; i < identifiables.length; i++) {
                identity.removeIdentifiable(identifiables[i]);
            }

            UserManager userManager = controller.getAccreditableManager().getUserManager();
            if (this.userId != null) {
                User user = userManager.getUser(this.userId);

                if (user == null) {
                    throw new RuntimeException("User [" + this.userId + "] does not exist!");
                }

                identity.addIdentifiable(user);
            }
            if (this.machineIp != null) {
                Machine machine = new Machine(this.machineIp);
                identity.addIdentifiable(machine);
            }

            controller.authorize(request);

        } finally {
            if (selector != null) {
                if (acResolver != null) {
                    if (controller != null) {
                        acResolver.release(controller);
                    }
                    selector.release(acResolver);
                }
                this.manager.release(selector);
            }
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
        this.userId = (String) objects.get(USER_ID);
        this.machineIp = (String) objects.get(MACHINE_IP);
    }

    private Context context;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        this.context = context;
    }

}