/*
 * Copyright  1999-2004 The Apache Software Foundation
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
package org.apache.lenya.cms.repository;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * Factory to create source nodes.
 * 
 * @version $Id$
 */
public class SourceNodeFactory extends AbstractLogEnabled implements NodeFactory, Serviceable,
        ThreadSafe {

    private ServiceManager manager;
    private Session session;

    /**
     * Ctor.
     */
    public SourceNodeFactory() {
    }

    /**
     * @param session The session.
     */
    public void setSession(Session session) {
        this.session = session;
    }

    public RepositoryItem buildItem(Session session, String key) throws RepositoryException {
        if (this.session == null) {
            throw new RepositoryException("The session has not been set!");
        }
        return new SourceNode(this.session, key, this.manager, getLogger());
    }

    public String getItemType() {
        return Node.IDENTIFIABLE_TYPE;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
