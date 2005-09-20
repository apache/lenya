/*
 * Copyright  1999-2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

/**
 * Notification service.
 */
public interface Notifier {

    /**
     * The service role.
     */
    String ROLE = Notifier.class.getName();
    
    /**
     * Send a notification.
     * @param recipients The recipients.
     * @param sender The sender.
     * @param message The message.
     * @throws NotificationException if an error occurs.
     */
    void notify(Identifiable[] recipients, Identifiable sender, Message message) throws NotificationException;
    
}
