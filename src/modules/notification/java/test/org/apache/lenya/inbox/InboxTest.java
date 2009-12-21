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
package org.apache.lenya.inbox;

import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.User;
import org.apache.lenya.inbox.xml.XmlSourceInbox;
import org.apache.lenya.notification.AbstractNotificationTest;
import org.apache.lenya.notification.Message;

/**
 * Inbox test.
 */
public class InboxTest extends AbstractNotificationTest {
    
    protected static final String SUBJECT = "hello";

    /**
     * The test.
     * @throws Exception if an error occurs.
     */
    public void testInbox() throws Exception {
        
        User lenya = getAccreditableManager().getUserManager().getUser("lenya");
        Inbox inbox = getInbox(lenya);
        
        cleanUp(inbox, SUBJECT);
        assertFalse(containsMessage(inbox, SUBJECT));
        
        Identifiable[] recipients = { lenya };

        Message message = new Message(SUBJECT, new String[0], "body", new String[0], lenya,
                recipients);
        InboxMessage inboxMessage = inbox.add(message);
        
        assertEquals(inboxMessage.getMessage().getSubject(), SUBJECT);
        
        assertFalse(inboxMessage.isMarkedAsRead());
        inboxMessage.markAsRead(true);
        assertTrue(inboxMessage.isMarkedAsRead());
        
        XmlSourceInbox xmlInbox = new XmlSourceInbox(getManager(), lenya);
        assertTrue(containsMessage(xmlInbox, SUBJECT));
        
        InboxMessage xmlMessage = getMessage(xmlInbox, SUBJECT);
        assertTrue(xmlMessage.isMarkedAsRead());
        
        cleanUp(inbox, SUBJECT);
        
    }

}
