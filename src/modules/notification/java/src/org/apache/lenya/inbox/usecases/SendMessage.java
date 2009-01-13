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
package org.apache.lenya.inbox.usecases;

import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.inbox.InboxMessage;
import org.apache.lenya.notification.Message;
import org.apache.lenya.notification.NotificationUtil;
import org.apache.lenya.util.Assert;

/**
 * Show and manage an inbox.
 */
public class SendMessage extends AbstractInboxUsecase {

    protected static final String PARAM_BODY = "body";
    protected static final String PARAM_SUBJECT = "subject";
    protected static final String PARAM_RECIPIENT = "recipient";
    protected static final String PARAM_USER = "user";
    protected static final String PARAM_REPLY_TO = "replyTo";

    protected void prepareView() throws Exception {
        super.prepareView();
        
        String replyToMessageId = getParameterAsString(PARAM_REPLY_TO);
        if (replyToMessageId != null) {
            org.apache.lenya.inbox.Inbox inbox = getInbox();
            InboxMessage message = inbox.getMessage(replyToMessageId);
            if (message != null) {
                User sender = (User) message.getMessage().getSender();
                setParameter(PARAM_RECIPIENT, sender.getId());
            }
        }

        User user = getSession().getIdentity().getUser();
        setParameter(PARAM_USER, user);

        try {
            UserManager userManager = user.getAccreditableManager().getUserManager();
            User[] users = userManager.getUsers();
            setParameter("users", users);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        if (getRecipient().equals("")) {
            addErrorMessage("Please choose a recipient.");
        }

        if (getSubject().equals("")) {
            addErrorMessage("Please choose a subject.");
        }
    }

    protected String getSubject() {
        return getParameterAsString(PARAM_SUBJECT);
    }

    protected String getRecipient() {
        return getParameterAsString(PARAM_RECIPIENT);
    }

    protected String getBody() {
        return getParameterAsString(PARAM_BODY);
    }

    protected void doExecute() throws Exception {
        super.doExecute();

        String recipientId = getRecipient();
        User sender = getSession().getIdentity().getUser();
        User recipient;

        try {
            UserManager userManager = sender.getAccreditableManager().getUserManager();
            recipient = userManager.getUser(recipientId);
            Assert.notNull("user " + recipientId, recipient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Identifiable[] recipients = { recipient };
        String subject = getSubject();
        String body = getBody();

        Message message = new Message(subject, new String[0], body, new String[0], sender,
                recipients);
        NotificationUtil.notify(this.manager, message);

    }

}
