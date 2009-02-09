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

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.Session;

/**
 * <p>
 * Inbox module.
 * </p>
 * <p>
 * Attributes:
 * </p>
 * <ul>
 * <li><strong>newMessageCount</strong> - the number of unread messages as
 * string</li>
 * </ul>
 */
public class InboxModule extends AbstractInputModule {

    protected static final String NEW_MESSAGE_COUNT = "newMessageCount";
    
    private InboxManager inboxManager;
    private Repository repository;

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        Object value = null;
        if (name.equals(NEW_MESSAGE_COUNT)) {
            try {
                Request request = ObjectModelHelper.getRequest(objectModel);
                Session session = this.repository.getSession(request);
                User user = session.getIdentity().getUser();
                if (user == null) {
                    return "0";
                } else {
                    Inbox inbox = inboxManager.getInbox(user);
                    int count = 0;
                    InboxMessage[] messages = inbox.getMessages();
                    for (int i = 0; i < messages.length; i++) {
                        if (!messages[i].isMarkedAsRead()) {
                            count++;
                        }
                    }
                    value = Integer.toString(count);
                }

            } catch (Exception e) {
                throw new ConfigurationException("Attribute [" + name + "]: ", e);
            }
        }
        else {
            throw new ConfigurationException("Attribute: [" + name + "] not supported.");
        }
        return value;
    }

    public void setInboxManager(InboxManager inboxManager) {
        this.inboxManager = inboxManager;
    }

    public InboxManager getInboxManager() {
        return inboxManager;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}
