/*
 * $Id: AntTask.java
<License>
 * =======================================================================
 * Copyright (c) 2000 wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products
 *    derived from this software without prior written permission.
 *    For written permission , please contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona"
 *    nor may "wyona" appear in their names without prior written
 *    permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY
 * EXPRESS OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND
 * THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS
 * A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE FOR
 * ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN
 * IF wyona HAS BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE.
 * wyona WILL NOT BE LIABLE FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 * =======================================================================
</License>
 */
package org.lenya.cms.task;

import org.lenya.cms.task.ExecutionException;

import java.io.File;

import java.util.Map;


/**
 * An object of this class is used to execute Ant tasks.
 * The task parameters are:
 *
 * <ul>
 *   <li><code><strong>publication-id</strong></code>: the publication ID</li>
 *   <li><code><strong>buildfile</strong></code>: the location of the build file
 *       relative to the publication directory</li>
 *   <li><code><strong>target</strong></code>: the build target</li>
 *   <li><code><strong>ant.*</strong></code>: the command-line parameters for Ant</li>
 *   <li><code><strong>properties.*</strong></code>: the project properties</li>
 * </ul>
 *
 * @author <a href="mailto:ah@wyona.org">Andreas Hartmann</a>
 */
public class AntTask
        extends AbstractTask {
    
    /**
     * Executes an Ant target.
     *
     * @param buildFile The build XML file.
     * @param target The name of the target to execute.
     * @param arguments A map mapping the command-line arguments to their values.
     * @param properties A map mapping the project properties to their values.
     */
    public void executeAntTarget(File buildFile, String target, Map arguments,
        Map properties) {
    }

    /**
     * Execute the task. All parameters must have been set with parameterize().
     *
     * @param servletContextPath The servlet context path.
     *
     * @throws ExecutionException when the execution of the task failed.
     */
    public void execute(String servletContextPath) throws ExecutionException {
    }
}
