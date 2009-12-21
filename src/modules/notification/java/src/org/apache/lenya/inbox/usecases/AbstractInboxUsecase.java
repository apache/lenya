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
package org.apache.lenya.inbox.usecases;

import org.apache.lenya.ac.User;
import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.lenya.inbox.InboxManager;

/**
 * Base class for inbox-related usecases.
 */
public abstract class AbstractInboxUsecase extends AbstractUsecase {

    protected static final String PARAM_USER = "user";
    protected static final String PARAM_INBOX = "inbox";

    protected void prepareView() throws Exception {
        super.prepareView();
    }

    protected User getUser() {
        return getSession().getIdentity().getUser();
    }

    protected org.apache.lenya.inbox.Inbox getInbox() {
        org.apache.lenya.inbox.Inbox inbox = (org.apache.lenya.inbox.Inbox) getParameter(PARAM_INBOX);
        if (inbox == null) {
            InboxManager inboxManager = null;
            try {
                inboxManager = (InboxManager) this.manager.lookup(InboxManager.ROLE);
                inbox = inboxManager.getInbox(getUser());
                setParameter(PARAM_INBOX, inbox);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (inboxManager != null) {
                    this.manager.release(inboxManager);
                }
            }
        }
        return inbox;
    }

}
