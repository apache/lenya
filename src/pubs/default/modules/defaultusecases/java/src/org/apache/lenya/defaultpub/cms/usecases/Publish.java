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
package org.apache.lenya.defaultpub.cms.usecases;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.util.DocumentSet;
import org.apache.lenya.cms.publication.util.DocumentVisitor;
import org.apache.lenya.cms.site.NodeFactory;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.cms.site.usecases.AssetUtil;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.usecase.scheduling.UsecaseScheduler;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.notification.Message;
import org.apache.lenya.notification.NotificationException;
import org.apache.lenya.notification.NotificationUtil;
import org.apache.lenya.workflow.WorkflowException;

/**
 * Publish usecase handler.
 * 
 * @version $Id$
 */
public class Publish extends DocumentUsecase implements DocumentVisitor {

    protected static final String MESSAGE_SUBJECT = "notification-message";
    protected static final String MESSAGE_DOCUMENT_PUBLISHED = "document-published";
    protected static final String MISSING_DOCUMENTS = "missingDocuments";
    protected static final String SUBTREE = "subtree";
    protected static final String ALLOW_SINGLE_DOCUMENT = "allowSingleDocument";
    protected static final String SCHEDULE = "schedule";
    protected static final String SCHEDULE_TIME = "schedule.time";
    protected static final String SEND_NOTIFICATION = "sendNotification";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        Date now = new GregorianCalendar().getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        setParameter(SCHEDULE_TIME, format.format(now));

