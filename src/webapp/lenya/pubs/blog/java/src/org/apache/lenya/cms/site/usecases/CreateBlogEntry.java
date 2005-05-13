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
package org.apache.lenya.cms.site.usecases;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;

import org.apache.lenya.ac.Identity;

import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.usecase.DocumentUsecase;


/**
 * Usecase to create a Blog entry.
 * 
 * @version $Id$
 */
public class CreateBlogEntry extends DocumentUsecase {

    protected static final String PARENT_ID = "parentId";
    protected static final String DOCUMENT_TYPE = "doctype";
    protected static final String DOCUMENT_ID = "documentId";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Document parent = getSourceDocument();
        setParameter(PARENT_ID, parent.getId());
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {

        String documentId = getParameterAsString(DOCUMENT_ID);

        if (documentId.equals("")) {
            addErrorMessage("The document ID is required.");
        }

        if (documentId.matches("[^a-zA-Z0-9\\-]+")) {
            addErrorMessage("The document ID is not valid.");
        }

        super.doCheckExecutionConditions();
    }

    protected String getNewDocumentName() {
        return getParameterAsString(DOCUMENT_ID);
    }

    protected void doExecute() throws Exception {
        super.doExecute();

        Document parent = getSourceDocument();

        Map objectModel = ContextHelper.getObjectModel(getContext());
        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = request.getSession(false);

        String documentId = "/" + getNewDocumentName();
        String title = getParameterAsString(DublinCore.ELEMENT_TITLE);
        String documentTypeName = getDocumentTypeName();

        Publication publication = parent.getPublication();
        String language = publication.getDefaultLanguage();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Creating document");
            getLogger().debug("    Parent document:   [" + parent.getId() + "]");
            getLogger().debug("    Child document:    [" + documentId + "]");
            getLogger().debug("    Language:          [" + language + "]");
            getLogger().debug("    Document Type:     [" + documentTypeName + "]");
            getLogger().debug("    Title:             [" + title + "]");
        }

        String area = parent.getArea();
        Document document = parent.getIdentityMap().get(parent.getPublication(),
                area,
                documentId,
                language);

        // create an instance of DocumentType
        DocumentTypeBuilder documentTypeBuilder = null;
        DocumentType documentType = null;
        try {
            documentTypeBuilder = (DocumentTypeBuilder) this.manager.lookup(DocumentTypeBuilder.ROLE);

            documentType = documentTypeBuilder.buildDocumentType(documentTypeName, publication);

            HashMap allParameters = new HashMap();
            allParameters.put(Identity.class.getName(), session.getAttribute(Identity.class.getName()));
            allParameters.put("title", title);

            documentType.getCreator().create(
                documentType.getSampleContentLocation(),
                new File(publication.getContentDirectory(area), ""),
                documentId,
                ParentChildCreatorInterface.LEAF_NODE,
                title,
                language,
                allParameters);
        } 
        finally {
            if (documentTypeBuilder != null) {
                this.manager.release(documentTypeBuilder);
            }
        }


    }

    /**
     * @see org.apache.lenya.cms.site.usecases.Create#getDocumentTypeName()
     */
    protected String getDocumentTypeName() {
        return getParameterAsString(DOCUMENT_TYPE);
    }
}
