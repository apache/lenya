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

/* $Id: ConditionFactory.java,v 1.9 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.WorkflowException;


/**
 * Factory to build conditions.
 */
public final class ConditionFactory {
    
    /**
     * Ctor.
     */
    private ConditionFactory() {
    }

    /**
     * Creates a condition.
     * @param className The condition class name.
     * @param expression The condition expression.
     * @return A condition.
     * @throws WorkflowException when creating the condition failed.
     */
    protected static Condition createCondition(String className, String expression)
        throws WorkflowException {

        Condition condition;

        try {
            Class clazz = Class.forName(className);
            condition = (Condition) clazz.newInstance();
            condition.setExpression(expression);
        } catch (ClassNotFoundException e) {
            throw new WorkflowException(e);
        } catch (InstantiationException e) {
            throw new WorkflowException(e);
        } catch (IllegalAccessException e) {
            throw new WorkflowException(e);
        }

        return condition;
    }
}
