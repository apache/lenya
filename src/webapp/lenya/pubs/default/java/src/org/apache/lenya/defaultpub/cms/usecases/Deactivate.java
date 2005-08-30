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
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.site.Node;
import org.apache.lenya.cms.site.NodeFactory;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.workflow.WorkflowException;

/**
 * Deactivate usecase handler.
 * 
 * @version $Id$
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

        if (!hasErrors()) {

            if (!getSourceDocument().getArea().equals(Publication.AUTHORING_AREA)) {
                addErrorMessage("This usecase can only be invoked from the authoring area.");
                return;
            }

            String event = getEvent();
            boolean allowSingle = true;

            if (!WorkflowUtil.canInvoke(this.manager,
                    getSession(),
                    getLogger(),
                    getSourceDocument(),
                    event)) {
                allowSingle = false;
                addInfoMessage("The single document cannot be deactivated because the workflow event cannot be invoked.");
            }

            DocumentIdentityMap map = getSourceDocument().getIdentityMap();
            Document liveDoc = map.getAreaVersion(getSourceDocument(), Publication.LIVE_AREA);
            DocumentSet subSite = SiteUtil.getSubSite(this.manager, liveDoc);
            Node node = NodeFactory.getNode(liveDoc);
            subSite.removeAll(SiteUtil.getExistingDocuments(map, node));

            if (!subSite.isEmpty()) {
                allowSingle = false;
                addInfoMessage("You have to deactivate the whole subtree because descendants are live.");
            }
            setParameter(Publish.ALLOW_SINGLE_DOCUMENT, Boolean.toString(allowSingle));
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected org.apache.lenya.cms.repository.Node[] getNodesToLock() throws UsecaseException {
        try {
            List nodes = new ArrayList();
            DocumentSet set = new DocumentSet();

            Document doc = getSourceDocument();
            set.addAll(SiteUtil.getSubSite(this.manager, doc));

            Document liveDoc = doc.getIdentityMap().getAreaVersion(doc, Publication.LIVE_AREA);
            set.addAll(SiteUtil.getSubSite(this.manager, liveDoc));

            Document[] documents = set.getDocuments();
            for (int i = 0; i < documents.length; i++) {
                nodes.add(documents[i].getRepositoryNode());
            }

            nodes.add(SiteUtil.getSiteStructure(this.manager, liveDoc).getRepositoryNode());
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
        if (isSubtreeEnabled()) {
            deactivateAll(getSourceDocument());
        } else {
            deactivate(getSourceDocument());
        }
    }

    /**
     * Deactivates a document.
     * 
     * @param authoringDocument The authoring document.
     */
    protected void deactivate(Document authoringDocument) {

        boolean success = false;

        DocumentManager documentManager = null;
        try {
            Document liveDocument = authoringDocument.getIdentityMap()
                    .getAreaVersion(authoringDocument, Publication.LIVE_AREA);

            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            documentManager.delete(liveDocument);

            WorkflowUtil.invoke(this.manager,
                    getSession(),
                    getLogger(),
                    authoringDocument,
                    getEvent());
            success = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Deactivate document [" + authoringDocument + "]. Success: ["
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
        return "deactivate";
    }

    /**
     * Deactivates a document or the subtree below a document, based on the parameter SUBTREE.
     * 
     * @param document The document.
     */
    protected void deactivateAll(Document document) {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Deactivating document [" + document + "]");
            getLogger().debug("Subtree deactivation: [" + isSubtreeEnabled() + "]");
        }

        try {
            DocumentSet set = SiteUtil.getSubSite(this.manager, document);
            SiteUtil.sortDescending(this.manager, set);
            set.visit(this);
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Publishing completed.");
        }
    }

    /**
     * Returns whether subtree publishing is enabled.
     * 
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
     * 
     * @param document The document.
     * @throws PublicationException if an error occurs.
     * @throws WorkflowException
     */
    protected void deactivateAllLanguageVersions(Document document) throws PublicationException,
            WorkflowException {
        String[] languages = document.getPublication().getLanguages();
        for (int i = 0; i < languages.length; i++) {
            Document version = document.getIdentityMap().getLanguageVersion(document, languages[i]);
            if (version.exists()
                    && WorkflowUtil.canInvoke(this.manager,
                            getSession(),
                            getLogger(),
                            version,
                            getEvent())) {
                deactivate(version);
            }
        }
    }

}