/*
$Id: TaskManager.java,v 1.21 2004/02/02 02:50:39 stefano Exp $
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

/**
 * DOCUMENT ME!
 *
 * @author ah
 */
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
