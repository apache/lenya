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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.scheduler.xml.TriggerHelper;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Store for scheduler jobs.
 */
public class SchedulerStore {

    /**
     * <code>ELEMENT_JOB_GROUP</code> The job group element
     */
    public static final String ELEMENT_JOB_GROUP = "job-group";
    /**
     * <code>ELEMENT_JOB</code> The job element
     */
    public static final String ELEMENT_JOB = "job";
    /**
     * <code>TITLE_ELEMENT</code> The title element
     */
    public static final String TITLE_ELEMENT = "title";
    /**
     * <code>SNAPSHOT_FILE</code> The path to the stored jobs
     */
    public static final String SNAPSHOT_FILE =
        "config/scheduler/jobs.xml".replace('/', File.separatorChar);

    /** The namespace for the <code>jobs.xml</code> file. */
    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/scheduler/1.0";

    private static final Logger log = Logger.getLogger(SchedulerStore.class);


    /**
     * Ctor.
     */
    public SchedulerStore() {
	    // do nothing
    }

    /**
     * Returns the job snapshot file for a publication..
     * @param publication The publication.
     * @return A file.
     * @throws SchedulerException when the publication could not be built.
     */
    protected File getJobsFile(Publication publication) throws SchedulerException {
        File jobsFile;
        jobsFile = new File(publication.getDirectory(), SNAPSHOT_FILE);
        log.debug("Resolved job snapshot file: [" + jobsFile.getAbsolutePath() + "]");
        return jobsFile;
    }

    /**
     * Writes a job snapshot.
     * @param publication The publication.
     * @param jobs The jobs to persist
     * @throws SchedulerException when something went wrong.
     */
    protected void writeSnapshot(Publication publication, JobWrapper[] jobs)
        throws SchedulerException {

        log.debug("Writing job snapshot for publication [" + publication.getId() + "]");
        File jobsFile = getJobsFile(publication);

        try {
            File directory = jobsFile.getParentFile();

            if (!directory.exists()) {
                directory.mkdirs();
                log.debug("Creating job snapshot directory: " + directory.getPath());
            }

            if (jobsFile.createNewFile()) log.debug("new jobs file created.");
            DocumentHelper.writeDocument(getSnapshot(publication, jobs), jobsFile);
        } catch (final TransformerConfigurationException e) {
            log.error("Writing job snapshot failed: ", e);
            throw new SchedulerException(e);
        } catch (final IOException e) {
            log.error("Writing job snapshot failed: ", e);
            throw new SchedulerException(e);
        } catch (final TransformerException e) {
            log.error("Writing job snapshot failed: ", e);
            throw new SchedulerException(e);
        } catch (final SchedulerException e) {
            log.error("Writing job snapshot failed: ", e);
            throw new SchedulerException(e);
        }

    }

    /**
     * Return an xml description of all scheduled jobs for the given publication.
     *
     * @param publication The publication.
     * @param jobs The jobs to return
     * @return An XML document.
     * @exception SchedulerException if an error occurs
     */
    public Document getSnapshot(Publication publication, JobWrapper[] jobs)
        throws SchedulerException {
        NamespaceHelper helper = SchedulerStore.getNamespaceHelper();
        Document document = helper.getDocument();
        Element root = document.getDocumentElement();

        log.debug("Creating job snapshot for publication [" + publication.getId() + "]");
        root.appendChild(createSnapshot(helper, publication, jobs));

        return document;
    }

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
     * Creates an XML element containing a snapshot of a job group.
     * @param helper The namespace helper to use.
     * @param publication The publication to create the snapshot for
     * @param jobs The jobs
     * @return An XML element.
     * @throws SchedulerException when something went wrong.
     */
    protected Element createSnapshot(
        NamespaceHelper helper,
        Publication publication,
        JobWrapper[] jobs)
        throws SchedulerException {
        Element jobGroupElement = helper.createElement(ELEMENT_JOB_GROUP);
        jobGroupElement.setAttribute("name", publication.getId());

        for (int i = 0; i < jobs.length; i++) {

            ServletJob job = jobs[i].getJob();
            Element jobElement = job.save(helper, jobs[i].getJobDetail());
            jobGroupElement.appendChild(jobElement);

            Trigger trigger = jobs[i].getTrigger();

            if (trigger != null) {
                Element triggerElement = TriggerHelper.createElement(helper, trigger);
                jobElement.appendChild(triggerElement);
            }
        }

        return jobGroupElement;
    }

