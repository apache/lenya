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

/* $Id: TaskWrapperParameters.java,v 1.4 2004/03/01 16:18:20 gregor Exp $  */

package org.apache.lenya.cms.task;

import java.util.Map;

public class TaskWrapperParameters extends ParameterWrapper {

    public static final String TASK_ID = "task-id";
    public static final String WEBAPP_URL = "webapp-url";

    protected static final String[] REQUIRED_KEYS = { TASK_ID, WEBAPP_URL };

    public static final String PREFIX = "wrapper";

    /**
     * Ctor.
     * @param parameters The parameter map to use.
     */
    public TaskWrapperParameters(Map parameters) {
        super(parameters);
    }

    /**
     * Returns the required keys.
     * @return A string array.
     */
    public String[] getRequiredKeys() {
        return REQUIRED_KEYS;
    }

    /**
     * Returns the task ID.
     * @return A string.
     */
    public String getTaskId() {
        return get(TASK_ID);
    }

    /**
     * Returns the webapp URL.
     * @return A string.
     */
    public String getWebappUrl() {
        return get(WEBAPP_URL);
    }

    /**
     * Sets the webapp URL.
     * @param url A url.
     */
    public void setWebappUrl(String url) {
        put(WEBAPP_URL, url);
    }

    /**
     * Sets the task ID.
     * @param taskId A string.
     */
    public void setTaskId(String taskId) {
        put(TASK_ID, taskId);
    }

    /**
     * @see org.apache.lenya.cms.task.ParameterWrapper#getPrefix()
     */
    public String getPrefix() {
        return PREFIX;
    }
    
}
