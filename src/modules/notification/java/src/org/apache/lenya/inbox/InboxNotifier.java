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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Groupable;
import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.User;
import org.apache.lenya.notification.AbstractNotifier;
import org.apache.lenya.notification.Message;
import org.apache.lenya.notification.NotificationException;

/**
 * Notifier which adds the message to the recipients' inboxes.
 */
public class InboxNotifier extends AbstractNotifier {

    public void notify(Message message) throws NotificationException {
        Identifiable[] recipients = message.getRecipients();

        Set users = new HashSet();
        for (int i = 0; i < recipients.length; i++) {
            if (recipients[i] instanceof User) {
                users.add(recipients[i]);
            } else if (recipients[i] instanceof Group) {
                Group group = (Group) recipients[i];
                Groupable[] members = group.getMembers();
                for (int m = 0; m < members.length; m++) {
                    if (members[m] instanceof User) {
                        users.add(members[m]);
                    }
                }
            } else {
                throw new NotificationException("Unsupported recipient type ["
                        + recipients[i].getClass() + "]");
            }
        }

        InboxManager inboxManager = null;
        try {
            inboxManager = (InboxManager) this.manager.lookup(InboxManager.ROLE);
            for (Iterator i = users.iterator(); i.hasNext();) {
                User user = (User) i.next();
                Inbox inbox = inboxManager.getInbox(user);
                inbox.add(message);
            }
        } catch (ServiceException e) {
            throw new NotificationException(e);
        } finally {
            if (inboxManager != null) {
                this.manager.release(inboxManager);
            }
        }

    }

}
