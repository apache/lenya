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
package org.apache.lenya.notification;

import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.inbox.Inbox;
import org.apache.lenya.inbox.InboxManager;
import org.apache.lenya.inbox.InboxMessage;

/**
 * Base class for notification tests.
 */
public abstract class AbstractNotificationTest extends AbstractAccessControlTest {

    protected Inbox getInbox(User user) throws Exception {
        InboxManager inboxManager = null;
        try {
            inboxManager = (InboxManager) getManager().lookup(InboxManager.ROLE);
            return inboxManager.getInbox(user);
        }
        finally {
            if (inboxManager != null) {
                getManager().release(inboxManager);
            }
        }
    }

    protected boolean containsMessage(Inbox inbox, String subject) {
        InboxMessage[] messages = inbox.getMessages();
        if (messages.length == 0) {
            return false;
        }
        return messages[messages.length - 1].getMessage().getSubject().equals(subject);
    }
    
    protected InboxMessage getMessage(Inbox inbox, String subject) {
        InboxMessage[] messages = inbox.getMessages();
        for (int i = 0; i < messages.length; i++) {
            if (messages[i].getMessage().getSubject().equals(subject)) {
                return messages[i];
            }
        }
        return null;
    }
    
    protected void cleanUp(Inbox inbox, String subject) {
        InboxMessage[] messages = inbox.getMessages();
        for (int i = 0; i < messages.length; i++) {
            if (messages[i].getMessage().getSubject().equals(subject)) {
                inbox.remove(messages[i]);
            }
        }
    }
}
