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
package org.apache.lenya.inbox.xml;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.ac.User;
import org.apache.lenya.inbox.AbstractInboxManager;
import org.apache.lenya.inbox.Inbox;

/**
 * XML-source based inbox manager.
 */
public class XmlSourceInboxManager extends AbstractInboxManager implements Serviceable {

    protected ServiceManager manager;

    protected Inbox doGetInbox(User user) {
        return new XmlSourceInbox(this.manager, user);
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
