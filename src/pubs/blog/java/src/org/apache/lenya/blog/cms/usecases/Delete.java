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
package org.apache.lenya.blog.cms.usecases;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;

/**
 * Deactivate usecase handler.
 * 
 * @version $Id: Deactivate.java 264805 2005-08-30 16:20:15Z andreas $
 */
public class Delete extends DocumentUsecase {
    
    /**
     * Checks if the workflow event is supported and the parent of the document exists in the live
     * area.
     * 
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!hasErrors()) {
            if (!getSourceDocument().getArea().equals(Publication.AUTHORING_AREA)) {
                addErrorMessage("This usecase can only be invoked from the authoring area.");
                return;
            }
            Document liveDocument = getSourceDocument().getFactory()
            .getAreaVersion(getSourceDocument(), Publication.LIVE_AREA);
            if (liveDocument.exists()) {
                addErrorMessage("The document cannot be deleted because it's Live, deactivate it first");
                return;
            }
            String event = getEvent();
            if (!WorkflowUtil.canInvoke(this.manager,
                    getSession(),
                    getLogger(),
                    getSourceDocument(),
                    event)) {
                addInfoMessage("The document cannot be deactivated because the workflow event cannot be invoked.");
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected org.apache.lenya.cms.repository.Node[] getNodesToLock() throws UsecaseException {
        try {
            List nodes = new ArrayList();
            Document doc = getSourceDocument();           
            nodes.add(doc.getRepositoryNode());            
            nodes.add(SiteUtil.getSiteStructure(this.manager, doc).getRepositoryNode());            
            return (org.apache.lenya.cms.repository.Node[]) nodes.toArray(new org.apache.lenya.cms.repository.Node[nodes.size()]);            
        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        delete(getSourceDocument());        
        setTargetDocument(getDocumentFactory().get(getSourceDocument().getPublication(), Publication.AUTHORING_AREA,"/feeds/all/index"));
    }

    /**
     * Deletes a document.
     * 
     * @param authoringDocument The authoring document.
     */
    protected void delete(Document document) {
        DocumentManager documentManager = null;
        boolean success = false;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            documentManager.delete(document);       
            WorkflowUtil.invoke(this.manager,
                    getSession(),
                    getLogger(),
                    document,
                    getEvent());
            success = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Delete document [" + getSourceDocument() + "]. Success: ["
                        + success + "]");
            }
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }
    }

    /**
     * @return The event to invoke.
     */
    private String getEvent() {
        return "delete";
    }
}