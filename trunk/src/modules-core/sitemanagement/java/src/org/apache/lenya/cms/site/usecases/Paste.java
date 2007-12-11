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

import org.apache.avalon.framework.service.ServiceException;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Paste a document from the clipboard.
 * 
 * @version $Id$
 */
public class Paste extends AbstractUsecase {

    protected static final String CLIPBOARD_LABEL = "clipboardLabel";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        if (hasErrors()) {
            return;
        }
        
        if (!getArea().getName().equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("only-in-authoring-area");
        }

        Clipboard clipboard = new ClipboardHelper().getClipboard(getContext());
        if (clipboard == null) {
            addErrorMessage("clipboard-empty");
        }
        else {
            Document doc = getSourceDocument();
            if(doc != null) {
                Document clippedDoc = clipboard.getDocument(getDocumentFactory(), doc.getPublication());
                String uuid = clippedDoc.getUUID();
                SiteNode node = doc.getLink().getNode();
                if (clipboard.getMethod() == Clipboard.METHOD_CUT) {
                    if (willPasteInOwnSubtree(node, uuid)) {
                        addErrorMessage("will-paste-in-own-subtree");
                    }
                }
            }
        }
    }

    protected boolean willPasteInOwnSubtree(SiteNode node, String uuid) throws SiteException {
        String nodeUuid = node.getUuid();
        if (nodeUuid.equals(uuid)) {
            return true;
        } else if (!node.isTopLevel()) {
            return willPasteInOwnSubtree(node.getParent(), uuid);
        } else {
            return false;
        }
    }

    protected Document getSourceDocument() {
        Document doc = null;
        try {
            DocumentFactory factory = getDocumentFactory();
            String sourceUrl = getParameterAsString(SOURCE_URL);
            if (factory.isDocument(sourceUrl)) {
                doc = factory.getFromURL(sourceUrl);
            }
        } catch (DocumentBuildException e) {
            throw new RuntimeException(e);
        }
        return doc;
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
                Publication pub = getPublication();
                label = clipboard.getDocument(getDocumentFactory(), pub).getLink().getLabel();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            setParameter(CLIPBOARD_LABEL, label);
        }
    }

    protected Publication getPublication() {
        URLInformation info = new URLInformation(getSourceURL());
        String pubId = info.getPublicationId();
        try {
            return getDocumentFactory().getPublication(pubId);
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        List nodes = new ArrayList();

        try {

            Clipboard clipboard = new ClipboardHelper().getClipboard(getContext());
            if (clipboard != null) {
                
                Node siteNode = getArea().getSite().getRepositoryNode();
                nodes.add(siteNode);

                DocumentFactory map = getDocumentFactory();
                Publication pub = getPublication();
                Document clippedDocument = clipboard.getDocument(map, pub);
    
                NodeSet subsite = SiteUtil
                        .getSubSite(this.manager, clippedDocument.getLink().getNode());
                Document[] subsiteDocs = subsite.getDocuments();
    
                for (int i = 0; i < subsiteDocs.length; i++) {
                    if (clipboard.getMethod() == Clipboard.METHOD_CUT) {
                        nodes.add(subsiteDocs[i].getRepositoryNode());
                    }
                }
            }

        } catch (Exception e) {
            throw new UsecaseException(e);
        }

        return (Node[]) nodes.toArray(new Node[nodes.size()]);
    }

    protected Area getArea() {
        Publication pub = getPublication();
        URLInformation info = new URLInformation(getSourceURL());
        try {
            return pub.getArea(info.getArea());
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        DocumentFactory identityMap = getDocumentFactory();
        ClipboardHelper helper = new ClipboardHelper();

        Clipboard clipboard = helper.getClipboard(getContext());
        Publication pub = getPublication();
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

        helper.removeClipboard(getContext());
    }

    protected String getTargetPath() throws SiteException, DocumentBuildException,
            ServiceException, DocumentException {
        DocumentFactory identityMap = getDocumentFactory();
        Clipboard clipboard = new ClipboardHelper().getClipboard(getContext());
        Publication pub = getPublication();
        Document clippedDocument = clipboard.getDocument(identityMap, pub);

        String targetArea = getArea().getName();
        String language = clippedDocument.getLanguage();
        String nodeId = clippedDocument.getName();

        Document sourceDoc = getSourceDocument();
        String basePath = sourceDoc != null ? sourceDoc.getPath() : "";

        String potentialPath = basePath + "/" + nodeId;

        DocumentLocator potentialLoc = DocumentLocator.getLocator(getPublication().getId(),
                targetArea, potentialPath, language);
        return SiteUtil.getAvailableLocator(this.manager, getDocumentFactory(), potentialLoc)
                .getPath();
    }
}
