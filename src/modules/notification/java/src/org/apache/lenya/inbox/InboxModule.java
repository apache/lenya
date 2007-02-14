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
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

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
public class InboxModule extends AbstractInputModule implements Serviceable {

    protected static final String NEW_MESSAGE_COUNT = "newMessageCount";
    protected ServiceManager manager;

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        Object value = null;
        if (name.equals(NEW_MESSAGE_COUNT)) {
            InboxManager inboxManager = null;
            try {
                inboxManager = (InboxManager) this.manager.lookup(InboxManager.ROLE);
                Request request = ObjectModelHelper.getRequest(objectModel);
                Session session = RepositoryUtil.getSession(manager, request);
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
            } finally {
                if (inboxManager != null) {
                    this.manager.release(inboxManager);
                }
            }
        }
        else {
            throw new ConfigurationException("Attribute: [" + name + "] not supported.");
        }
        return value;
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
