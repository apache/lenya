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

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

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
        return value;
    }

    /**
     * Returns the variable name to check.
     * @return A string.
     */
    protected String getVariableName() {
        return variableName;
    }

    /**
     * @see org.apache.lenya.workflow.Condition#setExpression(java.lang.String)
     */
    public void setExpression(String expression) throws WorkflowException {
        super.setExpression(expression);
        String[] sides = expression.split("=");
        if (sides.length != 2) {
            throw new WorkflowException(
                "The expression '" + expression + "' must be of the form 'name = [true|false]'");
        }
        
        variableName = sides[0].trim();
        value = Boolean.valueOf(sides[1].trim()).booleanValue();
        
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Expression:    [" + sides[1].trim() + "]");
            getLogger().debug("Setting value: [" + value + "]");
        }
    }

    /**
     * @see org.apache.lenya.workflow.Condition#isComplied(org.apache.lenya.workflow.Situation, org.apache.lenya.workflow.WorkflowInstance)
     */
    public boolean isComplied(Situation situation, WorkflowInstance instance) throws WorkflowException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Checking boolean variable condition");
            getLogger().debug("    Condition value: [" + getValue() + "]");
            getLogger().debug("    Variable value:  [" + instance.getValue(getVariableName()) + "]");
        }
        return instance.getValue(getVariableName()) == getValue();
    }

}
