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

import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.ResourceWrapper;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.cms.workflow.usecases.UsecaseWorkflowHelper;
import org.apache.lenya.util.ServletHelper;

/**
 * Usecase to upload a resource.
 * 
 */
public class UploadResource extends DocumentUsecase {

    protected String getEvent() {
        return "edit";
    }
    
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!ServletHelper.isUploadEnabled(manager)) {
            addErrorMessage("Upload is not enabled. Please check local.build.properties!");
        }
        Document doc = getSourceDocument();
        if (!getSourceDocument().getArea().equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("This usecase can only be invoked in the authoring area!");
        } else if (!getSourceDocument().exists()) {
            addErrorMessage("This usecase can only be invoked on existing documents.");
        }

        UsecaseWorkflowHelper.checkWorkflow(this.manager, this, getEvent(), doc,
                getLogger());
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        if (getLogger().isDebugEnabled())
            getLogger().debug("Resource::uploadResource() called");

        Part file = getPart("file");

        if (file.isRejected()) {
            String[] params = { Integer.toString(file.getSize()) };
            addErrorMessage("upload-size-exceeded", params);
        } else {
            Document document = getSourceDocument();
            ResourceWrapper wrapper = new ResourceWrapper(document, this.manager, getLogger());
            wrapper.write(file);
            WorkflowUtil.invoke(this.manager, getSession(), getLogger(), document, getEvent());
        }
    }

}