/*
$Id: SchedulerWrapper.java,v 1.26 2004/01/09 11:14:39 andreas Exp $
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
    private Scheduler scheduler = null;
    private String servletContextPath;
    private String schedulerConfigurationPath;
    private SchedulerStore store = new SchedulerStore();

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

            scheduler.addSchedulerListener(new AbstractSchedulerListener());
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("Can't initialize SchedulerWrapper: ", e);
            log.error("------- Startup failed -------------------");
        }

        log.info("------- Startup complete ------------------");
    }

    /**
     * Returns the store.
     * @return A scheduler store.
     */
    protected SchedulerStore getStore() {
        return store;
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

            Date now = new GregorianCalendar().getTime();
            if (log.isDebugEnabled()) {
                DateFormat format = new SimpleDateFormat();
                log.debug("Trigger time: [" + format.format(startTime) + "]");
                log.debug("Current time: [" + format.format(now) + "]");
            }

            if (startTime.after(now)) {
                Trigger trigger =
                    TriggerHelper.createSimpleTrigger(uniqueJobId, jobGroup, startTime);
                addJob(jobDetail, trigger);
                log.debug("Scheduling job.");
            } else {
                addJob(jobDetail);
                log.debug("Adding job without scheduling.");
            }

            log.debug("----------------------------------------------");

            store.writeSnapshot(getPublication(jobGroup), getJobWrappers(jobGroup));
        } catch (Exception e) {
            log.error("Adding job failed: ", e);
            throw new SchedulerException(e);
        }
    }

    /**
     * Returns the publication for a job group.
     * @param jobGroup A job group.
     * @return A publication.
     * @throws PublicationException when the publication does not exist.
     */
    protected Publication getPublication(String jobGroup) throws PublicationException {
        return PublicationFactory.getPublication(jobGroup, getServletContextPath());
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
            getStore().writeSnapshot(getPublication(jobGroup), getJobWrappers(jobGroup));
        } catch (Exception e) {
            log.error("Deleting job failed: ", e);
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

    public static final String ELEMENT_TRIGGERS = "triggers";
    public static final String ELEMENT_TRIGGER = "trigger";
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String CLASS_ATTRIBUTE = "class";

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
     * Return an XML description certain job groups.
     * @param jobGroupNames The job group names.
     * @return An XML document.
     * @exception SchedulerException if an error occurs
     */
    public Document getSnapshot(String[] jobGroupNames) throws SchedulerException {
        log.debug("Creating job snapshot");

        NamespaceHelper helper = SchedulerStore.getNamespaceHelper();
        Document document = helper.getDocument();
        Element root = document.getDocumentElement();

        // print a list of all available trigger types
        root.appendChild(getTriggerTypes(helper));

        for (int groupIndex = 0; groupIndex < jobGroupNames.length; groupIndex++) {
            log.debug("Creating job snapshot for group [" + jobGroupNames[groupIndex] + "]");
            root.appendChild(getSnapshot(helper, jobGroupNames[groupIndex]));
        }

        return document;
    }

    /**
     * Returns the snapshot of a certain job group.
     * @param helper The namespace helper.
     * @param group The job group.
     * @return An XML element.
     * @throws SchedulerException when something went wrong.
     */
    protected Element getSnapshot(NamespaceHelper helper, String group) throws SchedulerException {
        JobWrapper[] jobs = getJobWrappers(group);
        Element element;
        try {
            element = getStore().createSnapshot(helper, getPublication(group), jobs);
        } catch (SchedulerException e) {
            throw e;
        } catch (PublicationException e) {
            throw new SchedulerException(e);
        }
        return element;
    }

    /**
     * Returns the job wrappers for a certain job group.
     * @param jobGroupName The job group.
     * @return An array of job wrappers.
     * @throws SchedulerException when something went wrong.
     */
    protected JobWrapper[] getJobWrappers(String jobGroupName) throws SchedulerException {

        List wrappers = new ArrayList();
        String[] jobNames = getScheduler().getJobNames(jobGroupName);

        for (int nameIndex = 0; nameIndex < jobNames.length; nameIndex++) {
            JobDetail jobDetail = getScheduler().getJobDetail(jobNames[nameIndex], jobGroupName);
            Trigger trigger = getTrigger(jobNames[nameIndex], jobGroupName);
            wrappers.add(new JobWrapper(jobDetail, trigger));
        }

        return (JobWrapper[]) wrappers.toArray(new JobWrapper[wrappers.size()]);
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
     * Restores the jobs of a certain job group from the snapshot file.
     * @param jobGroup The job group.
     * @throws SchedulerException when something went wrong.
     */
    public void restoreJobs(String jobGroup) throws SchedulerException {

        log.debug("--------------------------------------------------");
        log.debug("Restoring jobs for job group [" + jobGroup + "]");
        log.debug("--------------------------------------------------");

        try {
            JobWrapper[] jobs = getStore().restoreJobs(getPublication(jobGroup));
            for (int i = 0; i < jobs.length; i++) {
                if (jobs[i].getTrigger() != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("    Trigger time in future - scheduling job.");
                    }
                    addJob(jobs[i].getJobDetail(), jobs[i].getTrigger());
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("    Trigger time has expired - adding job without scheduling.");
                    }
                    addJob(jobs[i].getJobDetail());
                }
            }
        } catch (Exception e) {
            log.error("Restoring jobs failed: ", e);
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
            log.debug("    No trigger found.");
        } else {
            log.debug("    Trigger found. Setting new start time.");
            jobDetail.setDurability(true);
            if (startTime.after(new GregorianCalendar().getTime())) {
                log.debug("    Start time is in future - re-scheduling job.");
                getScheduler().unscheduleJob(trigger.getName(), trigger.getGroup());
                trigger = TriggerHelper.createSimpleTrigger(jobId, jobGroup, startTime);
                getScheduler().scheduleJob(trigger);
            } else {
                log.debug("    Start time has already expired - deleting job.");
                getScheduler().deleteJob(jobId, jobGroup);
            }
            try {
                getStore().writeSnapshot(getPublication(jobGroup), getJobWrappers(jobGroup));
            } catch (SchedulerException e) {
                throw e;
            } catch (PublicationException e) {
                throw new SchedulerException(e);
            }
        }
    }

    /**
     * Deletes the jobs for a certain document. This method is called when
     * a document has been moved or deleted.
     * @param document A document.
     * @throws SchedulerException when something went wrong.
     * @throws PublicationException when something went wrong.
     */
    public void deleteJobs(org.apache.lenya.cms.publication.Document document)
        throws SchedulerException, PublicationException {
            
        log.debug("Deleting jobs for document [" + document + "]");
            
        String jobGroup = document.getPublication().getId();
        JobWrapper[] jobs = getJobWrappers(jobGroup);
        boolean changed = false;
        for (int i = 0; i < jobs.length; i++) {
            ServletJob job = jobs[i].getJob();
            String documentUrl = job.getDocumentUrl(jobs[i].getJobDetail());
            if (documentUrl.equals(document.getCompleteURL())) {
                deleteJob(jobs[i].getJobDetail().getName(), jobGroup);
                changed = true;
            }
        }
        if (changed) {
            getStore().writeSnapshot(getPublication(jobGroup), getJobWrappers(jobGroup));
        }
    }

}
