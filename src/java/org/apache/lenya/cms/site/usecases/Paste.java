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
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.transaction.Transactionable;

/**
 * Paste a document from the clipboard.
 * 
 * @version $Id$
 */
public class Paste extends DocumentUsecase {

    protected static final String CLIPBOARD_DOCUMENT_ID = "clipboardDocumentId";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        if (hasErrors()) {
            return;
        }

        Clipboard clipboard = new ClipboardHelper().getClipboard(getContext());
        if (clipboard == null) {
            addErrorMessage("Cannot paste - the clipboard is empty.");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Clipboard clipboard = new ClipboardHelper().getClipboard(getContext());
        if (clipboard != null) {
            String id;
            try {
                id = clipboard.getDocument(getDocumentIdentityMap()).getId();
            } catch (DocumentBuildException e) {
                throw new RuntimeException(e);
            }
            setParameter(CLIPBOARD_DOCUMENT_ID, id);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getObjectsToLock()
     */
    protected Transactionable[] getObjectsToLock() throws UsecaseException {
        List nodes = new ArrayList();

        try {
            Node siteNode = SiteUtil.getSiteStructure(this.manager, getSourceDocument())
                    .getRepositoryNode();
            nodes.add(siteNode);

            Clipboard clipboard = new ClipboardHelper().getClipboard(getContext());
            if (clipboard.getMethod() == Clipboard.METHOD_CUT) {
                DocumentIdentityMap identityMap = getDocumentIdentityMap();
                Document clippedDocument = clipboard.getDocument(identityMap);
                nodes.addAll(Arrays.asList(clippedDocument.getRepositoryNodes()));
            }

            Document targetDocument = getTargetDocument();
            nodes.addAll(Arrays.asList(targetDocument.getRepositoryNodes()));

        } catch (Exception e) {
            throw new UsecaseException(e);
        }

        return (Transactionable[]) nodes.toArray(new Transactionable[nodes.size()]);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        DocumentIdentityMap identityMap = getDocumentIdentityMap();
        Clipboard clipboard = new ClipboardHelper().getClipboard(getContext());
        Document clippedDocument = clipboard.getDocument(identityMap);

        Document targetDocument = getTargetDocument();
        DocumentManager documentManager = null;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);

            if (clipboard.getMethod() == Clipboard.METHOD_COPY) {
                documentManager.copyAll(clippedDocument, targetDocument);
            } else if (clipboard.getMethod() == Clipboard.METHOD_CUT) {
                documentManager.moveAll(clippedDocument, targetDocument);
            } else {
                throw new RuntimeException("This clipboard method is not supported!");
            }
        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }
    }

    protected Document getTargetDocument() throws SiteException, DocumentBuildException,
            ServiceException {
        DocumentIdentityMap identityMap = getDocumentIdentityMap();
        Clipboard clipboard = new ClipboardHelper().getClipboard(getContext());
        Document clippedDocument = clipboard.getDocument(identityMap);

        String targetArea = getSourceDocument().getArea();
        String language = clippedDocument.getLanguage();
        String nodeId = clippedDocument.getName();
        String potentialDocumentId = getSourceDocument().getId() + "/" + nodeId;

        Document potentialDocument = identityMap.get(getSourceDocument().getPublication(),
                targetArea,
                potentialDocumentId,
                language);
        DocumentManager documentManager = null;
        Document availableDocument = null;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            availableDocument = SiteUtil.getAvailableDocument(this.manager, potentialDocument);
        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }
        return availableDocument;
    }
}