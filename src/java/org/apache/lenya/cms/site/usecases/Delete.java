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
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.publication.util.UniqueDocumentId;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.usecase.DocumentUsecase;

/**
 * Delete a document and all its descendants, including all language versions.
 * The documents are moved to the trash.
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
        DocumentIdentityMap identityMap = getUnitOfWork().getIdentityMap();
        SiteManager manager = document.getPublication().getSiteManager();
        DocumentSet set = new DocumentSet(manager.getRequiringResources(document));
        set.add(document);
        Document[] documents = set.getDocuments();
        for (int i = 0; i < documents.length; i++) {
            Document liveVersion = identityMap.getFactory().getAreaVersion(documents[i],
                    Publication.LIVE_AREA);
            if (liveVersion.exists()) {
                addErrorMessage("Cannot delete because document [" + liveVersion + "] is live!");
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Document source = getSourceDocument();
        DocumentIdentityMap identityMap = source.getIdentityMap();

        String desiredId = "/" + source.getName();
        UniqueDocumentId helper = new UniqueDocumentId();
        String availableId = helper.computeUniqueDocumentId(source.getPublication(),
                Publication.TRASH_AREA,
                desiredId);
        Document target = identityMap.getFactory().get(Publication.TRASH_AREA, availableId);
        getDocumentManager().moveAll(source, target);

        setTargetDocument(target);
    }

}