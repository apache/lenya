/*
 * JobConfiguration.java
 *
 * Created on November 8, 2002, 3:38 PM
 */

package org.wyona.cms.scheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Category;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.wyona.cms.publishing.PublishingEnvironment;
import org.wyona.cms.scheduler.TaskJob;
import org.wyona.cms.scheduler.xml.SchedulerXMLFactory;
import org.wyona.cms.scheduler.xml.TriggerHelper;

/**
 *
 * @author  ah
 */
public class SchedulerWrapper {
    
    private Scheduler scheduler = null;
    
    static Category log = Category.getInstance(SchedulerWrapper.class);

    public static final String JOB_PREFIX = "job";
    public static final String DOCUMENT_URI = "documentUri";
    public static final String JOB_ID = "id";
    public static final String JOB_GROUP = "group";
    public static final String JOB_CLASS = "class";

    private Scheduler getScheduler() {
        return scheduler;
    }

    /** Creates a new instance of SchedulerWrapper */
    public SchedulerWrapper(
            String servletContextPath,
            String schedulerConfigurationPath) {
        this.servletContextPath = servletContextPath;
        this.schedulerConfigurationPath = schedulerConfigurationPath;
        SchedulerFactory factory = new StdSchedulerFactory();
	log.info("------- Starting up -----------------------");
        try {
            scheduler = factory.getScheduler();
            scheduler.start();
        }
        catch (SchedulerException e) {
            log.error("Can't initialize SchedulerWrapper: " + e);
            log.error("------- Startup failed -------------------");
        }
	log.info("------- Startup complete ------------------");
    }
    
    public void  shutdown() {
	log.info("------- Shutting Down ---------------------");
	// try to save state here
	try {
	    getScheduler().shutdown();
	} catch (SchedulerException e) {
	    log.error("------- Shutdown Failed -----------------");
	}
	log.info("------- Shutdown Complete -----------------");
    }

    private String servletContextPath;
    
    protected String getServletContextPath() {
        return servletContextPath;
    }
    
    private String schedulerConfigurationPath;
    
    protected String getSchedulerConfigurationPath() {
        return schedulerConfigurationPath;
    }
    
    private static int jobId = 0;
    
    protected synchronized static String getNextJobId() {
        return "job_" + jobId++ + System.currentTimeMillis();
    }
    
    protected void addJob(
            String jobGroup,
            Date startTime,
            HttpServletRequest request) {
        addJob("-", jobGroup, startTime, request);
    }
    
    protected void addJob(
            String documentUri,
            String jobGroup,
            Date startTime,
            HttpServletRequest request) {
        try {
            
            log.debug(
                    "\n-----------------------------------" +
                    "\n Adding Job for group '" + jobGroup + "'" +
                    "\n-----------------------------------");
            
            // FIXME: more flexible
            Class jobClass = TaskJob.class;
            
            ServletJob job = ServletJobFactory.createJob(jobClass);
            JobDataMap map = job.createJobData(getServletContextPath(), request);
            JobDataMapWrapper mapWrapper = new JobDataMapWrapper(map, JOB_PREFIX);

            String uniqueJobId = getNextJobId();

            mapWrapper.put(JOB_ID, uniqueJobId);
            mapWrapper.put(JOB_GROUP, jobGroup);
            mapWrapper.put(JOB_CLASS, jobClass.getName());
            mapWrapper.put(DOCUMENT_URI, documentUri);

            JobDetail jobDetail = new JobDetail(uniqueJobId, jobGroup, jobClass);
            jobDetail.setJobDataMap(map);

            if (startTime.after(new GregorianCalendar().getTime())) {
                Trigger trigger = TriggerHelper.createSimpleTrigger(uniqueJobId, jobGroup, startTime);
                addJob(jobDetail, trigger);
            }
            else
                addJob(jobDetail);
            
            writeSnapshot(jobGroup);

        } catch (Exception e) {
            log.error("Adding job failed: ", e);
        }
    }

    protected void addJob(JobDetail detail, Trigger trigger) {
        try {
            detail.setDurability(true);
            Date ft = getScheduler().scheduleJob(detail, trigger);
            log.debug("\nJob " + detail.getFullName() + " will run at: " + ft);
        } catch (Exception e) {
            log.error("Adding job failed: ", e);
        }
    }
    
    protected void addJob(JobDetail detail) {
        try {
            detail.setDurability(true);
            getScheduler().addJob(detail, true);
        }
        catch (SchedulerException e) {
            log.error("Adding job failed: ", e);
        }
    }
    
