/*
 * $Id: TaskJob.java,v 1.16 2003/04/20 22:16:03 michi Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
/*
 * TaskJob.java
 *
 * Created on November 7, 2002, 3:58 PM
 */
package org.lenya.cms.scheduler;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.log4j.Category;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.lenya.cms.publishing.PublishingEnvironment;
import org.lenya.cms.task.AbstractTask;
import org.lenya.cms.task.Task;
import org.lenya.cms.task.TaskManager;

import java.io.File;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import org.apache.avalon.framework.parameters.ParameterException;
import org.w3c.dom.Element;
import org.lenya.cms.task.ExecutionException;
import org.lenya.xml.NamespaceHelper;


/**
 * A TaskJob is a Job that executes a Task. The task ID is obtained from the <code>task.id</code>
 * request parameter.
 *
 * @author <a href="mailto:ah@lenya.org">Andreas Hartmann</a>
 */
public class TaskJob
        extends ServletJob {
    public static final String TASK_ID = "id";
    public static final String TASK_PREFIX = "task";
    static Category log = Category.getInstance(TaskJob.class);

    protected Parameters getParameters(String servletContextPath, HttpServletRequest request) {
        String taskId = request.getParameter(JobDataMapWrapper.getFullName(TASK_PREFIX, TASK_ID));

        if ((taskId == null) || taskId.equals("")) {
            log.error("No task-id is provided!", new IllegalStateException());
        }

        log.debug("Creating data map for job " + taskId);

        String contextPath = request.getContextPath();
        log.debug("Context path: " + contextPath);

        // the publicationID is fetched from the session
        String publicationId = (String) request.getSession().getAttribute("org.lenya.cms.cocoon.acting.Authenticator.id");

        if ((publicationId == null) || publicationId.equals("")) {
            log.error("No publication ID provided! ", new IllegalStateException());
            publicationId = "no_such_publication";
        }

        // FIXME: Don't translate parameters
        String publicationPath = servletContextPath + PublishingEnvironment.PUBLICATION_PREFIX +
            publicationId + File.separator;

        Parameters parameters = new Parameters();

        parameters.setParameter(Task.PARAMETER_SERVLET_CONTEXT, servletContextPath);
        parameters.setParameter(Task.PARAMETER_CONTEXT_PREFIX, request.getContextPath() + "/");
        parameters.setParameter(Task.PARAMETER_SERVER_PORT,
            Integer.toString(request.getServerPort()));
        log.debug("\n-----------------------------------------------" +
            "\n- Server port from request: " + request.getServerPort() +
            "\n-----------------------------------------------");
        parameters.setParameter(Task.PARAMETER_SERVER_URI, "http://" + request.getServerName());
        parameters.setParameter(Task.PARAMETER_PUBLICATION_ID, publicationId);

        // Add Request Parameters
        Parameters requestParameters = new Parameters();

        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();

            if (name.startsWith(TASK_PREFIX + JobDataMapWrapper.SEPARATOR)) {
                String shortName = JobDataMapWrapper.getShortName(TASK_PREFIX, name);
                requestParameters.setParameter(shortName, request.getParameter(name));
            }
        }

        parameters.merge(requestParameters);

        // /Add Request Parameters
        return parameters;
    }

    /**
     * DOCUMENT ME!
     *
     * @param servletContextPath DOCUMENT ME!
     * @param request DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JobDataMap createJobData(String servletContextPath, HttpServletRequest request) {
        Parameters parameters = getParameters(servletContextPath, request);

        log.debug("Creating job data map:");

        try {
            JobDataMapWrapper map = new JobDataMapWrapper(TASK_PREFIX);
            String[] names = parameters.getNames();

            for (int i = 0; i < names.length; i++) {
                map.put(names[i], parameters.getParameter(names[i]));
            }

            return map.getMap();
        } catch (Exception e) {
            log.error("Cannot create job data map: ", e);

            return null;
        }
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
        JobDetail jobDetail = context.getJobDetail();
        JobDataMapWrapper map = new JobDataMapWrapper(jobDetail.getJobDataMap(), TASK_PREFIX);

        //------------------------------------------------------------
        // execute task
        //------------------------------------------------------------
        String taskId = map.get(TASK_ID);

        log.debug("\n-----------------------------------" + "\n Executing task '" + taskId + "'" +
            "\n-----------------------------------");

        String contextPath = map.get(Task.PARAMETER_SERVLET_CONTEXT);
        String publicationId = map.get(Task.PARAMETER_PUBLICATION_ID);
        String publicationPath = PublishingEnvironment.getPublicationPath(contextPath, publicationId);
        TaskManager manager = new TaskManager(publicationPath);
        Task task = manager.getTask(taskId);

        try {
            task.parameterize(map.getParameters());
            task.execute(contextPath);
        }
        catch (ParameterException e) {
            log.debug("Initializing task failed: ", e);
        }
        catch (ExecutionException e) {
            log.debug("Executing task failed: ", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param jobElement DOCUMENT ME!
     * @param servletContext DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JobDetail load(Element jobElement, String servletContext) {
        JobDataMap map = new JobDataMap();
        NamespaceHelper helper = SchedulerWrapper.getNamespaceHelper();
        Element taskElement = helper.getFirstChild(jobElement, "task");

        JobDataMapWrapper taskMap = new JobDataMapWrapper(map, TASK_PREFIX);

        String debugString = "\n----------------------------------" + "\nRestoring tasks:" +
            "\n----------------------------------" + "\nTask parameters:";

        Element[] parameterElements = helper.getChildren(taskElement, "parameter");

        for (int i = 0; i < parameterElements.length; i++) {
            String key = parameterElements[i].getAttribute("name");
            String value = parameterElements[i].getAttribute("value");
            taskMap.put(key, value);
            debugString = debugString + "\n" + key + " = " + value;
        }

        // replace servlet-context parameter with actual servlet context
        taskMap.put(AbstractTask.PARAMETER_SERVLET_CONTEXT, servletContext);
        debugString = debugString + "\nReplacing: " + AbstractTask.PARAMETER_SERVLET_CONTEXT +
            " = " + servletContext;

        debugString = debugString + "\nJob parameters:";

        JobDataMapWrapper jobMap = new JobDataMapWrapper(map, SchedulerWrapper.JOB_PREFIX);

        parameterElements = helper.getChildren(jobElement, "parameter");

        for (int i = 0; i < parameterElements.length; i++) {
            String key = parameterElements[i].getAttribute("name");
            String value = parameterElements[i].getAttribute("value");
            jobMap.put(key, value);
            debugString = debugString + "\n" + key + " = " + value;
        }

        log.debug(debugString);

        Class cl = null;
        String jobId = jobMap.get(SchedulerWrapper.JOB_ID);
        String jobGroup = jobMap.get(SchedulerWrapper.JOB_GROUP);

        try {
            cl = Class.forName(jobMap.get(SchedulerWrapper.JOB_CLASS));
        } catch (Exception e) {
            log.error("Cannot load job: ", e);
        }

        JobDetail jobDetail = new JobDetail(jobId, jobGroup, cl);
        jobDetail.setJobDataMap(map);

        return jobDetail;
    }

    /**
     * DOCUMENT ME!
     *
     * @param jobDetail DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element save(NamespaceHelper helper, JobDetail jobDetail) {
        Element jobElement = helper.createElement("job");
        JobDataMap map = jobDetail.getJobDataMap();

        JobDataMapWrapper jobMap = new JobDataMapWrapper(map, SchedulerWrapper.JOB_PREFIX);
        JobDataMapWrapper taskMap = new JobDataMapWrapper(map, TASK_PREFIX);

        // task parameters
        Element taskElement = helper.createElement("task");
        jobElement.appendChild(taskElement);

        Parameters taskParameters = taskMap.getParameters();
        String[] names = taskParameters.getNames();

        for (int i = 0; i < names.length; i++) {
            Element parameterElement = helper.createElement("parameter");
            taskElement.appendChild(parameterElement);
            parameterElement.setAttribute("name", names[i]);
            parameterElement.setAttribute("value", taskMap.get(names[i]));
        }

        // job parameters
        Parameters jobParameters = jobMap.getParameters();
        names = jobParameters.getNames();

        for (int i = 0; i < names.length; i++) {
            Element parameterElement = helper.createElement("parameter");
            jobElement.appendChild(parameterElement);
            parameterElement.setAttribute("name", names[i]);
            parameterElement.setAttribute("value", jobMap.get(names[i]));
        }

        return jobElement;
    }
}
