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
package org.apache.lenya.cms.publication;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.lenya.cms.site.tree.TreeSiteManager;
import org.apache.lenya.cms.workflow.WorkflowManager;
import org.apache.lenya.transaction.UnitOfWork;

/**
 * Abstract DocumentManager implementation.
 * 
 * @version $Id:$
 */
public class DocumentManagerImpl extends AbstractLogEnabled implements DocumentManager,
        Serviceable, Contextualizable {

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#add(org.apache.lenya.cms.publication.Document)
     */
    public void add(Document document) throws PublicationException {

        Publication publication = document.getPublication();
        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            if (siteManager.contains(document)) {
                throw new PublicationException("The document [" + document
                        + "] is already contained in this publication!");
            }

            siteManager.add(document);
        } catch (ServiceException e) {
            throw new PublicationException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
    }

    /**
     * Template method to copy a document. Override {@link #copyDocumentSource(Document, Document)}
     * to implement access to a custom repository.
     * @see org.apache.lenya.cms.publication.DocumentManager#copy(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copy(Document sourceDocument, Document destinationDocument)
            throws PublicationException {

        Publication publication = sourceDocument.getPublication();
        copyDocumentSource(sourceDocument, destinationDocument);

        ResourcesManager resourcesManager = sourceDocument.getResourcesManager();
        WorkflowManager workflowManager = null;
        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            siteManager.copy(sourceDocument, destinationDocument);

            resourcesManager.copyResourcesTo(destinationDocument);

            workflowManager = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
            workflowManager.copyHistory(sourceDocument, destinationDocument);
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (workflowManager != null) {
                this.manager.release(workflowManager);
            }
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#delete(org.apache.lenya.cms.publication.Document)
     */
    public void delete(Document document) throws PublicationException {
        if (!document.exists()) {
            throw new PublicationException("Document [" + document + "] does not exist!");
        }

        ResourcesManager resourcesManager = document.getResourcesManager();
        resourcesManager.deleteResources();

        SiteManager siteManager = null;
        ServiceSelector selector = null;
        WorkflowManager workflowManager = null;
        try {
            workflowManager = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
            workflowManager.deleteHistory(document);

            Publication publication = document.getPublication();
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            siteManager.delete(document);
            
            UnitOfWork unit = document.getIdentityMap().getUnitOfWork();
            unit.registerRemoved(document);
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (workflowManager != null) {
                this.manager.release(workflowManager);
            }
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#move(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void move(Document sourceDocument, Document destinationDocument)
            throws PublicationException {
        copy(sourceDocument, destinationDocument);

        ResourcesManager resourcesManager = sourceDocument.getResourcesManager();
        WorkflowManager workflowManager = null;
        try {
            resourcesManager.copyResourcesTo(destinationDocument);
            resourcesManager.deleteResources();

            workflowManager = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
            workflowManager.moveHistory(sourceDocument, destinationDocument);
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (workflowManager != null) {
                this.manager.release(workflowManager);
            }
        }

        delete(sourceDocument);
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#copyToArea(org.apache.lenya.cms.publication.Document,
     *      java.lang.String)
     */
    public void copyToArea(Document sourceDocument, String destinationArea)
            throws PublicationException {
        Document destinationDocument = sourceDocument.getIdentityMap()
                .getAreaVersion(sourceDocument, destinationArea);
        copy(sourceDocument, destinationDocument);
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#copyToArea(org.apache.lenya.cms.publication.util.DocumentSet,
     *      java.lang.String)
     */
    public void copyToArea(DocumentSet documentSet, String destinationArea)
            throws PublicationException {
        Document[] documents = documentSet.getDocuments();
        for (int i = 0; i < documents.length; i++) {
            copyToArea(documents[i], destinationArea);
        }
    }

    protected ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    private Context context;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        this.context = context;
    }

    /**
     * @return The Avalon context.
     */
    protected Context getContext() {
        return this.context;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#canCreate(org.apache.lenya.cms.publication.DocumentIdentityMap,
     *      java.lang.String, org.apache.lenya.cms.publication.Document, java.lang.String,
     *      java.lang.String)
     */
    public String[] canCreate(DocumentIdentityMap identityMap, String area, Document parent,
            String nodeId, String language) throws DocumentBuildException, DocumentException {

        List errorMessages = new ArrayList();

        String newDocumentId;
        if (parent != null) {
            newDocumentId = parent.getId() + "/" + nodeId;
        } else {
            newDocumentId = "/" + nodeId;
        }

        if (nodeId.equals("")) {
            errorMessages.add("The document ID is required.");
        } else if (nodeId.indexOf("/") > -1) {
            errorMessages.add("The document ID may not contain a slash ('/').");
        } else if (identityMap.isValidDocumentId(newDocumentId)) {
            Document newDocument = identityMap.get(parent.getPublication(),
                    area,
                    newDocumentId,
                    language);

            if (newDocument.exists()) {
                errorMessages.add("A document with this ID already exists.");
            }
        } else {
            errorMessages.add("This document ID is not valid.");
        }

        return (String[]) errorMessages.toArray(new String[errorMessages.size()]);
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#getAvailableDocument(org.apache.lenya.cms.publication.Document)
     */
    public Document getAvailableDocument(Document document) throws DocumentBuildException {
        String availableDocumentId = computeUniqueDocumentId(document);
        Document availableDocument = document.getIdentityMap().get(document.getPublication(),
                document.getArea(),
                availableDocumentId,
                document.getLanguage());
        return availableDocument;
    }

    /**
     * compute an unique document id
     * @param document The document.
     * @return the unique documentid
     */
    protected String computeUniqueDocumentId(Document document) {
        String documentId = document.getId();

        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            Publication pub = document.getPublication();
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(pub.getSiteManagerHint());

            if (!(siteManager instanceof TreeSiteManager)) {
                throw new RuntimeException("Only supported for site trees!");
            }
            DocumentIdentityMap map = document.getIdentityMap();
            SiteTree tree = ((TreeSiteManager) siteManager).getTree(map, pub, document.getArea());

            SiteTreeNode node = tree.getNode(documentId);
            String suffix = null;
            int version = 0;
            String idwithoutsuffix = null;

            if (node != null) {
                int n = documentId.lastIndexOf("/");
                String lastToken = "";
                String substring = documentId;
                if ((n < documentId.length()) && (n > 0)) {
                    lastToken = documentId.substring(n);
                    substring = documentId.substring(0, n);
                }

                int l = lastToken.length();
                int index = lastToken.lastIndexOf("-");
                if (0 < index && index < l) {
                    suffix = lastToken.substring(index + 1);
                    idwithoutsuffix = substring + lastToken.substring(0, index);
                    version = Integer.parseInt(suffix);
                } else {
                    idwithoutsuffix = substring + lastToken;
                }

                while (node != null) {
                    version = version + 1;
                    documentId = idwithoutsuffix + "-" + version;
                    node = tree.getNode(documentId);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }

        return documentId;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#moveAll(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void moveAll(Document source, Document target) throws PublicationException {
        copyAll(source, target);
        deleteAll(source);
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#moveAllLanguageVersions(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void moveAllLanguageVersions(Document source, Document target)
            throws PublicationException {
        copyAllLanguageVersions(source, target);
        deleteAllLanguageVersions(source);
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#copyAll(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copyAll(Document source, Document target) throws PublicationException {

        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(source.getPublication()
                    .getSiteManagerHint());

            Document[] descendantsArray = siteManager.getRequiringResources(source);
            DocumentSet descendants = new DocumentSet(descendantsArray);
            descendants.add(source);
            siteManager.sortAscending(descendants);

            DocumentVisitor visitor = new CopyVisitor(this, source, target);
            descendants.visit(visitor);
        } catch (ServiceException e) {
            throw new PublicationException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#copyAllLanguageVersions(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copyAllLanguageVersions(Document source, Document target)
            throws PublicationException {
        DocumentIdentityMap identityMap = source.getIdentityMap();
        String[] languages = source.getLanguages();
        for (int i = 0; i < languages.length; i++) {

            Document sourceVersion = identityMap.getLanguageVersion(source, languages[i]);
            Document targetVersion = identityMap.get(target.getPublication(),
                    target.getArea(),
                    target.getId(),
                    languages[i]);
            copy(sourceVersion, targetVersion);
        }
    }

    /**
     * Copies a document source.
     * @param sourceDocument The source document.
     * @param destinationDocument The destination document.
     * @throws PublicationException when something went wrong.
     */
    public void copyDocumentSource(Document sourceDocument, Document destinationDocument)
            throws PublicationException {

        SourceResolver sourceResolver = null;
        Source source = null;
        Source destination = null;
        try {
            sourceResolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = sourceResolver.resolveURI(sourceDocument.getSourceURI());
            destination = sourceResolver.resolveURI(destinationDocument.getSourceURI());
            SourceUtil.copy(source, (ModifiableSource) destination, true);
            destinationDocument.getDublinCore().replaceBy(sourceDocument.getDublinCore());
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (sourceResolver != null) {
                if (source != null) {
                    sourceResolver.release(source);
                }
                if (destination != null) {
                    sourceResolver.release(destination);
                }
                this.manager.release(sourceResolver);
            }
        }
    }

    /**
     * Abstract base class for document visitors which operate on a source and target document.
     */
    public abstract class SourceTargetVisitor implements DocumentVisitor {

        private Document rootSource;
        private Document rootTarget;
        private DocumentManager manager;

        /**
         * Ctor.
         * @param manager The document manager.
         * @param source The root source.
         * @param target The root target.
         */
        public SourceTargetVisitor(DocumentManager manager, Document source, Document target) {
            this.manager = manager;
            this.rootSource = source;
            this.rootTarget = target;
        }

        protected Document getRootSource() {
            return rootSource;
        }

        protected Document getRootTarget() {
            return rootTarget;
        }

        protected DocumentManager getDocumentManager() {
            return this.manager;
        }

        /**
         * Returns the target corresponding to a source relatively to the root target document.
         * @param source The source.
         * @return A document.
         * @throws DocumentBuildException if the target could not be built.
         */
        protected Document getTarget(Document source) throws DocumentBuildException {
            String rootSourceId = getRootSource().getId();
            String rootTargetId = getRootTarget().getId();
            String childId = source.getId().substring(rootSourceId.length());
            String targetId = rootTargetId + childId;
            return getRootTarget().getIdentityMap().get(getRootTarget().getPublication(),
                    getRootTarget().getArea(),
                    targetId,
                    source.getLanguage());
        }
    }

    /**
     * DocumentVisitor to copy documents.
     */
    public class CopyVisitor extends SourceTargetVisitor {

        /**
         * Ctor.
         * @param manager The document manager.
         * @param source The root source.
         * @param target The root target.
         */
        public CopyVisitor(DocumentManager manager, Document source, Document target) {
            super(manager, source, target);
        }

        /**
         * @see org.apache.lenya.cms.publication.util.DocumentVisitor#visitDocument(org.apache.lenya.cms.publication.Document)
         */
        public void visitDocument(Document source) throws PublicationException {
            Document target = getTarget(source);
            getDocumentManager().copyAllLanguageVersions(source, target);
        }

    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#deleteAll(org.apache.lenya.cms.publication.Document)
     */
    public void deleteAll(Document document) throws PublicationException {

        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(document.getPublication()
                    .getSiteManagerHint());

            Document[] descendantsArray = siteManager.getRequiringResources(document);
            DocumentSet descendants = new DocumentSet(descendantsArray);
            descendants.add(document);
            delete(descendants);
        } catch (ServiceException e) {
            throw new PublicationException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#deleteAllLanguageVersions(org.apache.lenya.cms.publication.Document)
     */
    public void deleteAllLanguageVersions(Document document) throws PublicationException {
        DocumentIdentityMap identityMap = document.getIdentityMap();
        String[] languages = document.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            Document version = identityMap.getLanguageVersion(document, languages[i]);
            delete(version);
        }
    }

    /**
     * Visitor to delete documents.
     */
    public class DeleteVisitor implements DocumentVisitor {

        private DocumentManager manager;

        /**
         * Ctor.
         * @param manager The document manager.
         */
        public DeleteVisitor(DocumentManager manager) {
            this.manager = manager;
        }

        protected DocumentManager getDocumentManager() {
            return this.manager;
        }

        /**
         * @see org.apache.lenya.cms.publication.util.DocumentVisitor#visitDocument(org.apache.lenya.cms.publication.Document)
         */
        public void visitDocument(Document document) throws PublicationException {
            getDocumentManager().deleteAllLanguageVersions(document);
        }

    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#delete(org.apache.lenya.cms.publication.util.DocumentSet)
     */
    public void delete(DocumentSet documents) throws PublicationException {

        if (documents.isEmpty()) {
            return;
        }

        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            Publication pub = documents.getDocuments()[0].getPublication();
            siteManager = (SiteManager) selector.select(pub.getSiteManagerHint());

            DocumentSet set = new DocumentSet(documents.getDocuments());
            siteManager.sortAscending(set);
            set.reverse();

            DocumentVisitor visitor = new DeleteVisitor(this);
            set.visit(visitor);
        } catch (ServiceException e) {
            throw new PublicationException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }

    }
}