/*
 * HelloWorldTask.java
 *
 * Created on November 11, 2002, 9:43 AM
 */

package org.wyona.cms.task;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.log4j.Category;

/**
 * A simple task to demonstrate the task behaviour.
 * It outputs a string to the log4j log file.
 * The following parameter can be provided:<br/>
 * <code><strong>text</strong></code>: the string to be printed<br/>
 * If the parameter is omitted, "Hello World" is printed.
 * @author  ah
 */
public class HelloWorldTask
    extends AbstractTask {

    static Category log = Category.getInstance(HelloWorldTask.class);
        
    /** Creates a new instance of HelloWorldTask */
    public HelloWorldTask() {
    }
    
    /** Execute the task. All parameters must have been set with init(). */
    public void execute(String path) {
        log.debug(".execute(): " + getParameters().getParameter("text", "Hello World"));
    }
    
}
