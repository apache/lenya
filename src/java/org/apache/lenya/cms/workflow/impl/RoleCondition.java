/*
 * RoleCondition.java
 *
 * Created on 8. April 2003, 17:28
 */

package org.lenya.cms.workflow.impl;

import org.lenya.cms.ac.Role;
import org.lenya.cms.ac.User;
import org.lenya.cms.workflow.Condition;
import org.lenya.cms.workflow.Situation;

/**
 *
 * @author  andreas
 */
public class RoleCondition
    implements Condition {
    
    /**
     * Returns if the condition is complied in a certain situation.
     * The condition is complied when the current user has the
     * role that is required by the RoleCondition.
     * @param situation The situation to check.
     * @return if the condition is complied.
     */
        public boolean isComplied(Situation situation) {
        User user = situation.getUser();
        Role userRoles[] = user.getRoles();
        
        Role conditionRole = new Role(getExpression().trim());
        
        boolean complied = false;
        for (int i = 0; i < userRoles.length; i++) {
            if (conditionRole.equals(userRoles[i])) {
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
    
    public String getExpression() {
        return expression;
    }
    
}
