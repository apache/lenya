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

import java.util.Arrays;

import org.apache.lenya.cms.publication.Document;
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

    protected static final String RELATION = "relation";
    protected static final String RELATIONS = "relations";
    protected static final String RELATION_CHILD = "child";
    protected static final String RELATION_BEFORE = "sibling before";
    protected static final String RELATION_AFTER = "sibling after";
    protected static final String DOCUMENT_ID_PROVIDED = "documentIdProvided";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Document parent = getSourceDocument();
        if (parent == null) {
            setParameter(PARENT_ID, "");
        } else {
            setParameter(PARENT_ID, parent.getId());
            String[] languages = parent.getPublication().getLanguages();
            setParameter(LANGUAGES, languages);
        }

        String[] relations = { RELATION_CHILD, RELATION_AFTER };
        setParameter(RELATIONS, relations);
        setParameter(RELATION, RELATION_CHILD);

        String documentId = getParameterAsString(DOCUMENT_ID);
        boolean provided = documentId != null && !documentId.equals("");
        setParameter(DOCUMENT_ID_PROVIDED, Boolean.valueOf(provided));
    }

    /**
     * Override this method to support other relations.
     * @return The supported relations.
     */
    protected String[] getSupportedRelations() {
        return new String[] { RELATION_CHILD, RELATION_AFTER };
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        String documentName = getParameterAsString(DOCUMENT_ID);
        String language = getParameterAsString(LANGUAGE);
        String relation = getParameterAsString(RELATION);

        if (!Arrays.asList(getSupportedRelations()).contains(relation)) {
            addErrorMessage("The relation '" + relation + "' is not supported.");
        }

        DocumentManager documentManager = null;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);

            if (!documentManager.isValidDocumentName(documentName)) {
                addErrorMessage("The document ID may not contain any special characters.");
            } else {
                Document parent = getSourceDocument();
                Publication publication = getSourceDocument().getPublication();
                Document document = getSourceDocument().getIdentityMap().get(publication,
                        getSourceDocument().getArea(),
                        parent.getId() + "/" + documentName,
                        language);
                if (document.exists()) {
                    addErrorMessage("The document does already exist.");
                }
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
        final String documentId = getParameterAsString(DOCUMENT_ID);
        String documentName;
        if (getParameterAsBoolean(DOCUMENT_ID_PROVIDED, false)) {
            documentName = documentId.substring(documentId.lastIndexOf("/") + 1);
        } else {
            documentName = documentId;
        }
        return documentName;
    }

    /**
     * @return The relation between the source document and the created document.
     */
    protected String getRelation() {
        return getParameterAsString(RELATION);
    }

    /**
     * @see Create#getNewDocumentId()
     */
    protected String getNewDocumentId() {
        if (getParameterAsBoolean(DOCUMENT_ID_PROVIDED, false)) {
            return getParameterAsString(DOCUMENT_ID);
        } else {
            String relation = getRelation();
            Document sourceDoc = getSourceDocument();
            if (relation.equals(RELATION_CHILD)) {
                return sourceDoc.getId() + "/" + getNewDocumentName();
            } else if (relation.equals(RELATION_BEFORE)) {
                return sourceDoc.getId().substring(0,
                        sourceDoc.getId().lastIndexOf(sourceDoc.getName()))
                        + getNewDocumentName();
            } else if (relation.equals(RELATION_AFTER)) {
                return sourceDoc.getId().substring(0,
                        sourceDoc.getId().lastIndexOf(sourceDoc.getName()))
                        + getNewDocumentName();
            } else {
                return getSourceDocument().getId() + "/" + getNewDocumentName();
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
