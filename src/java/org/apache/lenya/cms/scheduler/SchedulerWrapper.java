/*
$Id: SchedulerWrapper.java,v 1.22 2003/08/29 17:17:07 andreas Exp $
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
 * JobConfiguration.java
 *
 * Created on November 8, 2002, 3:38 PM
 */
package org.apache.lenya.cms.scheduler;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.scheduler.xml.TriggerHelper;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;

import org.apache.log4j.Category;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;

import org.quartz.impl.StdSchedulerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

/**
 * DOCUMENT ME!
 *
 * @author ah
 */
public class SchedulerWrapper {
    private static Category log = Category.getInstance(SchedulerWrapper.class);
    public static final String JOB_PREFIX = "job";
    public static final String JOB_ID = "id";
    private static int jobId = 0;
    public static final String ELEMENT_JOB_GROUP = "job-group";
    public static final String ELEMENT_JOB = "job";
    public static final String ELEMENT_TRIGGERS = "triggers";
    public static final String ELEMENT_TRIGGER = "trigger";
    public static final String TITLE_ELEMENT = "title";
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String CLASS_ATTRIBUTE = "class";
    private Scheduler scheduler = null;
    private String servletContextPath;
    private String schedulerConfigurationPath;

    /**
     * Creates a new instance of SchedulerWrapper
     *
     * @param servletContextPath The servlet context path.
     * @param schedulerConfigurationPath The scheduler configuration path.
     */
    public SchedulerWrapper(String servletContextPath, String schedulerConfigurationPath) {
        this.servletContextPath = servletContextPath;
        this.schedulerConfigurationPath = schedulerConfigurationPath;

        SchedulerFactory factory = new StdSchedulerFactory();
        log.info("------- Starting up -----------------------");

        try {
            scheduler = factory.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("Can't initialize SchedulerWrapper: ", e);
            log.error("------- Startup failed -------------------");
        }

        log.info("------- Startup complete ------------------");
    }

    /**
     * Returns the scheduler.
     * @return A scheduler.
     */
    private Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * Shuts down the scheduler.
     */
    public void shutdown() {
        log.info("------- Shutting Down ---------------------");

        // try to save state here
        try {
            getScheduler().shutdown();
        } catch (SchedulerException e) {
            log.error("------- Shutdown Failed -----------------", e);
        }

        log.info("------- Shutdown Complete -----------------");
    }

    /**
     * Returns the servlet context path.
     * @return The servlet context path.
     */
    protected String getServletContextPath() {
        return servletContextPath;
    }

    /**
     * Returns the scheduler configuration path.
     * @return A string.
     */
    protected String getSchedulerConfigurationPath() {
        return schedulerConfigurationPath;
    }

    /**
     * Returns the next job ID to use (calculated using the current time).
     * @return A string.
     */
    protected synchronized static String getNextJobId() {
        return "job_" + jobId++ +System.currentTimeMillis();
    }

    /**
     * Adds a job.
     * @param jobGroup The job group.
     * @param startTime The start time.
     * @param request The request to obtain the parameters from.
     * @throws SchedulerException when something went wrong.
     */
    protected void addJob(String jobGroup, Date startTime, HttpServletRequest request)
        throws SchedulerException {

        if (jobGroup == null) {
            throw new SchedulerException("Job group must not be null!");
        }

        try {
            log.debug("----------------------------------------------");
            log.debug("Adding Job for group [" + jobGroup + "]");

            // FIXME: more flexible
            Class jobClass = TaskJob.class;

            ServletJob job = ServletJobFactory.createJob(jobClass);
            JobDataMap map = job.createJobData(getServletContextPath(), request);

            String uniqueJobId = getNextJobId();
            log.debug("Job ID: [" + uniqueJobId + "]");

            JobDetail jobDetail = new JobDetail(uniqueJobId, jobGroup, jobClass);
            jobDetail.setJobDataMap(map);

            if (startTime.after(new GregorianCalendar().getTime())) {
                Trigger trigger =
                    TriggerHelper.createSimpleTrigger(uniqueJobId, jobGroup, startTime);
                addJob(jobDetail, trigger);
            } else {
                addJob(jobDetail);
            }

            log.debug("----------------------------------------------");

            writeSnapshot(jobGroup);
        } catch (Exception e) {
            log.error("Adding job failed: ", e);
            throw new SchedulerException(e);
        }
    }

