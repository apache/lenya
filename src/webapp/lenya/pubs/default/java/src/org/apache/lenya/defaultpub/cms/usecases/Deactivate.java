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
package org.apache.lenya.defaultpub.cms.usecases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowManager;
import org.apache.lenya.transaction.Transactionable;
import org.apache.lenya.workflow.WorkflowException;

/**
 * Deactivate usecase handler.
 * 
 * @version $Id:$
 */
public class Deactivate extends DocumentUsecase implements DocumentVisitor {

    /**
     * Checks if the workflow event is supported and the parent of the document exists in the live
     * area.
     * 
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        if (! hasErrors()) {

            if (!getSourceDocument().getArea().equals(Publication.AUTHORING_AREA)) {
                addErrorMessage("This usecase can only be invoked from the authoring area.");
                return;
            }

            String event = getEvent();

            WorkflowManager wfManager = null;
            try {
                wfManager = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
                if (!wfManager.canInvoke(getSourceDocument(), event)) {
                    setParameter(Publish.ALLOW_SINGLE_DOCUMENT, Boolean.toString(false));
                    addInfoMessage("The single document cannot be deactivated because the workflow event cannot be invoked.");
                } else {
                    setParameter(Publish.ALLOW_SINGLE_DOCUMENT, Boolean.toString(true));
                }
            } finally {
                if (wfManager != null) {
                    this.manager.release(wfManager);
                }
            }

            // get involved objects to lock them
            Document doc = getSourceDocument();
            try {
                Document liveVersion = doc.getIdentityMap().getAreaVersion(doc,
                        Publication.LIVE_AREA);
                getInvolvedDocuments(liveVersion);
            } catch (DocumentBuildException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getObjectsToLock()
     */
    protected Transactionable[] getObjectsToLock() throws UsecaseException {
        try {
            List nodes = new ArrayList();
            Document doc = getSourceDocument();
            Document liveVersion = doc.getIdentityMap().getAreaVersion(doc, Publication.LIVE_AREA);
            DocumentSet set = getInvolvedDocuments(liveVersion);
            Document[] documents = set.getDocuments();
            for (int i = 0; i < documents.length; i++) {
                nodes.addAll(Arrays.asList(documents[i].getRepositoryNodes()));
            }

            nodes.add(SiteUtil.getSiteStructure(this.manager,
                    doc.getIdentityMap(),
                    doc.getPublication(),
                    Publication.LIVE_AREA).getRepositoryNode());
            return (Transactionable[]) nodes.toArray(new Transactionable[nodes.size()]);

        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        if (isSubtreeEnabled()) {
            deactivateAll(getSourceDocument());
        } else {
            deactivate(getSourceDocument());
        }
    }

    /**
     * Deactivates a document.
     * @param authoringDocument The authoring document.
     */
    protected void deactivate(Document authoringDocument) {

        boolean success = false;

        WorkflowManager wfManager = null;
        DocumentManager documentManager = null;
        try {
            wfManager = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
            Document liveDocument = authoringDocument.getIdentityMap()
                    .getAreaVersion(authoringDocument, Publication.LIVE_AREA);

            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            documentManager.delete(liveDocument);

            wfManager.invoke(authoringDocument, getEvent());
            success = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Deactivate document [" + authoringDocument + "]. Success: ["
                        + success + "]");
            }
            if (wfManager != null) {
                this.manager.release(wfManager);
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
        return "deactivate";
    }

    /**
     * Deactivates a document or the subtree below a document, based on the parameter SUBTREE.
     * @param document The document.
     */
    protected void deactivateAll(Document document) {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Deactivating document [" + document + "]");
            getLogger().debug("Subtree deactivation: [" + isSubtreeEnabled() + "]");
        }

        DocumentSet set = getInvolvedDocuments(document);
        try {
            set.visit(this);
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Publishing completed.");
        }
    }

    protected DocumentSet getInvolvedDocuments(Document document) {
        DocumentSet set;
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(document.getPublication()
                    .getSiteManagerHint());

            Document[] descendants = siteManager.getRequiringResources(document);
            set = new DocumentSet(descendants);
            set.add(document);
            siteManager.sortAscending(set);
            set.reverse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
        return set;
    }

    /**
     * Returns whether subtree publishing is enabled.
     * @return A boolean value.
     */
    protected boolean isSubtreeEnabled() {
        String value = getParameterAsString(Publish.SUBTREE);
        return value != null;
    }

    /**
     * @throws PublicationException
     * @see org.apache.lenya.cms.publication.util.DocumentVisitor#visitDocument(org.apache.lenya.cms.publication.Document)
     */
    public void visitDocument(Document document) throws PublicationException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Visiting resource [" + document + "]");
        }

        try {
            deactivateAllLanguageVersions(document);
        } catch (WorkflowException e) {
            throw new PublicationException(e);
        }
    }

    /**
     * Publishes all existing language versions of a document.
     * @param document The document.
     * @throws PublicationException if an error occurs.
     * @throws WorkflowException
     */
    protected void deactivateAllLanguageVersions(Document document) throws PublicationException,
            WorkflowException {
        String[] languages = document.getPublication().getLanguages();
        WorkflowManager wfManager = null;
        try {
            wfManager = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
            for (int i = 0; i < languages.length; i++) {
                Document version = document.getIdentityMap().getLanguageVersion(document,
                        languages[i]);
                if (version.exists() && wfManager.canInvoke(version, getEvent())) {
                    deactivate(version);
                }
            }
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } finally {
            if (wfManager != null) {
                this.manager.release(wfManager);
            }
        }

    }

}
