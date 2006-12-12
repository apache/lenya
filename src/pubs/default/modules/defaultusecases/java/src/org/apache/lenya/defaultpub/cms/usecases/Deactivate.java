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
package org.apache.lenya.defaultpub.cms.usecases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.linking.LinkManager;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.usecases.InvokeWorkflow;

/**
 * Deactivate usecase handler.
 * 
 * @version $Id$
 */
public class Deactivate extends InvokeWorkflow {

    protected static final String LINKS_TO_DOCUMENT = "linksToDocument";

    /**
     * Checks if the workflow event is supported and the parent of the document exists in the live
     * area.
     * 
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        if (!hasErrors()) {

            if (!getSourceDocument().getArea().equals(Publication.AUTHORING_AREA)) {
                addErrorMessage("This usecase can only be invoked from the authoring area.");
                return;
            }

            if (!getSourceDocument().existsAreaVersion(Publication.LIVE_AREA)) {
                addErrorMessage("This usecase can only be invoked when the live version exists.");
            } else {
                Document liveDoc = getSourceDocument().getAreaVersion(Publication.LIVE_AREA);
                NodeSet subSite = SiteUtil.getSubSite(this.manager, liveDoc.getLink().getNode());
                SiteNode node = liveDoc.getLink().getNode();
                subSite.remove(node);

                if (!subSite.isEmpty()) {
                    addErrorMessage("You can't deactivate this document because it has children.");
                }
            }

            setParameter(LINKS_TO_DOCUMENT, getLinksToDocument());
        }
    }

    protected Document[] getLinksToDocument() {
        Set docs = new HashSet();
        LinkManager linkMgr = null;
        try {
            linkMgr = (LinkManager) this.manager.lookup(LinkManager.ROLE);
            Document liveVersion = getSourceDocument().getAreaVersion(Publication.LIVE_AREA);
            Document[] referencingDocs = linkMgr.getReferencingDocuments(liveVersion);
            for (int d = 0; d < referencingDocs.length; d++) {
                Document doc = referencingDocs[d];
                if (doc.getArea().equals(Publication.LIVE_AREA)) {
                    docs.add(doc);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (linkMgr != null) {
                this.manager.release(linkMgr);
            }
        }
        return (Document[]) docs.toArray(new Document[docs.size()]);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected org.apache.lenya.cms.repository.Node[] getNodesToLock() throws UsecaseException {
        try {
            List nodes = new ArrayList();

            Document doc = getSourceDocument();
            Document liveDoc = doc.getAreaVersion(Publication.LIVE_AREA);
            nodes.add(liveDoc.getRepositoryNode());
            nodes.add(liveDoc.area().getSite().getRepositoryNode());
            return (org.apache.lenya.cms.repository.Node[]) nodes.toArray(new org.apache.lenya.cms.repository.Node[nodes.size()]);

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

        DocumentManager documentManager = null;
        SourceResolver resolver = null;
        Source source = null;
        try {
            Document liveDocument = authoringDocument.getAreaVersion(Publication.LIVE_AREA);

            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            documentManager.delete(liveDocument);

            success = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Deactivate document [" + authoringDocument + "]. Success: ["
                        + success + "]");
            }
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }

    }

    protected String getEvent() {
        return "deactivate";
    }

}