package org.wyona.cms.scheduler;  

import java.util.Date;

import org.apache.log4j.Category;
import org.quartz.*;

/**
 * A simple wrapper around a Quartz scheduler.
 *
 * @author <a href="mailto:christian.egli@wyona.com">Christian Egli</a>
 * @version CVS $Id: SchedulerAdaptor.java,v 1.2 2002/08/30 13:43:18 michicms Exp $
 */
public class SchedulerAdaptor {

    static final String DOCID = "docID";
    static final String JOBID = "jobID";

    static Category log = Category.getInstance(SchedulerAdaptor.class);

    protected static Scheduler sched = null;

    public SchedulerAdaptor() {
	
	SchedulerFactory schedFactory =
	    new org.quartz.impl.StdSchedulerFactory();

	log.info("Created schedFactory: ");

	log.info("------- Initializing -------------------");

	try {
	    sched = schedFactory.getScheduler();
	} catch (SchedulerException e) {
	    log.error("Failed to create a scheduler");
	}

	log.info("------- Initialization Complete -----------");

	// jobs don't start firing until start() has been called...
	log.info("------- Starting Scheduler ----------------");
	try {
	    sched.start();
	} catch (SchedulerException e) {
	    log.error("Failed to start the scheduler");
	}
	log.info("------- Started Scheduler -----------------");
    }

    public void  shutdown() {
	log.info("------- Shutting Down ---------------------");
	// try to save state here
	try {
	    sched.shutdown();
	} catch (SchedulerException e) {
	    log.error("------- Shutdown Failed -----------------");
	}
	log.info("------- Shutdown Complete -----------------");
    }

    /**
     * Simple interface to the scheduleJob method of the Quartz scheduler.
     *
     * @param context the publication for which this job is scheduled
     * @param uri the uri on which this job is acting on
     * @param jobClassName the class which implements the
     * 			   org.quartz.Job interface which will be
     * 			   invoked at <code>startTime</code> 
     * @param startTime the time at which the job will be triggered
     *
     * @exception SchedulerException if an error occurs
     */
    public void addJob(String context, String uri,
			String jobClassName,
			Date startTime)
	throws SchedulerException, ClassNotFoundException {

	this.addJob(context, uri, jobClassName, startTime, null, 0, 0);
    }
    
    /**
     * Simple interface to the scheduleJob method of the Quartz scheduler.
     *
     * @param context the publication for which this job is scheduled
     * @param uri the uri on which this job is acting on
     * @param jobClassName the class which implements the
     * 			   org.quartz.Job interface which will be
     * 			   invoked at <code>startTime</code> 
     * @param startTime the time at which the job will be triggered
     * @param endTime A <code>Date</code> set to the time for the
     * 		      Trigger to quit repeat firing. 
     * @param repeatCount how many times the job is to be repeated
     * @param repeatInterval the number of milliseconds to pause
     * 			     between the repeat firing. 
     * @exception SchedulerException if an error occurs
     */
    public void addJob(String context, String uri,
			String jobClassName,
			Date startTime, Date endTime,
			int repeatCount, long repeatInterval)
	throws SchedulerException, ClassNotFoundException {

	log.info("------- addJob -----------------");
	log.debug("addJob: Context:" + context + " uri:" + uri +
		  " jobClassName:" + jobClassName + " startTime:" + startTime +
		  " endTime:" + endTime + " repeatCount:" + repeatCount +
		  " repeatInterval:" + repeatInterval);
	
	String uniqueJobID = "job" + uri + System.currentTimeMillis();
	String uniqueTriggerID = "trigger" + uri + System.currentTimeMillis();
	
	// FIXME: Make sure that a class exists for scheduleJobName
	JobDetail job;
	try {
	    job = new JobDetail(uniqueJobID, context,
				Class.forName(jobClassName));
	} 
        catch (ClassNotFoundException e) {
	    log.error("Class: " + jobClassName + " not found." + e.getMessage());
            throw e;
	    //return;
	} 
        catch (ExceptionInInitializerError e) {
	    log.error("InitializerError for class: " + jobClassName +
		      e.getMessage());
	    return;
	} catch (LinkageError e) {
	    log.error("LinkageError for class: " + jobClassName +
		      e.getMessage());
	    return;
	}
	JobDataMap jobData = new JobDataMap();
	jobData.put(DOCID, uri);
	jobData.put(JOBID, uniqueJobID);
	// FIXME: Where do we get this information
// 	jobData.put("authoringPath", authoringPath);
// 	jobData.put("livePath", livePath);
// 	jobData.put("treeAuthoringPath", treeAuthoringPath);
// 	jobData.put("treeLivePath", treeLivePath);
	job.setJobDataMap(jobData);

	SimpleTrigger trigger = new SimpleTrigger(uniqueTriggerID, "triggerGroup1",
						  uniqueJobID, context,
						  startTime, endTime,
						  repeatCount, repeatInterval);
	
	Date ft = sched.scheduleJob(job, trigger);
	
	log.debug(job.getFullName() + " will run at: " +
		  ft + " & repeat: " + trigger.getRepeatCount() +
		  "/" + trigger.getRepeatInterval());
    }

    /**
     * Modify an existing job.
     *
     * With the current implementation the job is deleted and
     * recreated, i.e. the jobID will change.
     *
     * @param jobID the identifier for this job
     * @param context the publication for which this job is scheduled
     * @param uri the uri on which this job is acting on
     * @param jobClassName the class which implements the
     * 			   org.quartz.Job interface which will be
     * 			   invoked at <code>startTime</code> 
     * @param startTime the time at which the job will be triggered
     *
     * @exception SchedulerException if an error occurs
     */
    public void modifyJob(String jobID,
			   String context, String uri,
			   String jobClassName,
			   Date startTime)
	throws SchedulerException, ClassNotFoundException {
	
	log.info("------- modifyJob -----------------");

	// simply delete the job and add the new, modified one
	this.deleteJob(jobID, context);
	this.addJob(context, uri, jobClassName, startTime);
    }

    /**
     * Delete an existing job.
     *
     * @param jobID the identifier for this job
     * @param context the publication for which this job is scheduled
     *
     * @exception SchedulerException if an error occurs
     */
    public void deleteJob(String jobID, String context)
	throws SchedulerException {

	log.info("------- deleteJob -----------------");
	sched.deleteJob(jobID, context);
    }
}
