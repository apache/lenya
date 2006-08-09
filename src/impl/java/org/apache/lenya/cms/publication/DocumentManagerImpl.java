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

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryManager;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.repository.UUIDGenerator;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteUtil;

/**
 * DocumentManager implementation.
 * 
 * @version $Id$
 */
public class DocumentManagerImpl extends AbstractLogEnabled implements DocumentManager,
        Serviceable, Contextualizable {

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#add(org.apache.lenya.cms.publication.Document,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, boolean)
     */
    public Document add(Document sourceDocument, String area, String path, String language,
            String extension, String navigationTitle, boolean visibleInNav)
            throws DocumentBuildException, PublicationException {

        Document document = add(sourceDocument.getFactory(),
                sourceDocument.getResourceType(),
                sourceDocument.getSourceURI(),
                sourceDocument.getPublication(),
                area,
                path,
                language,
                extension,
                navigationTitle,
                visibleInNav);

        copyMetaData(sourceDocument, document);
        return document;
    }

    protected void copyMetaData(Document sourceDocument, Document document)
            throws PublicationException {
        try {
            String[] uris = sourceDocument.getMetaDataNamespaceUris();
            for (int i = 0; i < uris.length; i++) {
                document.getMetaData(uris[i]).replaceBy(sourceDocument.getMetaData(uris[i]));
            }
        } catch (MetaDataException e) {
            throw new PublicationException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#add(org.apache.lenya.cms.publication.DocumentFactory,
     *      org.apache.lenya.cms.publication.ResourceType, java.lang.String,
     *      org.apache.lenya.cms.publication.Publication, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public Document add(DocumentFactory factory, ResourceType documentType,
            String initialContentsURI, Publication pub, String area, String path, String language,
            String extension, String navigationTitle, boolean visibleInNav)
            throws DocumentBuildException, DocumentException, PublicationException {

        Document document = add(factory,
                documentType,
                initialContentsURI,
                pub,
                area,
                language,
                extension);

        addToSiteManager(path, document, navigationTitle, visibleInNav);
        return document;
    }

    public Document add(DocumentFactory factory, ResourceType documentType,
            String initialContentsURI, Publication pub, String area, String language,
            String extension) throws DocumentBuildException, DocumentException,
            PublicationException {

        String uuid = generateUUID();
        return add(factory, documentType, uuid, initialContentsURI, pub, area, language, extension);
    }

    protected Document add(DocumentFactory factory, ResourceType documentType, String uuid,
            String initialContentsURI, Publication pub, String area, String language,
            String extension) throws DocumentBuildException {
        try {

            if (exists(factory, pub, area, language, extension)) {
                throw new DocumentBuildException("The document [" + pub.getId() + ":" + area + ":"
                        + uuid + ":" + language + "] already exists!");
            }

            Document document = factory.get(pub, area, uuid, language);
            Node node = document.getRepositoryNode();
            node.lock();

            // Write Lenya-internal meta-data
            MetaData lenyaMetaData = document.getMetaData(DocumentImpl.METADATA_NAMESPACE);

            lenyaMetaData.setValue(DocumentImpl.METADATA_RESOURCE_TYPE, documentType.getName());
            lenyaMetaData.setValue(DocumentImpl.METADATA_CONTENT_TYPE, "xml");
            lenyaMetaData.setValue(DocumentImpl.METADATA_EXTENSION, extension);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Create");
                getLogger().debug("    document:     [" + document + "]");
                getLogger().debug("    contents URI: [" + initialContentsURI + "]");
            }

            create(initialContentsURI, document);
            return document;
        } catch (Exception e) {
            throw new DocumentBuildException("call to creator for new document failed", e);
        }
    }

    protected String generateUUID() throws DocumentBuildException {
        String uuid;
        UUIDGenerator generator = null;
        try {

            generator = (UUIDGenerator) this.manager.lookup(UUIDGenerator.ROLE);
            uuid = generator.nextUUID();

        } catch (Exception e) {
            throw new DocumentBuildException("call to creator for new document failed", e);
        } finally {
            if (generator != null) {
                this.manager.release(generator);
            }
        }
        return uuid;
    }

    protected void create(String initialContentsURI, Document document) throws Exception {

        // Read initial contents as DOM
        if (getLogger().isDebugEnabled())
            getLogger().debug("DefaultCreator::create(), ready to read initial contents from URI ["
                    + initialContentsURI + "]");

        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            SourceUtil.copy(resolver, initialContentsURI, document.getSourceURI());
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    private void addToSiteManager(String path, Document document, String navigationTitle,
            boolean visibleInNav) throws PublicationException {
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

            siteManager.add(path, document);
            siteManager.setLabel(document, navigationTitle);
            siteManager.setVisibleInNav(document, visibleInNav);
        } catch (final ServiceException e) {
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
     *      org.apache.lenya.cms.publication.DocumentLocator)
     */
    public void copy(Document sourceDocument, DocumentLocator destination)
            throws PublicationException {

        copyDocument(sourceDocument, destination);
        Document destinationDocument = sourceDocument.getFactory().get(destination);

        copyResources(sourceDocument, destinationDocument);
    }

    protected void copyResources(Document sourceDocument, Document destinationDocument)
            throws PublicationException {
        ResourcesManager resourcesManager = null;
        try {
            resourcesManager = (ResourcesManager) this.manager.lookup(ResourcesManager.ROLE);
            resourcesManager.copyResources(sourceDocument, destinationDocument);
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (resourcesManager != null) {
                this.manager.release(resourcesManager);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#copyDocument(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.DocumentLocator)
     */
    public void copyDocument(Document sourceDocument, DocumentLocator destination)
            throws DocumentBuildException, PublicationException, DocumentException, SiteException {

        add(sourceDocument,
                destination.getArea(),
                destination.getPath(),
                destination.getLanguage(),
                sourceDocument.getExtension(),
                sourceDocument.getLabel(),
                SiteUtil.isVisibleInNavigation(this.manager, sourceDocument));

    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#delete(org.apache.lenya.cms.publication.Document)
     */
    public void delete(Document document) throws PublicationException {
        if (!document.exists()) {
            throw new PublicationException("Document [" + document + "] does not exist!");
        }

        if (SiteUtil.contains(this.manager, document)) {
            deleteFromSiteStructure(document);
        }

        ResourcesManager resourcesManager = null;
        try {
            resourcesManager = (ResourcesManager) this.manager.lookup(ResourcesManager.ROLE);
            resourcesManager.deleteResources(document);
            document.delete();
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (resourcesManager != null) {
                this.manager.release(resourcesManager);
            }
        }
    }

    protected void deleteFromSiteStructure(Document document) throws PublicationException {
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            Publication publication = document.getPublication();
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            siteManager.delete(document);
        } catch (Exception e) {
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
     * @see org.apache.lenya.cms.publication.DocumentManager#move(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.DocumentLocator)
     */
    public void move(Document sourceDocument, DocumentLocator destination)
            throws PublicationException {

        Publication publication = sourceDocument.getPublication();
        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            if (siteManager.contains(sourceDocument.getFactory(),
                    sourceDocument.getPublication(),
                    destination.getArea(),
                    destination.getPath())) {
                throw new PublicationException("The path [" + destination
                        + "] is already contained in this publication!");
            }

            String label = sourceDocument.getLabel();
            boolean visible = siteManager.isVisibleInNav(sourceDocument);
            siteManager.delete(sourceDocument);

            siteManager.add(destination.getPath(), sourceDocument);
            siteManager.setLabel(sourceDocument, label);
            siteManager.setVisibleInNav(sourceDocument, visible);
        } catch (final ServiceException e) {
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
     * @see org.apache.lenya.cms.publication.DocumentManager#copyToArea(org.apache.lenya.cms.publication.Document,
     *      java.lang.String)
     */
    public void copyToArea(Document sourceDoc, String destinationArea) throws PublicationException {
        String language = sourceDoc.getLanguage();
        copyToVersion(sourceDoc, destinationArea, language);
    }

    protected void copyToVersion(Document sourceDoc, String destinationArea, String language)
            throws DocumentException, DocumentBuildException, PublicationException, SiteException {
        Document destinationDoc;
        if (sourceDoc.existsAreaVersion(destinationArea)) {
            destinationDoc = sourceDoc.getAreaVersion(destinationArea);
        } else {
            destinationDoc = addVersion(sourceDoc, destinationArea, language);
        }

        if (SiteUtil.contains(this.manager, sourceDoc)) {
            if (SiteUtil.contains(this.manager, destinationDoc)) {
                boolean visible = SiteUtil.isVisibleInNavigation(this.manager, sourceDoc);
                SiteUtil.setVisibleInNavigation(this.manager, destinationDoc, visible);
            } else {
                String path = sourceDoc.getPath();
                boolean visible = SiteUtil.isVisibleInNavigation(this.manager, sourceDoc);
                addToSiteManager(path, destinationDoc, sourceDoc.getLabel(), visible);
            }
        }

        copyResources(sourceDoc, destinationDoc);
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
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

    private Context context;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context _context) throws ContextException {
        this.context = _context;
    }

    /**
     * @return The Avalon context.
     */
    protected Context getContext() {
        return this.context;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#moveAll(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.DocumentLocator)
     */
    public void moveAll(Document source, DocumentLocator target) throws PublicationException {
        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(source.getPublication()
                    .getSiteManagerHint());

            DocumentSet subsite = SiteUtil.getSubSite(this.manager, source);
            siteManager.sortAscending(subsite);

            DocumentVisitor visitor = new MoveVisitor(this, source, target);
            subsite.visit(visitor);
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
     * @see org.apache.lenya.cms.publication.DocumentManager#moveAllLanguageVersions(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void moveAllLanguageVersions(Document source, DocumentLocator target)
            throws PublicationException {
        String[] languages = source.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            DocumentLocator sourceLoc = source.getLocator().getLanguageVersion(languages[i]);
            Document sourceVersion = source.getFactory().get(sourceLoc);
            DocumentLocator targetLoc = target.getLanguageVersion(languages[i]);
            move(sourceVersion, targetLoc);
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#copyAll(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.DocumentLocator)
     */
    public void copyAll(Document source, DocumentLocator target) throws PublicationException {

        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(source.getPublication()
                    .getSiteManagerHint());

            DocumentSet subsite = SiteUtil.getSubSite(this.manager, source);
            siteManager.sortAscending(subsite);

            DocumentVisitor visitor = new CopyVisitor(this, source, target);
            subsite.visit(visitor);
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
     *      org.apache.lenya.cms.publication.DocumentLocator)
     */
    public void copyAllLanguageVersions(Document source, DocumentLocator target)
            throws PublicationException {
        DocumentFactory identityMap = source.getFactory();
        String[] languages = source.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            DocumentLocator sourceLocator = source.getLocator().getLanguageVersion(languages[i]);
            Document sourceVersion = identityMap.get(sourceLocator);
            DocumentLocator targetLocator = target.getLanguageVersion(languages[i]);
            copy(sourceVersion, targetLocator);
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

        RepositoryManager repoManager = null;
        try {
            repoManager = (RepositoryManager) this.manager.lookup(RepositoryManager.ROLE);
            repoManager.copy(sourceDocument.getRepositoryNode(),
                    destinationDocument.getRepositoryNode());
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (repoManager != null) {
                this.manager.release(repoManager);
            }
        }
    }

    /**
     * Abstract base class for document visitors which operate on a source and target document.
     */
    public abstract class SourceTargetVisitor implements DocumentVisitor {

        private DocumentLocator rootSource;
        private DocumentLocator rootTarget;
        private DocumentManager manager;

        /**
         * Ctor.
         * @param manager The document manager.
         * @param source The root source.
         * @param target The root target.
         */
        public SourceTargetVisitor(DocumentManager manager, Document source, DocumentLocator target) {
            this.manager = manager;
            this.rootSource = source.getLocator();
            this.rootTarget = target;
        }

        /**
         * @return the root source
         */
        protected DocumentLocator getRootSource() {
            return rootSource;
        }

        /**
         * @return the root target
         */
        protected DocumentLocator getRootTarget() {
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
        protected DocumentLocator getTarget(Document source) throws DocumentBuildException {
            DocumentLocator sourceLocator = source.getLocator();
            String rootSourcePath = getRootSource().getPath();
            String relativePath = sourceLocator.getPath().substring(rootSourcePath.length());
            return rootTarget.getDescendant(relativePath);
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
        public CopyVisitor(DocumentManager manager, Document source, DocumentLocator target) {
            super(manager, source, target);
        }

        /**
         * @see org.apache.lenya.cms.publication.util.DocumentVisitor#visitDocument(org.apache.lenya.cms.publication.Document)
         */
        public void visitDocument(Document source) throws PublicationException {
            DocumentLocator target = getTarget(source);
            getDocumentManager().copyAllLanguageVersions(source, target);
        }

    }

    /**
     * DocumentVisitor to copy documents.
     */
    public class MoveVisitor extends SourceTargetVisitor {

        /**
         * Ctor.
         * @param manager The document manager.
         * @param source The root source.
         * @param target The root target.
         */
        public MoveVisitor(DocumentManager manager, Document source, DocumentLocator target) {
            super(manager, source, target);
        }

        /**
         * @see org.apache.lenya.cms.publication.util.DocumentVisitor#visitDocument(org.apache.lenya.cms.publication.Document)
         */
        public void visitDocument(Document source) throws PublicationException {
            DocumentLocator target = getTarget(source);
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

            DocumentSet subsite = SiteUtil.getSubSite(this.manager, document);
            delete(subsite);
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
        DocumentFactory identityMap = document.getFactory();
        String[] languages = document.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            DocumentLocator version = document.getLocator().getLanguageVersion(languages[i]);
            delete(identityMap.get(version));
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
        copy(sources, destinations);
        delete(sources);
        /*
         * Document[] sourceDocs = sources.getDocuments(); Document[] targetDocs =
         * destinations.getDocuments();
         * 
         * if (sourceDocs.length != targetDocs.length) { throw new PublicationException( "The number
         * of source and destination documents must be equal!"); }
         * 
         * Map source2target = new HashMap(); for (int i = 0; i < sourceDocs.length; i++) {
         * source2target.put(sourceDocs[i], targetDocs[i]); }
         * 
         * DocumentSet sortedSources = new DocumentSet(sourceDocs);
         * SiteUtil.sortAscending(this.manager, sortedSources); Document[] sortedSourceDocs =
         * sortedSources.getDocuments();
         * 
         * for (int i = 0; i < sortedSourceDocs.length; i++) { move(sortedSourceDocs[i], (Document)
         * source2target.get(sortedSourceDocs[i])); }
         */
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#copy(org.apache.lenya.cms.publication.util.DocumentSet,
     *      org.apache.lenya.cms.publication.util.DocumentSet)
     */
    public void copy(DocumentSet sources, DocumentSet destinations) throws PublicationException {
        Document[] sourceDocs = sources.getDocuments();
        Document[] targetDocs = destinations.getDocuments();

        if (sourceDocs.length != targetDocs.length) {
            throw new PublicationException("The number of source and destination documents must be equal!");
        }

        Map source2target = new HashMap();
        for (int i = 0; i < sourceDocs.length; i++) {
            source2target.put(sourceDocs[i], targetDocs[i]);
        }

        DocumentSet sortedSources = new DocumentSet(sourceDocs);
        SiteUtil.sortAscending(this.manager, sortedSources);
        Document[] sortedSourceDocs = sortedSources.getDocuments();

        for (int i = 0; i < sortedSourceDocs.length; i++) {
            copy(sortedSourceDocs[i],
                    ((Document) source2target.get(sortedSourceDocs[i])).getLocator());
        }
    }

    public DocumentFactory createDocumentIdentityMap(Session session) {
        return new DocumentFactoryImpl(session, this.manager, getLogger());
    }

    public Document addVersion(Document sourceDocument, String area, String language,
            boolean addToSiteStructure) throws DocumentBuildException, PublicationException {
        Document document = addVersion(sourceDocument, area, language);

        if (addToSiteStructure && SiteUtil.contains(this.manager, sourceDocument)) {
            String path = sourceDocument.getPath();
            boolean visible = SiteUtil.isVisibleInNavigation(this.manager, sourceDocument);
            addToSiteManager(path, document, sourceDocument.getLabel(), visible);
        }

        return document;
    }

    public Document addVersion(Document sourceDocument, String area, String language)
            throws DocumentBuildException, DocumentException, PublicationException {
        Document document = add(sourceDocument.getFactory(),
                sourceDocument.getResourceType(),
                sourceDocument.getUUID(),
                sourceDocument.getSourceURI(),
                sourceDocument.getPublication(),
                area,
                language,
                sourceDocument.getSourceExtension());
        copyMetaData(sourceDocument, document);

        if (!area.equals(sourceDocument.getArea())) {
            copyResources(sourceDocument, document);
        }
        return document;
    }

    public boolean exists(DocumentFactory factory, Publication pub, String area, String uuid,
            String language) throws PublicationException {
        String sourceUri = DocumentImpl.getSourceURI(pub, area, uuid, language);
        try {
            Node node = DocumentImpl.getRepositoryNode(this.manager, factory, sourceUri);
            return node.exists();
        } catch (RepositoryException e) {
            throw new PublicationException(e);
        }
    }

}