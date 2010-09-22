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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.ac.PolicyUtil;
import org.apache.lenya.cms.linking.LinkManager;
import org.apache.lenya.cms.linking.LinkResolver;
import org.apache.lenya.cms.linking.LinkTarget;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreHelper;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryEventFactory;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.usecase.scheduling.UsecaseScheduler;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.cms.workflow.usecases.InvokeWorkflow;
import org.apache.lenya.notification.Message;
import org.apache.lenya.notification.NotificationEventDescriptor;
import org.apache.lenya.notification.NotificationException;
import org.apache.lenya.notification.Text;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflowable;

/**
 * Publish usecase handler.
 * 
 * @version $Id$
 */
public class Publish extends InvokeWorkflow {

    /**
     * If the usecase should check for missing live ancestors in {@link #checkPreconditions()}.
     * Type: {@link Boolean} or {@link String}
     */
    public static final String PARAM_CHECK_MISSING_ANCESTORS = "checkMissingAncestors";

    /**
     * If a notification message shall be sent.
     */
    public static final String PARAM_SEND_NOTIFICATION = "sendNotification";

    /**
     * The notification message to send in addition to the default message.
     */
    public static final String PARAM_USER_NOTIFICATION_MESSAGE = "userNotificationMessage";

    protected static final String MESSAGE_SUBJECT = "notification-message";
    protected static final String MESSAGE_DOCUMENT_PUBLISHED = "document-published";
    protected static final String SCHEDULE = "schedule";
    protected static final String SCHEDULE_TIME = "schedule.time";
    protected static final String CAN_SEND_NOTIFICATION = "canSendNotification";
    protected static final String UNPUBLISHED_LINKS = "unpublishedLinks";
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        if (hasErrors() || getSourceDocument() == null) {
            return;
        }

        Date now = new GregorianCalendar().getTime();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        setParameter(SCHEDULE_TIME, format.format(now));

        Boolean canSendNotification = Boolean.valueOf(canNotifySubmitter());
        setParameter(CAN_SEND_NOTIFICATION, canSendNotification);
        setParameter(PARAM_SEND_NOTIFICATION, canSendNotification);
        
