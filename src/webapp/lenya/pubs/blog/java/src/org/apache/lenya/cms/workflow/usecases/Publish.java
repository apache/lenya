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
package org.apache.lenya.cms.workflow.usecases;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;

/**
 * Publish usecase handler.
 * 
 * @version $Id: Publish.java 209612 2005-07-07 16:52:44Z chestnut $
 */
public class Publish extends DocumentUsecase {

    protected static final String MISSING_DOCUMENTS = "missingDocuments";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        try {
            List nodes = new ArrayList();
            DocumentSet set = new DocumentSet();
            
            Document doc = getSourceDocument();
            set.addAll(SiteUtil.getSubSite(this.manager, doc));
            
            Document liveDoc = doc.getIdentityMap().getAreaVersion(doc, Publication.LIVE_AREA);
            if(liveDoc.exists())
                set.addAll(SiteUtil.getSubSite(this.manager, liveDoc));
            else
                set.add(liveDoc);
            
            
            Document[] documents = set.getDocuments();
            for (int i = 0; i < documents.length; i++) {
                nodes.add(documents[i].getRepositoryNode());
            }

            nodes.add(SiteUtil.getSiteStructure(this.manager, liveDoc).getRepositoryNode());
            return (Node[]) nodes.toArray(new Node[nodes.size()]);

        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * Checks if the workflow event is supported and the parent of the document exists in the live
     * area.
     * 
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!hasErrors()) {

            String event = getEvent();
            Document document = getSourceDocument();

            if (!document.getArea().equals(Publication.AUTHORING_AREA)) {
                addErrorMessage("This usecase can only be invoked from the authoring area.");
                return;
            }

            if (!WorkflowUtil.canInvoke(this.manager, getLogger(), getSourceDocument(), event)) {
                addErrorMessage("error-workflow-document", new String[] { getEvent(),
                        getSourceDocument().getId() });
            }

        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        DocumentManager documentManager = null;
        try {
            Document authoringDocument = getSourceDocument();
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            documentManager.copyToArea(authoringDocument, Publication.LIVE_AREA);
            WorkflowUtil.invoke(this.manager, getLogger(), authoringDocument, getEvent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }
    }

    /**
     * @return The event to invoke.
     */
    private String getEvent() {
        return "publish";
    }

}