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
package org.apache.lenya.cms.site.usecases;

import java.util.Arrays;

import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.workflow.WorkflowResolver;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * Super class for site related usecases.
 * 
 * @version $Id$
 */
public class SiteUsecase extends DocumentUsecase {

    protected static final String STATE = "state";
    protected static final String ISLIVE = "is_live";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doInitialize()
     */
    protected void doInitialize() {
        super.doInitialize();
        WorkflowResolver resolver = null;
        try {
            resolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            if (resolver.hasWorkflow(getSourceDocument())) {
                WorkflowInstance instance = resolver.getWorkflowInstance(getSourceDocument());
                setParameter(STATE, instance.getCurrentState().toString());
                String[] variableNames = instance.getWorkflow().getVariableNames();
                if (Arrays.asList(variableNames).contains(ISLIVE)) {
                    setParameter("islive", Boolean.valueOf(instance.getValue(ISLIVE)));
                }
            } else {
                setParameter("state", "");
            }
        } catch (Exception e) {
            getLogger().error("Could not get workflow state.", e);
            addErrorMessage("Could not get workflow state. See log files for details.");
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

}