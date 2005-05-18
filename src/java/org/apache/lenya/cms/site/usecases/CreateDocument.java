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

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;

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

        String documentName = getParameterAsString(DOCUMENT_ID);
        String language = getParameterAsString(LANGUAGE);
        DocumentManager documentManager = null;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            Document parent = getSourceDocument();
            Publication publication = getSourceDocument().getPublication();
            Document document = getSourceDocument().getIdentityMap().get(publication,
                    getSourceDocument().getArea(),
                    parent.getId() + "/" + documentName,
                    language);
            if (document.exists()) {
                addErrorMessage("The document does already exist.");
            }
        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }
    }

    /**
     * @see Create#getNewDocumentName()
     */
    protected String getNewDocumentName() {
        return getParameterAsString(DOCUMENT_ID);
    }

    /**
     * @see Create#getNewDocumentId()
     */
    protected String getNewDocumentId() {
        return getSourceDocument().getId() + "/" + getNewDocumentName();
    }

    /**
     * In this usecase, the parent document is simply the source document the usecase was invoked
     * upon.
     * @see Create#getParentDocument()
     */
    protected Document getParentDocument() throws DocumentBuildException {
        return getSourceDocument();
    }

    /**
     * New document: no existing document is referenced
     * @see Create#getInitialContentsURI()
     */
    protected String getInitialContentsURI() {
        return null;
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.Create#getDocumentTypeName()
     */
    protected String getDocumentTypeName() {
        return getParameterAsString(DOCUMENT_TYPE);
    }
}