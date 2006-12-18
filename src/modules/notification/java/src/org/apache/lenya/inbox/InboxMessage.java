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
 * A message entry in an inbox.
 */
public interface InboxMessage {
    
    /**
     * Marks this message as read.
     * @param marked A boolean value.
     */
    void markAsRead(boolean marked);
    
    /**
     * @return if this message is marked as read.
     */
    boolean isMarkedAsRead();
    
    /**
     * @return The message which is wrapped by this inbox message.
     */
    Message getMessage();
    
    /**
     * @return The ID of this message.
     */
    String getId();

}
