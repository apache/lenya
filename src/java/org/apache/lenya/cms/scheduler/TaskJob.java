/*
$Id: TaskJob.java,v 1.37 2004/02/21 13:44:43 gregor Exp $
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
 *
 * @author <a href="mailto:ah@apache.org">Andreas Hartmann</a>
 */
public class TaskJob extends ServletJob {
    private static Category log = Category.getInstance(TaskJob.class);

    /**
     * Get the parameters.
     * 
     * @param request the request
     * 
     * @return the parameters
     * @throws SchedulerException when something went wrong.
     */
    protected Map getParameters(HttpServletRequest request)
        throws SchedulerException {
        
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
    public JobDataMap createJobData(String servletContextPath, HttpServletRequest request)
        throws SchedulerException {
        log.debug("Creating job data map:");
        JobDataMap map = super.createJobData(servletContextPath, request);
        map.putAll(getParameters(request));
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
//            throw new JobExecutionException(e, true);
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
