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

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * Abstract baseclass for Task
 * @deprecated Use the usecase framework instead.
 */
public abstract class AbstractTask implements Task {
    private Parameters parameters = new Parameters();

    private ServiceManager manager;

    /**
     * Ctor.
     */
    public AbstractTask() {
    }

    /**
     * @param manager The service manager to use.
     */
    public void service(ServiceManager manager) {
        this.manager = manager;
    }

    protected ServiceManager getServiceManager() {
        return this.manager;
    }

    /**
     * Get parameters of the task
     * @return The parameters
     */
    public Parameters getParameters() {
        Parameters params = new Parameters();
        params = params.merge(this.parameters);

        return params;
    }

    /**
     * Set the parameters
     * @param _parameters The parameters
     * @throws ParameterException if the parametrizing fails
     */
    public void parameterize(Parameters _parameters) throws ParameterException {
        this.parameters = this.parameters.merge(_parameters);
    }

    /**
     * Set the label of the task
     * @param label The label
     */
    public void setLabel(String label) {
        // do nothing
    }

    private int result = SUCCESS;

    /**
     * @see org.apache.lenya.cms.task.Task#getResult()
     */
    public int getResult() {
        return this.result;
    }

    /**
     * Sets the result of this task.
     * @param _result An integer ({@link Task#SUCCESS}, {@link Task#FAILURE}).
     */
    protected void setResult(int _result) {
        this.result = _result;
    }
}
