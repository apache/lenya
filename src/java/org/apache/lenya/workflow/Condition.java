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

/* $Id: Condition.java,v 1.6 2004/03/01 16:18:23 gregor Exp $  */

package org.apache.lenya.workflow;

/**
 * <p>A condition can prevent a transition from firing,
 * based on the current situation. Examples:</p>
 * <ul>
 * <li>Does the current user have a certain role on the current URL?</li>
 * <li>Does a certain state variable have a certain value (e.g., is the document published)? (BooleanVariableCondition)<li>
 * <li>Is the sun shining? (e.g., if the weather report may only be published on sunny days)</li>
 * </ul>
 */
public interface Condition {

    /**
     * Returns if the condition is complied in a certain situation.
     * @param situation The situation to check.
     * @param instance The workflow instance to check the condition on.
     * @return if the condition is complied.
     * @throws WorkflowException when the expression could not be evaluated.
     */
    boolean isComplied(Situation situation, WorkflowInstance instance) throws WorkflowException;

    /** Sets the expression for this condition.
     * @param expression The expression.
     * @throws WorkflowException when the expression is not valid.
     */
    void setExpression(String expression) throws WorkflowException;
}
