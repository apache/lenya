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

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.workflow.WorkflowException;

/**
 * Helper class for workflow related usecases.
 */
public class UsecaseWorkflowHelper {

    /**
     * The error message that an event can not be invoked on a document. It
     * takes two parameters: the event name and the document title.
     */
    protected static final String ERROR_CANNOT_INVOKE_EVENT = "error-workflow-document";

    /**
     * Adds an error message to a usecase that an event cannot be invoked on a
     * document.
     * @param usecase The usecase.
     * @param event The event.
     * @param doc The document.
     */
    protected static final void addWorkflowError(AbstractUsecase usecase, String event, Document doc) {
        try {
            String title = DublinCoreHelper.getTitle(doc, true);
            if (title == null) {
                title = "";
            }
            usecase.addErrorMessage(ERROR_CANNOT_INVOKE_EVENT, new String[] { event, title });
        } catch (MetaDataException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds an error message if the event can not be invoked.
     * @param manager The service manager.
     * @param usecase The usecase.
     * @param event The event.
     * @param doc The document.
     * @param logger The logger.
     */
    public static final void checkWorkflow(ServiceManager manager, AbstractUsecase usecase,
            String event, Document doc, Logger logger) {
        try {
            if (!WorkflowUtil.canInvoke(manager, usecase.getSession(), logger, doc, event)) {
                UsecaseWorkflowHelper.addWorkflowError(usecase, event, doc);
            }
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

    }

}
