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
package org.apache.lenya.cms.workflow.usecases;

import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflowable;

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

        if (getSourceDocument() != null) {
            Workflowable workflowable = WorkflowUtil.getWorkflowable(this.manager, getSession(),
                    getLogger(), getSourceDocument());
            Version[] versions = workflowable.getVersions();
            setParameter("versions", versions);
        }
    }
}