package org.wyona.cms.scheduler;

import java.util.Date;

import org.apache.log4j.Category;

import org.quartz.*;
import org.quartz.simpl.*;

import org.wyona.cms.authoring.DefaultFilePublisher;

/**
 * Small wrapper around the Publisher class to implement the
 * <code>Job</code> interface.
 *
 * @author <a href="mailto:christian.egli@wyona.com">Christian Egli</a>
 */
public class PublishJob implements Job {

    static Category log = Category.getInstance(PublishJob.class);
    
    /**
     * <p>Called by the <code>{@link org.quartz.Scheduler}</code> when a
     * <code>{@link org.quartz.Trigger}</code> fires that is associated with the
     * <code>Job</code>.</p>
     *
     * @throws JobExecutionException if there is an exception while executing
     * the job.
     */
    public void execute(JobExecutionContext context)
	throws JobExecutionException {

	JobDetail jobDetail = context.getJobDetail();
	JobDataMap jobDataMap = jobDetail.getJobDataMap();
	String docID = jobDataMap.getString(SchedulerAdaptor.DOCID);

	String authoringPath = jobDataMap.getString("authoringPath");
	String livePath = jobDataMap.getString("livePath");
	String treeAuthoringPath = jobDataMap.getString("treeAuthoringPath");
	String treeLivePath = jobDataMap.getString("treeLivePath");

	log.info(jobDetail.getFullName() + "/" + docID +
		 " executing.[" + new Date() + "]");
	DefaultFilePublisher publisher =
	    new DefaultFilePublisher(authoringPath, livePath,
				     treeAuthoringPath, treeLivePath,
				     docID);
	publisher.publish();
    }
}
