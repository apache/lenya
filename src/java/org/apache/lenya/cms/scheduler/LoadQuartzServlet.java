package org.wyona.cms.scheduler;  

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;
import org.quartz.*;
import org.quartz.impl.*;
import org.quartz.simpl.*;
import org.quartz.spi.*;
import org.quartz.utils.*;

/**
 * A simple servlet that starts an instance of a Quartz scheduler.
 *
 * @author <a href="mailto:christian.egli@wyona.com">Christian Egli</a>
 * @version CVS $Id: LoadQuartzServlet.java,v 1.6 2002/11/04 23:30:25 michicms Exp $
 */
public class LoadQuartzServlet extends HttpServlet {

    static Category log = Category.getInstance(LoadQuartzServlet.class);

    private static SchedulerXMLAdaptor sched = null;

    private ServletContext servletContext;
    private String servletContextPath;
    private String schedulerConfigurations;

    public void init(ServletConfig config) throws ServletException{
	super.init(config);

        this.servletContext=config.getServletContext();
        this.servletContextPath = this.servletContext.getRealPath("/");
        this.schedulerConfigurations=config.getInitParameter("scheduler-configurations");
	log.debug(".init(): Servlet Context Path: "+this.servletContextPath);
	log.debug(".init(): Scheduler Configurations: "+this.schedulerConfigurations);
	
	try {
 	    log.info("Working?...");
	    process();
	    log.info("OK");
	} catch (Exception e) {
	    log.error("Init of LoadQuartzServlet failed");
	    throw new ServletException(e);
	}
    }
 
    public void process() throws ServletException, SchedulerException  {
	sched = new SchedulerXMLAdaptor();


        // <Add persistent jobs>
        // FIXME: Read from file. This is just an example yet
        Date startTime = null;
        try{
          //org.dom4j.Document doc_conf=new org.dom4j.io.SAXReader().read("file:"+this.servletContextPath+"/wyona/cms/docs/cms/scheduler.xconf");
          org.dom4j.Document doc_conf=new org.dom4j.io.SAXReader().read("file:"+this.servletContextPath+schedulerConfigurations);
/*
          // Add Simple Job
	  startTime = new GregorianCalendar(Integer.parseInt("2002"),
                                Integer.parseInt("10")-1,
                                Integer.parseInt("6"),
                                Integer.parseInt("23"),
                                Integer.parseInt("30")).getTime();
          sched.addJob("no_pub_id","no_doc_id","org.wyona.cms.scheduler.CommandLineJob",startTime);
*/

          // Add Cron Job (seconds,minutes,hours,day of month,months,day of week)
          //sched.addJob("no_pub_id","no_doc_id","org.wyona.cms.scheduler.CommandLineJob","0 * * * * ?");
          sched.addJob("no_pub_id","no_doc_id","org.wyona.cms.scheduler.HelloWorldJob","30 * * * * ?");
          }
        catch(NumberFormatException e){
          log.error(".process(): "+e);
          }
        catch(ClassNotFoundException e){
          log.error(".process(): "+e);
          }
        catch(org.dom4j.DocumentException e){
          log.error(".process(): "+e);
          }
        // </Add persistent jobs>



	try {
	    ShutdownHook();
	} catch (Exception e) {
 	    log.error(e.toString(), e);
	}
    }
    

    public void destroy () {
	log.debug("destroy: ");
	sched.shutdown();
    }