        setParameter(SEND_NOTIFICATION, Boolean.TRUE);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected org.apache.lenya.cms.repository.Node[] getNodesToLock() throws UsecaseException {
        try {
            List nodes = new ArrayList();
            DocumentSet set = new DocumentSet();

            Document doc = getSourceDocument();
            set.addAll(SiteUtil.getSubSite(this.manager, doc));
            Map targets = SiteUtil.getTransferedSubSite(this.manager,
                    doc,
                    Publication.LIVE_AREA,
                    SiteUtil.MODE_REPLACE);
            Document[] docs = set.getDocuments();
            for (int i = 0; i < docs.length; i++) {
                nodes.add(docs[i].getRepositoryNode());
                Document target = (Document) targets.get(docs[i]);
                nodes.add(target.getRepositoryNode());
                nodes.addAll(AssetUtil.getCopiedAssetNodes(docs[i],
                        target,
                        this.manager,
                        getLogger()));
            }

            nodes.add(SiteUtil.getSiteStructure(this.manager,
                    getDocumentFactory(),
                    doc.getPublication(),
                    Publication.LIVE_AREA).getRepositoryNode());
            return (org.apache.lenya.cms.repository.Node[]) nodes.toArray(new org.apache.lenya.cms.repository.Node[nodes.size()]);

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

            if (!WorkflowUtil.canInvoke(this.manager,
                    getSession(),
                    getLogger(),
                    getSourceDocument(),
                    event)) {
                setParameter(ALLOW_SINGLE_DOCUMENT, Boolean.toString(false));
                addInfoMessage("The single document cannot be published because the workflow event cannot be invoked.");
            } else {
                setParameter(ALLOW_SINGLE_DOCUMENT, Boolean.toString(true));
            }

            Publication publication = document.getPublication();
            DocumentFactory map = document.getFactory();

            List missingDocuments = new ArrayList();

            ServiceSelector selector = null;
            SiteManager siteManager = null;
            try {
                selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
                siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());

                String path = SiteUtil.getPath(this.manager, document);
                SiteNode liveNode = NodeFactory.getNode(document.getPublication(),
                        Publication.LIVE_AREA,
                        path);
                SiteNode[] requiredNodes = siteManager.getRequiredResources(map, liveNode);

                for (int i = 0; i < requiredNodes.length; i++) {

                    DocumentSet liveDocs = SiteUtil.getExistingDocuments(map, requiredNodes[i]);
                    if (liveDocs.isEmpty()) {
                        Document authoringDoc = map.get(requiredNodes[i].getPublication(),
                                Publication.AUTHORING_AREA,
                                requiredNodes[i].getPath());
                        if (authoringDoc.exists()) {
                            missingDocuments.add(authoringDoc);
                        } else {
                            missingDocuments.add(authoringDoc.getTranslation(authoringDoc.getPublication()
                                    .getDefaultLanguage()));
                        }
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (selector != null) {
                    if (siteManager != null) {
                        selector.release(siteManager);
                    }
                    this.manager.release(selector);
                }
            }

            if (!missingDocuments.isEmpty()) {
                addErrorMessage("Cannot publish document unless the following documents are published:");
                setParameter(MISSING_DOCUMENTS, missingDocuments);
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {

        boolean schedule = Boolean.valueOf(getBooleanCheckboxParameter(SCHEDULE)).booleanValue();
        if (schedule) {
            deleteParameter(SCHEDULE);
            String dateString = getParameterAsString(SCHEDULE_TIME);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UsecaseScheduler scheduler = null;
            try {
                Date date = null;
                try {
                    date = format.parse(dateString);
                } catch (ParseException e) {
                    addErrorMessage("The scheduler date must be of the form 'yyyy-MM-dd HH:mm:ss'.");
                }
                if (date != null) {
                    scheduler = (UsecaseScheduler) this.manager.lookup(UsecaseScheduler.ROLE);
                    scheduler.schedule(this, date);
                }
            } finally {
                if (scheduler != null) {
                    this.manager.release(scheduler);
                }
            }
        } else {
            super.doExecute();
            if (isSubtreeEnabled()) {
                publishAll(getSourceDocument());
            } else {
                publish(getSourceDocument());
            }
        }
    }

    /**
     * Publishes a document.
     * 
     * @param authoringDocument The authoring document.
     */
    protected void publish(Document authoringDocument) {

        DocumentManager documentManager = null;

        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            documentManager.copyToArea(authoringDocument, Publication.LIVE_AREA);
            WorkflowUtil.invoke(this.manager,
                    getSession(),
                    getLogger(),
                    authoringDocument,
                    getEvent());

            boolean notify = Boolean.valueOf(getBooleanCheckboxParameter(SEND_NOTIFICATION))
                    .booleanValue();
            if (notify) {
                sendNotification(authoringDocument);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (documentManager != null) {
                this.manager.release(documentManager);
            }
        }
    }

    protected void sendNotification(Document authoringDocument) throws NotificationException,
            DocumentException {
        User sender = getSession().getIdentity().getUser();
        Identifiable[] recipients = { sender };
        Document liveVersion = authoringDocument.getAreaVersion(Publication.LIVE_AREA);

        String url;

        Proxy proxy = liveVersion.getPublication().getProxy(liveVersion, false);
        if (proxy != null) {
            url = proxy.getURL(liveVersion);
        } else {
            Request request = ContextHelper.getRequest(this.context);
            final String serverUrl = "http://" + request.getServerName() + ":"
                    + request.getServerPort();
            final String webappUrl = liveVersion.getCanonicalWebappURL();
            url = serverUrl + request.getContextPath() + webappUrl;
        }
        String[] params = { url };
        Message message = new Message(MESSAGE_SUBJECT,
                new String[0],
                MESSAGE_DOCUMENT_PUBLISHED,
                params);

        NotificationUtil.notify(this.manager, recipients, sender, message);
    }

    /**
     * @return The event to invoke.
     */
    private String getEvent() {
        return "publish";
    }

    /**
     * Publishes a document or the subtree below a document, based on the parameter SUBTREE.
     * 
     * @param document The document.
     */
    protected void publishAll(Document document) {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Publishing document [" + document + "]");
            getLogger().debug("Subtree publishing: [" + isSubtreeEnabled() + "]");
        }

        try {
            DocumentSet set = SiteUtil.getSubSite(this.manager, document);
            SiteUtil.sortAscending(this.manager, set);
            set.visit(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Publishing completed.");
        }
    }

    /**
     * Returns whether subtree publishing is enabled.
     * 
     * @return A boolean value.
     */
    protected boolean isSubtreeEnabled() {
        String value = getParameterAsString(SUBTREE);
        return value != null;
    }

    /**
     * @throws PublicationException
     * @see org.apache.lenya.cms.publication.util.DocumentVisitor#visitDocument(org.apache.lenya.cms.publication.Document)
     */
    public void visitDocument(Document document) throws PublicationException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Visiting resource [" + document + "]");
        }

        try {
            DocumentLocator parentLocator = document.getLocator().getParent();
            Document parent = document.getFactory().get(parentLocator);
            boolean publish = true;
            if (parent != null) {
                Document liveParent = parent.getAreaVersion(Publication.LIVE_AREA);
                if (!liveParent.exists()) {
                    publish = false;
                }
            }
            if (publish) {
                publishAllLanguageVersions(document);
            }
        } catch (WorkflowException e) {
            throw new PublicationException(e);
        }
    }

    /**
     * Publishes all existing language versions of a document.
     * 
     * @param document The document.
     * @throws PublicationException if an error occurs.
     * @throws WorkflowException
     */
    protected void publishAllLanguageVersions(Document document) throws PublicationException,
            WorkflowException {
        String[] languages = document.getPublication().getLanguages();

        try {
            for (int i = 0; i < languages.length; i++) {
                if (document.existsTranslation(languages[i])) {
                    Document version = document.getTranslation(languages[i]);
                    if (WorkflowUtil.canInvoke(this.manager,
                            getSession(),
                            getLogger(),
                            version,
                            getEvent())) {
                        publish(version);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
