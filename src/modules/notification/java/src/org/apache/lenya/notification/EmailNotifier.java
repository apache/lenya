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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.mail.MailSender;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.User;
import org.apache.lenya.inbox.InboxNotifier;

/**
 * Default notifier implementation.
 */
public class EmailNotifier extends InboxNotifier implements Configurable {

    public void notify(Message message)
            throws NotificationException {
        
        super.notify(message);

        Set noDuplicates = new HashSet();
        
        Identifiable[] recipients = message.getRecipients();

        for (int i = 0; i < recipients.length; i++) {
            if (recipients[i] instanceof Group) {
                Group group = (Group) recipients[i];
                noDuplicates.addAll(Arrays.asList(group.getMembers()));
            } else {
                noDuplicates.add(recipients[i]);
            }
        }

        for (Iterator i = noDuplicates.iterator(); i.hasNext();) {
            Identifiable identifiable = (Identifiable) i.next();
            if (identifiable instanceof User) {
                notify((User) identifiable, message);
            }
        }

    }

    protected void notify(User recipient, Message message)
            throws NotificationException {

        Identifiable sender = message.getSender();
        
        if (!this.manager.hasService(MailSender.ROLE)) {
            getLogger().error("Can't send mails - no MailSender service found.");
            return;
        }
        
        MailSender mailer = null;
        try {
            mailer = (MailSender) this.manager.lookup(MailSender.ROLE);
            mailer.setSmtpHost(this.smtpHost);

            mailer.setTo(recipient.getEmail());
            if (sender instanceof User) {
                mailer.setFrom(((User) sender).getEmail());
            }

            Message translatedMessage = translateMessage(recipient.getDefaultMenuLocale(), message);

            mailer.setSubject(translatedMessage.getSubject());
            mailer.setBody(translatedMessage.getBody(), "text/plain");
            mailer.setCharset("UTF-8");
            mailer.send();

        } catch (Exception e) {
            throw new NotificationException(e);
        } finally {
            if (mailer != null) {
                this.manager.release(mailer);
            }
        }

    }

    private String smtpHost;

    protected static final String ELEMENT_SMTP_HOST = "smtp-host";

    public void configure(Configuration config) throws ConfigurationException {
        this.smtpHost = config.getChild(ELEMENT_SMTP_HOST).getValue();
    }

}
