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

/* $Id$  */

package org.apache.lenya.cms.scheduler;

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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publishing.PublishingEnvironment;
import org.apache.lenya.cms.scheduler.xml.TriggerHelper;
import org.apache.lenya.util.NamespaceMap;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.w3c.dom.Document;

/**
 * A simple servlet that starts an instance of a Quartz scheduler.
 */
public class LoadQuartzServlet extends HttpServlet {
    private static final class IsDirectoryFileFilter implements FileFilter {
        /**
         * @see java.io.FileFilter#accept(java.io.File)
         */
        public boolean accept(File file) {
            return file.isDirectory();
        }
    }

    private static Logger log = Logger.getLogger(LoadQuartzServlet.class);
    private static SchedulerWrapper scheduler = null;
    private ServletContext servletContext;
    private String schedulerConfigurations;

    /**
     * <code>PREFIX</code> Scheduler namespace prefix
     */
    public static final String PREFIX = "scheduler";
    /**
     * <code>PARAMETER_ACTION</code> The action parameter
     */
    public static final String PARAMETER_ACTION = "action";
    /**
     * <code>PARAMETER_PUBLICATION_ID</code> The publication id parameter
     */
    public static final String PARAMETER_PUBLICATION_ID = "publication-id";
    /**
     * <code>PARAMETER_DOCUMENT_URL</code> The document URL parameter
     */
    public static final String PARAMETER_DOCUMENT_URL = "document-url";
    /**
     * <code>CONFIGURATION_ELEMENT</code> The configuration element
     */
    public static final String CONFIGURATION_ELEMENT = "scheduler-configurations";
    /**
     * <code>SERVLET_URL</code> The scheduler servlet URL
     */
    public static final String SERVLET_URL = "/servlet/QuartzSchedulerServlet";

    /**
     * Returns the scheduler wrapper.
     * @return A scheduler wrapper.
     */
    public static SchedulerWrapper getScheduler() {
        return scheduler;
    }

    /**
     * Maps servlet context names to servlets.
     */
    private static Map servlets = new HashMap();

