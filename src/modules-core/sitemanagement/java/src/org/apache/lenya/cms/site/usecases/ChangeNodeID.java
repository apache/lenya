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
package org.apache.lenya.cms.site.usecases;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.NodeIterator;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.transaction.TransactionException;

/**
 * Change the node ID of a document.
 * 
 * @version $Id$
 */
public class ChangeNodeID extends DocumentUsecase {

    protected static final String NODE_ID = "nodeId";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        Document document = getSourceDocument();
        if (document != null) {
            setParameter(NODE_ID, document.getName());
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {

        List nodes = new ArrayList();

        try {
            if(getSourceDocument() != null) {
                Node siteNode = getSourceDocument().area().getSite().getRepositoryNode();
                nodes.add(siteNode);

                Document sourceDocument = getSourceDocument();
  
                NodeSet subsite = SiteUtil.getSubSite(this.manager, sourceDocument.getLink().getNode());
                for (NodeIterator i = subsite.iterator(); i.hasNext();) {
                    SiteNode node = i.next();
                    String[] languages = node.getLanguages();
                    for (int l = 0; l < languages.length; l++) {
                        Document doc = node.getLink(languages[l]).getDocument();
                        nodes.add(doc.getRepositoryNode());
                    }
                }
            }
        } catch (Exception e) {
            throw new UsecaseException(e);
        }

        return (Node[]) nodes.toArray(new Node[nodes.size()]);
    }

    protected List getAllLanguageVersionNodes(Document doc) throws DocumentException,
            TransactionException, DocumentBuildException {
        String[] languages = doc.getLanguages();
        List nodes = new ArrayList();
        for (int i = 0; i < languages.length; i++) {
            nodes.add(doc.getTranslation(languages[i]).getRepositoryNode());
        }
        return nodes;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (hasErrors()) {
            return;
        }

        if (!getSourceDocument().getArea().equals(Publication.AUTHORING_AREA)) {
            addErrorMessage("This usecase can only be invoked in the authoring area!");
        } else {
            if (getSourceDocument().existsAreaVersion(Publication.LIVE_AREA)) {
                addErrorMessage("This usecase cannot be invoked when the live version exists!");
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        String nodeId = getParameterAsString(NODE_ID);
        DocumentBuilder builder = getSourceDocument().getPublication().getDocumentBuilder();
        if (!builder.isValidDocumentName(nodeId)) {
            addErrorMessage("The document ID is not valid.");
        } else {
            DocumentLocator target = getTargetLocator();
            Publication pub = getDocumentFactory().getPublication(target.getPublicationId());
            SiteStructure site = pub.getArea(target.getArea()).getSite();
            if (site.contains(target.getPath(), target.getLanguage())) {
                addErrorMessage("The document does already exist.");
            }
        }
    }

    protected DocumentLocator getTargetLocator() throws DocumentBuildException, SiteException,
            DocumentException {
        String nodeId = getParameterAsString(NODE_ID);
        Document doc = getSourceDocument();
        DocumentLocator loc = DocumentLocator.getLocator(doc.getPublication().getId(), doc
                .getArea(), doc.getPath(), doc.getLanguage());
        DocumentLocator parent = loc.getParent();
        return parent.getChild(nodeId);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        Document targetDoc;
        Document source = getSourceDocument();
        DocumentLocator target = getTargetLocator();
        DocumentManager documentManager = null;
        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            documentManager.moveAll(source.area(), source.getPath(), source.area(), target
                    .getPath());
            targetDoc = getDocumentFactory().get(target);
        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }

        setTargetDocument(targetDoc);
    }

    /**
     * Returns the resulting document when the node ID would be changed.
     * @return A document.
     */
    protected String getNewDocumentId() {
        String nodeId = getParameterAsString(NODE_ID);

        String oldPath;
        try {
            oldPath = getSourceDocument().getPath();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        int lastSlashIndex = oldPath.lastIndexOf("/");
        String strippedDocumentId = oldPath.substring(0, lastSlashIndex + 1);
        String newDocumentId = strippedDocumentId + nodeId;

        return newDocumentId;
    }
}
