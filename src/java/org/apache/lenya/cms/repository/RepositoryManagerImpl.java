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
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.transaction.IdentityMapImpl;

/**
 * Repository manager implementation.
 * @version $Id:$
 */
public class RepositoryManagerImpl extends AbstractLogEnabled implements RepositoryManager,
        Serviceable {

    public void copy(Node source, Node destination) throws RepositoryException {

        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            String[] namespaces = source.getMetaDataNamespaceUris();
            for (int i = 0; i < namespaces.length; i++) {
                destination.getMetaData(namespaces[i]).replaceBy(source.getMetaData(namespaces[i]));
            }
            SourceUtil.copy(resolver, source.getSourceURI(), destination.getSourceURI());
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    protected ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * @see org.apache.lenya.cms.repository.RepositoryManager#delete(org.apache.lenya.cms.repository.Node)
     */
    public void delete(Node node) throws RepositoryException {
        node.registerRemoved();
    }

    public Session createSession(Identity identity) throws RepositoryException {
        return new SessionImpl(new IdentityMapImpl(getLogger()), identity, getLogger());
    }

}
