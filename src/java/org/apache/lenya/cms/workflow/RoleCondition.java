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

/* $Id: RoleCondition.java,v 1.9 2004/03/01 16:18:16 gregor Exp $  */

package org.apache.lenya.cms.workflow;

import java.util.HashSet;
import java.util.Set;

import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.workflow.impl.AbstractCondition;

public class RoleCondition extends AbstractCondition {
    
    private Set roleIds = new HashSet();
    
    protected static final String SEPARATOR = ",";
    
    /**
     * @see org.apache.lenya.workflow.Condition#setExpression(java.lang.String)
     */
    public void setExpression(String expression) throws WorkflowException {
        super.setExpression(expression);
        
        String[] roles = expression.split(SEPARATOR);
        for (int i = 0; i < roles.length; i++) {
            roleIds.add(roles[i].trim());
        }
    }

    /**
     * Returns if the condition is complied in a certain situation.
     * The condition is complied when the current user has the
     * role that is required by the RoleCondition.
     * @see org.apache.lenya.workflow.impl.AbstractCondition#isComplied(Situation, WorkflowInstance)
     */
    public boolean isComplied(Situation situation, WorkflowInstance instance) throws WorkflowException {
        CMSSituation situationImpl = (CMSSituation) situation;
        String[] roles = situationImpl.getRoleIds();

        boolean complied = false;

        for (int i = 0; i < roles.length; i++) {
            if (roleIds.contains(roles[i])) {
                complied = true;
            }
        }

        return complied;
    }

}
