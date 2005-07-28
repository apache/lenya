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
import java.util.Arrays;
import java.util.List;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.Transactionable;

/**
 * Change the node ID of a document.
 * 
 * @version $Id$
 */
public class ChangeNodeID extends DocumentUsecase {

    protected static final String NODE_ID = "nodeId";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        Document document = getSourceDocument();
        if (document != null) {
            setParameter(NODE_ID, document.getName());
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getObjectsToLock()
     */
    protected Transactionable[] getObjectsToLock() throws UsecaseException {

        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            Document doc = getSourceDocument();
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(doc.getPublication().getSiteManagerHint());
            SiteStructure structure = siteManager.getSiteStructure(doc.getIdentityMap(), doc
                    .getPublication(), doc.getArea());

            List objects = new ArrayList();
            objects.add(structure.getRepositoryNode());
            objects.addAll(getAllLanguageVersionNodes(doc));
            return (Transactionable[]) objects.toArray(new Transactionable[objects.size()]);
        } catch (Exception e) {
            throw new UsecaseException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
    }

    protected List getAllLanguageVersionNodes(Document doc) throws DocumentException,
            TransactionException, DocumentBuildException {
        String[] languages = doc.getLanguages();
        List nodes = new ArrayList();
        for (int i = 0; i < languages.length; i++) {
            nodes.addAll(Arrays.asList(doc.getIdentityMap().getLanguageVersion(doc, languages[i])
                    .getRepositoryNodes()));
        }
        return nodes;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (hasErrors()) {
            return;
        }

        if (!getSourceDocument().getArea().equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("This usecase can only be invoked in the authoring area!");
        } else {
            Document liveVersion = getDocumentIdentityMap().getAreaVersion(getSourceDocument(),
                    Publication.LIVE_AREA);
            if (liveVersion.exists()) {
                addErrorMessage("This usecase cannot be invoked when the live version exists!");
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        DocumentIdentityMap identityMap = getSourceDocument().getIdentityMap();

        String nodeId = getParameterAsString(NODE_ID);
        DocumentManager documentManager = null;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            if (!documentManager.isValidDocumentName(nodeId)) {
                addErrorMessage("The document ID is not valid.");
            } else {
                Document parent = identityMap.getParent(getSourceDocument());
                String parentId = "";
                // if the document is at the top level, the parent is null
                if (parent != null) parentId = parent.getId();
                Publication publication = getSourceDocument().getPublication();
                Document document = identityMap.get(publication,
                        getSourceDocument().getArea(),
                        parentId + "/" + nodeId,
                        getSourceDocument().getLanguage());
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
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Document document = getSourceDocument();
        Document newDocument = moveAllLanguageVersions(document);

        LinkRewriter rewriter = null;
        try {
            rewriter = (LinkRewriter) this.manager.lookup(LinkRewriter.ROLE);
            rewriter.rewriteLinks(document, newDocument);
        } finally {
            if (rewriter != null) {
                this.manager.release(rewriter);
            }
        }

        setTargetDocument(newDocument);
    }

    /**
     * Moves all language versions of a document.
     * @param document The document.
     * @return The moved version of the document.
     * @throws DocumentException if an error occurs.
     * @throws DocumentBuildException if an error occurs.
     * @throws PublicationException if an error occurs.
     * @throws ServiceException if an access error to a an Avalon service occurs
     */
    protected Document moveAllLanguageVersions(Document document) throws DocumentException,
            DocumentBuildException, PublicationException, ServiceException {
        Document newDocument = null;

        DocumentIdentityMap identityMap = document.getIdentityMap();
        String newDocumentId = getNewDocumentId();

        String[] availableLanguages = document.getLanguages();

        DocumentManager documentManager = null;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            for (int i = 0; i < availableLanguages.length; i++) {
                Document languageVersion = identityMap.get(document.getPublication(), document
                        .getArea(), document.getId(), availableLanguages[i]);

                Document newLanguageVersion = identityMap.get(document.getPublication(), document
                        .getArea(), newDocumentId, availableLanguages[i]);

                Transactionable[] nodes = newLanguageVersion.getRepositoryNodes();
                for (int j = 0; j < nodes.length; j++) {
                    nodes[j].lock();
                }
                documentManager.move(languageVersion, newLanguageVersion);

                if (availableLanguages[i].equals(document.getLanguage())) {
                    newDocument = newLanguageVersion;
                }
            }
        } catch (TransactionException e) {
            throw new PublicationException(e);
        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }

        return newDocument;
    }

    /**
     * Returns the resulting document when the node ID would be changed.
     * @return A document.
     */
    protected String getNewDocumentId() {
        String nodeId = getParameterAsString(NODE_ID);

        Document document = getSourceDocument();

        String oldDocumentId = document.getId();
        int lastSlashIndex = oldDocumentId.lastIndexOf("/");
        String strippedDocumentId = oldDocumentId.substring(0, lastSlashIndex + 1);
        String newDocumentId = strippedDocumentId + nodeId;

        return newDocumentId;
    }
}