    /**
     * Initializes the servlet.
     * @param config The servlet configuration.
     * @throws ServletException when something went wrong.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        this.schedulerConfigurations = config.getInitParameter(CONFIGURATION_ELEMENT);
        this.servletContext = config.getServletContext();

        log.debug(".init(): Servlet Context Path: "
                + getServletContextDirectory().getAbsolutePath());

        try {
            log.debug("Storing servlet");
            String contextPath = getServletContextDirectory().getCanonicalPath();
            log.debug("  Context path: [" + contextPath + "]");
            servlets.put(contextPath, this);
        } catch (IOException e) {
            throw new ServletException(e);
        }

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
     * @throws ServletException when an error occurs.
     * @throws SchedulerException when an error occurs.
     */
    public void process() throws ServletException, SchedulerException {
        scheduler = new SchedulerWrapper(getServletContextDirectory().getAbsolutePath(),
                this.schedulerConfigurations);

        try {
            shutdownHook();
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServletException(e);
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
     * This method sets a ShutdownHook to the system This traps the CTRL+C or
     * kill signal and shutdows Correctly the system.
     * 
     * @throws Exception when something went wrong.
     */
    public static void shutdownHook() throws Exception {
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
            ServletException {
        handleRequest(request, response);
    }

    /**
     * Handles a POST request.
     * @param req The requust.
     * @param resp The response.
     * @throws ServletException when an error occured.
     * @throws IOException when an error occured.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        doGet(req, resp);
    }

    protected static final String ADD = "add";
    protected static final String MODIFY = "modify";
    protected static final String DELETE = "delete";
    protected static final String DOCUMENT_DELETED = "document-deleted";

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
        log
                .debug(request.getServerName() + ":" + request.getServerPort()
                        + request.getRequestURI());
        log.debug("----------------------------------------------------------------");
        log.debug("Request parameters:");

        NamespaceMap schedulerParameters = getSchedulerParameters(request);

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
                // do nothing
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
            } else if (action.equals(DOCUMENT_DELETED)) {

                PublicationFactory factory = PublicationFactory.getInstance(new ConsoleLogger());
                Publication publication = factory.getPublication(publicationId,
                        getServletContextDirectory().getAbsolutePath());

                String documentUrl = (String) schedulerParameters.get(PARAMETER_DOCUMENT_URL);
                DocumentIdentityMap map = new DocumentIdentityMap(null);
                org.apache.lenya.cms.publication.Document document = map.getFactory()
                        .getFromURL(publication, documentUrl);
                deleteDocumentJobs(document);
            }

            // handle the remainder of the request by simply returning all
            // scheduled jobs (for the given publication ID).
            PrintWriter writer = response.getWriter();
            response.setContentType("text/xml");

            Document snapshot = getScheduler().getSnapshot();

            DocumentHelper.writeDocument(snapshot, writer);
        } catch (DocumentBuildException e) {
            log.error("Can't create job snapshot: ", e);
            throw new IOException(e.getMessage() + " (view log for details)");
        } catch (TransformerConfigurationException e) {
            log.error("Can't create job snapshot: ", e);
            throw new IOException(e.getMessage() + " (view log for details)");
        } catch (IOException e) {
            log.error("Can't create job snapshot: ", e);
            throw new IOException(e.getMessage() + " (view log for details)");
        } catch (SchedulerException e) {
            log.error("Can't create job snapshot: ", e);
            throw new IOException(e.getMessage() + " (view log for details)");
        } catch (PublicationException e) {
            log.error("Can't create job snapshot: ", e);
            throw new IOException(e.getMessage() + " (view log for details)");
        } catch (TransformerException e) {
            log.error("Can't create job snapshot: ", e);
            throw new IOException(e.getMessage() + " (view log for details)");
        }
    }

    /**
     * Extracts the scheduler parameters from a request.
     * @param request The request.
     * @return A namespace map.
     */
    public static NamespaceMap getSchedulerParameters(HttpServletRequest request) {
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
        return schedulerParameters;
    }

    /**
     * Deletes
     * @param document
     * @throws DocumentBuildException
     * @throws SchedulerException
     * @throws PublicationException
     */
    public void deleteDocumentJobs(org.apache.lenya.cms.publication.Document document)
            throws DocumentBuildException, SchedulerException, PublicationException {
        log.debug("Requested to delete jobs for document URL [" + document.getCanonicalWebappURL()
                + "]");
        getScheduler().deleteJobs(document);
    }

    /**
     * Extracts the job ID from the scheduler parameters.
     * @param schedulerParameters A namespace map.
     * @return A string.
     */
    protected String getJobId(NamespaceMap schedulerParameters) {
        String parameterName = NamespaceMap.getFullName(SchedulerWrapper.JOB_PREFIX,
                SchedulerWrapper.JOB_ID);
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

        File publicationsDirectory = new File(getServletContextDirectory(),
                PublishingEnvironment.PUBLICATION_PREFIX);

        File[] publicationDirectories = publicationsDirectory
                .listFiles(new IsDirectoryFileFilter());

        log.debug("=========================================");
        log.debug("  Restoring jobs.");
        log.debug("    servlet context: [" + getServletContextDirectory() + "]");
        log.debug("    publications directory: [" + publicationsDirectory + "]");
        log.debug("=========================================");

        for (int i = 0; i < publicationDirectories.length; i++) {
            File directory = publicationDirectories[i];
            String publicationId = directory.getName();
            PublicationFactory factory = PublicationFactory.getInstance(new ConsoleLogger());
            Publication publication;
            try {
                publication = factory.getPublication(publicationId, getServletContextDirectory());
            } catch (PublicationException e) {
                throw new SchedulerException(e);
            }
            if (publication.exists()) {
                getScheduler().restoreJobs(publicationId);
            }
        }
    }

    /**
     * Returns the servlet for a certain canonical servlet context path.
     * @param contextPath The canonical servlet context path.
     * @return A LoadQuartzServlet.
     */
    public static LoadQuartzServlet getServlet(String contextPath) {
        return (LoadQuartzServlet) servlets.get(contextPath);
    }

    /**
     * Generates the request URI needed to delete the jobs for a certain
     * document.
     * @param port The port of the servlet
     * @param servletContextPath The context path of the servlet
     * @param document The document.
     * @return A string.
     */
    public static String getDeleteDocumentRequestURI(String port, String servletContextPath,
            org.apache.lenya.cms.publication.Document document) {

        NamespaceMap requestParameters = new NamespaceMap(PREFIX);
        requestParameters.put(PARAMETER_ACTION, DOCUMENT_DELETED);
        requestParameters.put(PARAMETER_PUBLICATION_ID, document.getPublication().getId());
        requestParameters.put(PARAMETER_DOCUMENT_URL, document.getCanonicalWebappURL());

        StringBuffer buf = new StringBuffer();
        buf.append("http://127.0.0.1:" + port + servletContextPath + "?");
        Map map = requestParameters.getMap();

        String[] keys = (String[]) map.keySet().toArray(new String[map.keySet().size()]);
        for (int i = 0; i < keys.length; i++) {
            if (i > 0) {
                buf.append("&");
            }
            String value = (String) map.get(keys[i]);
            buf.append(keys[i] + "=" + value);
        }

        return buf.toString();
    }
}