/*
 * $Id: Task.java,v 1.10 2003/04/24 13:52:59 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.task;

import org.apache.avalon.framework.parameters.Parameterizable;

/**
 * A Task is a command that can be executed. <br/
 * > When a Task is executed from a TaskAction or initialized from a TaskJob, the default
 * parameters are provided. <strong>This is not a contract!</strong>
 *
 * @author <a href="mailto:ah@lenya.org">Andreas Hartmann</a>
 */
public interface Task
        extends Parameterizable {

    /**
     * The path of the servlet
     * context:<br/><code>/home/user_id/build/jakarta-tomcat/webapps/lenyacms</code>
     */
    static final String PARAMETER_SERVLET_CONTEXT = "servlet-context";

    /**
     * The server
     * URI:<br/><code><strong>http://www.yourhost.com</strong>:8080/lenya/publication/index.html</code>
     */
    static final String PARAMETER_SERVER_URI = "server-uri";

    /**
     * The server
     * port:<br/><code>http://www.yourhost.com:<strong>8080</strong>/lenya/publication/index.html</code>
     */
    static final String PARAMETER_SERVER_PORT = "server-port";

    /**
     * The part of the URI that precedes the publication
     * ID:<br/><code>http://www.yourhost.com:8080<strong>/lenya/</strong>publication/index.html</code>
     */
    static final String PARAMETER_CONTEXT_PREFIX = "context-prefix";

    /**
     * The publication
     * ID:<br/><code>http://www.yourhost.com:8080/lenya/<strong>publication</strong>/index.html</code>
     */
    static final String PARAMETER_PUBLICATION_ID = "publication-id";

    /**
     * Execute the task. All parameters must have been set with parameterize().
     */
    void execute(String servletContextPath) throws ExecutionException;

    /**
     * Set the label that is used to identify the task.
     */
    void setLabel(String label);
}
