/*
 * Task.java
 *
 * Created on November 6, 2002, 6:14 PM
 */

package org.wyona.cms.task;

import javax.servlet.http.HttpServletRequest;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;

/**
 * A Task is a command that can be executed.
 * @author  ah
 */
public interface Task
    extends Parameterizable {
    
    /**
     * Initialize some or all parameters of the task.
     * You can call this method multiple times, the parameter values are replaced.
     */
    void parameterize(Parameters parameters);
    
    /**
     * Execute the task. All parameters must have been set with init().
     */
    void execute(String servletContextPath);
    
    /**
     * Return the label that is used to identify the task.
     */
    String getLabel();
    
    /**
     * Set the label that is used to identify the task.
     */
    void setLabel(String label);
    
}
