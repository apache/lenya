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
import java.util.List;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Paste a document from the clipboard.
 * 
 * @version $Id$
 */
public class Paste extends DocumentUsecase {

    protected static final String CLIPBOARD_LABEL = "clipboardLabel";

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
            String label;
            try {
                Publication pub = getSourceDocument().getPublication();
                label = clipboard.getDocument(getDocumentFactory(), pub).getLink().getLabel();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            setParameter(CLIPBOARD_LABEL, label);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        List nodes = new ArrayList();

        try {
            Node siteNode = getSourceDocument().area().getSite().getRepositoryNode();
            nodes.add(siteNode);

            Clipboard clipboard = new ClipboardHelper().getClipboard(getContext());
            DocumentFactory map = getDocumentFactory();
            Publication pub = getSourceDocument().getPublication();
            Document clippedDocument = clipboard.getDocument(map, pub);

            NodeSet subsite = SiteUtil.getSubSite(this.manager, clippedDocument.getLink().getNode());
            Document[] subsiteDocs = subsite.getDocuments();

            for (int i = 0; i < subsiteDocs.length; i++) {
                if (clipboard.getMethod() == Clipboard.METHOD_CUT) {
                    nodes.add(subsiteDocs[i].getRepositoryNode());
                    nodes.addAll(AssetUtil.getAssetNodes(subsiteDocs[i], this.manager, getLogger()));
                }
            }

        } catch (Exception e) {
            throw new UsecaseException(e);
        }

        return (Node[]) nodes.toArray(new Node[nodes.size()]);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        DocumentFactory identityMap = getDocumentFactory();
        Clipboard clipboard = new ClipboardHelper().getClipboard(getContext());
        Publication pub = getSourceDocument().getPublication();
        Document clippedDocument = clipboard.getDocument(identityMap, pub);

        final String targetPath = getTargetPath();
        final Area area = clippedDocument.area();
        DocumentManager documentManager = null;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);

            if (clipboard.getMethod() == Clipboard.METHOD_COPY) {
                documentManager.copyAll(area, clippedDocument.getPath(), area, targetPath);
            } else if (clipboard.getMethod() == Clipboard.METHOD_CUT) {
                documentManager.moveAll(area, clippedDocument.getPath(), area, targetPath);
            } else {
                throw new RuntimeException("This clipboard method is not supported!");
            }
        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }
    }

    protected String getTargetPath() throws SiteException, DocumentBuildException,
            ServiceException, DocumentException {
        DocumentFactory identityMap = getDocumentFactory();
        Clipboard clipboard = new ClipboardHelper().getClipboard(getContext());
        Publication pub = getSourceDocument().getPublication();
        Document clippedDocument = clipboard.getDocument(identityMap, pub);

        String targetArea = getSourceDocument().getArea();
        String language = clippedDocument.getLanguage();
        String nodeId = clippedDocument.getName();
        String potentialPath = getSourceDocument().getPath() + "/" + nodeId;

        DocumentLocator potentialLoc = DocumentLocator.getLocator(getSourceDocument().getPublication()
                .getId(),
                targetArea,
                potentialPath,
                language);
        return SiteUtil.getAvailableLocator(this.manager, getDocumentFactory(), potentialLoc)
                .getPath();
    }
}