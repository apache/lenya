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
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.ModifiableSource;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.publication.util.OrderedDocumentSet;
import org.apache.lenya.cms.publication.util.UniqueDocumentId;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.workflow.WorkflowResolver;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.lenya.workflow.WorkflowInstance;

/**
 * Abstract DocumentManager implementation.
 * 
 * @version $Id:$
 */
public class DocumentManagerImpl extends AbstractLogEnabled implements
        DocumentManager, Serviceable, Contextualizable {

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#addDocument(org.apache.lenya.cms.publication.Document)
     */
    public void addDocument(Document document) throws PublicationException {

        Publication publication = document.getPublication();
        SiteManager siteManager = publication.getSiteManager(document.getIdentityMap());
        if (siteManager.contains(document)) {
            throw new PublicationException("The document [" + document
                    + "] is already contained in this publication!");
        }

        siteManager.add(document);

        DocumentTypeResolver doctypeResolver = null;
        WorkflowResolver workflowResolver = null;
        try {
            doctypeResolver = (DocumentTypeResolver) this.manager.lookup(DocumentTypeResolver.ROLE);
            workflowResolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);

            DocumentType doctype = doctypeResolver.resolve(document);

            if (doctype.hasWorkflow()) {
                Situation situation = workflowResolver.getSituation();
                workflowResolver.getWorkflowInstance(document).getHistory().initialize(situation);
            }

        } catch (ServiceException e) {
            throw new PublicationException(e);
        } catch (WorkflowException e) {
            throw new PublicationException(e);
        } finally {
            if (doctypeResolver != null) {
                this.manager.release(doctypeResolver);
            }
        }
    }

    /**
     * Template method to copy a document. Override
     * {@link #copyDocumentSource(Document, Document)}to implement access to a
     * custom repository.
     * @see org.apache.lenya.cms.publication.DocumentManager#copyDocument(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copyDocument(Document sourceDocument, Document destinationDocument)
            throws PublicationException {

        Publication publication = sourceDocument.getPublication();
        copyDocumentSource(sourceDocument, destinationDocument);
        publication.getSiteManager(sourceDocument.getIdentityMap()).copy(sourceDocument,
                destinationDocument);

        ResourcesManager resourcesManager = sourceDocument.getResourcesManager();
        WorkflowResolver workflowResolver = null;
        try {
            resourcesManager.copyResourcesTo(destinationDocument);
            
            workflowResolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            copyWorkflow(workflowResolver, sourceDocument, destinationDocument);
        } catch (Exception e) {
            throw new PublicationException(e);
        }
        finally {
            if (workflowResolver != null) {
                this.manager.release(workflowResolver);
            }
        }
    }

    /**
     * Moves the workflow history of a document.
     * @param resolver The workflow resolver.
     * @param sourceDocument The source document.
     * @param destinationDocument The destination document.
     * @throws WorkflowException if an error occurs.
     */
    protected void moveWorkflow(WorkflowResolver resolver, Document sourceDocument,
            Document destinationDocument) throws WorkflowException {
        copyWorkflow(resolver, sourceDocument, destinationDocument);
        deleteWorkflow(resolver, sourceDocument);
    }

    /**
     * Deletes the workflow history of a document.
     * @param resolver The workflow resolver.
     * @param sourceDocument The source document.
     * @throws WorkflowException if an error occurs.
     */
    protected void deleteWorkflow(WorkflowResolver resolver, Document sourceDocument)
            throws WorkflowException {
        if (resolver.hasWorkflow(sourceDocument)) {
            WorkflowInstance sourceInstance = resolver.getWorkflowInstance(sourceDocument);
            sourceInstance.getHistory().delete();
        }
    }

    /**
     * Copies the workflow history of a document.
     * @param resolver The workflow resolver.
     * @param sourceDocument The source document.
     * @param destinationDocument The destination document.
     * @throws WorkflowException if an error occurs.
     */
    protected void copyWorkflow(WorkflowResolver resolver, Document sourceDocument,
            Document destinationDocument) throws WorkflowException {
        if (resolver.hasWorkflow(sourceDocument)) {
            WorkflowInstance sourceInstance = resolver.getWorkflowInstance(sourceDocument);

            WorkflowInstance destinationInstance = resolver
                    .getWorkflowInstance(destinationDocument);
            destinationInstance.getHistory().replaceWith(sourceInstance.getHistory());
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#deleteDocument(org.apache.lenya.cms.publication.Document)
     */
    public void deleteDocument(Document document) throws PublicationException {
        if (!document.exists()) {
            throw new PublicationException("Document [" + document + "] does not exist!");
        }
        Publication publication = document.getPublication();
        publication.getSiteManager(document.getIdentityMap()).delete(document);
        deleteDocumentSource(document);

        ResourcesManager resourcesManager = document.getResourcesManager();
        resourcesManager.deleteResources();

        WorkflowResolver workflowResolver = null;
        try {
            workflowResolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            deleteWorkflow(workflowResolver, document);
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (workflowResolver != null) {
                this.manager.release(workflowResolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#moveDocument(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void moveDocument(Document sourceDocument, Document destinationDocument)
            throws PublicationException {
        copyDocument(sourceDocument, destinationDocument);
        deleteDocument(sourceDocument);

        ResourcesManager resourcesManager = sourceDocument.getResourcesManager();
        WorkflowResolver workflowResolver = null;
        try {
            resourcesManager.copyResourcesTo(destinationDocument);
            resourcesManager.deleteResources();

            workflowResolver = (WorkflowResolver) this.manager.lookup(WorkflowResolver.ROLE);
            moveWorkflow(workflowResolver, sourceDocument, destinationDocument);
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (workflowResolver != null) {
                this.manager.release(workflowResolver);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#copyDocumentToArea(org.apache.lenya.cms.publication.Document,
     *      java.lang.String)
     */
    public void copyDocumentToArea(Document sourceDocument, String destinationArea)
            throws PublicationException {
        Publication publication = sourceDocument.getPublication();
        Document destinationDocument = publication.getAreaVersion(sourceDocument, destinationArea);
        copyDocument(sourceDocument, destinationDocument);
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#copyDocumentSetToArea(org.apache.lenya.cms.publication.util.DocumentSet,
     *      java.lang.String)
     */
    public void copyDocumentSetToArea(DocumentSet documentSet, String destinationArea)
            throws PublicationException {
        Document[] documents = documentSet.getDocuments();
        for (int i = 0; i < documents.length; i++) {
            copyDocumentToArea(documents[i], destinationArea);
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
     *      java.lang.String, org.apache.lenya.cms.publication.Document,
     *      java.lang.String, java.lang.String)
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
        } else if (identityMap.getFactory().isValidDocumentId(newDocumentId)) {
            Document newDocument = identityMap.getFactory().get(area, newDocumentId, language);

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
        UniqueDocumentId uniqueDocumentId = new UniqueDocumentId();
        String availableDocumentId = uniqueDocumentId.computeUniqueDocumentId(document
                .getPublication(), document.getArea(), document.getId());
        DocumentFactory factory = document.getIdentityMap().getFactory();
        Document availableDocument = factory.get(document.getArea(), availableDocumentId, document
                .getLanguage());
        return availableDocument;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#moveAll(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void moveAll(Document source, Document target) throws PublicationException {
        DocumentIdentityMap identityMap = source.getIdentityMap();
        SiteManager manager = identityMap.getPublication().getSiteManager(identityMap);
        Document[] descendantsArray = manager.getRequiringResources(source);
        OrderedDocumentSet descendants = new OrderedDocumentSet(descendantsArray);
        descendants.add(source);

        DocumentVisitor visitor = new MoveVisitor(this, source, target);
        descendants.visitAscending(visitor);
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#moveAllLanguageVersions(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void moveAllLanguageVersions(Document source, Document target)
            throws PublicationException {
        DocumentIdentityMap identityMap = source.getIdentityMap();
        String[] languages = source.getLanguages();
        for (int i = 0; i < languages.length; i++) {

            Document sourceVersion = identityMap.getFactory().getLanguageVersion(source,
                    languages[i]);
            Document targetVersion = identityMap.getFactory().get(target.getArea(),
                    target.getId(),
                    languages[i]);
            moveDocument(sourceVersion, targetVersion);
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#copyAll(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copyAll(Document source, Document target) throws PublicationException {
        DocumentIdentityMap identityMap = source.getIdentityMap();
        SiteManager manager = identityMap.getPublication().getSiteManager(identityMap);
        Document[] descendantsArray = manager.getRequiringResources(source);
        OrderedDocumentSet descendants = new OrderedDocumentSet(descendantsArray);
        descendants.add(source);

        DocumentVisitor visitor = new CopyVisitor(this, source, target);
        descendants.visitAscending(visitor);
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

            Document sourceVersion = identityMap.getFactory().getLanguageVersion(source,
                    languages[i]);
            Document targetVersion = identityMap.getFactory().get(target.getArea(),
                    target.getId(),
                    languages[i]);
            copyDocument(sourceVersion, targetVersion);
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
        try {
            sourceResolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            Source source = sourceResolver.resolveURI(sourceDocument.getSourceURI());
            Source destination = sourceResolver.resolveURI(destinationDocument.getSourceURI());
            SourceUtil.copy(source, (ModifiableSource) destination, true);
            destinationDocument.getDublinCore().replaceBy(sourceDocument.getDublinCore());
        } catch (Exception e) {
            throw new PublicationException(e);
        }
        finally {
            if (sourceResolver != null) {
                this.manager.release(sourceResolver);
            }
        }
    }

    /**
     * Deletes the source of a document.
     * @param document The document to delete.
     * @throws PublicationException when something went wrong.
     */
    protected void deleteDocumentSource(Document document) throws PublicationException {
        
        SourceResolver sourceResolver = null;
        try {
            sourceResolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            Source source = sourceResolver.resolveURI(document.getSourceURI());
            ((ModifiableSource) source).delete();
        } catch (Exception e) {
            throw new PublicationException(e);
        }
        finally {
            if (sourceResolver != null) {
                this.manager.release(sourceResolver);
            }
        }
    }
    
    /**
     * Abstract base class for document visitors which operate on a source and
     * target document.
     */
    public abstract class Visitor implements DocumentVisitor {

        private Document rootSource;
        private Document rootTarget;
        private DocumentManager manager;

        /**
         * Ctor.
         * @param manager The document manager.
         * @param source The root source.
         * @param target The root target.
         */
        public Visitor(DocumentManager manager, Document source, Document target) {
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
         * Returns the target corresponding to a source relatively to the root
         * target document.
         * @param source The source.
         * @return A document.
         * @throws DocumentBuildException if the target could not be built.
         */
        protected Document getTarget(Document source) throws DocumentBuildException {
            String rootSourceId = getRootSource().getId();
            String rootTargetId = getRootTarget().getId();
            String childId = source.getId().substring(rootSourceId.length());
            String targetId = rootTargetId + childId;
            DocumentFactory factory = getRootTarget().getIdentityMap().getFactory();
            return factory.get(getRootTarget().getArea(), targetId, source.getLanguage());
        }
    }

    /**
     * DocumentVisitor to move documents.
     */
    public class MoveVisitor extends Visitor {

        /**
         * Ctor.
         * @param manager The document manager.
         * @param source The root source.
         * @param target The root target.
         */
        public MoveVisitor(DocumentManager manager, Document source, Document target) {
            super(manager, source, target);
        }

        /**
         * @see org.apache.lenya.cms.publication.util.DocumentVisitor#visitDocument(org.apache.lenya.cms.publication.Document)
         */
        public void visitDocument(Document source) throws PublicationException {
            Document target = getTarget(source);
            getDocumentManager().moveAllLanguageVersions(source, target);
        }

    }

    /**
     * DocumentVisitor to copy documents.
     */
    public class CopyVisitor extends Visitor {

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

}