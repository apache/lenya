/*
 * Condition.java
 *
 * Created on 8. April 2003, 17:07
 */

package org.apache.lenya.workflow;

/**
 *
 * @author  andreas
 */
public interface Condition {

    /**
     * Returns if the condition is complied in a certain situation.
     * @param situation The situation to check.
     * @return if the condition is complied.
     */
    boolean isComplied(Situation situation);

	/** Sets the expression for this condition.
	 * @param expression The expression.
	 *
	 */
	void setExpression(String expression);

}