    protected void deleteJob(String jobName, String jobGroup) {
        try {
            log.debug(
                    "\n-----------------------------------" +
                    "\n Deleting job '" + jobGroup + "/" + jobName + "'" +
                    "\n-----------------------------------");
            getScheduler().deleteJob(jobName, jobGroup);
            writeSnapshot(jobGroup);
        } catch (Exception e) {
            log.error("Deleting job failed: " + e);
        }
    }
    
    public File getJobsFile(String publicationId) {
        return new File(
                getServletContextPath() + PublishingEnvironment.PUBLICATION_PREFIX + 
                publicationId + File.separator + "docs" + File.separator + "publication"
                + File.separator +
                "scheduler" + File.separator + "jobs.xml");
    }
    
    protected void writeSnapshot(String publicationId)
        throws SchedulerException {

        File jobsFile = getJobsFile(publicationId);
            
        log.debug("\nUpdating jobs file:\n" + jobsFile.getPath());

        OutputFormat format = OutputFormat.createPrettyPrint();
        
        try {
            jobsFile.createNewFile();
            XMLWriter writer = new XMLWriter(new FileWriter(jobsFile), format);
            writer.write(getSnapshot(publicationId));
            writer.close();
        }
        catch (IOException e) {
            log.error("Writing job snapshot failed: ", e);
        }
    }
    
