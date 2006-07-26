/*
 * Copyright  1999-2006 The Apache Software Foundation
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
package org.apache.lenya.cms.site.usecases;

import java.io.IOException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.servlet.multipart.Part;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.ResourceWrapper;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Create a new resource document.
 */
public class CreateResource extends CreateDocument {

    /**
     * Validates the request parameters.
     * 
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {
        String title = getParameterAsString("title");

        if (title.length() == 0) {
            addErrorMessage("Please enter a title.");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        validate();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
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

        Part file = getPart("file");

        if (file.isRejected()) {
            String[] params = { Integer.toString(file.getSize()) };
            addErrorMessage("upload-size-exceeded", params);
        } else {
            Document document = getNewDocument();
            ResourceWrapper wrapper = new ResourceWrapper(document, this.manager, getLogger());
            wrapper.write(file);
        }
    }

    protected String getSourceExtension() {
        String extension = "";

        Part file = getPart("file");
        String fileName = file.getFileName();
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > -1) {
            extension = fileName.substring(lastDotIndex + 1);
        } else {
            addErrorMessage("Please upload a file with an extension.");
        }
        return extension;
    }

}
