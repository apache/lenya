/*
$Id: ServletJob.java,v 1.14 2003/08/29 11:35:21 andreas Exp $
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
package org.apache.lenya.cms.scheduler;

import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Category;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;

/**
 * Scheduling job that uses an HttpServletRequest to obtain its Job data.
 * @author Andreas Hartmann
 */
public abstract class ServletJob implements Job {

    private static Category log = Category.getInstance(ServletJob.class);

    /**
     * Creates the job data from an HTTP request.
     * @param servletContextPath The servlet context path.
     * @param request The request.
     * @return A job data map.
     * @throws SchedulerException when something went wrong.
     */
    public JobDataMap createJobData(String servletContextPath, HttpServletRequest request)
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

        JobDataMap map = jobDetail.getJobDataMap();
        NamespaceMap wrapper = new NamespaceMap(map, LoadQuartzServlet.PREFIX);
        String documentUrl = (String) wrapper.get(PARAMETER_DOCUMENT_URL);

        jobElement.setAttribute(ATTRIBUTE_DOCUMENT_URL, documentUrl);
        return jobElement;
    }
}
