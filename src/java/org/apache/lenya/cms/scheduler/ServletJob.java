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

/* $Id: ServletJob.java,v 1.18 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.cms.scheduler;

import javax.servlet.http.HttpServletRequest;

import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Category;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.w3c.dom.Element;

/**
 * Scheduling job that uses an HttpServletRequest to obtain its Job data.
 */
public abstract class ServletJob implements Job {

    private static Category log = Category.getInstance(ServletJob.class);
    
    /**
     * Creates the job data from an HTTP request.
     * @param request The request.
     * @return A job data map.
     * @throws SchedulerException when something went wrong.
     */
    public JobDataMap createJobData(HttpServletRequest request)
        throws SchedulerException {
        JobDataMap map = new JobDataMap();
        String key = NamespaceMap.getFullName(LoadQuartzServlet.PREFIX, PARAMETER_DOCUMENT_URL);
        String documentUrl = request.getParameter(key);
        if (documentUrl == null) {
            throw new SchedulerException("Document URL must not be null!");
        }
        map.put(key, documentUrl);
        return map;
    }

    /**
     * Loads the job data from an XML element.
     * @param element An XML element.
     * @param jobGroup The job group the job belongs to.
     * @param servletContextPath The servlet context path.
     * @return A job detail object.
     * @throws SchedulerException when something went wrong.
     */
    public JobDetail load(Element element, String jobGroup, String servletContextPath)
        throws SchedulerException {
        String jobId = element.getAttribute(ATTRIBUTE_ID);
        JobDetail jobDetail = new JobDetail(jobId, jobGroup, getClass());
        return jobDetail;

    }

    public static final String ELEMENT_JOB = "job";
    public static final String ATTRIBUTE_ID = "id";
    public static final String ATTRIBUTE_CLASS = "class";
    public static final String ATTRIBUTE_DOCUMENT_URL = "url";
    public static final String ATTRIBUTE_SERVLET_CONTEXT = "servletcontext";
    public static final String PARAMETER_DOCUMENT_URL = "document-url";

    /**
     * Saves the job data to an XML element.
     * @param helper The namespace helper of the document the element shall belong to.
     * @param jobDetail The job detail to save.
     * @return An XML element.
     * @throws SchedulerException when something went wrong.
     */
    public Element save(NamespaceHelper helper, JobDetail jobDetail) throws SchedulerException {
        log.debug("Saving job");

        Element jobElement = helper.createElement(ELEMENT_JOB);
        jobElement.setAttribute(ATTRIBUTE_ID, jobDetail.getName());
        jobElement.setAttribute(ATTRIBUTE_CLASS, getClass().getName());

        String documentUrl = getDocumentUrl(jobDetail);
        jobElement.setAttribute(ATTRIBUTE_DOCUMENT_URL, documentUrl);
        
        return jobElement;
    }

    /**
     * Returns the document URL of a certain job.
     * @param jobDetail The job detail.
     * @return A string.
     */
    public String getDocumentUrl(JobDetail jobDetail) {
        JobDataMap map = jobDetail.getJobDataMap();
        NamespaceMap wrapper = new NamespaceMap(map, LoadQuartzServlet.PREFIX);
        String documentUrl = (String) wrapper.get(PARAMETER_DOCUMENT_URL);
        return documentUrl;
    }
    
    /**
     * Sets the document URL of a job.
     * @param jobDetail The job detail.
     * @param url The URL.
     */
    public void setDocumentUrl(JobDetail jobDetail, String url) {
        JobDataMap map = jobDetail.getJobDataMap();
        NamespaceMap wrapper = new NamespaceMap(map, LoadQuartzServlet.PREFIX);
        wrapper.put(PARAMETER_DOCUMENT_URL, url);
        jobDetail.setJobDataMap(map);
    }

}
