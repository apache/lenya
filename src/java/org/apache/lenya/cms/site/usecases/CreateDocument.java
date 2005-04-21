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
import java.util.Collections;

import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.transaction.Transactionable;

/**
 * Usecase to create a document.
 * 
 * @version $Id$
 */
public class CreateDocument extends Create {

    protected static final String PARENT_ID = "parentId";
    protected static final String DOCUMENT_TYPE = "doctype";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Document parent = getSourceDocument();
        if (parent != null) {
            setParameter(PARENT_ID, parent.getId());
        } else {
            setParameter(PARENT_ID, "");
        }

        String[] languages = parent.getPublication().getLanguages();
        setParameter(LANGUAGES, languages);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        String nodeId = getParameterAsString(DOCUMENT_ID);
        Document parent = getSourceDocument();
        String language = getParameterAsString(LANGUAGE);
        DocumentManager documentManager = null;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            String[] messages = documentManager.canCreate(getDocumentIdentityMap(),
                    parent.getPublication(),
                    getArea(),
                    parent,
                    nodeId,
                    language);
            addErrorMessages(messages);
        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.Create#createDocument()
     */
    protected Document createDocument() throws Exception {

        if (getLogger().isDebugEnabled())
            getLogger().debug("createDocument() called; first retrieving parent");

        Document parent = getSourceDocument();

        String documentId = parent.getId() + "/" + getParameterAsString(DOCUMENT_ID);
        String navigationTitle = getParameterAsString(DublinCore.ELEMENT_TITLE);
        String documentTypeName = getDocumentTypeName();
        String language = getParameterAsString(LANGUAGE);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("createDocument() read parameters:");
            getLogger().debug("    Parent document:   [" + parent.getId() + "]");
            getLogger().debug("    Child document:    [" + documentId + "]");
            getLogger().debug("    Language:          [" + language + "]");
            getLogger().debug("    Document Type:     [" + documentTypeName + "]");
            getLogger().debug("    Navigation Title:  [" + navigationTitle + "]");
        }

        Publication publication = parent.getPublication();
        String area = parent.getArea();

        Document document = parent.getIdentityMap().get(publication,
                area,
                documentId,
                language);
        Transactionable[] nodes = document.getRepositoryNodes();
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].lock();
        }

        // create an instance of DocumentType
        DocumentTypeBuilder documentTypeBuilder = null;
        DocumentType documentType = null;
        try {
            documentTypeBuilder = (DocumentTypeBuilder) this.manager.lookup(DocumentTypeBuilder.ROLE);

            documentType = documentTypeBuilder.buildDocumentType(documentTypeName, publication);

            String parentId = parent.getId().substring(1);
            String childId = document.getName();

            documentType.getCreator().create(
                documentType.getSampleContentLocation(),
                new File(publication.getContentDirectory(area), parentId),
                childId,
                ParentChildCreatorInterface.BRANCH_NODE,
                navigationTitle,
                language,
                Collections.EMPTY_MAP);
        } 
        finally {
            if (documentTypeBuilder != null) {
                this.manager.release(documentTypeBuilder);
            }
        }

        return document;
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.Create#getDocumentTypeName()
     */
    protected String getDocumentTypeName() {
        return getParameterAsString(DOCUMENT_TYPE);
    }
}
