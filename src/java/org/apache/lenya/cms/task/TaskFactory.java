/*
 * TaskFactory.java
 *
 * Created on November 7, 2002, 11:08 AM
 */

package org.wyona.cms.task;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log4j.Category;

/**
 *
 * @author  ah
 */
public class TaskFactory {

    private static TaskFactory factory;
    
    static Category log = Category.getInstance(TaskFactory.class);

    public static TaskFactory getInstance() {
        if (factory == null)
            factory = new TaskFactory();
        return factory;
    }
    
    public Task createTask(Configuration configuration) {
        try {
            String className = configuration.getAttribute(
                "class", "org.wyona.cms.task.TaskSequence");
            Class cl = Class.forName(className);
            Task task = (Task) cl.newInstance();
            
            task.setLabel(configuration.getChild("label").getValue("default task"));
            
            task.parameterize(Parameters.fromConfiguration(configuration));
            if (task instanceof TaskSequence)
                ((TaskSequence) task).init(configuration);
            return task;
        }
        catch (Exception e) {
            log.error("Cannot create Task " + e);
            return null;
        }
    }

    /*
    public Task createTask(String className, Configuration configuration) {
        try {
            Class cl = Class.forName(className);
            Task task = (Task) cl.newInstance();
            if (
            return 
        }
        catch (Exception e) {
            log.error("Cannot create Task!", e);
            return null;
        }
    }
    */
    
}
