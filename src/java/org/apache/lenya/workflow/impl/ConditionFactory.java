/*
 * ConditionFactory.java
 *
 * Created on 8. April 2003, 19:57
 */

package org.apache.lenya.workflow.impl;

import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.WorkflowException;

/**
 *
 * @author  andreas
 */
public final class ConditionFactory {
	
	private ConditionFactory() {
	}

    protected static Condition createCondition(
        String className,
        String expression)
        throws WorkflowException {

        assert className != null;
        assert expression != null;

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
