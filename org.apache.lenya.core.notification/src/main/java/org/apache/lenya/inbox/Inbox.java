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

/**
 * An inbox.
 */
public interface Inbox {
    
    /**
     * @return All messages in this inbox.
     */
    InboxMessage[] getMessages();
    
    /**
     * @param message The message to add.
     * @return The inbox message which wraps the message.
     */
    InboxMessage add(Message message);

    /**
     * @param message The message to remove.
     */
    void remove(InboxMessage message);
    
    /**
     * @param id The ID.
     * @return The message with this ID.
     */
    InboxMessage getMessage(String id);
    
}
