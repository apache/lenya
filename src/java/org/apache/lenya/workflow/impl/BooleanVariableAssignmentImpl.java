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

/* $Id: BooleanVariableAssignmentImpl.java,v 1.7 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.BooleanVariableAssignment;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;


/**
 * Implementation of a boolean variable assignment.
 */
public class BooleanVariableAssignmentImpl implements BooleanVariableAssignment {
    
    /**
     * Ctor.
     * @param variable The variable.
     * @param value The value.
     */
    protected BooleanVariableAssignmentImpl(BooleanVariableImpl variable, boolean value) {
        this.variable = variable;

        this.value = value;
    }

    private BooleanVariableImpl variable;
    private boolean value;

    /**
     * @see org.apache.lenya.workflow.BooleanVariableAssignment#execute()
     */
    public void execute(WorkflowInstance instance) throws WorkflowException {
        WorkflowInstanceImpl impl = (WorkflowInstanceImpl) instance;
        impl.getVariableInstance(getVariable()).setValue(getValue());
    }

    /**
     * Returns the value of this assignment.
     * @return A boolean value.
     */
    public boolean getValue() {
        return value;
    }

    /**
     * Returns the variable of this assignment.
     * @return A variable.
     */
    public BooleanVariableImpl getVariable() {
        return variable;
    }
}