        setParameter(UNPUBLISHED_LINKS, new LinkList(this.manager, getSourceDocument()));
        
    }
    
    protected boolean canNotifySubmitter() {
        
        boolean shallNotifySubmitter = false;
        Workflowable workflowable = WorkflowUtil.getWorkflowable(this.manager, getSession(),
                getLogger(), getSourceDocument());
        Version versions[] = workflowable.getVersions();
        
        // consider the case that there was no submit transition
        if (versions.length > 0) {
            Version version = versions[versions.length - 1];
    
            // we check if the document has been submitted, otherwise we do nothing
            if (version.getEvent().equals("submit")) {
                shallNotifySubmitter = true;
            }
        }
        return shallNotifySubmitter;
    }
    
    protected boolean hasBrokenLinks() {
        LinkManager linkMgr = null;
        LinkResolver resolver = null;
        try {
            linkMgr = (LinkManager) this.manager.lookup(LinkManager.ROLE);
            resolver = (LinkResolver) this.manager.lookup(LinkResolver.ROLE);
            org.apache.lenya.cms.linking.Link[] links = linkMgr.getLinksFrom(getSourceDocument());
            for (int i = 0; i < links.length; i++) {
                LinkTarget target = resolver.resolve(getSourceDocument(), links[i].getUri());
                if (!target.exists()) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (linkMgr != null) {
                this.manager.release(linkMgr);
            }
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
        return false;
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected org.apache.lenya.cms.repository.Node[] getNodesToLock() throws UsecaseException {
        try {
            List nodes = new ArrayList();

            Document doc = getSourceDocument();
            if(doc != null) {
                nodes.add(doc.getRepositoryNode());
                
                // lock the authoring site to avoid having live nodes for which no corresponding
                // authoring node exists
                nodes.add(doc.area().getSite().getRepositoryNode());
                
                // lock the live site to avoid overriding changes made by others
                SiteStructure liveSite = doc.getPublication().getArea(Publication.LIVE_AREA).getSite();
                nodes.add(liveSite.getRepositoryNode());
            }

            return (org.apache.lenya.cms.repository.Node[]) nodes
                    .toArray(new org.apache.lenya.cms.repository.Node[nodes.size()]);

        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * Checks if the workflow event is supported and the parent of the document
     * exists in the live area.
     * 
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!hasErrors()) {

            Document document = getSourceDocument();
            if (!document.getArea().equals(Publication.AUTHORING_AREA)) {
                addErrorMessage("This usecase can only be invoked from the authoring area.");
                return;
            }

            checkMissingAncestors();
            
            if (hasBrokenLinks()) {
                addInfoMessage("publish-broken-links");
            }
        }
    }

    /**
     * @see #PARAM_CHECK_MISSING_ANCESTORS
     * @throws Exception if an error occurs.
     */
    protected void checkMissingAncestors() throws Exception {
        
        if (!getParameterAsBoolean(PARAM_CHECK_MISSING_ANCESTORS, true)) {
            return;
        }
        
        Document document = getSourceDocument();
        Publication publication = document.getPublication();
        DocumentFactory map = document.getFactory();
        SiteStructure liveSite = publication.getArea(Publication.LIVE_AREA).getSite();

        List missingDocuments = new ArrayList();

        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());

            if (!liveSite.contains(document.getPath())) {
                DocumentLocator liveLoc = document.getLocator().getAreaVersion(
                        Publication.LIVE_AREA);
                DocumentLocator[] requiredNodes = siteManager
                        .getRequiredResources(map, liveLoc);
                for (int i = 0; i < requiredNodes.length; i++) {
                    String path = requiredNodes[i].getPath();
                    if (!liveSite.contains(path)) {
                        Link link = getExistingLink(path, document);
                        if (link != null) {
                            missingDocuments.add(link.getDocument());
                        }
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
            addErrorMessage("publish-missing-documents");
            for (Iterator i = missingDocuments.iterator(); i.hasNext();) {
                Document doc = (Document) i.next();
                /*
                 * This doesn't work yet, see
                 * https://issues.apache.org/jira/browse/COCOON-2057
                 * String[] params = { doc.getCanonicalWebappURL(),
                 * doc.getPath() + " (" + doc.getLanguage() + ")" };
                 */
                String[] params = { doc.getPath() + ":" + doc.getLanguage(),
                        DublinCoreHelper.getTitle(doc, true) };
                addErrorMessage("missing-document", params);
            }
        }
    }

    /**
     * Returns a link of a certain node, preferably in the document's language,
     * or <code>null</code> if the node has no links.
     * @param path The path of the node.
     * @param document The document.
     * @return A link or <code>null</code>.
     * @throws SiteException if an error occurs.
     */
    protected Link getExistingLink(String path, Document document) throws SiteException {
        SiteNode node = document.area().getSite().getNode(path);
        Link link = null;
        String uuid = node.getUuid();
        if (uuid != null) {
            if (node.hasLink(document.getLanguage())) {
                link = node.getLink(document.getLanguage());
            } else if (node.getLanguages().length > 0) {
                link = node.getLink(node.getLanguages()[0]);
            }
        }
        return link;
    }

    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        boolean schedule = Boolean.valueOf(getBooleanCheckboxParameter(SCHEDULE)).booleanValue();
        if (schedule) {
            String dateString = getParameterAsString(SCHEDULE_TIME);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                format.parse(dateString);
            } catch (ParseException e) {
                addErrorMessage("scheduler-date-format-invalid");
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
                Date date = format.parse(dateString);
                scheduler = (UsecaseScheduler) this.manager.lookup(UsecaseScheduler.ROLE);
                scheduler.schedule(this, date);
            } catch (ParseException e) {
                addErrorMessage("scheduler-date-format-invalid");
            } finally {
                if (scheduler != null) {
                    this.manager.release(scheduler);
                }
            }
        } else {
            super.doExecute();
            publish(getSourceDocument());
        }
    }

    protected void publish(Document authoringDocument) throws DocumentException, SiteException,
            PublicationException {

        createAncestorNodes(authoringDocument);

        DocumentManager documentManager = null;

        try {
            documentManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            documentManager.copyToArea(authoringDocument, Publication.LIVE_AREA);

            boolean notify = Boolean.valueOf(getBooleanCheckboxParameter(PARAM_SEND_NOTIFICATION))
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
            DocumentException, AccessControlException {
        
        if (!getParameterAsBoolean(CAN_SEND_NOTIFICATION, false)) {
            getLogger().error("Can't notify submitter of document [" + authoringDocument +
                    "] because it hasn't been submitted.");
            return;
        }

        Workflowable workflowable = WorkflowUtil.getWorkflowable(this.manager, getSession(),
                getLogger(), authoringDocument);
        Version versions[] = workflowable.getVersions();
        
        // obtain submitted version
        Version version = versions[versions.length - 2];
        
        String userId = version.getUserId();
        User user = PolicyUtil.getUser(this.manager, authoringDocument.getCanonicalWebappURL(),
                userId, getLogger());

        Identifiable[] recipients = { user };

        Document liveVersion = authoringDocument.getAreaVersion(Publication.LIVE_AREA);
        String url;

        url = getWebUrl(liveVersion);
        User sender = getSession().getIdentity().getUser();
        
        Text[] subjectParams = { new Text(getEvent(), true) };
        Text subject = new Text(MESSAGE_SUBJECT, subjectParams);
        
        String userMessage = getParameterAsString(PARAM_USER_NOTIFICATION_MESSAGE, "");
        Text[] params = { new Text(url, false), new Text(userMessage, false) };
        Text body = new Text(MESSAGE_DOCUMENT_PUBLISHED, params);
        Message message = new Message(subject, body, sender, recipients);

        NotificationEventDescriptor descriptor = new NotificationEventDescriptor(message);
        RepositoryEvent event = RepositoryEventFactory.createEvent(this.manager, liveVersion,
                getLogger(), descriptor);
        getSession().enqueueEvent(event);
    }

    /**
     * @param document A document.
     * @return The complete HTTP URL of the document when requested via the web.
     */
    protected String getWebUrl(Document document) {
        String url;
        Proxy proxy = document.getPublication().getProxy(document, false);
        if (proxy != null) {
            url = proxy.getURL(document);
        } else {
            Request request = ContextHelper.getRequest(this.context);
            final String serverUrl = "http://" + request.getServerName() + ":"
                    + request.getServerPort();
            final String webappUrl = document.getCanonicalWebappURL();
            url = serverUrl + request.getContextPath() + webappUrl;
        }
        return url;
    }

    protected void createAncestorNodes(Document document) throws PublicationException,
            DocumentException, SiteException {
        SiteStructure liveSite = document.getPublication().getArea(Publication.LIVE_AREA).getSite();
        String[] steps = document.getPath().substring(1).split("/");
        int s = 0;
        String path = "";
        while (s < steps.length) {
            if (!liveSite.contains(path)) {
                liveSite.add(path);
            }
            path += "/" + steps[s];
            s++;
        }
    }
    
    /**
     * A list of links originating from a document. Allows lazy loading from the usecase view.
     */
    public static class LinkList {
        
        private Document document;
        private Document[] documents;
        private ServiceManager manager;
        
        /**
         * @param manager The manager.
         * @param doc The document to resolve the links from.
         */
        public LinkList(ServiceManager manager, Document doc) {
            this.manager = manager;
            this.document = doc;
        }
        
        /**
         * @return The link documents.
         */
        public Document[] getDocuments() {
            if (this.documents == null) {
                this.documents = getUnpublishedLinks();
            }
            return this.documents;
        }
        
        protected Document[] getUnpublishedLinks() {
            Set docs = new HashSet();
            LinkManager linkMgr = null;
            LinkResolver resolver = null;
            try {
                linkMgr = (LinkManager) this.manager.lookup(LinkManager.ROLE);
                resolver = (LinkResolver) this.manager.lookup(LinkResolver.ROLE);
                org.apache.lenya.cms.linking.Link[] links = linkMgr.getLinksFrom(this.document);
                for (int i = 0; i < links.length; i++) {
                    LinkTarget target = resolver.resolve(this.document, links[i].getUri());
                    if (target.exists()) {
                        Document doc = target.getDocument();
                        if (!doc.existsAreaVersion(Publication.LIVE_AREA)) {
                            docs.add(doc);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (linkMgr != null) {
                    this.manager.release(linkMgr);
                }
                if (resolver != null) {
                    this.manager.release(resolver);
                }
            }
            return (Document[]) docs.toArray(new Document[docs.size()]);
        }

    }

}
