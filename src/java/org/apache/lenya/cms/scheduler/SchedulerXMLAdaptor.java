package org.wyona.cms.scheduler;  

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Category;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 * A simple wrapper around a Quartz scheduler which provides
 * functionality to serialize the list of jobs as xml.
 *
 * @author <a href="mailto:christian.egli@wyona.com">Christian Egli</a>
 * @version CVS $Id: SchedulerXMLAdaptor.java,v 1.2 2002/10/06 23:36:39 michicms Exp $
 */
public class SchedulerXMLAdaptor extends SchedulerAdaptor {

    static Category log = Category.getInstance(SchedulerXMLAdaptor.class);
    
    /**
     * Return an xml description of all scheduled jobs for the given documentID.
     *
     * @param writer a <code>PrintWriter</code> value
     * @param documentID a <code>String</code> value
     * @exception SchedulerException if an error occurs
     */
    public void getJobs(PrintWriter writer, String documentID)
	throws SchedulerException {
	
	String[] triggerGroupNames = null;
	String[] triggerNames = null;

	String docID;
	String jobID;
	String publicationID;
	
	Trigger trigger;
	JobDetail jobDetail;
	GregorianCalendar nextFireTime;
	JobDataMap jobDataMap;

	// First print a list of all triggerTypes
	writer.println("<sch:triggers>");
	triggerGroupNames = sched.getTriggerGroupNames();
	for (int i = 0; i < triggerGroupNames.length; i++) {
	    triggerNames = sched.getTriggerNames(triggerGroupNames[i]);
	    for (int j = 0; j < triggerNames.length; j++) {
		trigger = sched.getTrigger(triggerNames[j],
						   triggerGroupNames[i]);
		
		jobDetail = sched.getJobDetail(trigger.getJobName(),
					       trigger.getJobGroup());
		writer.println("<sch:trigger name=\"" + triggerGroupNames[i]
			       + "\" src=\""
			       + jobDetail.getJobClass().getName() + "\"/>");
	    }
	}
	writer.println("<sch:trigger name=\"date\" " +
		       "src=\"org.wyona.cms.scheduler.DateTrigger\"/>");
	writer.println("<sch:trigger name=\"modified\" " +
		       "src=\"org.wyona.cms.scheduler.ContentModifiedTrigger\"/>");
	writer.println("</sch:triggers>");
	
	// then print a list of all JobTypes
	writer.println("<sch:actions>");
	writer.println("<sch:action name=\"publish\" " + 
		       "src=\"org.wyona.cms.scheduler.PublishAction\"/>");
	writer.println("<sch:action name=\"email\" " +
		       "src=\"org.wyona.cms.scheduler.EMailAction\"/>");
	writer.println("</sch:actions>");

	// and finally for all publications print all scheduled Jobs
	log.info("SchedulerXMLAdaptor.getJobs()");

	String previousPublicationID = "";
	boolean newPublication = false;
	for (int i = 0; i < triggerGroupNames.length; i++) {
	    triggerNames = sched.getTriggerNames(triggerGroupNames[i]);
	    for (int j = 0; j < triggerNames.length; j++) {
		trigger = sched.getTrigger(triggerNames[j],
					   triggerGroupNames[i]);
		nextFireTime = new GregorianCalendar();
		nextFireTime.setTime(trigger.getNextFireTime());
		
		jobDetail = sched.getJobDetail(trigger.getJobName(),
					       trigger.getJobGroup());
		publicationID = jobDetail.getGroup();
		
		jobDataMap = jobDetail.getJobDataMap();
		docID = jobDataMap.getString(DOCID);
// 		if (docID == null) docID = "";
		jobID = jobDataMap.getString(JOBID);
		String sentence = jobDataMap.getString("sentence");
		
		if (previousPublicationID.equals(publicationID) == false) {
		    if (previousPublicationID.equals("") == false) {
			writer.println("</sch:tasks>");
			writer.println("</sch:publication>");
		    }
		    writer.println("<sch:publication name=\"" +
				   publicationID + "\">");
		    writer.println("<sch:tasks>");
		    previousPublicationID = publicationID;
		    newPublication = true;
		}
		writer.println("<sch:task action-type= \"" +
			       jobDetail.getJobClass().getName() + "\">");
		writer.println("<sch:trigger type=\"date\">");
		writer.println("<sch:parameter name=\"year\" value=\"" +
			       nextFireTime.get(Calendar.YEAR) + "\"/>");
		writer.println("<sch:parameter name=\"month\" value=\"" +
			       // Month value is 0-based
			       (nextFireTime.get(Calendar.MONTH)+1)
			       + "\"/>");
		writer.println("<sch:parameter name=\"day\" value=\"" +
			       nextFireTime.get(Calendar.DAY_OF_MONTH) + "\"/>");
		writer.println("<sch:parameter name=\"hour\" value=\""+
			       nextFireTime.get(Calendar.HOUR_OF_DAY) + "\"/>");
		writer.println("<sch:parameter name=\"minute\" value=\"" +
			       nextFireTime.get(Calendar.MINUTE) + "\"/>");
		writer.println("</sch:trigger>");
		
		log.debug("Triggers: " + nextFireTime.get(Calendar.YEAR) + "-" +
			  nextFireTime.get(Calendar.MONTH) + "-" +
			  nextFireTime.get(Calendar.DAY_OF_MONTH) + "T" +
			  nextFireTime.get(Calendar.HOUR_OF_DAY) + ":" +
			  nextFireTime.get(Calendar.MINUTE));
		writer.println("<sch:parameter name=\"docid\" value=\"" + docID + "\"/>");
		writer.println("<sch:parameter name=\"jobid\" value=\"" + jobID + "\"/>");
		writer.println("<sch:parameter name=\"sentence\" value=\"" + sentence + "\"/>");
		writer.println("</sch:task>");
	    }
	    writer.println("</sch:tasks>");
	    writer.println("</sch:publication>");
	}
    }
}
