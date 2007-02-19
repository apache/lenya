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

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.site.SiteManager;

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
            addErrorMessage("All language versions already exist.");
        }
    }

    /**
     * @return All languages supported by the publication for which this document does not yet have
     *         a version
     * @throws DocumentBuildException if an error occurs.
     * @throws DocumentException if an error occurs.
     */
    protected List getNonExistingLanguages() throws DocumentBuildException, DocumentException {
        Document source = getSourceDocument();
        List nonExistingLanguages = new ArrayList();
        String[] languages = source.getPublication().getLanguages();
        for (int i = 0; i < languages.length; i++) {
            if (!source.existsTranslation(languages[i])) {
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
                setParameter(PATH, source.getPath());
                List nonExistingLanguages = getNonExistingLanguages();
                setParameter(LANGUAGES, nonExistingLanguages
                        .toArray(new String[nonExistingLanguages.size()]));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * For new language version of a document, name is the same as that document's
     * @see Create#getNewDocumentName()
     */
    protected String getNewDocumentName() {
        return getSourceDocument().getName();
    }

    /**
     * For new language version of a document, the path is the same as that document's
     * @see Create#getNewDocumentPath()
     */
    protected String getNewDocumentPath() {
        try {
            return getSourceDocument().getPath();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * New language version of a document: use that document's content
     * @see Create#getInitialDocument()
     */
    protected Document getInitialDocument() {
        return getSourceDocument();
    }

    /**
     * @see Create#getDocumentTypeName()
     */
    protected String getDocumentTypeName() {
        if (this.documentTypeName == null && getSourceDocument() != null) {
            try {
                ResourceType type = getSourceDocument().getResourceType();
                this.documentTypeName = type.getName();

                List nonExistingLanguages = getNonExistingLanguages();
                setParameter(LANGUAGES, nonExistingLanguages
                        .toArray(new String[nonExistingLanguages.size()]));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this.documentTypeName;
    }

    protected boolean getVisibleInNav() {
        Document source = getSourceDocument();
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(source.getPublication()
                    .getSiteManagerHint());
            return siteManager.isVisibleInNav(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                manager.release(selector);
            }
        }
    }

    protected String getSourceExtension() {
        return getSourceDocument().getSourceExtension();
    }

    protected boolean createVersion() {
        return true;
    }

}