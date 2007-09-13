/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

/* $Id: AbstractTask.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.cms.task;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;


public abstract class AbstractTask implements Task {
    private Parameters parameters = new Parameters();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Parameters getParameters() {
        Parameters params = new Parameters();
        params = params.merge(parameters);

        return params;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parameters DOCUMENT ME!
     * 
     * @throws ParameterException if the parametrizing fails
     */
    public void parameterize(Parameters parameters) throws ParameterException {
        this.parameters = this.parameters.merge(parameters);
    }

    /**
     * DOCUMENT ME!
     *
     * @param label DOCUMENT ME!
     */
    public void setLabel(String label) {
    }
    
    private int result = SUCCESS;

    /**
     * @see org.apache.lenya.cms.task.Task#getResult()
     */
    public int getResult() {
        return result;
    }
    
    /**
     * Sets the result of this task.
     * @param result An integer ({@link Task#SUCCESS}, {@link Task#FAILURE}).
     */
    protected void setResult(int result) {
        this.result = result;
    }
}
