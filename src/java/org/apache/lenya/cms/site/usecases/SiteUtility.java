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
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.publication.util.OrderedDocumentSet;
import org.apache.lenya.cms.publication.util.UniqueDocumentId;
import org.apache.lenya.cms.site.SiteManager;

/**
 * Utility methods for site usecases.
 * 
 * @version $Id:$
 */
public class SiteUtility {

    /**
     * Checks if a document can be created. This is the case if the document ID
     * is valid and the document does not yet exist.
     * @param identityMap The identity map to use.
     * @param area The area.
     * @param parent The parent of the document or <code>null</code> if the
     *            document has no parent.
     * @param nodeId The node ID.
     * @param language The language.
     * @return An array of error messages. The array is empty if the document
     *         can be created.
     * @throws DocumentBuildException if an error occurs.
     * @throws DocumentException if an error occurs.
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
     * Checks if the document does already exist. If it does, returns a
     * non-existing document with a similar document ID. If it does not, the
     * original document is returned.
     * @param document The document.
     * @return A document.
     * @throws DocumentBuildException if the new document could not be built.
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
     * Moves a document to another location, incl. all requiring documents.
     * If a sitetree is used, this means that the whole subtree is moved.
     * @param source The source document.
     * @param target The target document.
     * @throws PublicationException if an error occurs.
     */
    public void moveAll(Document source, Document target) throws PublicationException {
        DocumentIdentityMap identityMap = source.getIdentityMap();
        SiteManager manager = identityMap.getPublication().getSiteManager(identityMap);
        Document[] descendantsArray = manager.getRequiringResources(source);
        OrderedDocumentSet descendants = new OrderedDocumentSet(descendantsArray);
        descendants.add(source);
        
        DocumentVisitor visitor = new MoveVisitor(source, target);
        descendants.visitAscending(visitor);
    }

    /**
     * Moves all language versions of a document to another location.
     * @param source The source.
     * @param target The target.
     * @throws PublicationException if the documents could not be moved.
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
            identityMap.getPublication().moveDocument(sourceVersion, targetVersion);
        }
    }
    
    /**
     * Copies a document to another location, incl. all requiring documents.
     * If a sitetree is used, this means that the whole subtree is copied.
     * @param source The source document.
     * @param target The target document.
     * @throws PublicationException if an error occurs.
     */
    public void copyAll(Document source, Document target) throws PublicationException {
        DocumentIdentityMap identityMap = source.getIdentityMap();
        SiteManager manager = identityMap.getPublication().getSiteManager(identityMap);
        Document[] descendantsArray = manager.getRequiringResources(source);
        OrderedDocumentSet descendants = new OrderedDocumentSet(descendantsArray);
        descendants.add(source);
        
        DocumentVisitor visitor = new CopyVisitor(source, target);
        descendants.visitAscending(visitor);
    }

    /**
     * Copies all language versions of a document to another location.
     * @param source The source.
     * @param target The target.
     * @throws PublicationException if the documents could not be copied.
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
            identityMap.getPublication().copyDocument(sourceVersion, targetVersion);
        }
    }
    
    /**
     * Abstract base class for document visitors which operate on a source and target document.
     */
    public abstract class Visitor implements DocumentVisitor {
        
        private Document rootSource;
        private Document rootTarget;
        
        /**
         * Ctor.
         * @param source The root source.
         * @param target The root target.
         */
        public Visitor(Document source, Document target) {
            this.rootSource = source;
            this.rootTarget = target;
        }
        
        protected Document getRootSource() {
            return rootSource;
        }
        
        protected Document getRootTarget() {
            return rootTarget;
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
         * @param source The root source.
         * @param target The root target.
         */
        public MoveVisitor(Document source, Document target) {
            super(source, target);
        }

        /**
         * @see org.apache.lenya.cms.publication.util.DocumentVisitor#visitDocument(org.apache.lenya.cms.publication.Document)
         */
        public void visitDocument(Document source) throws PublicationException {
            Document target = getTarget(source);
            SiteUtility util = new SiteUtility();
            util.moveAllLanguageVersions(source, target);
        }
        
    }
    
    /**
     * DocumentVisitor to copy documents.
     */
    public class CopyVisitor extends Visitor {
        
        /**
         * Ctor.
         * @param source The root source.
         * @param target The root target.
         */
        public CopyVisitor(Document source, Document target) {
            super(source, target);
        }

        /**
         * @see org.apache.lenya.cms.publication.util.DocumentVisitor#visitDocument(org.apache.lenya.cms.publication.Document)
         */
        public void visitDocument(Document source) throws PublicationException {
            Document target = getTarget(source);
            SiteUtility util = new SiteUtility();
            util.copyAllLanguageVersions(source, target);
        }
        
    }
    
}