/*
 * TaskAction.java
 *
 * Created on November 6, 2002, 6:22 PM
 */

package org.wyona.cms.cocoon.acting;

import java.util.Enumeration;
import java.util.HashMap;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.log4j.Category;
import org.wyona.cms.publishing.PublishingEnvironment;
import org.wyona.cms.task.*;

/**
 *
 * @author  ah
 */
public class TaskAction
    extends AbstractComplementaryConfigurableAction {
    
    static Category log = Category.getInstance(TaskAction.class);
  
    private String taskId;
    
    public String getTaskId() {
        return taskId;
    }
    
    /**
     *
     */
    public void configure(Configuration configuration) throws ConfigurationException{
        super.configure(configuration);
        
        taskId = configuration.getChild("task").getAttribute(TaskManager.TASK_ID_ATTRIBUTE);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("CONFIGURATION:\ntask id = " +
                              taskId);
        }
    } 
    
    public java.util.Map act(
            org.apache.cocoon.environment.Redirector redirector,
            org.apache.cocoon.environment.SourceResolver sourceResolver,
            java.util.Map objectModel,
            String str,
            Parameters parameters)
            throws java.lang.Exception {
                
	// Get Source
	org.apache.cocoon.environment.Source inputSource = sourceResolver.resolve("");
	String publicationPath = inputSource.getSystemId();
	// Remove "file:" protocol
	publicationPath = publicationPath.substring(5); 
        
        log.info("######### " + publicationPath);
        
        if (publicationPath.endsWith("/"))
            publicationPath = publicationPath.substring(0, publicationPath.length()-1);

        log.info("######### " + publicationPath);
        
        int lastSlashIndex = publicationPath.lastIndexOf("/");
        String publicationId = publicationPath.substring(lastSlashIndex + 1);

        log.info("#######id " + publicationId);

        publicationPath = publicationPath.substring(0, lastSlashIndex + 1);

        log.info("######### " + publicationPath);
        
        String publicationPrefix = PublishingEnvironment.PUBLICATION_PREFIX;
//        String contextPath = publicationPath.replaceAll(publicationPrefix, "");
        String contextPath = publicationPath.substring(
            0, publicationPath.length() - publicationPrefix.length());

        log.info("######### " + contextPath);
        
	// Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

	if (request == null) {
	    getLogger().error ("No request object");
	    return null;
	}
	
        //------------------------------------------------------------
        // prepare default parameters
        //------------------------------------------------------------

        publicationPath = PublishingEnvironment.getPublicationPath(contextPath, publicationId);
        
        Parameters taskParameters = new Parameters();

        taskParameters.setParameter("server-uri", "http://" + request.getServerName());
        taskParameters.setParameter("server-port", Integer.toString(request.getServerPort()));
        taskParameters.setParameter("publication-id", publicationId);
        
        // set parameters using the request parameters
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            taskParameters.setParameter(name, request.getParameter(name));
        }
        
        //------------------------------------------------------------
        // execute task
        //------------------------------------------------------------
        
        log.debug("Executing task '" + getTaskId() + "'");
        
        TaskManager manager = new TaskManager(publicationPath);
        Task task = manager.getTask(getTaskId());

        task.parameterize(taskParameters);
        task.execute(contextPath);

        //------------------------------------------------------------
        // get session
        //------------------------------------------------------------
        
	Session session = request.getSession(true);
	if (session == null) {
	    getLogger().error("No session object");
	    return null;
	}
	
        //------------------------------------------------------------
	// Return referer
        //------------------------------------------------------------

        String parent_uri = (String)session.getAttribute("org.wyona.cms.cocoon.acting.TaskAction.parent_uri");
	HashMap actionMap = new HashMap();
	actionMap.put("parent_uri", parent_uri);
	session.removeAttribute("org.wyona.cms.cocoon.acting.TaskAction.parent_uri");
	return actionMap;
    }
    
}