    /**
     * Adds a job.
     * @param detail The job information.
     * @param trigger The trigger to trigger the job.
     */
    protected void addJob(JobDetail detail, Trigger trigger) {
        try {
            detail.setDurability(true);

            Date ft = getScheduler().scheduleJob(detail, trigger);
            log.debug("Job " + detail.getFullName() + " will run at: " + ft);
        } catch (Exception e) {
            log.error("Adding job failed: ", e);
        }
    }

    /**
     * Adds a job.
     * @param detail The job information.
     */
    protected void addJob(JobDetail detail) {
        try {
            detail.setDurability(true);
            getScheduler().addJob(detail, true);
        } catch (SchedulerException e) {
            log.error("Adding job failed: ", e);
        }
    }

    /**
     * Deletes a job.
     * @param jobName The job name.
     * @param jobGroup The job group.
     */
    protected void deleteJob(String jobName, String jobGroup) {
        try {
            log.debug("-----------------------------------");
            log.debug("\n Deleting job [" + jobGroup + "/" + jobName + "]");
            log.debug("-----------------------------------");
            getScheduler().deleteJob(jobName, jobGroup);
            writeSnapshot(jobGroup);
        } catch (Exception e) {
            log.error("Deleting job failed: ", e);
        }
    }

    public static final String SNAPSHOT_FILE =
        "config/scheduler/jobs.xml".replace('/', File.separatorChar);

    /**
     * Returns the job snapshot file for a job group. If a publication exists
     * with publication ID = job group, the file is obtained from the publication
     * directory. Otherwise, the generic Lenya snapshot file is used.
     * @param jobGroup The job group.
     * @return A file.
     * @throws SchedulerException when the publication could not be built.
     */
    public File getJobsFile(String jobGroup) throws SchedulerException {
        String publicationId = jobGroup;
        File jobsFile;
        if (PublicationFactory.existsPublication(publicationId, getServletContextPath())) {
            log.debug("Publication [" + publicationId + "] exists.");
            Publication publication;
            try {
                publication =
                    PublicationFactory.getPublication(publicationId, getServletContextPath());
            } catch (PublicationException e) {
                throw new SchedulerException(e);
            }
            jobsFile = new File(publication.getDirectory(), SNAPSHOT_FILE);
        } else {
            jobsFile = new File(getServletContextPath(), "lenya" + File.separator + SNAPSHOT_FILE);
        }
        log.debug("Resolved job snapshot file: [" + jobsFile.getAbsolutePath() + "]");
        return jobsFile;
    }

    /**
     * Writes a job snapshot.
     * @param jobGroup The job group.
     * @throws SchedulerException when something went wrong.
     */
    protected void writeSnapshot(String jobGroup) throws SchedulerException {

        log.debug("Writing job snapshot for group [" + jobGroup + "]");
        File jobsFile = getJobsFile(jobGroup);

        try {
            File directory = jobsFile.getParentFile();

            if (!directory.exists()) {
                directory.mkdirs();
                log.debug("Creating job snapshot directory: " + directory.getPath());
            }

            jobsFile.createNewFile();
            DocumentHelper.writeDocument(getSnapshot(jobGroup), jobsFile);
        } catch (Exception e) {
            log.error("Writing job snapshot failed: ", e);
        }
    }

