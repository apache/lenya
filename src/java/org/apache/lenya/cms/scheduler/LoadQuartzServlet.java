/*
$Id: LoadQuartzServlet.java,v 1.33 2003/08/29 17:14:43 andreas Exp $
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

import org.apache.lenya.cms.publishing.PublishingEnvironment;
import org.apache.lenya.cms.scheduler.xml.TriggerHelper;
import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.xml.DocumentHelper;

import org.apache.log4j.Category;

import org.quartz.SchedulerException;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A simple servlet that starts an instance of a Quartz scheduler.
 *
 * @author <a href="mailto:christian.egli@lenya.com">Christian Egli</a>
 * @version CVS $Id: LoadQuartzServlet.java,v 1.33 2003/08/29 17:14:43 andreas Exp $
 */
public class LoadQuartzServlet extends HttpServlet {
    private static Category log = Category.getInstance(LoadQuartzServlet.class);
    private static SchedulerWrapper scheduler = null;
    private ServletContext servletContext;
    private String schedulerConfigurations;

    public static final String PREFIX = "scheduler";
    public static final String PARAMETER_ACTION = "action";
    public static final String PARAMETER_PUBLICATION_ID = "publication-id";
    public static final String CONFIGURATION_ELEMENT = "scheduler-configurations";

    /**
     * Returns the scheduler wrapper.
     * @return A scheduler wrapper.
     */
    protected static SchedulerWrapper getScheduler() {
        return scheduler;
    }

