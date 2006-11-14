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
package org.apache.lenya.cms.publication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.NodeIterator;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.util.Assert;

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

    /**
     * Copies meta data from one document to another.
     * @param source
     * @param destination
     * @throws PublicationException
     */
    protected void copyMetaData(Document source, Document destination)
            throws PublicationException {
        try {
            String[] uris = source.getMetaDataNamespaceUris();
            for (int i = 0; i < uris.length; i++) {
                destination.getMetaData(uris[i]).replaceBy(source.getMetaData(uris[i]));
            }
        } catch (MetaDataException e) {
            throw new PublicationException(e);
        }
    }

    /**
     * Copies meta data from one document to another, whereas both documents will have
     * completely identical meta data afterwards (including workflow etc.). This is only 
     * useful if the source document will be deleted afterwards.
     * @param source
     * @param destination
     * @throws PublicationException
     */
    protected void duplicateMetaData(Document source, Document destination)
    throws PublicationException {
        try {
            String[] uris = source.getMetaDataNamespaceUris();
            for (int i = 0; i < uris.length; i++) {
                destination.getMetaData(uris[i]).forcedReplaceBy(source.getMetaData(uris[i]));
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

        Area areaObj = pub.getArea(area);
        SiteStructure site = areaObj.getSite();
        if (site.contains(path) && site.getNode(path).hasLink(language)) {
            throw new DocumentException("The link [" + path + ":" + language
                    + "] is already contained in site [" + site + "]");
        }

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

            if (exists(factory, pub, area, uuid, language)) {
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

    protected void addToSiteManager(String path, Document document, String navigationTitle,
            boolean visibleInNav) throws PublicationException {
        SiteStructure site = document.area().getSite();
        site.add(path, document);
        document.getLink().setLabel(navigationTitle);
        document.getLink().getNode().setVisible(visibleInNav);
    }

    /**
     * Template method to copy a document. Override {@link #copyDocumentSource(Document, Document)}
     * to implement access to a custom repository.
     * @see org.apache.lenya.cms.publication.DocumentManager#copy(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.DocumentLocator)
     */
    public void copy(Document sourceDocument, DocumentLocator destination)
            throws PublicationException {
        add(sourceDocument,
                destination.getArea(),
                destination.getPath(),
                destination.getLanguage(),
                sourceDocument.getExtension(),
                sourceDocument.getLink().getLabel(),
                sourceDocument.getLink().getNode().isVisible());
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#delete(org.apache.lenya.cms.publication.Document)
     */
    public void delete(Document document) throws PublicationException {
        if (!document.exists()) {
            throw new PublicationException("Document [" + document + "] does not exist!");
        }

        if (document.hasLink()) {
            document.getLink().delete();
        }

        document.delete();
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
            if (siteManager.getSiteStructure(sourceDocument.getFactory(),
                    sourceDocument.getPublication(),
                    destination.getArea()).contains(destination.getPath())) {
                throw new PublicationException("The path [" + destination
                        + "] is already contained in this publication!");
            }

            String label = sourceDocument.getLink().getLabel();
            boolean visible = sourceDocument.getLink().getNode().isVisible();
            sourceDocument.getLink().delete();

            siteManager.add(destination.getPath(), sourceDocument);
            sourceDocument.getLink().setLabel(label);
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
            copyContent(sourceDoc, destinationDoc);
        } else {
            destinationDoc = addVersion(sourceDoc, destinationArea, language);
        }

        if (SiteUtil.contains(this.manager, sourceDoc)) {
            if (SiteUtil.contains(this.manager, destinationDoc)) {
                boolean visible = sourceDoc.getLink().getNode().isVisible();
                destinationDoc.getLink().getNode().setVisible(visible);
            } else {
                String path = sourceDoc.getPath();
                String label = sourceDoc.getLink().getLabel();
                boolean visible = sourceDoc.getLink().getNode().isVisible();
                addToSiteManager(path, destinationDoc, label, visible);
            }
        }

    }

    /**
     * Copies content, resources, and meta data.
     * @param sourceDoc The source document.
     * @param destinationDoc The destination document.
     * @throws PublicationException if an error occurs.
     */
    protected void copyContent(Document sourceDoc, Document destinationDoc) throws PublicationException {
        try {
            SourceUtil.copy(this.manager, sourceDoc.getSourceURI(), destinationDoc.getSourceURI());
            copyMetaData(sourceDoc, destinationDoc);
        } catch (Exception e) {
            throw new PublicationException(e);
        }
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

    public void moveAll(Area sourceArea, String sourcePath, Area targetArea, String targetPath)
            throws PublicationException {
        SiteStructure site = sourceArea.getSite();

        SiteNode root = site.getNode(sourcePath);
        NodeSet subsite = SiteUtil.getSubSite(this.manager, root);

        for (NodeIterator n = subsite.ascending(); n.hasNext();) {
            SiteNode node = n.next();
            String subPath = node.getPath().substring(sourcePath.length());
            targetArea.getSite().add(targetPath + subPath);
        }
        for (NodeIterator n = subsite.descending(); n.hasNext();) {
            SiteNode node = n.next();
            String subPath = node.getPath().substring(sourcePath.length());
            moveAllLanguageVersions(sourceArea, sourcePath + subPath, targetArea, targetPath
                    + subPath);
        }
    }

    public void moveAllLanguageVersions(Area sourceArea, String sourcePath, Area targetArea,
            String targetPath) throws PublicationException {

        SiteNode sourceNode = sourceArea.getSite().getNode(sourcePath);
        String[] languages = sourceNode.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            Link sourceLink = sourceNode.getLink(languages[i]);
            String label = sourceLink.getLabel();
            Document sourceDoc = sourceLink.getDocument();
            
            Document targetDoc;
            if (sourceArea.getName().equals(targetArea.getName())) {
                targetDoc = sourceDoc;
            }
            else {
                targetDoc = duplicateVersion(sourceDoc, targetArea.getName(), sourceDoc.getLanguage());
                sourceDoc.delete();
            }
            
            sourceLink.delete();
            Link link = targetArea.getSite().add(targetPath, targetDoc);
            link.setLabel(label);
            Assert.isTrue("label set", targetDoc.getLink().getLabel().equals(label));
        }
        SiteNode targetNode = targetArea.getSite().getNode(targetPath);
        targetNode.setVisible(sourceNode.isVisible());
    }

    public void copyAll(Area sourceArea, String sourcePath, Area targetArea, String targetPath)
            throws PublicationException {

        SiteStructure site = sourceArea.getSite();

        SiteNode root = site.getNode(sourcePath);
        NodeSet subsite = SiteUtil.getSubSite(this.manager, root);

        for (NodeIterator i = subsite.ascending(); i.hasNext();) {
            SiteNode node = i.next();
            String subPath = node.getPath().substring(sourcePath.length());
            copyAllLanguageVersions(sourceArea, sourcePath + subPath, targetArea, targetPath
                    + subPath);
        }
    }

    public void copyAllLanguageVersions(Area sourceArea, String sourcePath, Area targetArea,
            String targetPath) throws PublicationException {
        Publication pub = sourceArea.getPublication();

        SiteNode sourceNode = sourceArea.getSite().getNode(sourcePath);
        String[] languages = sourceNode.getLanguages();

        Document targetDoc = null;

        for (int i = 0; i < languages.length; i++) {
            Document sourceVersion = sourceNode.getLink(languages[i]).getDocument();
            DocumentLocator targetLocator = DocumentLocator.getLocator(pub.getId(),
                    targetArea.getName(),
                    targetPath,
                    languages[i]);
            if (targetDoc == null) {
                copy(sourceVersion, targetLocator.getLanguageVersion(languages[i]));
                targetDoc = targetArea.getSite()
                        .getNode(targetPath)
                        .getLink(languages[i])
                        .getDocument();
            } else {
                targetDoc = addVersion(targetDoc, targetLocator.getArea(), languages[i]);
                addToSiteManager(targetLocator.getPath(), targetDoc, sourceVersion.getLink()
                        .getLabel(), sourceVersion.getLink().getNode().isVisible());
                try {
                    SourceUtil.copy(manager, sourceVersion.getSourceURI(), targetDoc.getSourceURI());
                } catch (Exception e) {
                    throw new PublicationException(e);
                }
                copyMetaData(sourceVersion, targetDoc);
            }
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
            if (sourceLocator.getPath().equals(rootSourcePath)) {
                return rootTarget;
            } else {
                String relativePath = sourceLocator.getPath().substring(rootSourcePath.length());
                return rootTarget.getDescendant(relativePath);
            }
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

            NodeSet subsite = SiteUtil.getSubSite(this.manager, document.getLink().getNode());
            for (NodeIterator i = subsite.descending(); i.hasNext();) {
                SiteNode node = i.next();
                String[] languages = node.getLanguages();
                for (int l = 0; l < languages.length; l++) {
                    Document doc = node.getLink(languages[l]).getDocument();
                    delete(doc);
                }
            }
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
            sortAscending(set);
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
        sortAscending(sortedSources);
        Document[] sortedSourceDocs = sortedSources.getDocuments();

        for (int i = 0; i < sortedSourceDocs.length; i++) {
            copy(sortedSourceDocs[i],
                    ((Document) source2target.get(sortedSourceDocs[i])).getLocator());
        }
    }

    protected void sortAscending(DocumentSet set) throws PublicationException {

        if (!set.isEmpty()) {

            Document[] docs = set.getDocuments();
            int n = docs.length;

            Publication pub = docs[0].getPublication();
            SiteManager siteManager = null;
            ServiceSelector selector = null;
            try {
                selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
                siteManager = (SiteManager) selector.select(pub.getSiteManagerHint());

                Set nodes = new HashSet();
                for (int i = 0; i < docs.length; i++) {
                    nodes.add(docs[i].getLink().getNode());
                }

                SiteNode[] ascending = siteManager.sortAscending((SiteNode[]) nodes.toArray(new SiteNode[nodes.size()]));

                set.clear();
                for (int i = 0; i < ascending.length; i++) {
                    for (int d = 0; d < docs.length; d++) {
                        if (docs[d].getPath().equals(ascending[i].getPath())) {
                            set.add(docs[d]);
                        }
                    }
                }

                if (set.getDocuments().length != n) {
                    throw new IllegalStateException("Number of documents has changed!");
                }

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
    }

    public DocumentFactory createDocumentIdentityMap(Session session) {
        return new DocumentFactoryImpl(session, this.manager, getLogger());
    }

    public Document addVersion(Document sourceDocument, String area, String language,
            boolean addToSiteStructure) throws DocumentBuildException, PublicationException {
        Document document = addVersion(sourceDocument, area, language);

        if (addToSiteStructure && SiteUtil.contains(this.manager, sourceDocument)) {
            String path = sourceDocument.getPath();
            boolean visible = sourceDocument.getLink().getNode().isVisible();
            addToSiteManager(path, document, sourceDocument.getLink().getLabel(), visible);
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

        return document;
    }

    protected Document duplicateVersion(Document sourceDocument, String area, String language)
            throws DocumentBuildException, DocumentException, PublicationException {
        Document document = add(sourceDocument.getFactory(),
                sourceDocument.getResourceType(),
                sourceDocument.getUUID(),
                sourceDocument.getSourceURI(),
                sourceDocument.getPublication(),
                area,
                language,
                sourceDocument.getSourceExtension());
        duplicateMetaData(sourceDocument, document);
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