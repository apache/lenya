/*
 * TaskSequence.java
 *
 * Created on November 6, 2002, 6:30 PM
 */

package org.wyona.cms.task;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import java.util.HashMap;
import java.util.SortedMap;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log4j.Category;
import org.dom4j.DocumentFactory;
import org.dom4j.Namespace;

/**
 * A TaskSequence contains of multiple tasks that are executed successively.
 *
 * @author  ah
 */
public class TaskSequence
    extends AbstractTask {

    public static String NAMESPACE_SEPARATOR = ":";
        
    // keeps the task order
    private Task[] tasks;
    private TaskManager taskManager;
    
    static Category log = Category.getInstance(TaskSequence.class);
    
    public void init(Configuration configuration)
        throws ConfigurationException {
            
        taskManager = new TaskManager();
        taskManager.configure(configuration);
            
        // create task list
        Configuration taskConfigurations[]
            = configuration.getChildren(TaskManager.TASK_ELEMENT);
        tasks = new Task[taskConfigurations.length];
        
        // set task IDs
        for (int i = 0; i < tasks.length; i++) {
            String taskId = taskConfigurations[i].getAttribute(TaskManager.TASK_ID_ATTRIBUTE);
            tasks[i] = taskManager.getTask(taskId);
            log.debug("Adding task '" + taskId + "' to sequence.");
        }
    } 

    /**
     * Returns the tasks in this sequence.
     */
    public Task[] getTasks() {
        return (Task[]) tasks.clone();
    }
    
    /**
     * Returns the TaskManager that is used to manage the tasks of this TaskSequence.
     */
    protected TaskManager getTaskManager() {
        return taskManager;
    }
    
    /**
     * Returns the ID of a specific Task.
     */
    public String getTaskId(Task task) {
        
        String taskIds[] = getTaskManager().getTaskIds();
        for (int j = 0; j < taskIds.length; j++) {
            if (getTaskManager().getTask(taskIds[j]) == task)
                return taskIds[j];
        }
        
        throw new IllegalStateException("Task-ID for " + task + " not found!");
    }
    
    /**
     * Executes the tasks.
     */
    public void execute(String path) {
        try {
            Task tasks[] = getTasks();
            for (int i = 0; i < tasks.length; i++) {
                
                Task task = tasks[i];
                String taskId = getTaskId(task);
                log.debug("Executing task '" + taskId + "'");
                
                // create task parameters
                Parameters taskParameters = new Parameters();
                String names[] = getParameters().getNames();
                for (int parIndex = 0; parIndex < names.length; parIndex++) {
                    String name = names[parIndex];
                    boolean useParameter = true;
/*
                    // is this a qualified parameter?
                    if (name.indexOf(NAMESPACE_SEPARATOR) > -1) {
                        
                        // remove namespace prefix (taskID + ":")
                        if (name.startsWith(taskId + NAMESPACE_SEPARATOR))
                            name = name.replaceFirst(taskId + NAMESPACE_SEPARATOR, "");
                        else
                            useParameter = false;
                    }
*/                    
                    if (useParameter)
                        taskParameters.setParameter(name, getParameters().getParameter(name));
                }
                
                // execute task
                task.parameterize(taskParameters);
                task.execute(path);
            }
        }
        catch(Exception e) {
            log.error("Cannot execute TaskSequence: ", e);
        }
    }
}