    /**
     * Initializes the servlet.
     * @param config The servlet configuration.
     * @throws ServletException when something went wrong.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        this.servletContext = config.getServletContext();
        this.schedulerConfigurations = config.getInitParameter(CONFIGURATION_ELEMENT);
        log.debug(
            ".init(): Servlet Context Path: " + getServletContextDirectory().getAbsolutePath());
        log.debug(".init(): Scheduler Configurations: " + this.schedulerConfigurations);

        try {
            log.info("Working?...");
            process();
            log.info("OK");
        } catch (Exception e) {
            log.error("Init of LoadQuartzServlet failed", e);
            throw new ServletException(e);
        }
    }

    /**
     * Process.
     *
     * @throws ServletException when an error occurs.
     * @throws SchedulerException when an error occurs.
     */
    public void process() throws ServletException, SchedulerException {
        scheduler =
            new SchedulerWrapper(
                getServletContextDirectory().getAbsolutePath(),
                schedulerConfigurations);

        try {
            ShutdownHook();
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        restoreJobs();
    }

    /**
     * Shuts down the scheduler.
     */
    public void destroy() {
        destroyScheduler();
    }

    /**
     * Shuts down the scheduler.
     */
    public static void destroyScheduler() {
        log.debug("destroy: ");
        getScheduler().shutdown();
    }

    /**
     * This method sets a ShutdownHook to the system This traps the CTRL+C or kill signal and
     * shutdows  Correctly the system.
     *
     * @throws Exception when something went wrong.
     */
    public static void ShutdownHook() throws Exception {
        log.debug("-------------------- ShutdownHook --------------------");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                LoadQuartzServlet.destroyScheduler();
            }
        });
        log.debug("-------------------- End ShutdownHook --------------------");
    }

    /**
     * Handles a GET request.
     * @param request The request.
     * @param response The response.
     * @throws IOException when an error occured.
     * @throws ServletException when an error occured.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        handleRequest(request, response);
    }

    /**
     * Handles a POST request.
     *
     * @param req The requust.
     * @param resp The response.
     *
     * @throws ServletException when an error occured.
     * @throws IOException when an error occured.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        doGet(req, resp);
    }

    protected static final String ADD = "add";
    protected static final String MODIFY = "modify";
    protected static final String DELETE = "delete";

    /**
     * Handles a servlet request.
     * @param request The request.
     * @param response The response.
     * @throws IOException when something went wrong.
     */
    protected void handleRequest(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        log.debug("----------------------------------------------------------------");
        log.debug("- Incoming request at URI: ");
        log.debug(
            request.getServerName() + ":" + request.getServerPort() + request.getRequestURI());
        log.debug("----------------------------------------------------------------");
        log.debug("Request parameters:");

        Map parameterMap = new HashMap();
        List keys = new ArrayList();
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            keys.add(key);
        }
        Collections.sort(keys);
        for (Iterator i = keys.iterator(); i.hasNext();) {
            String key = (String) i.next();
            String[] values = request.getParameterValues(key);
            log.debug("    [" + key + "] = [" + values[0] + "]");
            if (values.length == 1) {
                parameterMap.put(key, values[0]);
            } else {
                parameterMap.put(key, values);
            }
        }

        NamespaceMap schedulerParameters = new NamespaceMap(parameterMap, PREFIX);

        try {
            String publicationId = (String) schedulerParameters.get(PARAMETER_PUBLICATION_ID);
            log.debug("Scheduler invoked.");

            log.debug("Scheduler Parameters:");
            log.debug("    scheduler.publication-id: [" + publicationId + "]");

            logSessionAttributes(request);

            // check if the request wants to submit, modify or delete a job.
            String action = (String) schedulerParameters.get(PARAMETER_ACTION);
            log.debug("    scheduler.action:         [" + action + "]");
            if (action == null) {
            } else if (action.equals(ADD)) {
                Date startTime = TriggerHelper.getDate(schedulerParameters);
                getScheduler().addJob(publicationId, startTime, request);
            } else if (action.equals(MODIFY)) {
                Date startTime = TriggerHelper.getDate(schedulerParameters);
                String jobId = getJobId(schedulerParameters);
                getScheduler().modifyJob(jobId, publicationId, startTime);
            } else if (action.equals(DELETE)) {
                String jobId = getJobId(schedulerParameters);
                getScheduler().deleteJob(jobId, publicationId);
            }

            // handle the remainder of the request by simply returning all
            // scheduled jobs (for the given publication ID).
            PrintWriter writer = response.getWriter();
            response.setContentType("text/xml");

            Document snapshot = getScheduler().getSnapshot();

            DocumentHelper.writeDocument(snapshot, writer);
        } catch (Exception e) {
            log.error("Can't create job snapshot: ", e);
            throw new IOException(e.getMessage() + " (view log for details)");
        }
    }

    /**
     * Extracts the job ID from the scheduler parameters.
     * @param schedulerParameters A namespace map.
     * @return A string.
     */
    protected String getJobId(NamespaceMap schedulerParameters) {
        String parameterName =
            NamespaceMap.getFullName(SchedulerWrapper.JOB_PREFIX, SchedulerWrapper.JOB_ID);
        String jobId = (String) schedulerParameters.get(parameterName);
        log.debug("    scheduler.job.id:         [" + jobId + "]");
        return jobId;
    }

    /**
     * Logs the session attributes of a request.
     * @param request The request.
     */
    protected void logSessionAttributes(HttpServletRequest request) {
        log.debug("-------------------- Session Attributes --------------------");
        for (Enumeration e = request.getSession().getAttributeNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            log.debug(name + " = " + request.getSession().getAttribute(name));
        }
        log.debug("-------------------- End Session Attributes --------------------");
    }

    /**
     * Returns the servlet context path.
     *
     * @return A string.
     */
    public File getServletContextDirectory() {
        return new File(this.servletContext.getRealPath("/"));
    }

    /**
     * Restores the jobs.
     * @throws SchedulerException when something went wrong.
     */
    public void restoreJobs() throws SchedulerException {
        File publicationsDirectory =
            new File(getServletContextDirectory(), PublishingEnvironment.PUBLICATION_PREFIX);

        File[] publicationDirectories = publicationsDirectory.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        for (int i = 0; i < publicationDirectories.length; i++) {
            File directory = publicationDirectories[i];
            String publicationId = directory.getName();
            getScheduler().restoreJobs(publicationId);
        }
    }
}
