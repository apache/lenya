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

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.WorkflowException;


/**
 * Factory to build conditions.
 */
public final class ConditionFactory extends AbstractLogEnabled {
    
    /**
     * Ctor.
     * @param logger The logger to use.
     */
    public ConditionFactory(Logger logger) {
        ContainerUtil.enableLogging(this, logger);
    }

    /**
     * Creates a condition.
     * @param className The condition class name.
     * @param expression The condition expression.
     * @return A condition.
     * @throws WorkflowException when creating the condition failed.
     */
    protected Condition createCondition(String className, String expression)
        throws WorkflowException {

        Condition condition;

        try {
            Class clazz = Class.forName(className);
            condition = (Condition) clazz.newInstance();
            ContainerUtil.enableLogging(condition, getLogger());
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
