/*
 * RoleCondition.java
 *
 * Created on 8. April 2003, 17:28
 */

package org.apache.lenya.cms.workflow;

import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.Situation;

/**
 *
 * @author  andreas
 */
public class RoleCondition implements Condition {

    /**
     * Returns if the condition is complied in a certain situation.
     * The condition is complied when the current user has the
     * role that is required by the RoleCondition.
     * @param situation The situation to check.
     * @return if the condition is complied.
     */
    public boolean isComplied(Situation situation) {

        CMSSituation situationImpl = (CMSSituation) situation;
        Role roles[] = situationImpl.getRoles();
        
        Role conditionRole = new Role(getExpression().trim());

        boolean complied = false;
        for (int i = 0; i < roles.length; i++) {
        	if (conditionRole.equals(roles[i])) {
        		complied = true;
        	}
        }
        return complied;
    }

    private String expression;

    /** Sets the expression for this condition.
     * @param expression The expression.
     *
     */
    public void setExpression(String expression) {
        assert expression != null;
        this.expression = expression;
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
