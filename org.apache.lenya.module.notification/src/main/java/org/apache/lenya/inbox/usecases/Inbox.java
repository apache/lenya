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

import org.apache.lenya.inbox.InboxMessage;

/**
 * Show and manage an inbox.
 */
public class Inbox extends AbstractInboxUsecase {

    protected static final String PARAM_MESSAGE_ID = "messageId";
    protected static final String PARAM_NOF_MESSAGES = "nofMessages";
    protected static final String PARAM_NOF_UNREAD_MESSAGES = "nofUnreadMessages";

    protected void prepareView() throws Exception {
        super.prepareView();
        setParameter(PARAM_USER, getUser());

        org.apache.lenya.inbox.Inbox inbox = getInbox();

        String id = getParameterAsString(PARAM_MESSAGE_ID);
        if (id != null) {
            InboxMessage message = inbox.getMessage(id);
            if (message == null) {
                addErrorMessage("The message " + id + " does not exist.");
            } else {
                message.markAsRead(true);
            }
        }
        
        InboxMessage[] messages = inbox.getMessages();
        int nofUnreadMessages = 0;
        for (int i = 0; i < messages.length; i++) {
            if (!messages[i].isMarkedAsRead()) {
                nofUnreadMessages++;
            }
        }
        setParameter(PARAM_NOF_MESSAGES, new Integer(messages.length));
        setParameter(PARAM_NOF_UNREAD_MESSAGES, new Integer(nofUnreadMessages));
    }

}
