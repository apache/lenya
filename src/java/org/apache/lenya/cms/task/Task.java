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
 * A Task is a command that can be executed. <br/>
 * When a Task is executed from a TaskAction or initialized from a TaskJob,
 * the default parameters are provided. <strong>This is not a contract!</strong>
 * @author  <a href="mailto:ah@wyona.org">Andreas Hartmann</a>
 */
public interface Task
    extends Parameterizable {

    /**
     * The path of the servlet context:<br/>
     * <code>/home/user_id/build/jakarta-tomcat/webapps/wyonacms</code>
     */
    static final String PARAMETER_SERVLET_CONTEXT = "servlet-context";
    
    /**
     * The server URI:<br/>
     * <code><strong>http://www.yourhost.com</strong>:8080/wyona-cms/publication/index.html</code>
     */
    static final String PARAMETER_SERVER_URI = "server-uri";
    
    /**
     * The server port:<br/>
     * <code>http://www.yourhost.com:<strong>8080</strong>/wyona-cms/publication/index.html</code>
     */
    static final String PARAMETER_SERVER_PORT = "server-port";
    
    /**
     * The part of the URI that precedes the publication ID:<br/>
     * <code>http://www.yourhost.com:8080<strong>/wyona-cms/</strong>publication/index.html</code>
     */
    static final String PARAMETER_CONTEXT_PREFIX = "context-prefix";
    
    /**
     * The publication ID:<br/>
     * <code>http://www.yourhost.com:8080/wyona-cms/<strong>publication</strong>/index.html</code>
     */
    static final String PARAMETER_PUBLICATION_ID = "publication-id";
    
    /**
     * Initialize some or all parameters of the task.
     * You can call this method multiple times, the parameter values are replaced.
     */
    void parameterize(Parameters parameters);
    
    /**
     * Execute the task. All parameters must have been set with parameterize().
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
