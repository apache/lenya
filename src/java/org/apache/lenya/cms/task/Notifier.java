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

/* $Id: Notifier.java,v 1.6 2004/03/01 16:18:19 gregor Exp $  */

package org.apache.lenya.cms.task;

import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.util.NamespaceMap;
import org.apache.log4j.Category;

public class Notifier extends ParameterWrapper {

    private static Category log = Category.getInstance(Notifier.class);

    public static final String PREFIX = "notification";
    public static final String TARGET = "notification";
    public static final String PARAMETER_TO = "tolist";

    private TaskManager taskManager;

    /**
     * Ctor.
     * @param taskManager The task manager.
     * @param parameters The task wrapper parameters.
     */
    public Notifier(TaskManager taskManager, Map parameters) {
        super(parameters);
        this.taskManager = taskManager;
    }

    /**
     * Sends the notification message.
     * @param taskParameters The task parameters.
     * @throws ExecutionException when something went wrong.
     */
    public void sendNotification(TaskParameters taskParameters) throws ExecutionException {

        if (getMap().isEmpty()) {
            log.info("Not sending notification: no parameters provided.");
        } else if ("".equals(get(PARAMETER_TO).trim())) {
            log.info("Not sending notification: empty notification.tolist parameter.");
        }
        else {
            log.info("Sending notification");
            
            Task task = taskManager.getTask(TaskManager.ANT_TASK);

            Parameters params = new Parameters();

            params.setParameter(AntTask.TARGET, TARGET);
            
            String[] keys =
                {
                    Task.PARAMETER_PUBLICATION_ID,
                    Task.PARAMETER_CONTEXT_PREFIX,
                    Task.PARAMETER_SERVER_PORT,
                    Task.PARAMETER_SERVER_URI,
                    Task.PARAMETER_SERVLET_CONTEXT };

            for (int i = 0; i < keys.length; i++) {
                params.setParameter(keys[i], taskParameters.get(keys[i]));
            }

            NamespaceMap mailMap = new NamespaceMap(PREFIX);
            mailMap.putAll(getMap());
            NamespaceMap propertiesMap = new NamespaceMap(AntTask.PROPERTIES_PREFIX);
            propertiesMap.putAll(mailMap.getPrefixedMap());

            Map prefixMap = propertiesMap.getPrefixedMap();
            for (Iterator i = prefixMap.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                params.setParameter(key, (String) prefixMap.get(key));
            }

            try {
                task.parameterize(params);
            } catch (ParameterException e) {
                throw new ExecutionException(e);
            }
            log.info("    Executing notification target ...");
            try {
                task.execute(taskParameters.get(Task.PARAMETER_SERVLET_CONTEXT));
            }
            catch (Exception e) {
                log.error("Error during notification: ", e);
            }
            log.info("    Notification target executed.");
        }
    }

    /**
     * Returns the task manager.
     * @return A task manager.
     */
    protected TaskManager getTaskManager() {
        return taskManager;
    }

    /**
     * @see org.apache.lenya.cms.task.ParameterWrapper#getPrefix()
     */
    public String getPrefix() {
        return PREFIX;
    }

    /**
     * @see org.apache.lenya.cms.task.ParameterWrapper#getRequiredKeys()
     */
    protected String[] getRequiredKeys() {
        String[] requiredKeys = { };
        return requiredKeys;
    }

}
