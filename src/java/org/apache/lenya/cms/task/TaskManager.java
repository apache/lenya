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

/* $Id: TaskManager.java,v 1.22 2004/03/01 16:18:19 gregor Exp $  */

package org.apache.lenya.cms.task;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Category;
import org.xml.sax.SAXException;

public class TaskManager implements Configurable {
    private static Category log = Category.getInstance(TaskManager.class);
    public static final String TASK_ELEMENT = "task";
    public static final String TASK_ID_ATTRIBUTE = "id";
    public static final String CONFIGURATION_FILE =
        File.separator
            + "config"
            + File.separator
            + "tasks"
            + File.separator
            + "tasks.xconf";

    // maps task-ids to tasks
    private Map tasks = new HashMap();

    /**
     * Creates a new TaskManager object.
     */
    public TaskManager() {
    }

    /**
     * Creates a new instance of TaskManager
     *
     * @param publicationPath path to publication
     * @throws ConfigurationException if the configuration failed.
     * @throws SAXException when parsing the config file failed.
     * @throws IOException when an I/O error occured.
     */
    public TaskManager(String publicationPath)
        throws ConfigurationException, SAXException, IOException {
        String configurationFilePath = publicationPath + CONFIGURATION_FILE;
        log.debug("Loading tasks: " + configurationFilePath);

        File configurationFile = new File(configurationFilePath);

        if (configurationFile.isFile()) {
            DefaultConfigurationBuilder builder =
                new DefaultConfigurationBuilder();
            Configuration configuration =
                builder.buildFromFile(configurationFile);
            configure(configuration);

        } else {
            log.info(
                "Task configuration not loaded - file ["
                    + configurationFile.getAbsolutePath()
                    + "] does not exist.");
        }
        tasks.put(EMTPY_TASK, new EmptyTask());
        tasks.put(ANT_TASK, new AntTask());
    }

    public static final String EMTPY_TASK = "empty";
    public static final String ANT_TASK = "ant";

    /**
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration configuration)
        throws ConfigurationException {
        log.debug("Creating tasks:");

        // create task list
        Configuration[] taskConfigurations =
            configuration.getChildren(TASK_ELEMENT);

        // set task IDs
        for (int i = 0; i < taskConfigurations.length; i++) {
            String taskId =
                taskConfigurations[i].getAttribute(TASK_ID_ATTRIBUTE);
            log.debug("Creating task '" + taskId + "'");

            Task task =
                TaskFactory.getInstance().createTask(taskConfigurations[i]);
            tasks.put(taskId, task);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getTaskIds() {
        return (String[]) tasks.keySet().toArray(new String[tasks.size()]);
    }

    /**
     * Get the task with a given task-id
     *
     * @param taskId the task-id of the requested task
     *
     * @return the task
     * 
     * @throws ExecutionException if there is no task with the given task-id.
     */
    public Task getTask(String taskId) throws ExecutionException {
        if (!tasks.containsKey(taskId)) {
            throw new ExecutionException(
                "Task with ID '" + taskId + "' not found!");
        }

        return (Task) tasks.get(taskId);
    }
}
