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
package org.apache.lenya.transaction;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Default implementation of a unit of work.
 * 
 * @version $Id: UnitOfWorkImpl.java 159567 2005-03-31 07:27:09Z andreas $
 */
public class UnitOfWorkImpl extends AbstractLogEnabled implements UnitOfWork, Serviceable {

    /**
     * Ctor.
     */
    public UnitOfWorkImpl() {
        // do nothing
    }

    private List identityMaps = new ArrayList();

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#getIdentityMaps()
     */
    public IdentityMap[] getIdentityMaps() {
        return (IdentityMap[]) this.identityMaps.toArray(new IdentityMap[this.identityMaps.size()]);
    }

    protected ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * @see org.apache.lenya.transaction.UnitOfWork#addIdentityMap(org.apache.lenya.transaction.IdentityMap)
     */
    public void addIdentityMap(IdentityMap map) {
        this.identityMaps.add(map);
    }

}