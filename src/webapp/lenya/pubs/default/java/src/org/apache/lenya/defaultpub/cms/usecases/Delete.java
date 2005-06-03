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
package org.apache.lenya.defaultpub.cms.usecases;

import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.workflow.WorkflowUtil;

/**
 * Delete usecase handler.
 * 
 * @version $Id$
 */
public class Delete extends org.apache.lenya.cms.site.usecases.Delete {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        DocumentSet set = SiteUtil.getSubSite(this.manager, getSourceDocument());
        if (!WorkflowUtil.canInvoke(this.manager, getLogger(), set, getEvent())) {
            addErrorMessage("The workflow event cannot be invoked on all documents.");
        }
    }

    /**
     * @return The workflow event.
     */
    protected String getEvent() {
        return "delete";
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        DocumentSet set = SiteUtil.getSubSite(this.manager, getSourceDocument());
        WorkflowUtil.invoke(this.manager, getLogger(), set, getEvent(), true);
        super.doExecute();
    }

}