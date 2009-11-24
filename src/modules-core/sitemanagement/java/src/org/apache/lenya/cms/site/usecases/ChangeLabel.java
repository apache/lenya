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
package org.apache.lenya.cms.site.usecases;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.cms.workflow.usecases.UsecaseWorkflowHelper;

/**
 * Change the label of a document.
 * 
 * @version $Id$
 */
public class ChangeLabel extends DocumentUsecase {

    protected static final String LABEL = "label";
    protected static final String DOCUMENT_ID = "documentId";

    protected String getEvent() {
        return "edit";
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
        if (!getSourceDocument().getArea().equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("This usecase can only be invoked in the authoring area!");
        }

        UsecaseWorkflowHelper.checkWorkflow(this.manager, this, getEvent(), doc,
                getLogger());
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        List nodes = new ArrayList();
        nodes.add(getSourceDocument().getRepositoryNode());
        return (Node[]) nodes.toArray(new Node[nodes.size()]);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        Document document = getSourceDocument();
        try {
            if (document != null && document.exists()) {
                setParameter(DOCUMENT_ID, document.getUUID());
                setParameter(LABEL, document.getLink().getLabel());
            }
        } catch (final DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        String label = getParameterAsString(LABEL);
        if (label.trim().equals("")) {
            addErrorMessage("missing-navigation-title");
        }
    }


    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Document document = getSourceDocument();
        String label = getParameterAsString(LABEL).trim();
        document.getLink().setLabel(label);

        WorkflowUtil.invoke(this.manager, getSession(), getLogger(), document, getEvent());

    }
}
