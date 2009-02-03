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

package org.apache.lenya.cms.workflow;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.RoleManager;
import org.apache.lenya.workflow.Condition;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.Workflowable;

/**
 * Role condition
 */
public class RoleCondition implements Condition {

    private Set roleIds = new HashSet();

    protected static final String SEPARATOR = ",";

    /**
     * @see org.apache.lenya.workflow.Condition#setExpression(java.lang.String)
     */
    public void setExpression(String expression) throws WorkflowException {
        this.expression = expression;

        String[] roles = expression.split(SEPARATOR);
        for (int i = 0; i < roles.length; i++) {
            this.roleIds.add(roles[i].trim());
        }
    }

    /**
     * Returns if the condition is complied in a certain situation. The condition is complied when
     * the current user has the role that is required by the RoleCondition.
     * 
     * @see org.apache.lenya.workflow.impl.AbstractCondition#isComplied(Workflow, Workflowable)
     */
    public boolean isComplied(Workflow workflow, Workflowable instance) {

        DocumentWorkflowable workflowable = (DocumentWorkflowable) instance;
        ServiceManager manager = workflowable.getServiceManager();
        String url = workflowable.getDocument().getCanonicalWebappURL();

        ServiceSelector selector = null;
        AccessControllerResolver acResolver = null;
        AccessController accessController = null;
        try {

            selector = (ServiceSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
            acResolver = (AccessControllerResolver) selector
                    .select(AccessControllerResolver.DEFAULT_RESOLVER);
            accessController = acResolver.resolveAccessController(url);

            PolicyManager policyManager = accessController.getPolicyManager();
            Identity identity = workflowable.getSession().getIdentity();
            if (identity == null) {
                throw new IllegalArgumentException("The session of the workflowable "
                        + workflowable + " has no identity.");
            }
            AccreditableManager accreditableMgr = accessController.getAccreditableManager();
            Policy policy = policyManager.getPolicy(accreditableMgr, url);
            RoleManager roleManager = accreditableMgr.getRoleManager();

            boolean complied = false;

            for (Iterator i = this.roleIds.iterator(); i.hasNext();) {
                String roleId = (String) i.next();
                Role role = roleManager.getRole(roleId);
                if (policy.check(identity, role) == Policy.RESULT_GRANTED) {
                    complied = true;
                }
            }

            return complied;

        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (acResolver != null) {
                    if (accessController != null) {
                        acResolver.release(accessController);
                    }
                    selector.release(acResolver);
                }
                manager.release(selector);
            }
        }

    }

    private String expression;

    /**
     * Returns the expression of this condition.
     * 
     * @return A string.
     */
    public String getExpression() {
        return this.expression;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getExpression();
    }

}
