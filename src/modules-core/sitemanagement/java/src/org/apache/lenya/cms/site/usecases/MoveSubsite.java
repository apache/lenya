/*
 * Copyright  1999-2004 The Apache Software Foundation
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
import java.util.Map;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.util.DocumentHelper;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.NodeFactory;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;

/**
 * Usecase to move a subsite to another area.
 * 
 * @version $Id:$
 */
public abstract class MoveSubsite extends DocumentUsecase {

    /**
     * @return The possible source areas.
     */
    protected abstract String[] getSourceAreas();

    /**
     * @return The target area.
     */
    protected abstract String getTargetArea();

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (hasErrors()) {
            return;
        }

        List sourceAreas = Arrays.asList(getSourceAreas());
        if (!sourceAreas.contains(getSourceDocument().getArea())) {
            addErrorMessage("This usecase can only be invoked in the authoring area!");
        } else {

            Document document = getSourceDocument();
            DocumentFactory identityMap = getDocumentIdentityMap();

            DocumentSet set = SiteUtil.getSubSite(this.manager, document);
            Document[] documents = set.getDocuments();
            for (int i = 0; i < documents.length; i++) {
                DocumentLocator loc = documents[i].getLocator().getAreaVersion(Publication.LIVE_AREA);
                Document liveVersion = identityMap.get(loc);
                if (liveVersion.exists()) {
                    addErrorMessage("delete-doc-live", new String[] { liveVersion.toString() });
                }
            }
            if (!WorkflowUtil.canInvoke(this.manager, getSession(), getLogger(), set, getEvent())) {
                addErrorMessage("The workflow event cannot be invoked on all documents.");
            }
        }
    }

    /**
     * @return The workflow event.
     */
    protected abstract String getEvent();

    /**
     * Lock the following objects:
     * <ul>
     * <li>all involved documents in the document's area</li>
     * <li>the target versions of these documents</li>
     * <li>the document area's site structure</li>
     * <li>the target site structure</li>
     * </ul>
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected org.apache.lenya.cms.repository.Node[] getNodesToLock() throws UsecaseException {
        List nodes = new ArrayList();
        Document doc = getSourceDocument();
        try {
            DocumentSet sources = SiteUtil.getSubSite(this.manager, doc);
            Map targets = SiteUtil.getTransferedSubSite(this.manager,
                    doc,
                    getTargetArea(),
                    SiteUtil.MODE_CHANGE_ID);

            Document[] docs = sources.getDocuments();
            for (int i = 0; i < docs.length; i++) {
                nodes.add(docs[i].getRepositoryNode());
                nodes.addAll(AssetUtil.getAssetNodes(docs[i], this.manager, getLogger()));

                Document target = (Document) targets.get(docs[i]);
                nodes.add(target.getRepositoryNode());
                nodes.addAll(AssetUtil.getCopiedAssetNodes(docs[i],
                        target,
                        this.manager,
                        getLogger()));
            }

            DocumentSet furtherDocs = new DocumentSet();
            furtherDocs.addAll(getTargetDocsToCopy());
            furtherDocs.addAll(getSourceDocsToDelete(sources));
            docs = furtherDocs.getDocuments();
            for (int i = 0; i < docs.length; i++) {
                nodes.add(docs[i].getRepositoryNode());
            }

            nodes.add(SiteUtil.getSiteStructure(this.manager, doc).getRepositoryNode());
            nodes.add(SiteUtil.getSiteStructure(this.manager,
                    getDocumentIdentityMap(),
                    doc.getPublication(),
                    getTargetArea()).getRepositoryNode());
        } catch (Exception e) {
            throw new UsecaseException(e);
        }
        return (org.apache.lenya.cms.repository.Node[]) nodes.toArray(new org.apache.lenya.cms.repository.Node[nodes.size()]);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {

        Document doc = getSourceDocument();
        DocumentSet sources = SiteUtil.getSubSite(this.manager, doc);
        DocumentFactory map = getDocumentIdentityMap();

        DocumentLocator loc = doc.getLocator().getAreaVersion(getTargetArea());
        Document target = doc.getIdentityMap().get(loc);
        target = SiteUtil.getAvailableDocument(this.manager, target);

        DocumentSet docsToCopy = getTargetDocsToCopy();

        DocumentManager documentManager = null;
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {

            WorkflowUtil.invoke(this.manager, getSession(), getLogger(), sources, getEvent(), true);

            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);

            SiteUtil.sortAscending(this.manager, docsToCopy);
            Document[] targetDocs = docsToCopy.getDocuments();
            for (int i = 0; i < targetDocs.length; i++) {
                DocumentLocator sourceLoc = targetDocs[i].getLocator().getAreaVersion(doc.getArea());
                Document sourceDoc = map.get(sourceLoc);
                Document existingSourceDoc = DocumentHelper.getExistingLanguageVersion(sourceDoc,
                        doc.getLanguage());
                DocumentLocator targetLoc = existingSourceDoc.getLocator().getAreaVersion(getTargetArea());
                Document targetDoc = map.get(targetLoc);
                documentManager.copyDocument(existingSourceDoc, targetDoc);
                if (!targetDoc.getArea().equals(Publication.AUTHORING_AREA)) {
                    targetDoc.setPlaceholder();
                }
            }

            Map targetMap = SiteUtil.getTransferedSubSite(this.manager,
                    doc,
                    getTargetArea(),
                    SiteUtil.MODE_CHANGE_ID);
            DocumentSet targets = new DocumentSet();
            Document[] docs = sources.getDocuments();
            for (int i = 0; i < docs.length; i++) {
                targets.add((Document) targetMap.get(docs[i]));
            }
            documentManager.move(sources, targets);

            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(doc.getPublication().getSiteManagerHint());

            DocumentSet docsToDelete = getSourceDocsToDelete(sources);
            documentManager.delete(docsToDelete);

        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }

        setTargetDocument(target);

    }

    /**
     * @return All target documents that are required by the moved documents and have to be copied.
     * @throws ServiceException if an error occurs.
     * @throws SiteException if an error occurs.
     * @throws DocumentBuildException if an error occurs.
     */
    protected DocumentSet getTargetDocsToCopy() throws ServiceException, SiteException,
            DocumentBuildException {
        Document doc = getSourceDocument();
        DocumentFactory map = getDocumentIdentityMap();
        DocumentSet docsToCopy = new DocumentSet();
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(doc.getPublication().getSiteManagerHint());

            SiteNode node = NodeFactory.getNode(doc);
            SiteNode[] requiredNodes = siteManager.getRequiredResources(map, node);
            for (int i = 0; i < requiredNodes.length; i++) {
                Document targetDoc = map.get(getSourceDocument().getPublication(),
                        getTargetArea(),
                        requiredNodes[i].getPath(),
                        doc.getLanguage());
                if (!siteManager.containsInAnyLanguage(targetDoc)) {
                    docsToCopy.add(targetDoc);
                }
            }
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
        return docsToCopy;
    }

    /**
     * @param sources The sources to be moved.
     * @return All placeholder source documents that can be deleted..
     * @throws ServiceException if an error occurs.
     * @throws SiteException if an error occurs.
     * @throws DocumentBuildException if an error occurs.
     * @throws DocumentException
     */
    protected DocumentSet getSourceDocsToDelete(DocumentSet sources) throws ServiceException,
            SiteException, DocumentBuildException, DocumentException {
        DocumentSet docsToDelete = new DocumentSet();
        Document doc = getSourceDocument();
        DocumentFactory map = getDocumentIdentityMap();
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(doc.getPublication().getSiteManagerHint());

            NodeSet nodesToDelete = new NodeSet();

            SiteNode sourceNode = NodeFactory.getNode(doc);
            SiteNode[] requiredSourceNodes = siteManager.getRequiredResources(map, sourceNode);
            for (int i = 0; i < requiredSourceNodes.length; i++) {
                SiteNode node = requiredSourceNodes[i];
                boolean delete = true;

                Document requiredDoc = map.get(node.getPublication(), node.getArea(), node.getPath());
                String[] languages = requiredDoc.getLanguages();
                for (int l = 0; l < languages.length; l++) {
                    Document langVersion = map.getLanguageVersion(requiredDoc, languages[l]);
                    if (!sources.contains(langVersion) && !langVersion.isPlaceholder()) {
                        delete = false;
                    }
                }
                
                SiteNode[] requiringNodes = siteManager.getRequiringResources(map, node);
                
                for (int j = 0; j < requiringNodes.length; j++) {
                    SiteNode n = requiringNodes[j];
                    Document reqDoc = map.get(n.getPublication(), n.getArea(), n.getPath());
                    languages = reqDoc.getLanguages();
                    for (int l = 0; l < languages.length; l++) {
                        Document langVersion = map.getLanguageVersion(reqDoc, languages[l]);
                        if (!sources.contains(langVersion) && !langVersion.isPlaceholder()) {
                            delete = false;
                        }
                    }
                }
                if (delete) {
                    nodesToDelete.add(node);
                }
            }

            SiteNode[] nodes = nodesToDelete.getNodes();
            for (int i = 0; i < nodes.length; i++) {
                SiteNode n = nodes[i];
                Document d = map.get(n.getPublication(), n.getArea(), n.getPath());
                String[] languages = d.getLanguages();
                for (int l = 0; l < languages.length; l++) {
                    Document langVersion = map.getLanguageVersion(d, languages[l]);
                    docsToDelete.add(langVersion);
                }
            }
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
        return docsToDelete;
    }
    
}