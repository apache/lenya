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

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Node;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
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

    private DocumentManager documentManager;

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

        Clipboard clipboard = new ClipboardHelper().getClipboard(getRequest());
        if (clipboard == null) {
            addErrorMessage("clipboard-empty");
        } else {
            Document doc = getSourceDocument();
            if (doc != null) {
                Document clippedDoc = clipboard.getDocument(getSession());
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
        String sourceUrl = getParameterAsString(SOURCE_URL);
        if (getSession().getUriHandler().isDocument(sourceUrl)) {
            doc = getSession().getUriHandler().getDocument(sourceUrl);
        }
        return doc;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Clipboard clipboard = new ClipboardHelper().getClipboard(getRequest());
        if (clipboard != null) {
            String label;
            try {
                label = clipboard.getDocument(getSession()).getLink().getLabel();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            setParameter(CLIPBOARD_LABEL, label);
        }
    }

    protected Publication getPublication() {
        URLInformation info = new URLInformation(getSourceURL());
        String pubId = info.getPublicationId();
        return getSession().getPublication(pubId);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        List nodes = new ArrayList();

        try {

            Clipboard clipboard = new ClipboardHelper().getClipboard(getRequest());
            if (clipboard != null) {

                Node siteNode = getArea().getSite();
                nodes.add(siteNode);

                Publication pub = getPublication();
                Document clippedDocument = clipboard.getDocument(getSession());

                NodeSet subsite = SiteUtil.getSubSite(clippedDocument.getLink().getNode());
                Document[] subsiteDocs = subsite.getDocuments();

                for (int i = 0; i < subsiteDocs.length; i++) {
                    if (clipboard.getMethod() == Clipboard.METHOD_CUT) {
                        nodes.add(subsiteDocs[i]);
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
        return pub.getArea(info.getArea());
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        ClipboardHelper helper = new ClipboardHelper();

        HttpServletRequest request = getRequest();
        Clipboard clipboard = helper.getClipboard(request);
        Publication pub = getPublication();
        Document clippedDocument = clipboard.getDocument(getSession());

        final String targetPath = getTargetPath();
        final Area area = clippedDocument.area();
        if (clipboard.getMethod() == Clipboard.METHOD_COPY) {
            getDocumentManager().copyAll(area, clippedDocument.getPath(), area, targetPath);
        } else if (clipboard.getMethod() == Clipboard.METHOD_CUT) {
            getDocumentManager().moveAll(area, clippedDocument.getPath(), area, targetPath);
        } else {
            throw new RuntimeException("This clipboard method is not supported!");
        }

        helper.removeClipboard(request);
    }

    protected String getTargetPath() throws SiteException, DocumentBuildException,
            ServiceException, DocumentException {
        Clipboard clipboard = new ClipboardHelper().getClipboard(getRequest());
        Publication pub = getPublication();
        Document clippedDocument = clipboard.getDocument(getSession());

        String targetArea = getArea().getName();
        String language = clippedDocument.getLanguage();
        String nodeId = clippedDocument.getName();

        Document sourceDoc = getSourceDocument();
        String basePath = sourceDoc != null ? sourceDoc.getPath() : "";

        String potentialPath = basePath + "/" + nodeId;

        DocumentLocator potentialLoc = DocumentLocator.getLocator(getPublication().getId(),
                targetArea, potentialPath, language);
        return SiteUtil.getAvailableLocator(getSession(), potentialLoc).getPath();
    }

    protected HttpServletRequest getRequest() {
        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        return process.getRequest();
    }

    protected DocumentManager getDocumentManager() {
        return documentManager;
    }

    /**
     * TODO: Bean wiring
     */
    public void setDocumentManager(DocumentManager documentManager) {
        this.documentManager = documentManager;
    }

}
