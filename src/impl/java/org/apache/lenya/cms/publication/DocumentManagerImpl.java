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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryException;
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
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, boolean)
     */
    public Document add(Document sourceDocument, String area, String path, String language,
            String extension, String navigationTitle, boolean visibleInNav)
            throws DocumentBuildException, PublicationException {

        Document document = add(sourceDocument.getFactory(), sourceDocument.getResourceType(),
                sourceDocument.getInputStream(), sourceDocument.getPublication(), area, path,
                language, extension, navigationTitle, visibleInNav, sourceDocument.getMimeType());

        copyMetaData(sourceDocument, document);
        return document;
    }

    /**
     * Copies meta data from one document to another. If the destination
     * document is a different area version, the meta data are duplicated (i.e.,
     * onCopy = delete is neglected).
     * @param source
     * @param destination
     * @throws PublicationException
     */
    protected void copyMetaData(Document source, Document destination) throws PublicationException {

        boolean duplicate = source.getUUID().equals(destination.getUUID())
                && source.getLanguage().equals(destination.getLanguage())
                && !source.getArea().equals(destination.getArea());

        try {
            String[] uris = source.getMetaDataNamespaceUris();
            for (int i = 0; i < uris.length; i++) {
                if (duplicate) {
                    destination.getMetaData(uris[i]).forcedReplaceBy(source.getMetaData(uris[i]));
                } else {
                    destination.getMetaData(uris[i]).replaceBy(source.getMetaData(uris[i]));
                }
            }
        } catch (MetaDataException e) {
            throw new PublicationException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentManager#add(org.apache.lenya.cms.publication.DocumentFactory,
     *      org.apache.lenya.cms.publication.ResourceType, java.lang.String,
     *      org.apache.lenya.cms.publication.Publication, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, boolean)
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

        Document document = add(factory, documentType, initialContentsURI, pub, area, language,
                extension);

        addToSiteManager(path, document, navigationTitle, visibleInNav);
        return document;
    }

    protected Document add(DocumentFactory factory, ResourceType documentType,
            InputStream initialContentsStream, Publication pub, String area, String path,
            String language, String extension, String navigationTitle, boolean visibleInNav, String mimeType)
            throws DocumentBuildException, DocumentException, PublicationException {

        Area areaObj = pub.getArea(area);
        SiteStructure site = areaObj.getSite();
        if (site.contains(path) && site.getNode(path).hasLink(language)) {
            throw new DocumentException("The link [" + path + ":" + language
                    + "] is already contained in site [" + site + "]");
        }

        Document document = add(factory, documentType, initialContentsStream, pub, area, language,
                extension, mimeType);

        addToSiteManager(path, document, navigationTitle, visibleInNav);
        return document;
    }

    public Document add(DocumentFactory factory, ResourceType documentType,
            String initialContentsURI, Publication pub, String area, String language,
            String extension) throws DocumentBuildException, DocumentException,
            PublicationException {

        String uuid = generateUUID();
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(initialContentsURI);
            return add(factory, documentType, uuid, source.getInputStream(), pub, area, language,
                    extension, getMimeType(source));
        } catch (Exception e) {
            throw new PublicationException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    protected String getMimeType(Source source) {
        String mimeType = source.getMimeType();
        if (mimeType == null) {
            mimeType = "";
        }
        return mimeType;
    }

    protected Document add(DocumentFactory factory, ResourceType documentType,
            InputStream initialContentsStream, Publication pub, String area, String language,
            String extension, String mimeType) throws DocumentBuildException, DocumentException,
            PublicationException {

        String uuid = generateUUID();
        return add(factory, documentType, uuid, initialContentsStream, pub, area, language,
                extension, mimeType);
    }

    protected Document add(DocumentFactory factory, ResourceType documentType, String uuid,
            InputStream stream, Publication pub, String area, String language, String extension, String mimeType)
            throws DocumentBuildException {
        try {

            if (exists(factory, pub, area, uuid, language)) {
                throw new DocumentBuildException("The document [" + pub.getId() + ":" + area + ":"
                        + uuid + ":" + language + "] already exists!");
            }

            Document document = factory.get(pub, area, uuid, language);
            Node node = document.getRepositoryNode();
            node.lock();

            document.setResourceType(documentType);
            document.setSourceExtension(extension);
            document.setMimeType(mimeType);

            // Write Lenya-internal meta-data
            MetaData lenyaMetaData = document.getMetaData(DocumentImpl.METADATA_NAMESPACE);
            lenyaMetaData.setValue(DocumentImpl.METADATA_CONTENT_TYPE, "xml");

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Create");
                getLogger().debug("    document:     [" + document + "]");
            }

            create(stream, document);
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

    protected void create(InputStream stream, Document document) throws Exception {

        // Read initial contents as DOM
        if (getLogger().isDebugEnabled())
            getLogger().debug(
                    "DefaultCreator::create(), ready to read initial contents from URI [" + stream
                            + "]");

        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            copy(resolver, stream, document);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    protected void copy(SourceResolver resolver, InputStream sourceInputStream, Document destination)
            throws IOException {

        boolean useBuffer = true;

        OutputStream destOutputStream = null;
        try {
            destOutputStream = destination.getOutputStream();

            if (useBuffer) {
                final ByteArrayOutputStream sourceBos = new ByteArrayOutputStream();
                IOUtils.copy(sourceInputStream, sourceBos);
                IOUtils.write(sourceBos.toByteArray(), destOutputStream);
            } else {
                IOUtils.copy(sourceInputStream, destOutputStream);
            }
        } finally {
            if (destOutputStream != null) {
                destOutputStream.flush();
                destOutputStream.close();
            }
            if (sourceInputStream != null) {
                sourceInputStream.close();
            }
        }
    }

    protected void addToSiteManager(String path, Document document, String navigationTitle,
            boolean visibleInNav) throws PublicationException {
        addToSiteManager(path, document, navigationTitle, visibleInNav, null);
    }

    protected void addToSiteManager(String path, Document document, String navigationTitle,
            boolean visibleInNav, String followingSiblingPath) throws PublicationException {
        SiteStructure site = document.area().getSite();
        if (!site.contains(path) && followingSiblingPath != null) {
            site.add(path, followingSiblingPath);
        }
        site.add(path, document);
        document.getLink().setLabel(navigationTitle);
        document.getLink().getNode().setVisible(visibleInNav);
    }

    /**
     * Template method to copy a document. Override
     * {@link #copyDocumentSource(Document, Document)} to implement access to a
     * custom repository.
     * @see org.apache.lenya.cms.publication.DocumentManager#copy(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.DocumentLocator)
     */
    public void copy(Document sourceDoc, DocumentLocator destination) throws PublicationException {

        if (!destination.getPublicationId().equals(sourceDoc.getPublication().getId())) {
            throw new PublicationException("Can't copy to a different publication!");
        }

        SiteStructure destSite = sourceDoc.getPublication().getArea(destination.getArea())
                .getSite();
        String destPath = destination.getPath();
        if (destSite.contains(destination.getPath(), destination.getLanguage())) {
            Document destDoc = destSite.getNode(destPath).getLink(destination.getLanguage())
                    .getDocument();
            copyDocumentSource(sourceDoc, destDoc);
            copyInSiteStructure(sourceDoc, destDoc, destPath);
        } else {
            add(sourceDoc, destination.getArea(), destPath, destination.getLanguage(), sourceDoc
                    .getExtension(), sourceDoc.getLink().getLabel(), sourceDoc.getLink().getNode()
                    .isVisible());
        }

    }

    protected void copyInSiteStructure(Document sourceDoc, Document destDoc, String destPath)
            throws PublicationException, DocumentException, SiteException {

        String destArea = destDoc.getArea();

        SiteStructure destSite = sourceDoc.getPublication().getArea(destArea).getSite();

        if (sourceDoc.hasLink()) {
            if (destDoc.hasLink()) {
                Link srcLink = sourceDoc.getLink();
                Link destLink = destDoc.getLink();
                destLink.setLabel(srcLink.getLabel());
                destLink.getNode().setVisible(srcLink.getNode().isVisible());
            } else {
                String label = sourceDoc.getLink().getLabel();
                boolean visible = sourceDoc.getLink().getNode().isVisible();
                if (destSite.contains(sourceDoc.getLink().getNode().getPath())) {
                    addToSiteManager(destPath, destDoc, label, visible);
                } else {

                    String followingSiblingPath = null;

                    if (sourceDoc.getPath().equals(destPath)) {
                        SiteStructure sourceSite = sourceDoc.area().getSite();

                        SiteNode[] sourceSiblings;
                        SiteNode sourceNode = sourceDoc.getLink().getNode();
                        if (sourceNode.isTopLevel()) {
                            sourceSiblings = sourceSite.getTopLevelNodes();
                        } else if (sourceNode.getParent() != null) {
                            sourceSiblings = sourceNode.getParent().getChildren();
                        } else {
                            sourceSiblings = new SiteNode[1];
                            sourceSiblings[0] = sourceNode;
                        }

                        final int sourcePos = Arrays.asList(sourceSiblings).indexOf(sourceNode);

                        int pos = sourcePos;
                        while (followingSiblingPath == null && pos < sourceSiblings.length) {
                            String siblingPath = sourceSiblings[pos].getPath();
                            if (destSite.contains(siblingPath)) {
                                followingSiblingPath = siblingPath;
                            }
                            pos++;
                        }
                    }

                    if (followingSiblingPath == null) {
                        addToSiteManager(destPath, destDoc, label, visible);
                    } else {
                        addToSiteManager(destPath, destDoc, label, visible, followingSiblingPath);
                    }
                }
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

        if (!destination.getArea().equals(sourceDocument.getArea())) {
            throw new PublicationException("Can't move to a different area!");
        }

        SiteStructure site = sourceDocument.area().getSite();
        if (site.contains(destination.getPath())) {
            throw new PublicationException("The path [" + destination
                    + "] is already contained in this publication!");
        }

        String label = sourceDocument.getLink().getLabel();
        boolean visible = sourceDocument.getLink().getNode().isVisible();
        sourceDocument.getLink().delete();

        site.add(destination.getPath(), sourceDocument);
        sourceDocument.getLink().setLabel(label);
        sourceDocument.getLink().getNode().setVisible(visible);

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

        Document destDoc;
        if (sourceDoc.existsAreaVersion(destinationArea)) {
            destDoc = sourceDoc.getAreaVersion(destinationArea);
            copyDocumentSource(sourceDoc, destDoc);
        } else {
            destDoc = addVersion(sourceDoc, destinationArea, language);
        }

        if (sourceDoc.hasLink()) {
            copyInSiteStructure(sourceDoc, destDoc, sourceDoc.getPath());
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
        List subsite = preOrder(root);

        for (Iterator n = subsite.iterator(); n.hasNext();) {
            SiteNode node = (SiteNode) n.next();
            String subPath = node.getPath().substring(sourcePath.length());
            targetArea.getSite().add(targetPath + subPath);
        }
        Collections.reverse(subsite);
        for (Iterator n = subsite.iterator(); n.hasNext();) {
            SiteNode node = (SiteNode) n.next();
            String subPath = node.getPath().substring(sourcePath.length());
            moveAllLanguageVersions(sourceArea, sourcePath + subPath, targetArea, targetPath
                    + subPath);
        }
    }
    
    protected List preOrder(SiteNode node) {
    	List list = new ArrayList();
    	list.add(node);
    	SiteNode[] children = node.getChildren();
    	for (int i = 0; i < children.length; i++) {
    		list.addAll(preOrder(children[i]));
    	}
    	return list;
    }

    public void moveAllLanguageVersions(Area sourceArea, String sourcePath, Area targetArea,
            String targetPath) throws PublicationException {

        SiteNode sourceNode = sourceArea.getSite().getNode(sourcePath);
        String[] languages = sourceNode.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            Link sourceLink = sourceNode.getLink(languages[i]);
            String label = sourceLink.getLabel();
            Document sourceDoc = sourceLink.getDocument();
            sourceLink.delete();

            Document targetDoc;
            if (sourceArea.getName().equals(targetArea.getName())) {
                targetDoc = sourceDoc;
            } else {
                targetDoc = addVersion(sourceDoc, targetArea.getName(), sourceDoc.getLanguage());
                copyRevisions(sourceDoc, targetDoc);
                sourceDoc.delete();
            }

            Link link = targetArea.getSite().add(targetPath, targetDoc);
            link.setLabel(label);
            Assert.isTrue("label set", targetDoc.getLink().getLabel().equals(label));
        }
        SiteNode targetNode = targetArea.getSite().getNode(targetPath);
        targetNode.setVisible(sourceNode.isVisible());
    }

    protected void copyRevisions(Document sourceDoc, Document targetDoc) throws PublicationException {
        try {
            Node targetNode = targetDoc.getRepositoryNode();
            targetNode.copyRevisionsFrom(sourceDoc.getRepositoryNode());
        } catch (Exception e) {
            throw new PublicationException(e);
        }
    }

    public void copyAll(Area sourceArea, String sourcePath, Area targetArea, String targetPath)
            throws PublicationException {

        SiteStructure site = sourceArea.getSite();
        SiteNode root = site.getNode(sourcePath);
        
        List preOrder = preOrder(root);
        for (Iterator i = preOrder.iterator(); i.hasNext(); ) {
            SiteNode node = (SiteNode) i.next();
            String nodeSourcePath = node.getPath();
            String nodeTargetPath = targetPath + nodeSourcePath.substring(sourcePath.length());
            copyAllLanguageVersions(sourceArea, nodeSourcePath, targetArea, nodeTargetPath);
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
            DocumentLocator targetLocator = DocumentLocator.getLocator(pub.getId(), targetArea
                    .getName(), targetPath, languages[i]);
            if (targetDoc == null) {
                copy(sourceVersion, targetLocator.getLanguageVersion(languages[i]));
                targetDoc = targetArea.getSite().getNode(targetPath).getLink(languages[i])
                        .getDocument();
            } else {
                targetDoc = addVersion(targetDoc, targetLocator.getArea(), languages[i]);
                addToSiteManager(targetLocator.getPath(), targetDoc, sourceVersion.getLink()
                        .getLabel(), sourceVersion.getLink().getNode().isVisible());
                copyDocumentSource(sourceVersion, targetDoc);
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
        copyContent(sourceDocument, destinationDocument);
        copyMetaData(sourceDocument, destinationDocument);
    }

    protected void copyContent(Document sourceDocument, Document destinationDocument)
            throws PublicationException {
        boolean useBuffer = true;

        OutputStream destOutputStream = null;
        InputStream sourceInputStream = null;
        try {
            try {
                sourceInputStream = sourceDocument.getInputStream();
                destOutputStream = destinationDocument.getOutputStream();

                if (useBuffer) {
                    final ByteArrayOutputStream sourceBos = new ByteArrayOutputStream();
                    IOUtils.copy(sourceInputStream, sourceBos);
                    IOUtils.write(sourceBos.toByteArray(), destOutputStream);
                } else {
                    IOUtils.copy(sourceInputStream, destOutputStream);
                }
            } finally {
                if (destOutputStream != null) {
                    destOutputStream.flush();
                    destOutputStream.close();
                }
                if (sourceInputStream != null) {
                    sourceInputStream.close();
                }
            }
        } catch (Exception e) {
            throw new PublicationException(e);
        }
    }

    /**
     * Abstract base class for document visitors which operate on a source and
     * target document.
     */
    public static abstract class SourceTargetVisitor implements DocumentVisitor {

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
         * Returns the target corresponding to a source relatively to the root
         * target document.
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
    public static class DeleteVisitor implements DocumentVisitor {

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
            getDocumentManager().delete(document);
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
         * if (sourceDocs.length != targetDocs.length) { throw new
         * PublicationException( "The number of source and destination documents
         * must be equal!"); }
         * 
         * Map source2target = new HashMap(); for (int i = 0; i <
         * sourceDocs.length; i++) { source2target.put(sourceDocs[i],
         * targetDocs[i]); }
         * 
         * DocumentSet sortedSources = new DocumentSet(sourceDocs);
         * SiteUtil.sortAscending(this.manager, sortedSources); Document[]
         * sortedSourceDocs = sortedSources.getDocuments();
         * 
         * for (int i = 0; i < sortedSourceDocs.length; i++) {
         * move(sortedSourceDocs[i], (Document)
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
            throw new PublicationException(
                    "The number of source and destination documents must be equal!");
        }

        Map source2target = new HashMap();
        for (int i = 0; i < sourceDocs.length; i++) {
            source2target.put(sourceDocs[i], targetDocs[i]);
        }

        DocumentSet sortedSources = new DocumentSet(sourceDocs);
        sortAscending(sortedSources);
        Document[] sortedSourceDocs = sortedSources.getDocuments();

        for (int i = 0; i < sortedSourceDocs.length; i++) {
            copy(sortedSourceDocs[i], ((Document) source2target.get(sortedSourceDocs[i]))
                    .getLocator());
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

                SiteNode[] ascending = siteManager.sortAscending((SiteNode[]) nodes
                        .toArray(new SiteNode[nodes.size()]));

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

    public Document addVersion(Document sourceDocument, String area, String language,
            boolean addToSiteStructure) throws DocumentBuildException, PublicationException {
        Document document = addVersion(sourceDocument, area, language);

        if (addToSiteStructure && sourceDocument.hasLink()) {
            String path = sourceDocument.getPath();
            boolean visible = sourceDocument.getLink().getNode().isVisible();
            addToSiteManager(path, document, sourceDocument.getLink().getLabel(), visible);
        }

        return document;
    }

    public Document addVersion(Document sourceDocument, String area, String language)
            throws DocumentBuildException, DocumentException, PublicationException {
        Document document = add(sourceDocument.getFactory(), sourceDocument.getResourceType(),
                sourceDocument.getUUID(), sourceDocument.getInputStream(), sourceDocument
                        .getPublication(), area, language, sourceDocument.getSourceExtension(),
                        sourceDocument.getMimeType());
        copyMetaData(sourceDocument, document);

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