    /**
     * Reads the scheduler configuration.
     * @return A configuration.
     */
    protected Configuration getSchedulerConfiguration() {
        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            String path = getServletContextPath() + getSchedulerConfigurationPath();
            log.debug("Initializing scheduler configuration: " + path);

            File configurationFile = new File(path);
            Configuration configuration = builder.buildFromFile(configurationFile);

            return configuration;
        } catch (Exception e) {
            log.error("Can't initialize scheduler configuration: ", e);

            return null;
        }
    }

    /**
     * Returns an XML element containing the trigger types.
     * @param helper The namespace helper of the document that shall contain the element.
     * @return An XML element.
     */
    protected Element getTriggerTypes(NamespaceHelper helper) {
        try {
            Configuration configuration = getSchedulerConfiguration();
            Configuration[] triggerConfigurations =
                configuration.getChild(ELEMENT_TRIGGERS).getChildren(ELEMENT_TRIGGER);

            Element triggersElement = helper.createElement("triggers");

            for (int i = 0; i < triggerConfigurations.length; i++) {
                Configuration conf = triggerConfigurations[i];
                String type = conf.getAttribute(TYPE_ATTRIBUTE);
                String className = conf.getAttribute(CLASS_ATTRIBUTE);

                Element triggerElement = helper.createElement("trigger");
                triggerElement.setAttribute("name", type);
                triggerElement.setAttribute("src", className);
                triggersElement.appendChild(triggerElement);
            }

            return triggersElement;
        } catch (Exception e) {
            log.error("Can't configure trigger types: " + e);

            return null;
        }
    }

    /**
     * Returns the trigger of a certain job.
     * @param jobName The job name.
     * @param jobGroup The job group.
     * @return A trigger.
     * @throws SchedulerException when something went wrong.
     */
    protected Trigger getTrigger(String jobName, String jobGroup) throws SchedulerException {
        log.debug("Resolving trigger for job [" + jobName + " ][ " + jobGroup + "]");
        String[] triggerGroups = getScheduler().getTriggerGroupNames();

        for (int groupIndex = 0; groupIndex < triggerGroups.length; groupIndex++) {
            String[] triggerNames = getScheduler().getTriggerNames(triggerGroups[groupIndex]);

            for (int nameIndex = 0; nameIndex < triggerNames.length; nameIndex++) {
                log.debug("Trigger name:  " + triggerNames[nameIndex]);

                Trigger trigger =
                    getScheduler().getTrigger(triggerNames[nameIndex], triggerGroups[groupIndex]);
                log.debug("Job group:     " + trigger.getJobGroup());

                if (trigger.getJobGroup().equals(jobGroup)
                    && trigger.getJobName().equals(jobName)) {
                    return trigger;
                }
            }
        }

        return null;
    }

    /**
     * Return an xml description of all scheduled jobs.
     * @return DOCUMENT ME!
     * @exception SchedulerException if an error occurs
     */
    public Document getSnapshot() throws SchedulerException {
        String[] jobGroupNames = getScheduler().getJobGroupNames();
        return getSnapshot(jobGroupNames);
    }

    /**
     * Return an xml description of all scheduled jobs for the given job group.
     *
     * @param jobGroup a<code>PrintWriter</code> value
     * @return DOCUMENT ME!
     * @exception SchedulerException if an error occurs
     */
    public Document getSnapshot(String jobGroup) throws SchedulerException {
        String[] jobGroupNames = { jobGroup };
        return getSnapshot(jobGroupNames);
    }

    /**
     * Return an XML description certain job groups.
     * @param jobGroupNames The job group names.
     * @return An XML document.
     * @exception SchedulerException if an error occurs
     */
    public Document getSnapshot(String[] jobGroupNames) throws SchedulerException {
        log.debug("Creating job snapshot");

        NamespaceHelper helper = getNamespaceHelper();
        Document document = helper.getDocument();
        Element root = document.getDocumentElement();

        // print a list of all available trigger types
        root.appendChild(getTriggerTypes(helper));

        for (int groupIndex = 0; groupIndex < jobGroupNames.length; groupIndex++) {
            log.debug("Creating job snapshot for group [" + jobGroupNames[groupIndex] + "]");
            root.appendChild(createSnapshot(helper, jobGroupNames[groupIndex]));
        }

        return document;
    }

    /**
     * Creates an XML element containting a snapshot of a job group.
     * @param helper The namespace helper to use.
     * @param jobGroup The job group.
     * @return An XMl element.
     * @throws SchedulerException when something went wrong.
     */
    protected Element createSnapshot(NamespaceHelper helper, String jobGroup)
        throws SchedulerException {
        Element jobGroupElement = helper.createElement(ELEMENT_JOB_GROUP);
        jobGroupElement.setAttribute("name", jobGroup);

        String[] jobNames = getScheduler().getJobNames(jobGroup);

        for (int nameIndex = 0; nameIndex < jobNames.length; nameIndex++) {
            JobDetail jobDetail = getScheduler().getJobDetail(jobNames[nameIndex], jobGroup);

            ServletJob job = ServletJobFactory.createJob(jobDetail.getJobClass());
            Element jobElement = job.save(helper, jobDetail);
            jobGroupElement.appendChild(jobElement);

            Trigger trigger = getTrigger(jobNames[nameIndex], jobGroup);

            if (trigger != null) {
                Element triggerElement = TriggerHelper.createElement(helper, trigger);
                jobElement.appendChild(triggerElement);
            }
        }

        return jobGroupElement;
    }

    /**
     * Restores the jobs of a certain job group from the snapshot file.
     * @param jobGroup The job group.
     * @throws SchedulerException when something went wrong.
     */
    public void restoreJobs(String jobGroup) throws SchedulerException {
        log.debug("--------------------------------------------------");
        log.debug("Restoring jobs for job group [" + jobGroup + "]");
        log.debug("--------------------------------------------------");

        File jobsFile = getJobsFile(jobGroup);

        if (jobsFile.exists()) {
            try {
                Document document = DocumentHelper.readDocument(jobsFile);
                Element schedulerElement = document.getDocumentElement();
                NamespaceHelper helper = getNamespaceHelper(document);

                Element jobGroupElement = helper.getFirstChild(schedulerElement, ELEMENT_JOB_GROUP);
                if (jobGroupElement == null) {
                    throw new SchedulerException("No <job-group> element found!");
                }

                String jobGroupAttribute = jobGroupElement.getAttribute("name");

                if (!jobGroupAttribute.equals(jobGroup)) {
                    throw new SchedulerException(
                        "The jobs.xml file contains a wrong job group: ["
                            + jobGroupAttribute
                            + "]");
                } else {
                    Element[] jobElements = helper.getChildren(jobGroupElement, ELEMENT_JOB);

                    for (int i = 0; i < jobElements.length; i++) {
                        restoreJob(jobElements[i], jobGroup);
                    }
                }
            } catch (Exception e) {
                log.error("Restoring jobs failed: ", e);
            }
        }
    }

    /**
     * Restores the jobs from a certain XML element.
     * @param jobElement The XML element.
     * @param jobGroup The job group the job belongs to.
     */
    protected void restoreJob(Element jobElement, String jobGroup) {
        log.debug("Restoring job ");

        NamespaceHelper helper = getNamespaceHelper();

        try {
            String jobClassName = jobElement.getAttribute(ServletJob.ATTRIBUTE_CLASS);
            ServletJob job = ServletJobFactory.createJob(jobClassName);
            JobDetail jobDetail = job.load(jobElement, jobGroup, getServletContextPath());

            Element triggerElement = helper.getFirstChild(jobElement, "trigger");

            if (triggerElement != null) {
                Trigger trigger =
                    TriggerHelper.createTrigger(
                        triggerElement,
                        jobDetail.getName(),
                        jobDetail.getGroup());

                // FIXME: In the case of CronTrigger, getFinalFireTime does not make sense!
                addJob(jobDetail, trigger);
            } else {
                addJob(jobDetail);
            }
        } catch (Exception e) {
            log.error("Could not restore job: ", e);
        }
    }

    /** The namespace for the <code>jobs.xml</code> file. */
    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/scheduler/1.0";

    /**
     * Returns a scheduler namespace helper for a document.
     * @param document The XML document.
     * @return a namespace helper.
     */
    public static NamespaceHelper getNamespaceHelper(Document document) {
        return new NamespaceHelper(NAMESPACE, "sch", document);
    }

    /**
     * Returns a new scheduler namespace helper with an document containing
     * a &lt;sch:scheduler&gt; element.
     * @return a namespace helper.
     */
    public static NamespaceHelper getNamespaceHelper() {
        try {
            return new NamespaceHelper(NAMESPACE, "sch", "scheduler");
        } catch (Exception e) {
            log.error("Could not create namespace helper: ", e);

            return null;
        }
    }

    /**
     * Modifies the execution time of a job.
     * @param jobId The job ID.
     * @param jobGroup The job group.
     * @param startTime The new start time.
     * @throws SchedulerException when the job was not found.
     */
    public void modifyJob(String jobId, String jobGroup, Date startTime)
        throws SchedulerException {
        log.debug("Modifying job [" + jobId + "][" + jobGroup + "]");
        
        JobDetail jobDetail = getScheduler().getJobDetail(jobId, jobGroup);
        if (jobDetail == null) {
            throw new SchedulerException("Job not found!");
        }
        
        Trigger trigger = getTrigger(jobDetail.getName(), jobGroup);
        if (trigger == null) {
            log.debug("No trigger found.");
        }
        else {
            log.debug("Trigger found. Setting new start time.");
            getScheduler().unscheduleJob(trigger.getName(), trigger.getGroup());

            jobDetail.setDurability(true);
            if (startTime.after(new GregorianCalendar().getTime())) {
                trigger = TriggerHelper.createSimpleTrigger(jobId, jobGroup, startTime);
                getScheduler().scheduleJob(trigger);
            }
            writeSnapshot(jobGroup);
        }
    }
}
