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
package org.apache.lenya.cms.contactform;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.notification.Message;
import org.apache.lenya.notification.NotificationUtil;

/**
 * Contact form. The recipient user ID is set using the "recipient" parameter. The sender user ID is
 * set using the "sender" parameter;
 */
public class ContactForm extends AbstractUsecase {

    protected static final String RECIPIENT = "recipient";
    protected static final String SENDER = "sender";
    protected static final String MESSAGE = "message";
    protected static final String NAME = "name";
    protected static final String FROM = "email";

    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (getParameterAsString(SENDER).equals("")) {
            addErrorMessage("The sender is not configured!");
        }
    }

    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        if (getParameterAsString(NAME).trim().equals("")) {
            addErrorMessage("Please enter your name!");
        }
        if (getParameterAsString(FROM).trim().equals("")) {
            addErrorMessage("Please enter your e-mail address!");
        }
        if (getParameterAsString(MESSAGE).trim().equals("")) {
            addErrorMessage("Please enter a message!");
        }
        if (getParameterAsString(RECIPIENT).trim().equals("")) {
            addErrorMessage("Please choose a recipient!");
        }
    }

    protected void doExecute() throws Exception {
        super.doExecute();

        String senderUserId = getParameterAsString(SENDER);
        String recipientUserId = getParameterAsString(RECIPIENT);

        User sender = getUser(senderUserId);
        User recipient = getUser(recipientUserId);
        User[] recipients = { recipient };

        String name = getParameterAsString(NAME);
        String body = getParameterAsString(MESSAGE);
        String from = getParameterAsString(FROM);

        Message message = new Message("Contact form submitted by " + name + " (" + from + ")",
                new String[0],
                body,
                new String[0],
                sender,
                recipients);
        
        NotificationUtil.notify(this.manager, message);

        setDefaultTargetURL(getSourceURL() + "?sent=true");
    }

    protected User getUser(String userId) throws Exception {
        User user;
        ServiceSelector selector = null;
        AccessControllerResolver acResolver = null;
        AccessController accessController = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(AccessControllerResolver.ROLE
                    + "Selector");
            acResolver = (AccessControllerResolver) selector.select(AccessControllerResolver.DEFAULT_RESOLVER);
            accessController = acResolver.resolveAccessController(getSourceURL());

            UserManager userManager = accessController.getAccreditableManager().getUserManager();
            user = userManager.getUser(userId);

        } finally {
            if (selector != null) {
                if (acResolver != null) {
                    if (accessController != null) {
                        acResolver.release(accessController);
                    }
                    selector.release(acResolver);
                }
                this.manager.release(selector);
            }
        }
        return user;
    }

}
