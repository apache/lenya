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

import org.apache.lenya.notification.Message;
import org.apache.lenya.util.Assert;

/**
 * Inbox message implementation.
 */
public abstract class AbstractInboxMessage implements InboxMessage {

    private boolean markedAsRead;
    private Message message;
    private String id;
    
    /**
     * @param id The ID.
     * @param message The message to wrap.
     * @param markedAsRead if the message is marked as read.
     */
    public AbstractInboxMessage(String id, Message message, boolean markedAsRead) {
        Assert.notNull("id", id);
        this.id = id;
        Assert.notNull("message", message);
        this.message = message;
        this.markedAsRead = markedAsRead;
    }
    
    public boolean isMarkedAsRead() {
        return this.markedAsRead;
    }

    public Message getMessage() {
        return this.message;
    }

    public void markAsRead(boolean marked) {
        this.markedAsRead = marked;
        changed();
    }

    protected abstract void changed();

    public String getId() {
        return this.id;
    }

}
