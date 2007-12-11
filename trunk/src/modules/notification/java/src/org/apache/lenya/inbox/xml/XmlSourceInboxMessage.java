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
package org.apache.lenya.inbox.xml;

import org.apache.lenya.inbox.AbstractInboxMessage;
import org.apache.lenya.notification.Message;

/**
 * XML source inbox message.
 */
public class XmlSourceInboxMessage extends AbstractInboxMessage {

    private XmlSourceInbox inbox;

    /**
     * @param inbox The inbox this message belongs to.
     * @param id The ID.
     * @param message The message to wrap.
     * @param markedAsRead if the message is marked as read.
     */
    public XmlSourceInboxMessage(XmlSourceInbox inbox, String id, Message message,
            boolean markedAsRead) {
        super(id, message, markedAsRead);
        this.inbox = inbox;
    }

    protected void changed() {
        this.inbox.save();
    }

}
