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

import org.apache.lenya.workflow.BooleanVariableAssignment;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.WorkflowException;

/**
 * Implementation of a boolean variable assignment.
 */
public class BooleanVariableAssignmentImpl implements BooleanVariableAssignment {
    
    /**
     * Ctor.
     * @param name The variable name.
     * @param value The value.
     */
    protected BooleanVariableAssignmentImpl(String name, boolean value) {
        this.variable = name;
        this.value = value;
    }

    private String variable;
    private boolean value;

    /**
     * @see org.apache.lenya.workflow.Action#execute(org.apache.lenya.workflow.Version)
     */
    public void execute(Version resultingVersion) throws WorkflowException {
        resultingVersion.setValue(getVariable(), getValue());
    }

    /**
     * Returns the value of this assignment.
     * @return A boolean value.
     */
    public boolean getValue() {
        return this.value;
    }

    /**
     * Returns the variable of this assignment.
     * @return A variable.
     */
    public String getVariable() {
        return this.variable;
    }
}
