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
package org.apache.lenya.cms.workflow.usecases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lenya.cms.linking.LinkManager;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Node;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Deactivate usecase handler.
 * 
 * @version $Id$
 */
public class Deactivate extends InvokeWorkflow {
    
    /**
     * If the usecase should check for live children in {@link #checkPreconditions()}.
     * Type: {@link Boolean} or {@link String}
     */
    public static final String PARAM_CHECK_LIVE_CHILDREN = "checkLiveChildren";

    protected static final String LINKS_TO_DOCUMENT = "linksToDocument";
    
    private DocumentManager documentManager;
    private LinkManager linkManager;

    /**
     * Checks if the workflow event is supported and the parent of the document exists in the live
     * area.
     * 
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        if (!hasErrors()) {
            
            Document doc = getSourceDocument();

            if (!doc.getArea().equals(Publication.AUTHORING_AREA)) {
                addErrorMessage("This usecase can only be invoked from the authoring area.");
                return;
            }

            if (!doc.existsAreaVersion(Publication.LIVE_AREA)) {
                addErrorMessage("This usecase can only be invoked when the live version exists.");
            } else {
                checkChildren();
                //TODO : Florent : seems not to be used, and create cyclic, remove this code when compile ok
                //if remove ok, suppress LINKS_TO_DOCUMENT declaration, just use here seems
                //setParameter(LINKS_TO_DOCUMENT, new LinkList(doc));
            }
        }
    }

    /**
     * @see #PARAM_CHECK_LIVE_CHILDREN
     * @throws Exception if an error occurs.
     */
    protected void checkChildren() throws Exception {
        
        if (!getParameterAsBoolean(PARAM_CHECK_LIVE_CHILDREN, true)) {
            return;
        }

        Document doc = getSourceDocument();
        Document liveDoc = doc.getAreaVersion(Publication.LIVE_AREA);
        NodeSet subSite = SiteUtil.getSubSite(liveDoc.getLink().getNode());
        SiteNode node = liveDoc.getLink().getNode();
        subSite.remove(node);

        if (!subSite.isEmpty()) {
            addErrorMessage("You can't deactivate this document because it has children.");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        try {
            List nodes = new ArrayList();

            Document doc = getSourceDocument();
            if (doc != null) {
                nodes.add(doc);
                Document liveDoc = doc.getAreaVersion(Publication.LIVE_AREA);
                nodes.add(liveDoc);
                nodes.add(liveDoc.area().getSite());
            }
            return (Node[]) nodes.toArray(new Node[nodes.size()]);

        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        deactivate(getSourceDocument());
    }

    /**
     * Deactivates a document.
     * 
     * @param authoringDocument The authoring document.
     */
    protected void deactivate(Document authoringDocument) {
        boolean success = false;
        try {
            Document liveDocument = authoringDocument.getAreaVersion(Publication.LIVE_AREA);
            getDocumentManager().delete(liveDocument);
            success = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Deactivate document [" + authoringDocument + "]. Success: ["
                        + success + "]");
            }
        }
    }

    protected String getEvent() {
        return "deactivate";
    }
    
    /**
     * A list of links pointing to a document. Allows lazy loading from the usecase view.
     */
    //TODO : Florent : seems not to be used, and create cyclic, remove this code when compile ok
//    public class LinkList {
//        
//        private Document document;
//        private Document[] documents;
//        
//        /**
//         * @param doc The document to resolve the links from.
//         */
//        public LinkList(Document doc) {
//            this.document = doc;
//        }
//        
//        /**
//         * @return The link documents.
//         */
//        public Document[] getDocuments() {
//            if (this.documents == null) {
//                this.documents = getLinksToDocument();
//            }
//            return this.documents;
//        }
//        
//        protected Document[] getLinksToDocument() {
//            Set docs = new HashSet();
//            LinkManager linkMgr = Deactivate.this.getLinkManager();
//            try {
//                Document liveVersion = this.document.getAreaVersion(Publication.LIVE_AREA);
//                Document[] referencingDocs = linkMgr.getReferencingDocuments(liveVersion);
//                for (int d = 0; d < referencingDocs.length; d++) {
//                    Document doc = referencingDocs[d];
//                    if (doc.getArea().equals(Publication.LIVE_AREA)) {
//                        docs.add(doc);
//                    }
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            return (Document[]) docs.toArray(new Document[docs.size()]);
//        }
//
//    }

    protected DocumentManager getDocumentManager() {
        return documentManager;
    }

    /**
     * TODO: Bean wiring
     */
    public void setDocumentManager(DocumentManager documentManager) {
        this.documentManager = documentManager;
    }

    protected LinkManager getLinkManager() {
        return linkManager;
    }

    /**
     * TODO: Bean wiring
     */
    public void setLinkManager(LinkManager linkManager) {
        this.linkManager = linkManager;
    }

}
