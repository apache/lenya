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

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.workflow.WorkflowUtil;

/**
 * Check if a workflow event can be invoked on the current document without actually invoking it.
 * The event is obtained from the configuration in <code>cocoon.xconf</code>:<code>
 * <pre>
 *     &lt;component-instance name=&quot;default/workflow.submit&quot;
 *                            logger=&quot;lenya.usecases.workflow&quot;
 *                            class=&quot;org.apache.lenya.cms.workflow.usecases.CheckWorkflow&quot;&gt;
 *       &lt;event id=&quot;submit&quot;/&gt;
 *     &lt;/component-instance&gt;
 * </pre>
 * </code>
 * 
 * @version $Id: InvokeWorkflow.java 426254 2006-07-27 21:27:04Z andreas $
 */
public class CheckWorkflow extends DocumentUsecase implements Configurable {

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

        Document doc = getSourceDocument();
        if (!WorkflowUtil.canInvoke(this.manager, getSession(), getLogger(), doc, getEvent())) {
            UsecaseWorkflowHelper.addWorkflowError(this, getEvent(), doc);
        }
    }

    protected static final String ELEMENT_EVENT = "event";
    protected static final String ATTRIBUTE_ID = "id";

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        this.event = config.getChild(ELEMENT_EVENT).getAttribute(ATTRIBUTE_ID);
    }

}
