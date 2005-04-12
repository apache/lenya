/*
 * Copyright  1999-2004 The Apache Software Foundation
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.DocumentTypeResolver;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.transaction.Transactionable;

/**
 * Usecase to create a new language version of a resource.
 * 
 * @version $Id:$
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
            DocumentTypeResolver resolver = null;

            try {
                resolver = (DocumentTypeResolver) this.manager.lookup(DocumentTypeResolver.ROLE);
                DocumentType type = resolver.resolve(source);
                this.documentTypeName = type.getName();

                List nonExistingLanguages = getNonExistingLanguages();
                setParameter(LANGUAGES, nonExistingLanguages
                        .toArray(new String[nonExistingLanguages.size()]));

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                this.manager.release(resolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.Create#createDocument()
     */
    protected Document createDocument() throws Exception {

        Document source = getSourceDocument();
        String navigationTitle = getParameterAsString(DublinCore.ELEMENT_TITLE);
        String documentTypeName = getDocumentTypeName();
        String language = getParameterAsString(LANGUAGE);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Creating document language version");
            getLogger().debug("    Child document:    [" + source.getId() + "]");
            getLogger().debug("    Language:          [" + language + "]");
            getLogger().debug("    Document Type:     [" + documentTypeName + "]");
            getLogger().debug("    Navigation Title:  [" + navigationTitle + "]");
        }

        Publication publication = source.getPublication();
        DocumentIdentityMap map = source.getIdentityMap();
        String area = source.getArea();
        Document document = map.get(publication, area, source.getId(), language);
        Transactionable[] nodes = document.getRepositoryNodes();
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].lock();
        }

        DocumentType documentType = DocumentTypeBuilder.buildDocumentType(documentTypeName,
                publication);

        String parentId = "";
        Document parent = map.getParent(document);
        if (parent != null) {
            parentId = map.getParent(document).getId().substring(1);
        }

        String childId = document.getName();

        File doctypesDirectory = new File(publication.getDirectory(),
                DocumentTypeBuilder.DOCTYPE_DIRECTORY);

        documentType.getCreator().create(new File(doctypesDirectory, "samples"),
                new File(publication.getContentDirectory(area), parentId),
                childId,
                ParentChildCreatorInterface.BRANCH_NODE,
                navigationTitle,
                language,
                Collections.EMPTY_MAP);

        return document;
    }

    /**
     * @see org.apache.lenya.cms.site.usecases.Create#getDocumentTypeName()
     */
    protected String getDocumentTypeName() {
        return this.documentTypeName;
    }

}