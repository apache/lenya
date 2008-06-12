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

import java.io.IOException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.ResourceWrapper;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.util.ServletHelper;

/**
 * Create a new resource document.
 */
public class CreateResource extends CreateDocument {

    protected static final String PARAMETER_FILE = "file";
    protected static final String PARAMETER_CAN_SUBMIT = "canSubmit";
    
    protected static final String MESSAGE_UPLOAD_DISABLED = "upload-disabled";
    protected static final String MESSAGE_UPLOAD_ENTER_TITLE = "upload-enter-title";
    protected static final String MESSAGE_UPLOAD_CHOOSE_FILE = "upload-choose-file";
    protected static final String MESSAGE_UPLOAD_SIZE_EXCEEDED = "upload-size-exceeded";
    protected static final String MESSAGE_UPLOAD_RESET = "upload-reset";
    protected static final String MESSAGE_UPLOAD_MISSING_EXTENSION = "upload-missing-extension";

    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!ServletHelper.isUploadEnabled(this.manager)) {
            addErrorMessage(MESSAGE_UPLOAD_DISABLED);
            setParameter(PARAMETER_CAN_SUBMIT, Boolean.FALSE);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {

        super.doCheckExecutionConditions();

        Part file = getPart(PARAMETER_FILE);
        if (hasErrors() && file != null) {
            resetUploadField();
        } else {
            if (file == null) {
                addErrorMessage(MESSAGE_UPLOAD_CHOOSE_FILE);
            } else if (file.isRejected()) {
                String[] params = { Integer.toString(file.getSize()) };
                addErrorMessage(MESSAGE_UPLOAD_SIZE_EXCEEDED, params);
            }
        }
    }

    /**
     * The browser can't set the value of the file upload widget for security reasons, so we have to
     * remove the file parameter and the user has to select the file again.
     */
    protected void resetUploadField() {
        deleteParameter(PARAMETER_FILE);
        addErrorMessage(MESSAGE_UPLOAD_RESET);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        deleteParameter(RESOURCE_TYPES);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        addResource();
    }

    /**
     * Adds the ressource. If asset upload is not enabled, an error message is added.
     * @throws IOException
     * @throws ServiceException
     * @throws DocumentException
     * @throws MetaDataException
     * @throws RepositoryException
     */
    protected void addResource() throws ServiceException, IOException, DocumentException,
            RepositoryException, MetaDataException {

        if (getLogger().isDebugEnabled())
            getLogger().debug("Assets::addAsset() called");

        Part file = getPart(PARAMETER_FILE);
        Document document = getNewDocument();
        ResourceWrapper wrapper = new ResourceWrapper(document, this.manager, getLogger());
        wrapper.write(file);
    }

    protected String getSourceExtension() {
        String extension = "";

        Part file = getPart(PARAMETER_FILE);
        String fileName = file.getFileName();
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > -1) {
            extension = fileName.substring(lastDotIndex + 1);
        } else {
            addErrorMessage(MESSAGE_UPLOAD_MISSING_EXTENSION);
        }
        return extension.toLowerCase();
    }

}
