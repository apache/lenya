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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.components.cron.CronJob;
import org.apache.cocoon.components.cron.JobScheduler;
import org.apache.cocoon.components.cron.JobSchedulerEntry;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.UUIDGenerator;
import org.apache.lenya.cms.usecase.Usecase;
import org.apache.lenya.cms.usecase.scheduling.UsecaseScheduler;

/**
 * <p>
 * Usecase scheduler implementation.
 * </p>
 * <p>
 * The names of the scheduled jobs have the syntax
 * <code>{usecaseName}:{userId}:{pubId}</code>.
 * 
 * @version $Id$
 */
public class UsecaseSchedulerImpl extends AbstractLogEnabled implements UsecaseScheduler,
        Serviceable, Contextualizable, Disposable {

    private UUIDGenerator uuidGenerator;

    /**
     * @see org.apache.lenya.cms.usecase.scheduling.UsecaseScheduler#schedule(org.apache.lenya.cms.usecase.Usecase,
     *      java.util.Date)
     */
    public void schedule(Usecase usecase, Date date) {
        JobScheduler scheduler = null;
        try {
            scheduler = (JobScheduler) this.manager.lookup(JobScheduler.ROLE);

            Parameters parameters = new Parameters();
            String[] names = usecase.getParameterNames();
            for (int i = 0; i < names.length; i++) {
                Object value = usecase.getParameter(names[i]);
                if (value instanceof String) {
                    parameters.setParameter(names[i], (String) value);
                } else {
                    getLogger().warn(
                            "Parameter [" + names[i] + "] = [" + value + "] ("
                                    + value.getClass().getName()
                                    + ") ignored, only string values are supported.");
                }
            }

            Map objects = new HashMap();
            objects.put(UsecaseCronJob.USECASE_NAME, usecase.getName());
            objects.put(UsecaseCronJob.SOURCE_URL, usecase.getSourceURL());

            String userId = "";
            Request request = ContextHelper.getRequest(this.context);
            Session session = request.getSession(false);
            if (session != null) {
                Identity identity = (Identity) session.getAttribute(Identity.class.getName());
                if (identity != null) {
                    User user = identity.getUser();
                    if (user != null) {
                        userId = user.getId();
                        objects.put(UsecaseCronJob.USER_ID, userId);
                    }
                    Machine machine = identity.getMachine();
                    if (machine != null) {
                        objects.put(UsecaseCronJob.MACHINE_IP, machine.getIp());
                    }
                }
            }

            String role = CronJob.class.getName() + "/usecase";
            String name = getJobName();
            scheduler.fireJobAt(date, name, role, parameters, objects);

        } catch (Exception e) {
            getLogger().error("Could not create job: ", e);
            throw new RuntimeException(e);
        } finally {
            if (scheduler != null) {
                this.manager.release(scheduler);
            }
        }
    }
    
    protected UUIDGenerator getUuidGenerator() {
        if (this.uuidGenerator == null) {
            try {
                this.uuidGenerator = (UUIDGenerator) this.manager.lookup(UUIDGenerator.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.uuidGenerator;
    }

    protected String getJobName() {
        return getUuidGenerator().nextUUID();
    }

    protected ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    private Context context;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        this.context = context;
    }

    /**
     * @see org.apache.lenya.cms.usecase.scheduling.UsecaseScheduler#getJobs()
     */
    public JobSchedulerEntry[] getJobs() {
        JobScheduler scheduler = null;
        JobSchedulerEntry[] entries = null;
        try {
            scheduler = (JobScheduler) this.manager.lookup(JobScheduler.ROLE);

            String[] jobNames = scheduler.getJobNames();
            entries = new JobSchedulerEntry[jobNames.length];

            for (int i = 0; i < jobNames.length; i++) {
                JobSchedulerEntry entry = scheduler.getJobSchedulerEntry(jobNames[i]);
                entries[i] = entry;
            }

        } catch (Exception e) {
            getLogger().error("Could not obtain job list: ", e);
            throw new RuntimeException(e);
        } finally {
            if (scheduler != null) {
                this.manager.release(scheduler);
            }
        }
        return entries;
    }

    protected String getPublicationName(Usecase usecase) {
        URLInformation info = new URLInformation(usecase.getSourceURL());
        return info.getPublicationId();
    }

    public void dispose() {
        if (this.uuidGenerator != null) {
            this.manager.release(this.uuidGenerator);
        }
    }
}
