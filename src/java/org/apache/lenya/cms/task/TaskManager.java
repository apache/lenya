/*
 * TaskManager.java
 *
 * Created on November 7, 2002, 11:18 AM
 */

package org.wyona.cms.task;

import java.io.File;
import java.net.URL;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import java.util.HashMap;
import org.apache.log4j.Category;

/**
 *
 * @author  ah
 */
public class TaskManager
    implements Configurable {
    
    static Category log = Category.getInstance(TaskManager.class);

    public static String TASKS_ELEMENT = "tasks";
    public static String TASK_ELEMENT = "task";
    public static String TASK_ID_ATTRIBUTE = "id";
    public static String PARAMETER_ELEMENT = "parameter";

    public static String CONFIGURATION_FILE = File.separator + "docs" + File.separator
                + "publication" + File.separator + "tasks.xconf";

    // maps task-ids to tasks
    private Map tasks = new HashMap();

    public TaskManager() {
    }
    
    /**
     * Creates a new instance of TaskManager
     * @param publicationPath: path to publication,
     */
    public TaskManager(String publicationPath) {

        String configurationFilePath = publicationPath + CONFIGURATION_FILE;
        log.debug("Loading tasks: " + configurationFilePath);
        File configurationFile = new File(configurationFilePath);
        
        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            Configuration configuration = builder.buildFromFile(configurationFile);
            configure(configuration);
        }
        catch (Exception e) {
            log.error("Cannot load task configuration! ", e);
        }
    }
    
    public void configure(Configuration configuration)
            throws ConfigurationException {
        
        log.debug("Creating tasks:");
        
        // create task list
        Configuration taskConfigurations[]
            = configuration.getChildren(TASK_ELEMENT);
        
        // set task IDs
        for (int i = 0; i < taskConfigurations.length; i++) {
            String taskId = taskConfigurations[i].getAttribute(TASK_ID_ATTRIBUTE);
            log.debug("Creating task '" + taskId + "'");
            Task task = TaskFactory.getInstance().createTask(taskConfigurations[i]);
            tasks.put(taskId, task);
        }

    }
    
    public String[] getTaskIds() {
        return (String[]) tasks.keySet().toArray(new String[tasks.size()]);
    }
    
    public Task getTask(String taskId) {
        return (Task) tasks.get(taskId);
    }

}
