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

/* $Id: TaskJob.java,v 1.39 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.cms.scheduler;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.lenya.cms.task.DefaultTaskWrapper;
import org.apache.lenya.cms.task.ExecutionException;
import org.apache.lenya.cms.task.TaskParameters;
import org.apache.lenya.cms.task.TaskWrapper;
import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Category;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.w3c.dom.Element;

/**
 * A TaskJob is a Job that executes a Task. The task ID is obtained from the <code>task-id</code>
 * request parameter.
 */
public class TaskJob extends ServletJob {
    private static Category log = Category.getInstance(TaskJob.class);

    /**
     * Un-prefix the parameters.
     * 
     * @param wrapperMap the prefixed parameters.
     * 
     * @return the parameters
     * @throws SchedulerException when something went wrong.
     */
    protected Map stripPrefixes(Map wrapperMap)
        throws SchedulerException {
        
        NamespaceMap taskParameters = new NamespaceMap(TaskParameters.PREFIX);
        taskParameters.putAll(wrapperMap);
        wrapperMap.putAll(taskParameters.getPrefixedMap());
        
        DefaultTaskWrapper wrapper = new DefaultTaskWrapper(wrapperMap);
        return wrapper.getParameters();
    }

    /**
     * Creates the job data for a job.
     *
     * @param servletContextPath The servlet context path.
     * @param request The request.
     *
     * @return A job data map.
     * @throws SchedulerException when something went wrong.
     */
    public JobDataMap createJobData(HttpServletRequest request)
        throws SchedulerException {
        log.debug("Creating job data map:");
        JobDataMap map = super.createJobData(request);

        Enumeration parameters = request.getParameterNames();
        Map wrapperMap = new HashMap();
        while (parameters.hasMoreElements()) {
            String key = (String) parameters.nextElement();
            Object value;
            String[] values = request.getParameterValues(key);
            if (values.length == 1) {
                value = values[0];
            }
            else {
                value = values;
            }
            wrapperMap.put(key, value);
        }

        map.putAll(stripPrefixes(wrapperMap));
        return map;
    }

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a <code>{@link
     * org.quartz.Trigger}</code> fires that is associated with the <code>Job</code>.
     * </p>
     *
     * @param context DOCUMENT ME!
     *
     * @throws JobExecutionException if there is an exception while executing the job.
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Executing job");
        JobDetail jobDetail = context.getJobDetail();
        
        DefaultTaskWrapper wrapper = new DefaultTaskWrapper(jobDetail.getJobDataMap());
        try {
            wrapper.execute();
        } catch (ExecutionException e) {
        	log.error("Task execution failed: ", e);
        }
    }

    /**
     * Loads a job details object from an XML element. 
     *
     * @param jobElement The XML element.
     * @param jobGroup The job group the job belongs to.
     * @param servletContextPath The servlet context path.
     * @throws SchedulerException when something went wrong.
     *
     * @return A job details object.
     */
    public JobDetail load(Element jobElement, String jobGroup, String servletContextPath) throws SchedulerException {
        JobDetail jobDetail = super.load(jobElement, jobGroup, servletContextPath);
        
        NamespaceHelper helper = SchedulerStore.getNamespaceHelper();
        DefaultTaskWrapper wrapper = new DefaultTaskWrapper(helper, jobElement);
        wrapper.getTaskParameters().setServletContextPath(servletContextPath);
        
        JobDataMap map = new JobDataMap(wrapper.getParameters());
        jobDetail.setJobDataMap(map);
        
        return jobDetail;
    }

    /**
     * DOCUMENT ME!
     *
     * @param jobDetail DOCUMENT ME!
     * @param helper namespace helper
     * @throws SchedulerException when something went wrong.
     *
     * @return DOCUMENT ME!
     */
    public Element save(NamespaceHelper helper, JobDetail jobDetail) throws SchedulerException {
        
        Element jobElement = super.save(helper, jobDetail);
        TaskWrapper wrapper = new DefaultTaskWrapper(jobDetail.getJobDataMap());
        jobElement.appendChild(wrapper.save(helper));
        
        return jobElement;
    }
}