    /**
     * Restores the jobs of a certain job group from the snapshot file.
     * @param publication The publication
     * @return A job wrapper
     * @throws SchedulerException when something went wrong.
     */
    public JobWrapper[] restoreJobs(Publication publication) throws SchedulerException {
        
        log.debug("Restoring jobs for publication [" + publication.getId() + "]");

        List wrappers = new ArrayList();
        File jobsFile = getJobsFile(publication);
        
        if (jobsFile.exists()) {
            Element[] jobElements = getJobElements(publication);
            Document document;
            try {
                document = DocumentHelper.readDocument(jobsFile);
            } catch (Exception e) {
                throw new SchedulerException(e);
            }
            NamespaceHelper helper = SchedulerStore.getNamespaceHelper(document);

            for (int i = 0; i < jobElements.length; i++) {
                wrappers.add(restoreJob(helper, jobElements[i], publication));
            }
        }
        else {
            log.debug("Could not restore jobs for publication [" + publication.getId() + "] - jobs file does not exist.");
        }

        return (JobWrapper[]) wrappers.toArray(new JobWrapper[wrappers.size()]);
    }

    /**
     * Restores the jobs from a certain XML element.
     * @param helper The namespace helper
     * @param jobElement The XML element.
     * @param publication The publication to restore jobs for
     * @return A job wrapper
     * @throws SchedulerException if an error occurs
     */
    protected JobWrapper restoreJob(
        NamespaceHelper helper,
        Element jobElement,
        Publication publication)
        throws SchedulerException {
        log.debug("Restoring job ");
        JobWrapper wrapper;

        try {
            String jobClassName = jobElement.getAttribute(ServletJob.ATTRIBUTE_CLASS);
            ServletJob job = ServletJobFactory.createJob(jobClassName);
            JobDetail jobDetail =
                job.load(
                    jobElement,
                    publication.getId(),
                    publication.getServletContext().getAbsolutePath());

            Trigger trigger = null;

            Element triggerElement = helper.getFirstChild(jobElement, "trigger");
            if (triggerElement != null) {
                trigger =
                    TriggerHelper.createTrigger(
                        triggerElement,
                        jobDetail.getName(),
                        jobDetail.getGroup());

                Date now = new GregorianCalendar().getTime();
                if (log.isDebugEnabled()) {
                    DateFormat format = new SimpleDateFormat();
                    log.debug(
                        "    Trigger time: [" + format.format(trigger.getFinalFireTime()) + "]");
                    log.debug("    Current time: [" + format.format(now) + "]");
                }
                if (!trigger.getFinalFireTime().after(now)) {
                    trigger = null;
                }
            }
            wrapper = new JobWrapper(jobDetail, trigger);

        } catch (Exception e) {
            throw new SchedulerException(e);
        }
        return wrapper;
    }

    /**
     * Returns the job elements of a publication.
     * @param publication
     * @return
     * @throws SchedulerException when something went wrong.
     */
    protected Element[] getJobElements(Publication publication) throws SchedulerException {
        Element[] jobElements;
        try {
            File jobsFile = getJobsFile(publication);
            if (jobsFile.exists()) {
                Document document = DocumentHelper.readDocument(jobsFile);
                Element schedulerElement = document.getDocumentElement();
                NamespaceHelper helper = SchedulerStore.getNamespaceHelper(document);

                Element jobGroupElement =
                    helper.getFirstChild(schedulerElement, SchedulerStore.ELEMENT_JOB_GROUP);
                if (jobGroupElement == null) {
                    throw new SchedulerException("No <job-group> element found!");
                }

                String jobGroupAttribute = jobGroupElement.getAttribute("name");

                if (!jobGroupAttribute.equals(publication.getId())) {
                    throw new SchedulerException(
                        "The jobs.xml file contains a wrong job group: ["
                            + jobGroupAttribute
                            + "]");
                }
                jobElements = helper.getChildren(jobGroupElement, SchedulerStore.ELEMENT_JOB);

            } else {
                throw new SchedulerException(
                    "The jobs file [" + jobsFile.getAbsolutePath() + "] does not exist!");
            }
        } catch (SchedulerException e) {
            throw e;
        } catch (Exception e) {
            throw new SchedulerException(e);
        }
        return jobElements;
    }
}
