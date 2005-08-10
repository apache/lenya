/*
 * Copyright  1999-2005 The Apache Software Foundation
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

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.transaction.Transactionable;

/**
 * Invoke a workflow event on the current document. The event is obtained from the configuration in
 * <code>cocoon.xconf</code>:<code>
 * <pre>
 * 
 *  
 *   
 *    
 *       &lt;component-instance name=&quot;default/workflow.submit&quot;
 *                           logger=&quot;lenya.usecases.workflow&quot;
 *                           class=&quot;org.apache.lenya.cms.workflow.usecases.InvokeWorkflow&quot;&gt;
 *         &lt;event id=&quot;submit&quot;/&gt;
 *       &lt;/component-instance&gt;
 *     
 *    
 *   
 *  
 * </pre>
 * </code>
 * 
 * @version $Id$
 */
public class InvokeWorkflow extends DocumentUsecase implements Configurable {

    private String event;

    /**
     * @return The workflow event to use.
     */
    protected String getEvent() {
        return event;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        if (hasErrors()) {
            return;
        }

        if (!WorkflowUtil.canInvoke(this.manager, getLogger(), getSourceDocument(), getEvent())) {
            addErrorMessage("error-workflow-document", new String[] { getEvent(),
                    getSourceDocument().getId() });
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getObjectsToLock()
     */
    protected Transactionable[] getObjectsToLock() throws UsecaseException {
        Transactionable[] objects = { getSourceDocument().getRepositoryNode() };
        return objects;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        WorkflowUtil.invoke(this.manager, getLogger(), getSourceDocument(), getEvent());
    }

    protected static final String ELEMENT_EVENT = "event";
    protected static final String ATTRIBUTE_ID = "id";

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {
        this.event = config.getChild(ELEMENT_EVENT).getAttribute(ATTRIBUTE_ID);
    }

}