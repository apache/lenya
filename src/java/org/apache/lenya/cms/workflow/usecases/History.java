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
package org.apache.lenya.cms.workflow.usecases;

import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.workflow.CMSHistory;
import org.apache.lenya.cms.workflow.WorkflowResolver;
import org.apache.lenya.workflow.WorkflowInstance;
import org.apache.lenya.workflow.impl.Version;

/**
 * Display the workflow history tab in the site area
 * 
 * @version $Id$
 */
public class History extends DocumentUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters() TODO
     *      get wf variables, get date and machine ip for versions
     */
    protected void initParameters() {
        super.initParameters();

        WorkflowResolver resolver = null;
        try {
            resolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            WorkflowInstance instance = resolver.getWorkflowInstance(getSourceDocument());
            CMSHistory history = (CMSHistory) instance.getHistory();
            Version[] versions = history.getVersions();
            setParameter("versions", versions);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }
}