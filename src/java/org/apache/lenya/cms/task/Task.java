/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: Task.java,v 1.19 2004/03/01 16:18:19 gregor Exp $  */

package org.apache.lenya.cms.task;

import org.apache.avalon.framework.parameters.Parameterizable;

/**
 * A Task is a command that can be executed. <br/
 * > When a Task is executed from a TaskAction or initialized from a TaskJob, the default
 * parameters are provided. <strong>This is not a contract!</strong>
 */
public interface Task extends Parameterizable {
    
    String NAMESPACE = "http://apache.org/cocoon/lenya/task/1.0";
    String DEFAULT_PREFIX = "task";
    int SUCCESS = 0;
    int FAILURE = 1;

    /**
     * The path of the servlet
     * context:<br/><code>/home/user_id/build/jakarta-tomcat/webapps/lenyacms</code>
     */
    String PARAMETER_SERVLET_CONTEXT = "servlet-context";

    /**
     * The server
     * URI:<br/><code><strong>http://www.yourhost.com</strong>:8080/lenya/publication/index.html</code>
     */
    String PARAMETER_SERVER_URI = "server-uri";

    /**
     * The server
     * port:<br/><code>http://www.yourhost.com:<strong>8080</strong>/lenya/publication/index.html</code>
     */
    String PARAMETER_SERVER_PORT = "server-port";

    /**
     * The part of the URI that precedes the publication
     * ID:<br/><code>http://www.yourhost.com:8080<strong>/lenya</strong>/publication/index.html</code>
     */
    String PARAMETER_CONTEXT_PREFIX = "context-prefix";

    /**
     * The publication
     * ID:<br/><code>http://www.yourhost.com:8080/lenya/<strong>publication</strong>/index.html</code>
     */
    String PARAMETER_PUBLICATION_ID = "publication-id";

    /**
     * Execute the task. All parameters must have been set with parameterize().
     * 
     * @param servletContextPath the servlet-context
     * 
     * @throws ExecutionException if the execution fails
     */
    void execute(String servletContextPath) throws ExecutionException;

    /**
     * Set the label that is used to identify the task.
     * 
     * @param label the label
     */
    void setLabel(String label);
    
    /**
     * Returns the result of the task ({@link #SUCCESS}, {@link FAILURE}).
     * @return
     */
    int getResult();
}
