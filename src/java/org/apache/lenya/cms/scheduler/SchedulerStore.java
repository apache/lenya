/*
$Id: SchedulerStore.java,v 1.1 2004/01/07 18:37:23 andreas Exp $
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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.scheduler.xml.TriggerHelper;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.log4j.Category;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Store for scheduler jobs.
 *
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 * @version CVS $Id: SchedulerStore.java,v 1.1 2004/01/07 18:37:23 andreas Exp $
 */
public class SchedulerStore {

    public static final String ELEMENT_JOB_GROUP = "job-group";
    public static final String ELEMENT_JOB = "job";
    public static final String TITLE_ELEMENT = "title";

    private static final Category log = Category.getInstance(SchedulerStore.class);

    public static final String SNAPSHOT_FILE =
        "config/scheduler/jobs.xml".replace('/', File.separatorChar);

    /**
     * Ctor.
     * @param publication The publication.
     */
    public SchedulerStore() {
    }

    /**
     * Returns the job snapshot file for a publication..
     * @param Publication The publication.
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

            jobsFile.createNewFile();
            DocumentHelper.writeDocument(getSnapshot(publication, jobs), jobsFile);
        } catch (Exception e) {
            log.error("Writing job snapshot failed: ", e);
        }
    }

    /**
     * Return an xml description of all scheduled jobs for the given publication.
     *
     * @param publication The publication.
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
     * Creates an XML element containting a snapshot of a job group.
     * @param helper The namespace helper to use.
     * @param jobGroup The job group.
     * @return An XMl element.
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
     * @param jobGroup The job group.
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
            Element schedulerElement = document.getDocumentElement();
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
     * @param jobElement The XML element.
     * @param jobGroup The job group the job belongs to.
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
     * Called when a document has moved. This updates the corresponding
     * entry in the scheduler store.
     * @param source The source document.
     * @param destination The destination document.
     */
    public void documentMoved(
        org.apache.lenya.cms.publication.Document source,
        org.apache.lenya.cms.publication.Document destination)
        throws SchedulerException {

        Publication publication = source.getPublication();
        if (!publication.equals(destination.getPublication())) {
            throw new SchedulerException("Source and destination document must be in the same publication!");
        }

        JobWrapper[] wrappers = restoreJobs(publication);
        boolean changed = false;
        for (int i = 0; i < wrappers.length; i++) {
            ServletJob job = wrappers[i].getJob();
            String documentUrl = job.getDocumentUrl(wrappers[i].getJobDetail());
            if (documentUrl.equals(source.getCompleteURL())) {
                job.setDocumentUrl(wrappers[i].getJobDetail(), destination.getCompleteURL());
                changed = true;
            }
        }
        if (changed) {
            writeSnapshot(publication, wrappers);
        }
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
                } else {
                    jobElements = helper.getChildren(jobGroupElement, SchedulerStore.ELEMENT_JOB);

                }
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
