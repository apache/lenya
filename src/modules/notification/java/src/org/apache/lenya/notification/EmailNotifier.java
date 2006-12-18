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

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.mail.MailSender;
import org.apache.lenya.ac.Identifiable;
import org.apache.lenya.ac.User;
import org.apache.lenya.inbox.InboxNotifier;

/**
 * Default notifier implementation.
 */
public class EmailNotifier extends InboxNotifier implements Configurable {

    protected void notify(User recipient, Message translatedMessage) throws NotificationException {
        
        super.notify(recipient, translatedMessage);
        
        if (!this.manager.hasService(MailSender.ROLE)) {
            getLogger().error("Can't send mails - no MailSender service found.");
            return;
        }

        Identifiable sender = translatedMessage.getSender();
        MailSender mailer = null;
        try {
            mailer = (MailSender) this.manager.lookup(MailSender.ROLE);
            if (this.username == null) {
                mailer.setSmtpHost(this.smtpHost);
            } else {
                mailer.setSmtpHost(this.smtpHost, this.username, this.password);
            }

            mailer.setTo(recipient.getEmail());
            if (sender instanceof User) {
                mailer.setFrom(((User) sender).getEmail());
            }

            mailer.setSubject(translatedMessage.getSubject());
            mailer.setBody(translatedMessage.getBody(), "text/plain; charset=UTF-8");
            mailer.send();
        
        } catch (AddressException e) {
            getLogger().error("Sending mail failed (address error): ", e);
            throw new NotificationException(e);
        } catch (MessagingException e) {
            getLogger().error("Sending mail failed (mail error): ", e);
            throw new NotificationException(e);
        } catch (ServiceException e) {
            throw new NotificationException(e);
        } finally {
            if (mailer != null) {
                this.manager.release(mailer);
            }
        }
    }

    private String smtpHost;
    private String username;
    private String password;

    public void configure(Configuration config) throws ConfigurationException {
        Configuration smtp = config.getChild("smtp");
        this.smtpHost = smtp.getAttribute("host");
        this.username = smtp.getAttribute("username", null);
        if (this.username != null) {
            this.password = smtp.getAttribute("password");
        }
    }

}
