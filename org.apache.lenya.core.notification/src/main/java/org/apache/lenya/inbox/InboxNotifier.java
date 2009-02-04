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

import org.apache.avalon.framework.service.ServiceException;
import org.apache.lenya.ac.User;
import org.apache.lenya.notification.AbstractNotifier;
import org.apache.lenya.notification.Message;
import org.apache.lenya.notification.NotificationException;

/**
 * Notifier which adds the message to the recipients' inboxes.
 */
public class InboxNotifier extends AbstractNotifier {

    protected void notify(User user, Message message) throws NotificationException {
        
        InboxManager inboxManager = null;
        try {
            inboxManager = (InboxManager) this.manager.lookup(InboxManager.ROLE);
            Inbox inbox = inboxManager.getInbox(user);
            inbox.add(message);
        } catch (ServiceException e) {
            throw new NotificationException(e);
        } finally {
            if (inboxManager != null) {
                this.manager.release(inboxManager);
            }
        }

    }

}
