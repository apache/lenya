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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.transaction.TransactionException;

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
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {

        List nodes = new ArrayList();

        try {
            Node siteNode = SiteUtil.getSiteStructure(this.manager, getSourceDocument())
                    .getRepositoryNode();
            nodes.add(siteNode);

            Document sourceDocument = getSourceDocument();

            DocumentSet subsite = SiteUtil.getSubSite(this.manager, sourceDocument);
            Document[] subsiteDocs = subsite.getDocuments();
            for (int i = 0; i < subsiteDocs.length; i++) {
                nodes.add(subsiteDocs[i].getRepositoryNode());
                nodes.addAll(AssetUtil.getAssetNodes(subsiteDocs[i], this.manager, getLogger()));
            }

        } catch (Exception e) {
            throw new UsecaseException(e);
        }

        return (Node[]) nodes.toArray(new Node[nodes.size()]);
    }

    protected List getAllLanguageVersionNodes(Document doc) throws DocumentException,
            TransactionException, DocumentBuildException {
        String[] languages = doc.getLanguages();
        List nodes = new ArrayList();
        for (int i = 0; i < languages.length; i++) {
            nodes.add(doc.getTranslation(languages[i]).getRepositoryNode());
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
            if (getSourceDocument().existsAreaVersion(Publication.LIVE_AREA)) {
                addErrorMessage("This usecase cannot be invoked when the live version exists!");
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        String nodeId = getParameterAsString(NODE_ID);
        ServiceSelector selector = null;
        DocumentBuilder builder = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            String hint = getSourceDocument().getPublication().getDocumentBuilderHint();
            builder = (DocumentBuilder) selector.select(hint);
            if (!builder.isValidDocumentName(nodeId)) {
                addErrorMessage("The document ID is not valid.");
            } else {
                DocumentLocator target = getTargetLocator();
                if (SiteUtil.contains(this.manager, getDocumentFactory(), target)) {
                    addErrorMessage("The document does already exist.");
                }
            }
        } finally {
            if (selector != null) {
                if (builder != null) {
                    selector.release(builder);
                }
                this.manager.release(selector);
            }
        }
    }

    protected DocumentLocator getTargetLocator() throws DocumentBuildException, SiteException {
        String nodeId = getParameterAsString(NODE_ID);
        Document doc = getSourceDocument();
        String path = SiteUtil.getPath(this.manager, doc);
        DocumentLocator loc = DocumentLocator.getLocator(doc.getPublication().getId(),
                doc.getArea(),
                path,
                doc.getLanguage());
        DocumentLocator parent = loc.getParent();
        return parent.getChild(nodeId);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Document targetDoc;
        Document source = getSourceDocument();
        DocumentLocator target = getTargetLocator();
        DocumentManager documentManager = null;
        LinkRewriter rewriter = null;
        try {

            DocumentSet subsite = SiteUtil.getSubSite(this.manager, source);
            Map targets = SiteUtil.getTransferedSubSite(this.manager,
                    source,
                    getTargetLocator(),
                    SiteUtil.MODE_CANCEL);
            Document[] subsiteDocs = subsite.getDocuments();
            List nodes = new ArrayList();
            for (int i = 0; i < subsiteDocs.length; i++) {

                Document targetSubsiteDoc = (Document) targets.get(subsiteDocs[i]);
                nodes.add(targetSubsiteDoc.getRepositoryNode());
                nodes.addAll(AssetUtil.getCopiedAssetNodes(subsiteDocs[i],
                        targetSubsiteDoc,
                        this.manager,
                        getLogger()));
            }
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                ((Node) i.next()).lock();
            }

            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            documentManager.moveAll(source, target);

            targetDoc = getDocumentFactory().get(target);

            rewriter = (LinkRewriter) this.manager.lookup(LinkRewriter.ROLE);
            rewriter.rewriteLinks(source, targetDoc);
        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
            if (rewriter != null) {
                this.manager.release(rewriter);
            }
        }

        setTargetDocument(targetDoc);
    }

    /**
     * Returns the resulting document when the node ID would be changed.
     * @return A document.
     */
    protected String getNewDocumentId() {
        String nodeId = getParameterAsString(NODE_ID);

        String oldPath;
        try {
            oldPath = SiteUtil.getPath(this.manager, getSourceDocument());
        } catch (SiteException e) {
            throw new RuntimeException(e);
        }
        int lastSlashIndex = oldPath.lastIndexOf("/");
        String strippedDocumentId = oldPath.substring(0, lastSlashIndex + 1);
        String newDocumentId = strippedDocumentId + nodeId;

        return newDocumentId;
    }
}