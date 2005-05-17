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
package org.apache.lenya.cms.publication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.lenya.cms.authoring.NodeCreatorInterface;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.LenyaMetaData;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.workflow.WorkflowManager;
import org.apache.lenya.transaction.Transactionable;

/**
 * DocumentManager implementation.
 * 
 * @version $Id$
 */
public class DocumentManagerImpl extends AbstractLogEnabled implements DocumentManager,
        Serviceable, Contextualizable {

    /**
     * The instance of Document will be built by the implementation of DocumentBuilder; the physical
     * representation will be built by the implementation of NodeCreatorInterface, where the
     * implementation to be used is specified in doctypes.xconf (and thus depends on the publication
     * and the resource type to be used)
     * 
     * @see DocumentManager#add(Document,String,String,String,String,String,String,short,Map)
     * @see org.apache.lenya.cms.authoring.NodeCreatorInterface
     * @see org.apache.lenya.cms.publication.DocumentBuilder
     */
    public Document add(Document parentDocument, String newDocumentNodeName, String newDocumentId,
            String documentTypeName, String language, String navigationTitle,
            String initialContentsURI, short nodeType, Map parameters)
            throws DocumentBuildException, PublicationException {

        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentManagerImpl::add() called with:\n"
                    + "\t parentDocument.getId() [" + (parentDocument != null ? parentDocument.getId() : "null") + "]\n"
                    + "\t newDocumentNodeName [" + newDocumentNodeName + "]\n"
                    + "\t newDocumentId [" + newDocumentId + "]\n" + "\t documentTypeName ["
                    + documentTypeName + "]\n" + "\t language [" + language + "]\n"
                    + "\t navigationTitle [" + navigationTitle + "]\n" + "\t initialContentsURI ["
                    + initialContentsURI + "]\n" + "\t nodeType [" + nodeType + "]\n"
                    + "\t non-empty parameters [" + (parameters != null) + "]\n");

        Publication publication = parentDocument.getPublication();
        DocumentIdentityMap map = parentDocument.getIdentityMap();
        String area = parentDocument.getArea();

        /*
         * Get an instance of Document. This will (ultimately) be created by the implementation for
         * the DocumentBuilder role.
         */
        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentManagerImpl::add() creating Document instance");
        Document newDocument = map.get(publication, area, newDocumentId, language);

        if (getLogger().isDebugEnabled())
            getLogger()
                    .debug("DocumentManagerImpl::add() looking up a DocumentTypeBuilder so that we can call the creator");

        // Get an instance of DocumentType
        DocumentTypeBuilder documentTypeBuilder = null;
        DocumentType documentType = null;
        try {
            documentTypeBuilder = (DocumentTypeBuilder) this.manager
                    .lookup(DocumentTypeBuilder.ROLE);

            documentType = documentTypeBuilder.buildDocumentType(documentTypeName, publication);
        } catch (Exception e) {
            throw new DocumentBuildException("could not build type for new document", e);
        } finally {
            if (documentTypeBuilder != null) {
                this.manager.release(documentTypeBuilder);
            }
        }

        // Call the creator for the document type to physically create a document of this type
        try {

            if (initialContentsURI == null)
                initialContentsURI = documentType.getSampleContentLocation();

            if (getLogger().isDebugEnabled())
                getLogger().debug("DocumentManagerImpl::add() using initialContentsURI ["
                        + initialContentsURI + "]");

            // look up creator for documents of this type
            NodeCreatorInterface creator = documentType.getCreator();

            // the concrete creator implementation decides
            // where, relative to content base (and potentially to the parent
            // as well), the new document shall be created
            String contentBaseURI = publication.getContentURI(area);
            String parentId = (parentDocument != null ? parentDocument.getId() : "/");
            String newDocumentURI = creator.getNewDocumentURI(contentBaseURI, parentId, newDocumentNodeName, language);

            // Important note:
            // how the new document's source URI is constructed is
            // publication dependant; this is handled through the creator
            // for that type in that publication.
            // Therefore, we must ask the creator what this URI is
            // and set it in the document.
            newDocument.setSourceURI(newDocumentURI);

            // now that the source is determined, lock involved nodes
            Transactionable[] nodes = newDocument.getRepositoryNodes();
            for (int i = 0; i < nodes.length; i++) {
                nodes[i].lock();
            }

            //
            creator.create(initialContentsURI,
                    newDocumentURI,
                    newDocumentNodeName,
                    nodeType,
                    navigationTitle,
                    parameters);
        } catch (Exception e) {
            throw new DocumentBuildException("call to creator for new document failed", e);
        } finally {
            if (documentTypeBuilder != null) {
                this.manager.release(documentTypeBuilder);
            }
        }

        // Write Lenya-internal meta-data
        Map lenyaMetaData = new HashMap(2);
        lenyaMetaData.put(LenyaMetaData.ELEMENT_RESOURCE_TYPE, documentTypeName);
        lenyaMetaData.put(LenyaMetaData.ELEMENT_CONTENT_TYPE, "xml");
        newDocument.getMetaDataManager().setLenyaMetaData(lenyaMetaData);

        // Notify site manager about new document
        addToSiteManager(newDocument, navigationTitle);

        return newDocument;
    }

    private void addToSiteManager(Document document, String navigationTitle)
            throws PublicationException {
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
            siteManager.setLabel(document, navigationTitle);
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

        ResourcesManager resourcesManager = null;
        WorkflowManager workflowManager = null;
        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            resourcesManager = (ResourcesManager) this.manager.lookup(ResourcesManager.ROLE);
            resourcesManager.copyResources(sourceDocument, destinationDocument);
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            siteManager.copy(sourceDocument, destinationDocument);

            workflowManager = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
            workflowManager.copyHistory(sourceDocument, destinationDocument);
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (workflowManager != null) {
                this.manager.release(workflowManager);
            }
            if (resourcesManager != null) {
                this.manager.release(resourcesManager);
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

        SiteManager siteManager = null;
        ServiceSelector selector = null;
        WorkflowManager workflowManager = null;
        ResourcesManager resourcesManager = null;
        try {
            resourcesManager = (ResourcesManager) this.manager.lookup(ResourcesManager.ROLE);
            resourcesManager.deleteResources(document);
            workflowManager = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
            workflowManager.deleteHistory(document);

            Publication publication = document.getPublication();
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            siteManager.delete(document);

            document.delete();
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (workflowManager != null) {
                this.manager.release(workflowManager);
            }
            if (resourcesManager != null) {
                this.manager.release(resourcesManager);
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

        ResourcesManager resourcesManager = null;
        WorkflowManager workflowManager = null;
        try {
            resourcesManager = (ResourcesManager) this.manager.lookup(ResourcesManager.ROLE);
            resourcesManager.copyResources(sourceDocument, destinationDocument);
            resourcesManager.deleteResources(sourceDocument);

            workflowManager = (WorkflowManager) this.manager.lookup(WorkflowManager.ROLE);
            workflowManager.moveHistory(sourceDocument, destinationDocument);
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (workflowManager != null) {
                this.manager.release(workflowManager);
            }
            if (resourcesManager != null) {
                this.manager.release(resourcesManager);
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
     *      org.apache.lenya.cms.publication.Publication, java.lang.String,
     *      org.apache.lenya.cms.publication.Document, java.lang.String, java.lang.String)
     */
    public String[] canCreate(DocumentIdentityMap identityMap, Publication publication,
            String area, Document parent, String nodeId, String language)
            throws DocumentBuildException, DocumentException {

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
            Document newDocument = identityMap.get(publication, area, newDocumentId, language);

            if (newDocument.exists()) {
                errorMessages.add("A document with this ID already exists.");
            }
        } else {
            errorMessages.add("This document ID is not valid.");
        }

        return (String[]) errorMessages.toArray(new String[errorMessages.size()]);
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

            destinationDocument.getMetaDataManager().replaceMetaData(sourceDocument
                    .getMetaDataManager());

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

        /**
         * @return the root source
         */
        protected Document getRootSource() {
            return rootSource;
        }

        /**
         * @return the root target
         */
        protected Document getRootTarget() {
            return rootTarget;
        }

        /**
         * @return the document manager
         */
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

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#move(org.apache.lenya.cms.publication.util.DocumentSet,
     *      org.apache.lenya.cms.publication.util.DocumentSet)
     */
    public void move(DocumentSet sources, DocumentSet destinations) throws PublicationException {
        Document[] sourceDocs = sources.getDocuments();
        Document[] targetDocs = destinations.getDocuments();

        if (sourceDocs.length != targetDocs.length) {
            throw new PublicationException(
                    "The number of source and destination documents must be equal!");
        }

        Map source2target = new HashMap();
        for (int i = 0; i < sourceDocs.length; i++) {
            source2target.put(sourceDocs[i], targetDocs[i]);
        }

        DocumentSet sortedSources = new DocumentSet(sourceDocs);
        SiteUtil.sortAscending(this.manager, sortedSources);
        Document[] sortedSourceDocs = sortedSources.getDocuments();

        for (int i = 0; i < sortedSourceDocs.length; i++) {
            move(sortedSourceDocs[i], (Document) source2target.get(sortedSourceDocs[i]));
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#copy(org.apache.lenya.cms.publication.util.DocumentSet,
     *      org.apache.lenya.cms.publication.util.DocumentSet)
     */
    public void copy(DocumentSet sources, DocumentSet destinations) throws PublicationException {
        Document[] sourceDocs = sources.getDocuments();
        Document[] targetDocs = destinations.getDocuments();

        if (sourceDocs.length != targetDocs.length) {
            throw new PublicationException(
                    "The number of source and destination documents must be equal!");
        }

        Map source2target = new HashMap();
        for (int i = 0; i < sourceDocs.length; i++) {
            source2target.put(sourceDocs[i], targetDocs[i]);
        }

        DocumentSet sortedSources = new DocumentSet(sourceDocs);
        SiteUtil.sortAscending(this.manager, sortedSources);
        Document[] sortedSourceDocs = sortedSources.getDocuments();

        for (int i = 0; i < sortedSourceDocs.length; i++) {
            copy(sortedSourceDocs[i], (Document) source2target.get(sortedSourceDocs[i]));
        }
    }
}
