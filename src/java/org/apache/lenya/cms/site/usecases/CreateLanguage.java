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

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeResolver;
import org.apache.lenya.cms.publication.Publication;

/**
 * Usecase to create a new language version of a resource.
 * 
 * @version $Id$
 */
public class CreateLanguage extends Create {

    private String documentTypeName;

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        if (getSourceDocument() == null) {
            addErrorMessage("This operation must be invoked on a document.");
            return;
        }

        String area = getSourceDocument().getArea();
        if (!area.equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("This operation is only supported in the authoring area.");
            return;
        }

        if (getNonExistingLanguages().isEmpty()) {
            addErrorMessage("All language versions do already exist.");
        }
    }

    /**
     * @return All non-existing language strings for the source document.
     * @throws DocumentBuildException if an error occurs.
     * @throws DocumentException if an error occurs.
     */
    protected List getNonExistingLanguages() throws DocumentBuildException, DocumentException {
        Document source = getSourceDocument();
        List nonExistingLanguages = new ArrayList();
        String[] languages = source.getPublication().getLanguages();
        DocumentIdentityMap map = source.getIdentityMap();
        for (int i = 0; i < languages.length; i++) {
            Document version = map.get(source.getPublication(),
                    source.getArea(),
                    source.getId(),
                    languages[i]);
            if (!version.exists()) {
                nonExistingLanguages.add(languages[i]);
            }
        }
        return nonExistingLanguages;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Document source = getSourceDocument();
        if (source != null) {
            try {
                List nonExistingLanguages = getNonExistingLanguages();
                setParameter(LANGUAGES, nonExistingLanguages
                        .toArray(new String[nonExistingLanguages.size()]));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * For new language version of a document, id is the same
     * as that document's
     * @see Create#getNewDocumentId()
     */
    protected String getNewDocumentId() {
        return getSourceDocument().getId();
    }

    /**
     * The parent document is retrieved via the new document's
     * identity map.
     * @see Create#getParentDocument(Document)
     */
    protected Document getParentDocument(Document newDocument) throws DocumentBuildException {
        DocumentIdentityMap documentMap = newDocument.getIdentityMap();
        Document parent = documentMap.getParent(newDocument);
        return parent;
    }

    /**
     * New language version of a document: 
     * use that document's content
     * @see Create#getInitialContentsURI(Document, DocumentType)
     */
    protected String getInitialContentsURI(Document referenceDocument, DocumentType type) {
        return referenceDocument.getSourceURI();
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.Create#getDocumentTypeName()
     */
    protected String getDocumentTypeName() {
        if (this.documentTypeName == null) {
            Document source = getSourceDocument();
            DocumentTypeResolver resolver = null;
            try {
                resolver = (DocumentTypeResolver) this.manager.lookup(DocumentTypeResolver.ROLE);
                DocumentType type = resolver.resolve(source);
                this.documentTypeName = type.getName();

                List nonExistingLanguages = getNonExistingLanguages();
                setParameter(LANGUAGES, nonExistingLanguages.toArray(new String[nonExistingLanguages
                        .size()]));

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                this.manager.release(resolver);
            }
        }
        return this.documentTypeName;
    }

}
