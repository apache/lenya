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

import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryEventFactory;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.inbox.Inbox;
import org.apache.lenya.inbox.InboxManager;

/**
 * Notification test.
 */
public class NotificationTest extends AbstractAccessControlTest {

    protected static final String SUBJECT = "hello";

    /**
     * The test.
     * @throws Exception
     */
    public void testNotification() throws Exception {

        login("lenya");

        Session session = getFactory().getSession();

        User lenya = getAccreditableManager().getUserManager().getUser("lenya");
        User alice = getAccreditableManager().getUserManager().getUser("alice");

        Identifiable[] recipients = { alice };

        Message message = new Message(SUBJECT, new String[0], "body", new String[0], lenya,
                recipients);
        NotificationEventDescriptor descr = new NotificationEventDescriptor(message);
        RepositoryEvent event = RepositoryEventFactory.createEvent(getManager(), session,
                getLogger(), descr);

        session.enqueueEvent(event);
        
        Inbox inbox = getInbox(alice);
        cleanUp(inbox);
        
        assertFalse(containsMessage(inbox));
        session.commit();
        Thread.sleep(100);
        assertTrue(containsMessage(inbox));
        
        cleanUp(inbox);

    }

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

    protected boolean containsMessage(Inbox inbox) {
        Message[] messages = inbox.getMessages();
        if (messages.length == 0) {
            return false;
        }
        return messages[messages.length - 1].getSubject().equals(SUBJECT);
    }
    
    protected void cleanUp(Inbox inbox) {
        Message[] messages = inbox.getMessages();
        for (int i = 0; i < messages.length; i++) {
            if (messages[i].getSubject().equals(SUBJECT)) {
                inbox.remove(messages[i]);
            }
        }
    }
}
