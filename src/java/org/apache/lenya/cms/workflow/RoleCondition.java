/*
 * RoleCondition.java
 *
 * Created on 8. April 2003, 17:28
 */

package org.apache.lenya.cms.workflow;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lenya.cms.ac.Group;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
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
        User user = situationImpl.getUser();
        Iterator userGroups = user.getGroups();
        Set userRoles = new HashSet();
        
        while (userGroups.hasNext()) {
        	Iterator groupRoles = ((Group)userGroups.next()).getRoles();
            assert groupRoles != null;
        	while (groupRoles.hasNext()) {
        		userRoles.add(groupRoles.next());
        	}
        }
        
        Role conditionRole = new Role(getExpression().trim());

        boolean complied = false;
        Iterator roles = userRoles.iterator();
        while (!complied && roles.hasNext()) {
        	if (conditionRole.equals(roles.next())) {
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getExpression();
    }

}
