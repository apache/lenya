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

/* $Id$  */

package org.apache.lenya.cms.task;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log4j.Logger;


/**
 * A TaskSequence contains of multiple tasks that are executed successively.
 */
public class TaskSequence extends AbstractTask {
    private static Logger log = Logger.getLogger(TaskSequence.class);

    // keeps the task order
    private Task[] tasks;
    private TaskManager taskManager;

    /**
     * Initialize the task
     * @param configuration The configuration
     * @throws ConfigurationException if an error occurs
     */
    public void init(Configuration configuration) throws ConfigurationException {
        this.taskManager = new TaskManager();
        this.taskManager.configure(configuration);

        // create task list
        Configuration[] taskConfigurations = configuration.getChildren(TaskManager.TASK_ELEMENT);
        this.tasks = new Task[taskConfigurations.length];

        // set task IDs
        for (int i = 0; i < this.tasks.length; i++) {
            String taskId = taskConfigurations[i].getAttribute(TaskManager.TASK_ID_ATTRIBUTE);

            try {
                this.tasks[i] = this.taskManager.getTask(taskId);
            } catch (ExecutionException e) {
                throw new ConfigurationException("Sequence initialization failed: ", e);
            }

            log.debug("Adding task '" + taskId + "' to sequence.");
        }
    }

    /**
     * Returns the tasks in this sequence.
     * @return The tasks
     */
    public Task[] getTasks() {
        return (Task[]) this.tasks.clone();
    }

    /**
     * Returns the TaskManager that is used to manage the tasks of this TaskSequence.
     * @return The task manager
     */
    protected TaskManager getTaskManager() {
        return this.taskManager;
    }

    /**
     * Returns the ID of a specific Task.
     * @param task the specific task for which the task id is requested.
     * @return the task id of the given task
     * @throws ExecutionException if the task could not be found.
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
     * @param path The path to the tasks
     * @throws ExecutionException if the execution fails
     */
    public void execute(String path) throws ExecutionException {

        try {
            Task[] _tasks = getTasks();

            for (int i = 0; i < _tasks.length; i++) {
                Task task = _tasks[i];
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
        } catch (final IllegalStateException e) {
            log.error("Cannot execute TaskSequence: ", e);
            throw new ExecutionException(e);
        } catch (final ParameterException e) {
            log.error("Cannot execute TaskSequence: ", e);
            throw new ExecutionException(e);
        } catch (final ExecutionException e) {
            log.error("Cannot execute TaskSequence: ", e);
            throw new ExecutionException(e);
        }
    }
}
