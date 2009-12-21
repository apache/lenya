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

/* $Id$  */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.Workflowable;

/**
 * Implementation of a boolean variable condition.
 */
public class BooleanVariableCondition extends AbstractCondition {

    private String variableName;
    private boolean value;

    /**
     * Returns the variable value to check.
     * @return A boolean value.
     */
    protected boolean getValue() {
        return this.value;
    }

    /**
     * Returns the variable name to check.
     * @return A string.
     */
    protected String getVariableName() {
        return this.variableName;
    }

    /**
     * @see org.apache.lenya.workflow.Condition#setExpression(java.lang.String)
     */
    public void setExpression(String expression) throws WorkflowException {
        super.setExpression(expression);
        String[] sides = expression.split("=");
        if (sides.length != 2) {
            throw new WorkflowException("The expression '" + expression
                    + "' must be of the form 'name = [true|false]'");
        }

        this.variableName = sides[0].trim();
        this.value = Boolean.valueOf(sides[1].trim()).booleanValue();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Expression:    [" + sides[1].trim() + "]");
            getLogger().debug("Setting value: [" + this.value + "]");
        }
    }

    /**
     * @see org.apache.lenya.workflow.Condition#isComplied(org.apache.lenya.workflow.Workflow,
     *      org.apache.lenya.workflow.Workflowable)
     */
    public boolean isComplied(Workflow workflow, Workflowable workflowable)
            throws WorkflowException {
        Version latestVersion = workflowable.getLatestVersion();
        boolean value = false;
        if (latestVersion == null) {
            value = workflow.getInitialValue(getVariableName());
        } else {
            value = latestVersion.getValue(getVariableName());
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Checking boolean variable condition");
            getLogger().debug("    Condition value: [" + getValue() + "]");
            getLogger().debug("    Variable value:  [" + value + "]");
        }
        return value == getValue();
    }
}