package org.wyona.cms.scheduler;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Category;

import java.net.URL;

import org.quartz.Job;
import org.quartz.JobExecutionException;
import org.quartz.JobExecutionContext;

/*
import org.quartz.*;
import org.quartz.simpl.*;
*/

/**
 * @author Michael Wechner
 */
public class RSSFetchJob implements Job{
  static Category log = Category.getInstance(RSSFetchJob.class);
/**
 *
 */
  public void execute(JobExecutionContext context) throws JobExecutionException{
    try{
      URL url=new URL("http://www.freshmeat.net/backend/fm.rdf");
      log.debug(".execute(): Remote URL: "+url);

      HttpClient httpClient=new HttpClient();
        
      HttpMethod httpMethod=new GetMethod();
      httpMethod.setRequestHeader("Content-type","text/plain");
      httpMethod.setPath(url.getPath());

      httpClient.startSession(url);
      httpClient.executeMethod(httpMethod);
      byte[] sresponse=httpMethod.getResponseBody();
      log.fatal(".execute(): Response from remote server: "+new String(sresponse));
      httpClient.endSession();
      }
    catch(Exception e){
      log.error(e);
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
}
