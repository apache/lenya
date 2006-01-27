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
 * A Task factory
 * @deprecated Use the usecase framework instead.
 */
public class TaskFactory {

    /**
     * Create a new instance of <code>TaskFactory</code>
     */
    protected TaskFactory() {
        // do nothing
    }

    private static TaskFactory factory;
    private static Logger log = Logger.getLogger(TaskFactory.class);

    /**
     * Get an instance of the task factory
     * @return A task factory
     */
    public static TaskFactory getInstance() {
        if (factory == null) {
            factory = new TaskFactory();
        }

        return factory;
    }

    /**
     * Create a task
     * @param configuration The configuration for the task
     * @return The task
     */
    public Task createTask(Configuration configuration) {

        try {
            String className = configuration.getAttribute("class",
                    "org.apache.lenya.cms.task.TaskSequence");
            Class cl = Class.forName(className);
            Task task = (Task) cl.newInstance();

            task.setLabel(configuration.getChild("label").getValue("default task"));

            task.parameterize(Parameters.fromConfiguration(configuration));

            if (task instanceof TaskSequence) {
                ((TaskSequence) task).init(configuration);
            }

            return task;
        } catch (final ParameterException e) {
            log.error("Cannot create Task: ", e);
            return null;
        } catch (final ConfigurationException e) {
            log.error("Cannot create Task: ", e);
            return null;
        } catch (final ClassNotFoundException e) {
            log.error("Cannot create Task: ", e);
            return null;
        } catch (final InstantiationException e) {
            log.error("Cannot create Task: ", e);
            return null;
        } catch (final IllegalAccessException e) {
            log.error("Cannot create Task: ", e);
            return null;
        }
    }
}
