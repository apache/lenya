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

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.ac.PolicyUtil;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryEventFactory;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.cms.workflow.usecases.InvokeWorkflow;
import org.apache.lenya.notification.Message;
import org.apache.lenya.notification.NotificationException;
import org.apache.lenya.notification.NotificationEventDescriptor;
import org.apache.lenya.notification.Text;
import org.apache.lenya.workflow.Version;
import org.apache.lenya.workflow.Workflowable;

/**
 * Reject usecase handler.
 */
public class Reject extends InvokeWorkflow {
    
    /**
     * The reason for rejection.
     */
    public static final String PARAM_REJECT_REASON = "rejectReason";
    
    /**
     * If a notification message shall be sent. 
     */
    public static final String PARAM_SEND_NOTIFICATION = "sendNotification";

    protected static final String MESSAGE_SUBJECT = "notification-message";
    protected static final String MESSAGE_DOCUMENT_REJECTED = "document-rejected";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {

        super.doExecute();

        boolean notify = Boolean.valueOf(getBooleanCheckboxParameter(PARAM_SEND_NOTIFICATION))
                .booleanValue();
        if (notify) {
            sendNotification(getSourceDocument());
        }
    }

    protected void sendNotification(Document authoringDocument) throws NotificationException,
            DocumentException, AccessControlException {

        User sender = getSession().getIdentity().getUser();

        String reason = getParameterAsString(PARAM_REJECT_REASON);
        Workflowable workflowable = WorkflowUtil.getWorkflowable(this.manager, getSession(),
                getLogger(), authoringDocument);
        Version versions[] = workflowable.getVersions();
        // current version is reject, want originating submit
        Version version = versions[versions.length - 2];

        // we assume that the document has been submitted, otherwise we do
        // nothing
        if (version.getEvent().equals("submit")) {

            String userId = version.getUserId();
            User user = PolicyUtil.getUser(this.manager, authoringDocument.getCanonicalWebappURL(),
                    userId, getLogger());

            Identifiable[] recipients = { user };

            Document authoringVersion = authoringDocument
                    .getAreaVersion(Publication.AUTHORING_AREA);
            String url;

            Proxy proxy = authoringVersion.getPublication().getProxy(authoringVersion, false);
            if (proxy != null) {
                url = proxy.getURL(authoringVersion);
            } else {
                Request request = ContextHelper.getRequest(this.context);
                final String serverUrl = "http://" + request.getServerName() + ":"
                        + request.getServerPort();
                final String webappUrl = authoringVersion.getCanonicalWebappURL();
                url = serverUrl + request.getContextPath() + webappUrl;
            }
            
            Text[] subjectParams = { new Text(getEvent(), true) };
            Text[] params = { new Text(reason, false), new Text(url, false) };
            Text subject = new Text(MESSAGE_SUBJECT, subjectParams);
            Text body = new Text(MESSAGE_DOCUMENT_REJECTED, params);
            Message message = new Message(subject, body, sender, recipients);

            NotificationEventDescriptor descriptor = new NotificationEventDescriptor(message);
            RepositoryEvent event = RepositoryEventFactory.createEvent(this.manager, getSession(),
                    getLogger(), descriptor);
            getSession().enqueueEvent(event);
        }
    }
}
