/*
$Id: LoadQuartzServlet.java,v 1.29 2003/08/18 17:13:40 andreas Exp $
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
import org.apache.lenya.xml.DocumentHelper;

import org.apache.log4j.Category;

import org.quartz.SchedulerException;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;

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
 * @version CVS $Id: LoadQuartzServlet.java,v 1.29 2003/08/18 17:13:40 andreas Exp $
 */
public class LoadQuartzServlet extends HttpServlet {
    private static Category log = Category.getInstance(LoadQuartzServlet.class);
    private static SchedulerWrapper scheduler = null;
    private ServletContext servletContext;
    private String servletContextPath;
    private String schedulerConfigurations;

    public static final String PARAMETER_PUBLICATION_ID = "publication-id";
    public static final String PARAMETER_ACTION = "action";
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
        this.servletContextPath = this.servletContext.getRealPath("/");
        this.schedulerConfigurations = config.getInitParameter(CONFIGURATION_ELEMENT);
        log.debug(".init(): Servlet Context Path: " + this.servletContextPath);
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
        scheduler = new SchedulerWrapper(servletContextPath, schedulerConfigurations);

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
        log.debug("\n----------------------------------------------------------------");

        String action = request.getParameter(PARAMETER_ACTION);

        // in the case of modification or deletion the jobId is also
        // passed through a hidden field.
        String parameterName =
            JobDataMapWrapper.getFullName(SchedulerWrapper.JOB_PREFIX, SchedulerWrapper.JOB_ID);
        String jobId = request.getParameter(parameterName);

        String publicationId = request.getParameter(PARAMETER_PUBLICATION_ID);
        String documentUri = request.getParameter(SchedulerWrapper.DOCUMENT_URL);

        log.debug("--- Request Parameters -----------------------------------------");
        log.debug("Action:         [" + action + "]");
        log.debug("Job ID:         [" + jobId + "]");
        log.debug("Publication ID: [" + publicationId + "]");
        log.debug("Document URI:   [" + documentUri + "]");
        log.debug("----------------------------------------------------------------");

        logSessionAttributes(request);

        try {
            // check if the request wants to submit, modify or delete a job.
            if (action == null) {
                // simply return all scheduled jobs, which is done below
            } else if (action.equals("Add") || action.equals("Modify")) {
                Date startTime = getDate(request);

                if (action.equals("Add")) {
                    getScheduler().addJob(documentUri, publicationId, startTime, request);
                    log.debug(".handleRequest() Add : server-port:" + request.getServerPort());
                } else if (action.equals("Modify")) {
                    getScheduler().deleteJob(jobId, publicationId);
                    getScheduler().addJob(documentUri, publicationId, startTime, request);
                }
            } else if (action.equals("Delete")) {
                getScheduler().deleteJob(jobId, publicationId);
            }

            // handle the remainder of the request by simply returning all
            // scheduled jobs (for the gived documentID).
            PrintWriter writer = response.getWriter();
            response.setContentType("text/xml");

            Document snapshot;

            if (publicationId == null) {
                snapshot = getScheduler().getSnapshot();
            } else {
                snapshot = getScheduler().getSnapshot(publicationId);
            }

            DocumentHelper.writeDocument(snapshot, writer);
        } catch (Exception e) {
            log.error("Can't create job snapshot: ", e);
            throw new IOException(e.getMessage() + " (view log for details)");
        }
    }

    /**
     * Extracts the date from the request parameters.
     * @param request A request.
     * @return A date.
     * @throws IOException when something went wrong.
     */
    protected Date getDate(HttpServletRequest request) throws IOException {
        String startDay = request.getParameter("trigger.startDay");
        String startMonth = request.getParameter("trigger.startMonth");
        String startYear = request.getParameter("trigger.startYear");
        String startHour = request.getParameter("trigger.startHour");
        String startMin = request.getParameter("trigger.startMin");

        Date startTime = null;

        try {
            // Month value is 0-based
            startTime =
                new GregorianCalendar(
                    Integer.parseInt(startYear),
                    Integer.parseInt(startMonth) - 1,
                    Integer.parseInt(startDay),
                    Integer.parseInt(startHour),
                    Integer.parseInt(startMin))
                    .getTime();
        } catch (NumberFormatException e) {
            log.error(
                "NumberFormatException with parameters "
                    + "startYear, startMonth, startDay, startHour, startMin: "
                    + startDay
                    + ", "
                    + startMonth
                    + ", "
                    + startDay
                    + ", "
                    + startHour
                    + ", "
                    + startMin,
                e);
            throw new IOException("Parsing scheduling date/time failed!");
        }
        return startTime;
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
    public String getServletContextPath() {
        return servletContextPath;
    }

    /**
     * Restores the jobs.
     * @throws SchedulerException when something went wrong.
     */
    public void restoreJobs() throws SchedulerException {
        File publicationsDirectory =
            new File(getServletContextPath() + PublishingEnvironment.PUBLICATION_PREFIX);

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
