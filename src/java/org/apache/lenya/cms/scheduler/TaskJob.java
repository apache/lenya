/*
 * TaskJob.java
 *
 * Created on November 7, 2002, 3:58 PM
 */

package org.wyona.cms.scheduler;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log4j.Category;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.wyona.cms.publishing.PublishingEnvironment;
import org.wyona.cms.scheduler.xml.SchedulerXMLFactory;
import org.wyona.cms.task.Task;
import org.wyona.cms.task.TaskManager;

/**
 * A TaskJob is a Job that executes a Task.
 * The task ID is obtained from the jobType request parameter.
 * @author  ah
 */
public class TaskJob
    extends ServletJob {
    
    public static final String TASK_ID = "id";
    public static final String TASK_PREFIX = "task";
        
    static Category log = Category.getInstance(TaskJob.class);
    
    protected Parameters getParameters(String servletContextPath, HttpServletRequest request) {

        String taskId = request.getParameter(JobDataMapWrapper.getFullName(TASK_PREFIX, TASK_ID));
        if (taskId == null || taskId.equals(""))
            log.error("No task-id is provided!", new IllegalStateException());

        log.debug("Creating data map for job " + taskId);
        String contextPath = request.getContextPath();
	log.debug("Context path: " + contextPath);
        
	// the publicationID is fetched from the session
	String publicationId = (String) request.getSession().getAttribute(
                "org.wyona.cms.cocoon.acting.IMLAuthenticator.type");
        
	if (publicationId == null || publicationId.equals("")) {
            log.error("No publication ID provided! ", new IllegalStateException());
            publicationId = "no_such_publication";
	}
        
        // FIXME: Don't translate parameters
        String publicationPath
                = servletContextPath
                + PublishingEnvironment.PUBLICATION_PREFIX
                + publicationId
                + File.separator;
        
        PublishingEnvironment environment = new PublishingEnvironment(publicationPath);

        Parameters parameters = new Parameters();

        parameters.setParameter("context-path", servletContextPath);
        parameters.setParameter("server-port", Integer.toString(request.getServerPort()));
        log.debug(".getParameters() : server-port: "+Integer.toString(request.getServerPort()));
        parameters.setParameter("server-uri", "http://" + request.getServerName());
        parameters.setParameter("publication-id", publicationId);
        parameters.setParameter("authoring-path", environment.getAuthoringPath());
        parameters.setParameter("tree-authoring-path", environment.getTreeAuthoringPath());

        // Add Request Parameters
        Parameters requestParameters = new Parameters();
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            if (name.startsWith(TASK_PREFIX + JobDataMapWrapper.SEPARATOR)) {
                String shortName = JobDataMapWrapper.getShortName(TASK_PREFIX, name);
                requestParameters.setParameter(shortName, request.getParameter(name));
            }
        }
        parameters.merge(requestParameters);
        // /Add Request Parameters

        return parameters;
    }
        
    public JobDataMap createJobData(String servletContextPath, HttpServletRequest request) {
        Parameters parameters = getParameters(servletContextPath, request);

        log.debug("Creating job data map:");
        
        try {
            JobDataMapWrapper map = new JobDataMapWrapper(TASK_PREFIX);
            String names[] = parameters.getNames();
            for (int i = 0; i < names.length; i++) {
                map.put(names[i], parameters.getParameter(names[i]));
            }
            return map.getMap();
        }
        catch(Exception e) {
            log.error("Cannot create job data map: ", e);
            return null;
        }
    }
    
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
	JobDataMapWrapper map = new JobDataMapWrapper(jobDetail.getJobDataMap(), TASK_PREFIX);
        
        //------------------------------------------------------------
        // execute task
        //------------------------------------------------------------
        
        String taskId = map.get(TASK_ID);
        
        log.debug(
                    "\n-----------------------------------" +
                    "\n Executing task '" + taskId + "'" + 
                    "\n-----------------------------------");
        
        String contextPath = map.get("context-path");
        String publicationId = map.get("publication-id");
        String publicationPath = PublishingEnvironment.getPublicationPath(
                contextPath, publicationId);
        TaskManager manager = new TaskManager(publicationPath);
        Task task = manager.getTask(taskId);

        task.parameterize(map.getParameters());
        task.execute(contextPath);
    }
    
    public JobDetail load(Element jobElement) {
        
        JobDataMap map = new JobDataMap();
        
        Element taskElement = jobElement.element(SchedulerXMLFactory.getQName("task"));
        
        JobDataMapWrapper taskMap = new JobDataMapWrapper(map, TASK_PREFIX);
        
        String debugString = 
            "\n----------------------------------" +
            "\nRestoring tasks:" +
            "\n----------------------------------" +
            "\nTask parameters:";
        
        List parameterElements = taskElement.elements(SchedulerXMLFactory.getQName("parameter"));
        for (Iterator i = parameterElements.iterator(); i.hasNext(); ) {
            Element parameterElement = (Element) i.next();
            String key = parameterElement.attribute("name").getValue();
            String value = parameterElement.attribute("value").getValue();
            taskMap.put(key, value);
            debugString = debugString + "\n" + key + " = " + value;
        }

        debugString = debugString +
            "\nJob parameters:";
        
        JobDataMapWrapper jobMap = new JobDataMapWrapper(map, SchedulerWrapper.JOB_PREFIX);
        
        parameterElements = jobElement.elements(SchedulerXMLFactory.getQName("parameter"));
        for (Iterator i = parameterElements.iterator(); i.hasNext(); ) {
            Element parameterElement = (Element) i.next();
            String key = parameterElement.attribute("name").getValue();
            String value = parameterElement.attribute("value").getValue();
            jobMap.put(key, value);
            debugString = debugString + "\n" + key + " = " + value;
        }

        log.debug(debugString);
        
        Class cl = null;
        String jobId = jobMap.get(SchedulerWrapper.JOB_ID);
        String jobGroup = jobMap.get(SchedulerWrapper.JOB_GROUP);
        try {
            cl = Class.forName(jobMap.get(SchedulerWrapper.JOB_CLASS));
        }
        catch (Exception e) {
            log.error("Cannot load job: ", e);
        }
        
        JobDetail jobDetail = new JobDetail(jobId, jobGroup, cl);
        jobDetail.setJobDataMap(map);
        return jobDetail;
    }
    
    public Element save(JobDetail jobDetail) {
        DocumentFactory factory = DocumentFactory.getInstance();
        Element jobElement = SchedulerXMLFactory.createElement("job");
        JobDataMap map = jobDetail.getJobDataMap();
        
        JobDataMapWrapper jobMap = new JobDataMapWrapper(map, SchedulerWrapper.JOB_PREFIX);
        JobDataMapWrapper taskMap = new JobDataMapWrapper(map, TASK_PREFIX);

        // task parameters
        Element taskElement = SchedulerXMLFactory.createElement("task");
        jobElement.add(taskElement);
        
        Parameters taskParameters = taskMap.getParameters();
        String names[] = taskParameters.getNames();
        for (int i = 0; i < names.length; i++) {
            Element parameterElement = SchedulerXMLFactory.createElement("parameter");
            taskElement.add(parameterElement);
            parameterElement.add(factory.createAttribute(parameterElement,
                    "name", names[i]));
            parameterElement.add(factory.createAttribute(parameterElement,
                    "value", taskMap.get(names[i])));
        }
        
        // job parameters
        Parameters jobParameters = jobMap.getParameters();
        names = jobParameters.getNames();
        for (int i = 0; i < names.length; i++) {
            Element parameterElement = SchedulerXMLFactory.createElement("parameter");
            jobElement.add(parameterElement);
            parameterElement.add(factory.createAttribute(parameterElement,
                    "name", names[i]));
            parameterElement.add(factory.createAttribute(parameterElement,
                    "value", jobMap.get(names[i])));
        }
        
        return jobElement;
    }
}
