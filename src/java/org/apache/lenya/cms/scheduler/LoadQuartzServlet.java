package org.wyona.cms.scheduler;  

import java.io.File;
import java.io.FileFilter;
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
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.quartz.*;
import org.quartz.SchedulerFactory;
import org.quartz.impl.*;
import org.quartz.simpl.*;
import org.quartz.spi.*;
import org.quartz.utils.*;
import org.wyona.cms.publishing.PublishingEnvironment;

/**
 * A simple servlet that starts an instance of a Quartz scheduler.
 *
 * @author <a href="mailto:christian.egli@wyona.com">Christian Egli</a>
 * @version CVS $Id: LoadQuartzServlet.java,v 1.10 2002/11/17 21:29:21 michicms Exp $
 */
public class LoadQuartzServlet extends HttpServlet {

    static Category log = Category.getInstance(LoadQuartzServlet.class);

    private ServletContext servletContext;
    private String servletContextPath;
    private String schedulerConfigurations;

    private static SchedulerWrapper scheduler = null;

    protected static SchedulerWrapper getScheduler() {
        return scheduler;
    }

    public void init(ServletConfig config) throws ServletException{
	super.init(config);

        this.servletContext=config.getServletContext();
        this.servletContextPath = this.servletContext.getRealPath("/");
        this.schedulerConfigurations = config.getInitParameter("scheduler-configurations");
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

    public void process()
            throws ServletException, SchedulerException  {
        
	scheduler = new SchedulerWrapper(servletContextPath, schedulerConfigurations);

        // <Add persistent jobs>
        // FIXME: Read from file. This is just an example yet
        Date startTime = null;
        try{
          //org.dom4j.Document doc_conf=new org.dom4j.io.SAXReader().read("file:"+this.servletContextPath+"/wyona/cms/docs/cms/scheduler.xconf");
          org.dom4j.Document doc_conf
            = new org.dom4j.io.SAXReader().read(
            "file:"+this.servletContextPath+schedulerConfigurations);

          // Add Cron Job (seconds,minutes,hours,day of month,months,day of week)
          //sched.addJob("no_pub_id","no_doc_id","org.wyona.cms.scheduler.CommandLineJob","0 * * * * ?");
          //scheduler.addJob("no_pub_id","no_doc_id","org.wyona.cms.scheduler.HelloWorldJob","30 * * * * ?");
          }
        catch(NumberFormatException e){
          log.error(".process(): " , e);
          }
/*
        catch(ClassNotFoundException e){
          log.error(".process(): "+e);
          }
*/
        catch(org.dom4j.DocumentException e){
          log.error(".process(): "+e);
          }
        // </Add persistent jobs>

	try {
	    ShutdownHook();
	} catch (Exception e) {
 	    log.error(e.toString(), e);
	}
        
        // ----------------------------------------------------------
        // restore persistent jobs
        // ----------------------------------------------------------

        restoreJobs();
    }
    
    public void destroy () {
        destroyScheduler();
    }

    public static void destroyScheduler() {
	log.debug("destroy: ");
        getScheduler().shutdown();
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
                LoadQuartzServlet.destroyScheduler();
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

    protected void handleRequest(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

       log.debug(".handleRequest() : server-port:"+request.getServerPort());

	// Fetch all the params from the post request. In particular
	// we are interested in the following parameters: 
        //
	// * Action: add, modify or delete a job
	// * startDay, startMonth, startYear, startHour, startMin:
	//   when is the job to be scheduled.
	// * publicationID:
	// * documentID:
	// * scheduleJobName: which class will be invoked when the job
	//   is triggered.

	String action = request.getParameter("Action");

	// in the case of modification or deletion the jobId is also
	// passed through a hidden field.
	String jobId = request.getParameter(JobDataMapWrapper.getFullName(
                SchedulerWrapper.JOB_PREFIX, SchedulerWrapper.JOB_ID));

	log.debug("-------------------- Session Attributes --------------------");
	for (Enumeration e = request.getSession().getAttributeNames();
	     e.hasMoreElements() ;) {
	    log.debug(e.nextElement());
	}
	log.debug("-------------------- End Session Attributes --------------------");
	
	// the publicationID is fetched from the session
	String publicationId = (String) request.getSession().getAttribute(
                "org.wyona.cms.cocoon.acting.IMLAuthenticator.type");
	if (publicationId == null || publicationId.equals("")) {
	    publicationId = "No_session_was_passed_in"; 
            log.error("No publication ID provided! ", new IllegalStateException());
            // FIXME:
            publicationId = "no_such_publication";
	}

	// we grab the document URI from from a hidden field if a job is
	// modified or deleted or from the referer in the case of
	// addition.

        String documentUri = request.getParameter(SchedulerWrapper.DOCUMENT_URI);
	log.debug("documentUri: " + documentUri);

	if ((documentUri == null) || (documentUri.equals(""))) {
	    documentUri = request.getHeader("referer");
	    log.debug("documentUri from referer: " + documentUri);
	}
	
	String startDay = request.getParameter("trigger.startDay");
	String startMonth = request.getParameter("trigger.startMonth");
	String startYear = request.getParameter("trigger.startYear");
	String startHour = request.getParameter("trigger.startHour");
	String startMin = request.getParameter("trigger.startMin");

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
	}
        
        else if (action.equals("Add")) {
            getScheduler().addJob(documentUri, publicationId, startTime, request);
            log.debug(".handleRequest() Add : server-port:"+request.getServerPort());
        }
        
        else if (action.equals("Modify")) {
            getScheduler().deleteJob(jobId, publicationId);
            getScheduler().addJob(documentUri, publicationId, startTime, request);
        }

        else if (action.equals("Delete")) {
            getScheduler().deleteJob(jobId, publicationId);
	}

	// handle the remainder of the request by simply returning all
	// scheduled jobs (for the gived documentID).

        PrintWriter writer = response.getWriter();
        response.setContentType("text/xml");
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter xmlWriter = new XMLWriter(writer, format);
            
        try {
            xmlWriter.write(getScheduler().getSnapshot(publicationId));
        }
        catch (SchedulerException e) {
            log.error("Can't create job snapshot: ", e);
        }
            
    }

    public String getServletContextPath() {
        return servletContextPath;
    }
    
    public void restoreJobs() {
        
        File publicationsDirectory = new File(
                getServletContextPath() + PublishingEnvironment.PUBLICATION_PREFIX);
        
        File publicationDirectories[] = publicationsDirectory.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        
        for (int i = 0; i < publicationDirectories.length; i++) {
            File directory = publicationDirectories[i];
            String publicationId = directory.getName();
            getScheduler().restoreJobs(publicationId);
        }
        
    }
    
}
