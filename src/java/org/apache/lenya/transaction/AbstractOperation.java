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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Abstract operation implementation.
 * 
 * @version $Id: AbstractOperation.java 158088 2005-03-18 16:17:38Z jwkaltz $
 */
public class AbstractOperation extends AbstractLogEnabled implements Operation, Serviceable,
        Initializable, Disposable {

    /**
     * Ctor.
     */
    public AbstractOperation() {
        super();
    }

    private UnitOfWork unitOfWork;

    /**
     * Retrieves a unit-of-work, which gives the operation access to business
     * objects affected by the operation.
     *
     * @return a UnitOfWork, the interface to access the objects
     * @throws ServiceException if the unit-of-work component can not be initialized by the component framework
     *
     * @see org.apache.lenya.transaction.Operation#getUnitOfWork()
     */
    public UnitOfWork getUnitOfWork() throws ServiceException {
        if (this.unitOfWork == null) {
           if (getLogger().isDebugEnabled())
               getLogger().debug("AbstractOperation.getUnitOfWork() does not yet have instance, looking up role [" + UnitOfWork.ROLE + "]");

           this.unitOfWork = new UnitOfWorkImpl();
           ContainerUtil.enableLogging(this.unitOfWork, getLogger());
        }

        return this.unitOfWork;
    }
    
    /**
     * @param unit The unit of work.
     */
    public void setUnitOfWork(UnitOfWork unit) {
        this.unitOfWork = unit;
    }

    protected ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception {
        // do nothing
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (this.manager != null) {
            if (this.unitOfWork != null) {
                this.manager.release(this.unitOfWork);
            }
        }
    }

}
