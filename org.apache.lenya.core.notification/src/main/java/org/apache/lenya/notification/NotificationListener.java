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

import org.apache.lenya.cms.observation.AbstractRepositoryListener;
import org.apache.lenya.cms.observation.RepositoryEvent;

/**
 * Repository listener to distribute notification events.
 */
public class NotificationListener extends AbstractRepositoryListener {

    public void eventFired(RepositoryEvent event) {

        if (!(event.getDescriptor() instanceof NotificationEventDescriptor)) {
            return;
        }

        NotificationEventDescriptor descriptor = (NotificationEventDescriptor) event
                .getDescriptor();

        Message message = descriptor.getMessage();

        Notifier notifier = null;
        try {
            notifier = (Notifier) this.manager.lookup(Notifier.ROLE);
            notifier.notify(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (notifier != null) {
                this.manager.release(notifier);
            }
        }

    }

}
