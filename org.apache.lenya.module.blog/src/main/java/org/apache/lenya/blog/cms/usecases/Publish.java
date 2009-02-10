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
package org.apache.lenya.blog.cms.usecases;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Node;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.cms.workflow.usecases.UsecaseWorkflowHelper;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflowable;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;

/**
 * Publish usecase handler.
 * 
 * @version $Id: Publish.java 209612 2005-07-07 16:52:44Z chestnut $
 */
public class Publish extends DocumentUsecase {

    protected static final String MISSING_DOCUMENTS = "missingDocuments";

    private DocumentManager documentManager;

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected Node[] getNodesToLock() throws UsecaseException {
        try {
            List nodes = new ArrayList();
            DocumentSet set = new DocumentSet();

            Document doc = getSourceDocument();
            NodeSet subsite = SiteUtil.getSubSite(doc.getLink().getNode());
            set.addAll(new DocumentSet(subsite.getDocuments()));

            Document[] documents = set.getDocuments();
            for (int i = 0; i < documents.length; i++) {
                nodes.add(documents[i]);
            }

            Area live = doc.getPublication().getArea(Publication.LIVE_AREA);
            nodes.add(live.getSite());
            return (Node[]) nodes.toArray(new Node[nodes.size()]);

        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * Checks if the workflow event is supported and the parent of the document exists in the live
     * area.
     * 
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!hasErrors()) {

            String event = getEvent();
            Document document = getSourceDocument();

            if (!document.getArea().equals(Publication.AUTHORING_AREA)) {
                addErrorMessage("This usecase can only be invoked from the authoring area.");
                return;
            }

            UsecaseWorkflowHelper.checkWorkflow(this, event, document, getLogger());
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        try {
            Document authoringDocument = getSourceDocument();
            if (authoringDocument.getResourceType().getName().equals("entry")) {
                updateBlogEntry(authoringDocument);
            }
            updateFeed();
            getDocumentManager().copyToArea(authoringDocument, Publication.LIVE_AREA);
            WorkflowUtil.invoke(authoringDocument, getEvent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void updateFeed() throws Exception {

        Document[] docs = new Document[2];
        org.w3c.dom.Document[] doms = new org.w3c.dom.Document[2];

        Publication pub = getSourceDocument().getPublication();
        Area authoring = pub.getArea(Publication.AUTHORING_AREA);
        Area live = pub.getArea(Publication.LIVE_AREA);
        String path = "/feeds/all/index";
        String language = pub.getDefaultLanguage();

        docs[0] = live.getSite().getNode(path).getLink(language).getDocument();
        docs[1] = authoring.getSite().getNode(path).getLink(language).getDocument();

        DateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormat ofsfmt = new SimpleDateFormat("Z");
        Date date = new Date();

        String dateofs = ofsfmt.format(date);
        String datestr = datefmt.format(date) + dateofs.substring(0, 3) + ":"
                + dateofs.substring(3, 5);

        for (int i = 0; i < 2; i++) {
            doms[i] = DocumentHelper.readDocument(docs[i].getInputStream());
            Element parent = doms[i].getDocumentElement();
            // set modified date on publish
            Element element = (Element) XPathAPI.selectSingleNode(parent,
                    "/*[local-name() = 'feed']/*[local-name() = 'modified']");
            DocumentHelper.setSimpleElementText(element, datestr);
            DocumentHelper.writeDocument(doms[i], docs[i].getOutputStream());
        }
    }

    protected void updateBlogEntry(Document doc) throws Exception {
        org.w3c.dom.Document dom = DocumentHelper.readDocument(doc.getInputStream());
        Element parent = dom.getDocumentElement();

        DateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormat ofsfmt = new SimpleDateFormat("Z");
        Date date = new Date();

        String dateofs = ofsfmt.format(date);
        String datestr = datefmt.format(date) + dateofs.substring(0, 3) + ":"
                + dateofs.substring(3, 5);

        // set modified date on re-publish
        Element element = (Element) XPathAPI.selectSingleNode(parent,
                "/*[local-name() = 'entry']/*[local-name() = 'modified']");
        DocumentHelper.setSimpleElementText(element, datestr);

        // set issued date on first time publish
        Workflowable dw = WorkflowUtil.getWorkflowable(doc);
        Version versions[] = dw.getVersions();
        boolean wasLive = false;
        for (int i = 0; i < versions.length; i++) {
            if (versions[i].getValue("is_live")) {
                wasLive = true;
                break;
            }
        }
        if (!wasLive) {
            element = (Element) XPathAPI.selectSingleNode(parent,
                    "/*[local-name() = 'entry']/*[local-name() = 'issued']");
            DocumentHelper.setSimpleElementText(element, datestr);
        }

        DocumentHelper.writeDocument(dom, doc.getOutputStream());
    }

    /**
     * @return The event to invoke.
     */
    private String getEvent() {
        return "publish";
    }

    public DocumentManager getDocumentManager() {
        return documentManager;
    }

    /**
     * TODO: Bean wiring
     */
    public void setDocumentManager(DocumentManager documentManager) {
        this.documentManager = documentManager;
    }

}