    /**
     * This method sets a ShutdownHook to the system
     * This traps the CTRL+C or kill signal and shutdows 
     * Correctly the system.
     * @throws Exception
     */ 
    public static void ShutdownHook() throws Exception {
	log.debug("-------------------- ShutdownHook --------------------");
	Runtime.getRuntime().addShutdownHook(new Thread() {         
	 	public void run() {
		    log.debug("ShutdownHook: ");
		    sched.shutdown();
		}
	    });
	log.debug("-------------------- End ShutdownHook --------------------");
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
	throws IOException, ServletException {

	handleRequest(request, response);
	
    }

    public void doPost(HttpServletRequest req,
		       HttpServletResponse resp)
	throws ServletException, IOException {
	doGet(req, resp);
    }

    private void handleRequest(HttpServletRequest request,
				 HttpServletResponse response)
	throws IOException {

	// Fetch all the params from the post request. In particular
	// we are interested in the following parameters: 
        //
	// * action: add, modify or delete a job
	// * startDay, startMonth, startYear, startHour, startMin:
	//   when is the job to be scheduled.
	// * publicationID:
	// * documentID:
	// * scheduleJobName: which class will be invoked when the job
	//   is triggered.

	response.setContentType("text/xml");
	PrintWriter writer = response.getWriter();
	writer.println("<sch:scheduler xmlns:sch=\"http://www.wyona.org/2002/sch\">");

	String action = request.getParameter("Action");
	String scheduleJobName = request.getParameter("scheduleJobName");

	// in the case of modification or deletion the jobId is also
	// passed through a hidden field.
	String jobID = request.getParameter("jobID");

	log.debug("-------------------- Session Attributes --------------------");
	for (Enumeration e = request.getSession().getAttributeNames();
	     e.hasMoreElements() ;) {
	    log.debug(e.nextElement());
	}
	log.debug("-------------------- End Session Attributes --------------------");
	
	// the publicationID is fetched from the session
	String publicationID =
	    (String)request.getSession().getAttribute("publicationID");
	if ((publicationID == null) || (publicationID.equals(""))) {
	    publicationID = "No_session_was_passed_in"; 
	}

	// we grab the documentID from from a hidden field if a job is
	// modified or deleted or from the referer in the case of
	// addition.
	String documentID = request.getParameter("documentID");
	log.debug("documentID:" + documentID);

	if ((documentID == null) || (documentID.equals(""))) {
	    documentID = request.getHeader("referer");
	    log.debug("documentID:" + documentID);

	}
	
	String startDay = request.getParameter("startDay");
	String startMonth = request.getParameter("startMonth");
	String startYear = request.getParameter("startYear");
	String startHour = request.getParameter("startHour");
	String startMin = request.getParameter("startMin");

	Date startTime = null;
	try {
	    startTime =
		new GregorianCalendar(Integer.parseInt(startYear),
				      // Month value is 0-based
				      Integer.parseInt(startMonth)-1,
				      Integer.parseInt(startDay),
				      Integer.parseInt(startHour),
				      Integer.parseInt(startMin)).getTime();
	} catch (NumberFormatException e) {
	    log.error("NumberFormatException with parameters " +
		      "startYear, startMonth, startDay, startHour, startMin: " +
		      startDay + ", " + startMonth + ", " + startDay + ", " +
		      startHour + ", " + startMin);
	}
	
	// check if the request wants to submit, modify or delete a job.
	if (action == null) {
	    // simply return all scheduled jobs, which is done below
	} else if (action.equals("Add")) {
	    try {
		sched.addJob(publicationID, documentID,
			     scheduleJobName, startTime);
	    } catch (SchedulerException e) {
	        writer.println("<sch:exception type=\"SchedulerException\"/>");
		log.error("sched.addJob failed");
	    } catch (ClassNotFoundException e) {
	        writer.println("<sch:exception type=\"ClassNotFoundException\"/>");
		log.error(".handleRequest(): sched.addJob failed");
	    }
	} else if (action.equals("Modify")) {
	    try {
		sched.modifyJob(jobID, publicationID,
				documentID, scheduleJobName,
				startTime);
	    } catch (SchedulerException e) {
		log.error("sched.modifyJob failed");
	    } catch (ClassNotFoundException e) {
		log.error(".handleRequest(): sched.modifyJob failed");
	    }
	} else if (action.equals("Delete")) {
	    try {
		sched.deleteJob(jobID, publicationID);
	    } catch (SchedulerException e) {
		log.error("sched.deleteJob failed");
	    }
	}
	
	

	// handle the remainder of the request by simply returning all
	// scheduled jobs (for the gived documentID).
	try {
	    sched.getJobs(writer, documentID);
	} catch (SchedulerException e) {
	    log.error("getSchedulerInfo failed");
	}

	writer.println("</sch:scheduler>");
    }
    
}
