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

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.metadata.LenyaMetaData;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.util.DocumentHelper;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.site.Node;
import org.apache.lenya.cms.site.NodeFactory;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.transaction.Transactionable;

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
            DocumentIdentityMap identityMap = getDocumentIdentityMap();

            DocumentSet set = SiteUtil.getSubSite(this.manager, document);
            Document[] documents = set.getDocuments();
            for (int i = 0; i < documents.length; i++) {
                Document liveVersion = identityMap.getAreaVersion(documents[i],
                        Publication.LIVE_AREA);
                if (liveVersion.exists()) {
                    addErrorMessage("delete-doc-live", new String[] { liveVersion.getId() });
                }
            }
            if (!WorkflowUtil.canInvoke(this.manager, getLogger(), set, getEvent())) {
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
     * <li>the trash versions of these documents</li>
     * <li>the document area's site structure</li>
     * <li>the trash site structure</li>
     * </ul>
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getObjectsToLock()
     */
    protected Transactionable[] getObjectsToLock() throws UsecaseException {
        List nodes = new ArrayList();
        Document doc = getSourceDocument();
        try {
            DocumentSet sources = SiteUtil.getSubSite(this.manager, doc);
            Document[] docs = sources.getDocuments();
            for (int i = 0; i < docs.length; i++) {
                nodes.addAll(Arrays.asList(docs[i].getRepositoryNodes()));
            }

            DocumentSet targets = SiteUtil.getTransferedSubSite(this.manager,
                    doc,
                    getTargetArea(),
                    SiteUtil.MODE_CHANGE_ID);
            targets.addAll(getTargetDocsToCopy());
            targets.addAll(getSourceDocsToDelete(sources));
            docs = targets.getDocuments();
            for (int i = 0; i < docs.length; i++) {
                nodes.addAll(Arrays.asList(docs[i].getRepositoryNodes()));
            }

            nodes.add(SiteUtil.getSiteStructure(this.manager, doc).getRepositoryNode());
            nodes.add(SiteUtil.getSiteStructure(this.manager, targets.getDocuments()[0])
                    .getRepositoryNode());
        } catch (Exception e) {
            throw new UsecaseException(e);
        }
        return (Transactionable[]) nodes.toArray(new Transactionable[nodes.size()]);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {

        Document doc = getSourceDocument();
        DocumentSet sources = SiteUtil.getSubSite(this.manager, doc);
        DocumentIdentityMap map = getDocumentIdentityMap();

        Document target = doc.getIdentityMap().getAreaVersion(doc, getTargetArea());
        target = SiteUtil.getAvailableDocument(this.manager, target);

        DocumentSet docsToCopy = getTargetDocsToCopy();

        DocumentManager documentManager = null;
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {

            WorkflowUtil.invoke(this.manager, getLogger(), sources, getEvent(), true);

            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);

            SiteUtil.sortAscending(this.manager, docsToCopy);
            Document[] targetDocs = docsToCopy.getDocuments();
            for (int i = 0; i < targetDocs.length; i++) {
                Document sourceDoc = map.getAreaVersion(targetDocs[i], doc.getArea());
                Document existingSourceDoc = DocumentHelper.getExistingLanguageVersion(sourceDoc,
                        doc.getLanguage());
                Document targetDoc = map.getAreaVersion(existingSourceDoc, getTargetArea());
                documentManager.copy(existingSourceDoc, targetDoc);
                if (!targetDoc.getArea().equals(Publication.AUTHORING_AREA)) {
                    LenyaMetaData meta = targetDoc.getMetaDataManager().getLenyaMetaData();
                    meta.setValue(LenyaMetaData.ELEMENT_PLACEHOLDER, "true");
                }
            }

            DocumentSet targets = SiteUtil.getTransferedSubSite(this.manager,
                    doc,
                    getTargetArea(),
                    SiteUtil.MODE_CHANGE_ID);
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
        DocumentIdentityMap map = getDocumentIdentityMap();
        DocumentSet docsToCopy = new DocumentSet();
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(doc.getPublication().getSiteManagerHint());

            Node node = NodeFactory.getNode(doc);
            Node[] requiredNodes = siteManager.getRequiredResources(map, node);
            for (int i = 0; i < requiredNodes.length; i++) {
                Document targetDoc = map.get(getSourceDocument().getPublication(),
                        getTargetArea(),
                        requiredNodes[i].getDocumentId(),
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
        DocumentIdentityMap map = getDocumentIdentityMap();
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(doc.getPublication().getSiteManagerHint());

            NodeSet nodesToDelete = new NodeSet();

            Node sourceNode = NodeFactory.getNode(doc);
            Node[] requiredSourceNodes = siteManager.getRequiredResources(map, sourceNode);
            for (int i = 0; i < requiredSourceNodes.length; i++) {
                Node node = requiredSourceNodes[i];
                boolean delete = true;

                Node[] requiringNodes = siteManager.getRequiringResources(map, node);
                for (int j = 0; j < requiringNodes.length; j++) {
                    Node n = requiringNodes[j];
                    Document reqDoc = map.get(n.getPublication(), n.getArea(), n.getDocumentId());
                    String[] languages = reqDoc.getLanguages();
                    for (int l = 0; l < languages.length; l++) {
                        Document langVersion = map.getLanguageVersion(reqDoc, languages[l]);
                        if (!sources.contains(langVersion)) {
                            LenyaMetaData meta = langVersion.getMetaDataManager()
                                    .getLenyaMetaData();
                            String placeholder = meta.getFirstValue(LenyaMetaData.ELEMENT_PLACEHOLDER);
                            if (placeholder == null || !placeholder.equals("true")) {
                                delete = false;
                            }
                        }
                    }
                }
                if (delete) {
                    nodesToDelete.add(node);
                }
            }

            Node[] nodes = nodesToDelete.getNodes();
            for (int i = 0; i < nodes.length; i++) {
                Node n = nodes[i];
                Document d = map.get(n.getPublication(), n.getArea(), n.getDocumentId());
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