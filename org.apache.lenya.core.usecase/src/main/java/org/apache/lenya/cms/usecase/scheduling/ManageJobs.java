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
package org.apache.lenya.cms.usecase.scheduling;

import java.util.Arrays;

import org.apache.cocoon.components.cron.JobSchedulerEntry;
import org.apache.lenya.cms.usecase.DocumentUsecase;

/**
 * Usecase to manage scheduled jobs.
 * 
 * @version $Id$
 */
public class ManageJobs extends DocumentUsecase {

    protected static final String JOBS = "jobs";

    private UsecaseScheduler usecaseScheduler;

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        JobSchedulerEntry[] jobs = getUsecaseScheduler().getJobs();
        setParameter(JOBS, Arrays.asList(jobs));
    }

    protected UsecaseScheduler getUsecaseScheduler() {
        return usecaseScheduler;
    }

    /**
     * TODO: Bean wiring
     */
    public void setUsecaseScheduler(UsecaseScheduler usecaseScheduler) {
        this.usecaseScheduler = usecaseScheduler;
    }

}