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

/* $Id: AbstractCondition.java,v 1.4 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.WorkflowException;

/**
 * Abstract base class for workflow conditions.
 */
public abstract class AbstractCondition implements Condition {

    private String expression;

    /**
     * @see org.apache.lenya.workflow.impl.AbstractCondition#setExpression(java.lang.String)
     */
    public void setExpression(String expression) throws WorkflowException {
        this.expression = expression.trim();
    }

    /**
     * Returns the expression of this condition.
     * @return A string.
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getExpression();
    }
    
}
