/*
$Id: TaskJob.java,v 1.29 2003/08/25 09:53:24 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
/*
 * TaskJob.java
 *
 * Created on November 7, 2002, 3:58 PM
 */
package org.apache.lenya.cms.scheduler;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.lenya.cms.task.ExecutionException;
import org.apache.lenya.cms.task.DefaultTaskWrapper;
import org.apache.lenya.cms.task.TaskWrapperParameters;
import org.apache.lenya.xml.NamespaceHelper;

import org.apache.log4j.Category;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;

/**
 * A TaskJob is a Job that executes a Task. The task ID is obtained from the <code>task.id</code>
 * request parameter.
 *
 * @author <a href="mailto:ah@apache.org">Andreas Hartmann</a>
 */
public class TaskJob extends ServletJob {
    private static Category log = Category.getInstance(TaskJob.class);

    /**
     * Get the parameters.
     * 
     * @param servletContextPath ther servlet-context
     * @param request the request
     * 
     * @return the parameters
     * @throws SchedulerException when something went wrong.
     */
    protected Parameters getParameters(String servletContextPath, HttpServletRequest request)
        throws SchedulerException {
        DefaultTaskWrapper wrapper = new DefaultTaskWrapper(request.getParameterMap());
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
    public JobDataMap createJobData(String servletContextPath, HttpServletRequest request)
        throws SchedulerException {
        log.debug("Creating job data map:");
        Parameters parameters = getParameters(servletContextPath, request);
        return new JobDataMap(Parameters.toProperties(parameters));

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
        DefaultTaskWrapper wrapper = new DefaultTaskWrapper(jobDetail.getJobDataMap());
        try {
            wrapper.execute();
        } catch (ExecutionException e) {
            throw new JobExecutionException(e, true);
        }
    }

    /**
     * Loads a job details object from an XML element. 
     *
     * @param jobElement The XML element.
     * @param servletContext The servlet context.
     * @param jobGroup The job group the job belongs to.
     *
     * @return A job details object.
     */
    public JobDetail load(Element jobElement, String servletContext, String jobGroup) {
        NamespaceHelper helper = SchedulerWrapper.getNamespaceHelper();

        // replace servlet-context parameter with actual servlet context
        /*
        taskMap.put(AbstractTask.PARAMETER_SERVLET_CONTEXT, servletContext);
        debugString =
            debugString
                + "\nReplacing: "
                + AbstractTask.PARAMETER_SERVLET_CONTEXT
                + " = "
                + servletContext;
        */

        JobDataMapWrapper jobMap = new JobDataMapWrapper(SchedulerWrapper.JOB_PREFIX);
        Element[] parameterElements = helper.getChildren(jobElement, "parameter");

        for (int i = 0; i < parameterElements.length; i++) {
            String key = parameterElements[i].getAttribute("name");
            String value = parameterElements[i].getAttribute("value");
            jobMap.put(key, value);
            log.debug("Setting job parameter: [" + key + "] = [" + value + "]");
        }

        Class cl = null;
        String jobId = jobMap.get(SchedulerWrapper.JOB_ID);

        try {
            cl = Class.forName(jobMap.get(SchedulerWrapper.JOB_CLASS));
        } catch (Exception e) {
            log.error("Cannot load job: ", e);
        }

        JobDetail jobDetail = new JobDetail(jobId, jobGroup, cl);
        
        JobDataMap jobDataMap = jobMap.getMap();
        DefaultTaskWrapper wrapper = new DefaultTaskWrapper(helper, jobElement);
        jobDataMap.putAll(Parameters.toProperties(wrapper.getParameters()));
        jobDetail.setJobDataMap(jobDataMap);

        return jobDetail;
    }

    /**
     * DOCUMENT ME!
     *
     * @param jobDetail DOCUMENT ME!
     * @param helper namespace helper
     *
     * @return DOCUMENT ME!
     */
    public Element save(NamespaceHelper helper, JobDetail jobDetail) {
        Element jobElement = helper.createElement("job");
        JobDataMap map = jobDetail.getJobDataMap();
        
        DefaultTaskWrapper wrapper = new DefaultTaskWrapper(map);
        jobElement.appendChild(wrapper.save(helper));

        JobDataMapWrapper jobMap = new JobDataMapWrapper(map, SchedulerWrapper.JOB_PREFIX);

        // job parameters
        Parameters jobParameters = jobMap.getParameters();
        String[] names = jobParameters.getNames();

        for (int i = 0; i < names.length; i++) {
            Element parameterElement = helper.createElement("parameter");
            jobElement.appendChild(parameterElement);
            parameterElement.setAttribute("name", names[i]);
            parameterElement.setAttribute("value", jobMap.get(names[i]));
        }

        return jobElement;
    }
}
