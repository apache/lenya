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
import org.apache.lenya.cms.repository.RepositoryManager;
import org.apache.lenya.cms.repository.Session;
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
     * The instance of Document will be built by the implementation of DocumentBuilder; the physical
     * representation will be built by the implementation of NodeCreatorInterface, where the
     * implementation to be used is specified in doctypes.xconf (and thus depends on the publication
     * and the resource type to be used)
     * 
     * @see DocumentManager#add(Document, ResourceType, String, String, boolean)
     * @see org.apache.lenya.cms.publication.DocumentBuilder
     */
    public void add(Document document, ResourceType documentType, String extension,
            String navigationTitle, boolean visibleInNav) throws DocumentBuildException,
            PublicationException {

        String contentsURI = documentType.getSampleURI();
        add(document, documentType, extension, navigationTitle, visibleInNav, contentsURI);
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#add(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document, String, java.lang.String, boolean)
     */
    public void add(Document document, Document sourceDocument, String extension,
            String navigationTitle, boolean visibleInNav) throws DocumentBuildException,
            PublicationException {
        String contentsURI = sourceDocument.getSourceURI();
        add(document,
                sourceDocument.getResourceType(),
                extension,
                navigationTitle,
                visibleInNav,
                contentsURI);

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
     * Adds a document.
     * @param document The document.
     * @param documentType The document type.
     * @param extension The extension for the document source.
     * @param navigationTitle The navigation title.
     * @param visibleInNav determines the visibility of a node in the navigation
     * @param initialContentsURI A URI to read the contents from.
     * @throws DocumentBuildException if an error occurs.
     * @throws DocumentException if an error occurs.
     * @throws PublicationException if an error occurs.
     */

    protected void add(Document document, ResourceType documentType, String extension,
            String navigationTitle, boolean visibleInNav, String initialContentsURI)
            throws DocumentBuildException, DocumentException, PublicationException {

        try {

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
                getLogger().debug("    nav title:    [" + navigationTitle + "]");
                getLogger().debug("    contents URI: [" + initialContentsURI + "]");
            }

            create(initialContentsURI, document);
        } catch (Exception e) {
            throw new DocumentBuildException("call to creator for new document failed", e);
        }

        // Notify site manager about new document
        addToSiteManager(document, navigationTitle, visibleInNav);
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

    private void addToSiteManager(Document document, String navigationTitle, boolean visibleInNav)
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
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copy(Document sourceDocument, Document destinationDocument)
            throws PublicationException {

        Publication publication = sourceDocument.getPublication();
        copyDocumentSource(sourceDocument, destinationDocument);

        ResourcesManager resourcesManager = null;
        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            resourcesManager = (ResourcesManager) this.manager.lookup(ResourcesManager.ROLE);
            resourcesManager.copyResources(sourceDocument, destinationDocument);
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            siteManager.copy(sourceDocument, destinationDocument);
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
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
     * Method to copy a document without it's resources. Override
     * {@link #copyDocumentSource(Document, Document)} to implement access to a custom repository.
     * @see org.apache.lenya.cms.publication.DocumentManager#copy(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copyDocument(Document sourceDocument, Document destinationDocument)
            throws PublicationException {

        Publication publication = sourceDocument.getPublication();
        copyDocumentSource(sourceDocument, destinationDocument);

        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            siteManager.copy(sourceDocument, destinationDocument);
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
     * @see org.apache.lenya.cms.publication.DocumentManager#delete(org.apache.lenya.cms.publication.Document)
     */
    public void delete(Document document) throws PublicationException {
        if (!document.exists()) {
            throw new PublicationException("Document [" + document + "] does not exist!");
        }

        SiteManager siteManager = null;
        ServiceSelector selector = null;
        ResourcesManager resourcesManager = null;
        try {
            resourcesManager = (ResourcesManager) this.manager.lookup(ResourcesManager.ROLE);
            resourcesManager.deleteResources(document);

            Publication publication = document.getPublication();
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            siteManager.delete(document);

            document.delete();
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
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
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copyAllLanguageVersions(Document source, Document target)
            throws PublicationException {
        DocumentFactory identityMap = source.getIdentityMap();
        String[] languages = source.getLanguages();
        for (int i = 0; i < languages.length; i++) {

            Document sourceVersion = identityMap.getLanguageVersion(source, languages[i]);
            DocumentLocator targetLocator = sourceVersion.getLocator().getLanguageVersion(languages[i]);
            Document targetVersion = identityMap.get(targetLocator);
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
            String rootSourcePath = getRootSource().getPath();
            String rootTargetPath = getRootTarget().getPath();
            String childId = source.getPath().substring(rootSourcePath.length());
            String targetId = rootTargetPath + childId;
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
        DocumentFactory identityMap = document.getIdentityMap();
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
            copy(sortedSourceDocs[i], (Document) source2target.get(sortedSourceDocs[i]));
        }
    }

    public DocumentFactory createDocumentIdentityMap(Session session) {
        return new DocumentFactoryImpl(session, this.manager, getLogger());
    }

}