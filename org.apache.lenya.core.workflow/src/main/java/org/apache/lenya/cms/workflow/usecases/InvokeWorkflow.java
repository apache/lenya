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

import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;

/**
 * Invoke a workflow event on the current document. The event is obtained from the configuration in
 * <code>cocoon.xconf</code>:<code>
 * <pre>
 *   &lt;component-instance name=&quot;default/workflow.submit&quot;
 *                          logger=&quot;lenya.usecases.workflow&quot;
 *                          class=&quot;org.apache.lenya.cms.workflow.usecases.InvokeWorkflow&quot;&gt;
 *     &lt;event id=&quot;submit&quot;/&gt;
 *   &lt;/component-instance&gt;
 * </pre>
 * </code>
 * 
 * @version $Id$
 */
public class InvokeWorkflow extends CheckWorkflow {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        Node[] objects = new Node[0];
        if(getSourceDocument() != null) {
            objects = new Node[] { getSourceDocument().getRepositoryNode() };
        }
        return objects;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        WorkflowUtil.invoke(this.manager, getSession(), getLogger(), getSourceDocument(),
                getEvent());
    }

}
