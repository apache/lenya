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

/* $Id: JobWrapper.java,v 1.3 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.cms.scheduler;

import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * Wrapper for job information.
 */
public class JobWrapper {
    
    private JobDetail jobDetail;
    private Trigger trigger;
    
    /**
     * Ctor.
     * @param detail The job detail.
     * @param trigger The trigger.
     */
    public JobWrapper(JobDetail detail, Trigger trigger) {
        this.jobDetail = detail;
        this.trigger = trigger;
    }
    
    /**
     * Returns the job detail.
     * @return A job detail object.
     */
    public JobDetail getJobDetail() {
        return jobDetail;
    }
    
    /**
     * Returns the servlet job.
     * @return A servlet job.
     */
    public ServletJob getJob() {
        ServletJob job = ServletJobFactory.createJob(jobDetail.getJobClass());
        return job;
    }
    
    /**
     * Returns the trigger.
     * @return A trigger.
     */
    public Trigger getTrigger() {
        return trigger;
    }

}
