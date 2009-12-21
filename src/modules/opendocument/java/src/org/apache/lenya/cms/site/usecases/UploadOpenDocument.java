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
import org.apache.lenya.cms.publication.OpenDocumentWrapper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.workflow.usecases.InvokeWorkflow;
import org.apache.lenya.util.ServletHelper;

/**
 * Usecase to create a document.
 * 
 * @version $Id: CreateDocument.java 379098 2006-02-20 11:35:10Z andreas $
 */
public class UploadOpenDocument extends InvokeWorkflow {

    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!ServletHelper.isUploadEnabled(manager)) {
            addErrorMessage("Upload is not enabled. Please check local.build.properties!");
        }
        Document doc = getSourceDocument();
        if (!doc.getArea().equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("This usecase can only be invoked in the authoring area!");
        }
    }

    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        Part file = getPart("file");
        if (file == null) {
            addErrorMessage("missing-file");
        } else {
            if (file.isRejected()) {
                String[] params = { Integer.toString(file.getSize()) };
                addErrorMessage("upload-size-exceeded", params);
            } else {
                String mimeType = file.getMimeType();
                if (!OpenDocumentWrapper.ODT_MIME_TYPE.equals(mimeType)) {
                    String[] params = { mimeType, OpenDocumentWrapper.ODT_MIME_TYPE };
                    addErrorMessage("wrong-mime-type", params);
                }
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        OpenDocumentWrapper odt = new OpenDocumentWrapper(getSourceDocument(), getLogger());
        Part file = getPart("file");
        odt.write(file);
    }

}