    protected Configuration getSchedulerConfiguration() {
        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            String path = getServletContextPath() + getSchedulerConfigurationPath();
            log.debug("Initializing scheduler configuration: " + path);
            File configurationFile = new File(path);
            Configuration configuration = builder.buildFromFile(configurationFile);
            return configuration;
        }
        catch (Exception e) {
            log.error("Can't initialize scheduler configuration: ", e);
            return null;
        }
    }
    
    public static final String TRIGGERS_ELEMENT = "triggers";
    public static final String TRIGGER_ELEMENT = "trigger";
    public static final String TITLE_ELEMENT = "title";
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String CLASS_ATTRIBUTE = "class";
    
    protected Element getTriggerTypes() {
        try {
            Configuration configuration = getSchedulerConfiguration();
            Configuration triggerConfigurations[]
                = configuration.getChild(TRIGGERS_ELEMENT).getChildren(TRIGGER_ELEMENT);

            DocumentFactory factory = DocumentFactory.getInstance();
            Element triggersElement = SchedulerXMLFactory.createElement("triggers");
            for (int i = 0; i < triggerConfigurations.length; i++) {
                Configuration conf = triggerConfigurations[i];
                String type = conf.getAttribute(TYPE_ATTRIBUTE);
                String className = conf.getAttribute(CLASS_ATTRIBUTE);
                String title = conf.getChild(TITLE_ELEMENT).getValue();

                Element triggerElement = SchedulerXMLFactory.createElement("trigger");
                triggerElement.add(factory.createAttribute(triggerElement, "name", type));
                triggerElement.add(factory.createAttribute(triggerElement, "src", className));
                triggersElement.add(triggerElement);
            }
            return triggersElement;
        }
        catch (Exception e) {
            log.error("Can't configure trigger types: " + e);
            return null;
        }
    }

    protected Trigger getTrigger(String jobName, String jobGroup)
        throws SchedulerException {
        String[] triggerGroups = getScheduler().getTriggerGroupNames();
        for (int groupIndex = 0; groupIndex < triggerGroups.length; groupIndex++) {
	    String triggerNames[] = getScheduler().getTriggerNames(triggerGroups[groupIndex]);
	    for (int nameIndex = 0; nameIndex < triggerNames.length; nameIndex++) {
                log.debug("Trigger name:  " + triggerNames[nameIndex]);
		Trigger trigger = getScheduler().getTrigger(
                        triggerNames[nameIndex], triggerGroups[groupIndex]);
                log.debug("Job group:     " + trigger.getJobGroup());
                if (trigger.getJobGroup().equals(jobGroup) &&
                    trigger.getJobName().equals(jobName))
                    return trigger;
            }
        }
        return null;
    }
    
    protected Element getJobsElement(String jobGroup)
        throws SchedulerException{
        
        DocumentFactory factory = DocumentFactory.getInstance();
        Element jobsElement = SchedulerXMLFactory.createElement("jobs");
        
        String jobNames[] = getScheduler().getJobNames(jobGroup);
        for (int nameIndex = 0; nameIndex < jobNames.length; nameIndex++) {
            
            JobDetail jobDetail = getScheduler().getJobDetail(
                jobNames[nameIndex], jobGroup);
            
            Element jobElement = createJob(jobDetail).save(jobDetail);
            jobsElement.add(jobElement);

            Trigger trigger = getTrigger(jobNames[nameIndex], jobGroup);
            if (trigger != null) {
                Element triggerElement = TriggerHelper.createElement(trigger);
                jobElement.add(triggerElement);
            }
        }
        
        return jobsElement;
    }
    
    protected ServletJob createJob(JobDetail jobDetail) {
        JobDataMap map = jobDetail.getJobDataMap();
        Class cl = jobDetail.getJobClass();
        
        try {
            return (ServletJob) cl.newInstance();
        }
        catch(Exception e) {
            log.error("Creating job failed: ", e);
            return null;
        }
    }

    /**
     * Return an xml description of all scheduled jobs for the given documentID.
     *
     * @param writer a <code>PrintWriter</code> value
     * @param documentID a <code>String</code> value
     * @exception SchedulerException if an error occurs
     */
    public Document getSnapshot(String jobGroup)
	throws SchedulerException {
            
        log.debug("Creating job snapshot for group '" +jobGroup + "'");
	
	String[] triggerNames = null;

        Trigger trigger;
	JobDetail jobDetail;
	GregorianCalendar nextFireTime;
	JobDataMap jobDataMap;

        DocumentFactory factory = DocumentFactory.getInstance();
        
        Element root = SchedulerXMLFactory.createElement("scheduler");
        Document document = factory.createDocument(root);
        
	// print a list of all available trigger types
        root.add(getTriggerTypes());
        
	// print a list of all available tasks
        // root.add(TaskJob.getTasks(getServletContextPath(), jobGroup));
        
	// and finally for all publications print all scheduled Jobs

        String jobGroupNames[] = getScheduler().getJobGroupNames();
        for (int groupIndex = 0; groupIndex < jobGroupNames.length; groupIndex++) {
            Element publicationElement = SchedulerXMLFactory.createElement("publication");
            publicationElement.add(factory.createAttribute(
                publicationElement, "name", jobGroupNames[groupIndex]));
            root.add(publicationElement);
            Element jobsElement = getJobsElement(jobGroupNames[groupIndex]);
            publicationElement.add(jobsElement);
        }
        
        return document;
    }

    public void restoreJobs(String publicationId) {

        log.debug(
            "\n------------------------------------" + 
            "\n Restoring jobs for publication " + publicationId +
            "\n------------------------------------");
        
        File jobsFile = getJobsFile(publicationId);
        if (jobsFile.exists()) {
            
            SAXReader reader = new SAXReader();
            try {
                Document document = reader.read(jobsFile);

                Element schedulerElement = document.getRootElement();
                Element publicationElement
                    = schedulerElement.element(
                    SchedulerXMLFactory.getQName("publication"));
                Element jobsElement = publicationElement.element(
                    SchedulerXMLFactory.getQName("jobs"));
                List jobElements = jobsElement.elements(
                    SchedulerXMLFactory.getQName("job"));

                for (Iterator i = jobElements.iterator(); i.hasNext(); ) {
                    Element jobElement = (Element) i.next();
                    restoreJob(jobElement);
                }
            }
            catch (Exception e) {
                log.error("Restoring jobs failed: ", e);
            }
        }
    }
    
    protected void restoreJob(Element jobElement) {
        
        log.debug(
            "\n Restoring job ");
        
        String className = null;
        
        List parameterElements = jobElement.elements(
            SchedulerXMLFactory.getQName("parameter"));
        for (Iterator i = parameterElements.iterator(); i.hasNext(); ) {
            Element parameterElement = (Element) i.next();
            String key = parameterElement.attribute("name").getValue();
            if ((key).equals(JOB_CLASS)) {
                className = parameterElement.attribute("value").getValue();
            }
        }

        ServletJob job = null;
        try {
            Class cl = Class.forName(className);
            job = (ServletJob) cl.newInstance();
        }
        catch (Exception e) {
            log.error("Could not restore job: ", e);
        }
        
        JobDetail jobDetail = job.load(jobElement);
        
        Element triggerElement = jobElement.element(
            SchedulerXMLFactory.getQName("trigger"));
        if (triggerElement != null) {
            Trigger trigger = TriggerHelper.createTrigger(
                triggerElement, jobDetail.getName(), jobDetail.getGroup());
            
            GregorianCalendar now = new GregorianCalendar();
            if (trigger.getFinalFireTime().after(now.getTime()))
                addJob(jobDetail, trigger);
            else
                addJob(jobDetail);
        }
        else {
            addJob(jobDetail);
        }
    }
    
}
