package org.wyona.cms.scheduler;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Category;

import java.net.URL;

import org.quartz.Job;
import org.quartz.JobExecutionException;
import org.quartz.JobExecutionContext;

/**
 * @author Michael Wechner
 */
public class CommandLineJob implements Job{
  static Category log = Category.getInstance(CommandLineJob.class);
/**
 *
 */
  public void execute(JobExecutionContext context) throws JobExecutionException{
    try{
      byte[] standard_output=runProcess("pwd");
      log.fatal(".execute(): Standard Output: "+new String(standard_output));
      }
    catch(Exception e){
      log.error(".execute(): "+e);
      }

/*
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
*/
    }
/**
 *
*/
  public byte[] runProcess(String command) throws Exception{
    Process process=Runtime.getRuntime().exec(command);
    java.io.InputStream in=process.getInputStream();
    byte[] buffer=new byte[1024];
    int bytes_read=0;
    java.io.ByteArrayOutputStream baout=new java.io.ByteArrayOutputStream();
    while((bytes_read=in.read(buffer)) != -1){
      baout.write(buffer,0,bytes_read);
      }
    log.debug(".download(): InputStream: "+baout.toString());

    java.io.InputStream in_e=process.getErrorStream();
    java.io.ByteArrayOutputStream baout_e=new java.io.ByteArrayOutputStream();
    while((bytes_read=in_e.read(buffer)) != -1){
      baout_e.write(buffer,0,bytes_read);
      }
    log.error(".download(): ErrorStream: "+baout_e.toString());

    return baout.toByteArray();
    }
}
