/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
/*
 * TaskSequence.java
 *
 * Created on November 6, 2002, 6:30 PM
 */
package org.apache.lenya.cms.task;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.log4j.Category;


/**
 * A TaskSequence contains of multiple tasks that are executed successively.
 *
 * @author ah
 */
public class TaskSequence extends AbstractTask {
    private static Category log = Category.getInstance(TaskSequence.class);

    // keeps the task order
    private Task[] tasks;
    private TaskManager taskManager;

    /**
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void init(Configuration configuration) throws ConfigurationException {
        taskManager = new TaskManager();
        taskManager.configure(configuration);

        // create task list
        Configuration[] taskConfigurations = configuration.getChildren(TaskManager.TASK_ELEMENT);
        tasks = new Task[taskConfigurations.length];

        // set task IDs
        for (int i = 0; i < tasks.length; i++) {
            String taskId = taskConfigurations[i].getAttribute(TaskManager.TASK_ID_ATTRIBUTE);

            try {
                tasks[i] = taskManager.getTask(taskId);
            } catch (ExecutionException e) {
                throw new ConfigurationException("Sequence initialization failed: ", e);
            }

            log.debug("Adding task '" + taskId + "' to sequence.");
        }
    }

    /**
     * Returns the tasks in this sequence.
     *
     * @return DOCUMENT ME!
     */
    public Task[] getTasks() {
        return (Task[]) tasks.clone();
    }

    /**
     * Returns the TaskManager that is used to manage the tasks of this TaskSequence.
     *
     * @return DOCUMENT ME!
     */
    protected TaskManager getTaskManager() {
        return taskManager;
    }

    /**
     * Returns the ID of a specific Task.
     *
     * @param task DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IllegalStateException DOCUMENT ME!
     */
    public String getTaskId(Task task) throws ExecutionException {
        String[] taskIds = getTaskManager().getTaskIds();

        for (int j = 0; j < taskIds.length; j++) {
            if (getTaskManager().getTask(taskIds[j]) == task) {
                return taskIds[j];
            }
        }

        throw new IllegalStateException("Task-ID for " + task + " not found!");
    }

    /**
     * Executes the tasks.
     *
     * @param path DOCUMENT ME!
     */
    public void execute(String path) throws ExecutionException {
        try {
            Task[] tasks = getTasks();

            for (int i = 0; i < tasks.length; i++) {
                Task task = tasks[i];
                String taskId = getTaskId(task);
                log.debug("Executing task '" + taskId + "'");

                // create task parameters
                Parameters taskParameters = new Parameters();
                String[] names = getParameters().getNames();

                for (int parIndex = 0; parIndex < names.length; parIndex++) {
                    String name = names[parIndex];
                    boolean useParameter = true;

                    if (useParameter) {
                        taskParameters.setParameter(name, getParameters().getParameter(name));
                    }
                }

                // execute task
                task.parameterize(taskParameters);
                task.execute(path);
            }
        } catch (Exception e) {
            log.error("Cannot execute TaskSequence: ", e);
        }
    }
}
