package org.wyona.cms.scheduler;

import org.apache.log4j.Category;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Michael Wechner
 * @version 2002.10.8
 */
public class HelloWorldJob implements Job{
  static Category log = Category.getInstance(HelloWorldJob.class);
/**
 *
 */
  public void execute(JobExecutionContext context) throws JobExecutionException{
    JobDetail jobDetail = context.getJobDetail();
    JobDataMap jobDataMap = jobDetail.getJobDataMap();
    String sentence = jobDataMap.getString("sentence");
    log.debug(".execute(): "+sentence);
    }
}
