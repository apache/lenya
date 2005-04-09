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

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Delete a document and all its descendants, including all language versions. The documents are
 * moved to the trash.
 * 
 * @version $Id:$
 */
public class Delete extends DocumentUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!getErrorMessages().isEmpty()) {
            return;
        }

        if (!getSourceDocument().getArea().equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("This usecase can only be invoked in the authoring area!");
        }

        Document document = getSourceDocument();
        DocumentIdentityMap identityMap = getDocumentIdentityMap();

        DocumentSet set = SiteUtil.getSubSite(this.manager, document);
        Document[] documents = set.getDocuments();
        for (int i = 0; i < documents.length; i++) {
            Document liveVersion = identityMap.getAreaVersion(documents[i], Publication.LIVE_AREA);
            if (liveVersion.exists()) {
                addErrorMessage("Cannot delete because document [" + liveVersion + "] is live!");
            }
        }
    }

    /**
     * Lock the following objects:
     * <ul>
     * <li>all involved documents in the document's area</li>
     * <li>the trash versions of these documents</li>
     * <li>the document area's site structure</li>
     * <li>the trash site structure</li>
     * </ul>
     * @see org.apache.lenya.cms.usecase.Usecase#lockInvolvedObjects()
     */
    public void lockInvolvedObjects() throws UsecaseException {
        super.lockInvolvedObjects();

        Document doc = getSourceDocument();
        try {
            DocumentSet sources = SiteUtil.getSubSite(this.manager, doc);
            sources.lock();

            DocumentSet targets = SiteUtil.getTransferedSubSite(this.manager,
                    doc,
                    Publication.TRASH_AREA,
                    SiteUtil.MODE_CHANGE_ID);
            targets.lock();

            SiteUtil.getSiteStructure(this.manager, doc).lock();
            SiteUtil.getSiteStructure(this.manager, targets.getDocuments()[0]).lock();
        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Document doc = getSourceDocument();
        Document target = doc.getIdentityMap().getAreaVersion(doc, Publication.TRASH_AREA);
        target = SiteUtil.getAvailableDocument(this.manager, target);

        DocumentManager documentManager = null;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);

            DocumentSet sources = SiteUtil.getSubSite(this.manager, doc);
            DocumentSet targets = SiteUtil.getTransferedSubSite(this.manager,
                    doc,
                    Publication.TRASH_AREA,
                    SiteUtil.MODE_CHANGE_ID);

            documentManager.move(sources, targets);
        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }

        setTargetDocument(target);
    }

}