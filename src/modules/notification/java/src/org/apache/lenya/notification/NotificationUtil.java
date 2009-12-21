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

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * Notification utility.
 */
public final class NotificationUtil {

    /**
     * Invokes a notification.
     * @param manager The service manager.
     * @param message The message.
     * @throws NotificationException if an error occurs.
     */
    public static final void notify(ServiceManager manager, Message message)
            throws NotificationException {

        Notifier notifier = null;
        try {
            notifier = (Notifier) manager.lookup(Notifier.ROLE);
            notifier.notify(message);
        } catch (ServiceException e) {
            throw new NotificationException(e);
        } finally {
            if (notifier != null) {
                manager.release(notifier);
            }
        }
    }

}
