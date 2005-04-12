/*
 * Copyright  1999-2005 The Apache Software Foundation
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
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Session;

/**
 * Abstract operation implementation.
 * 
 * @version $Id$
 */
public class AbstractOperation extends AbstractLogEnabled implements Operation, Serviceable,
        Initializable, Disposable, Contextualizable {

    /**
     * Ctor.
     */
    public AbstractOperation() {
        super();
    }

    private UnitOfWork unitOfWork;

    /**
     * Retrieves a unit-of-work, which gives the operation access to business objects affected by
     * the operation.
     * 
     * @return a UnitOfWork, the interface to access the objects
     * @throws ServiceException if the unit-of-work component can not be initialized by the
     *             component framework
     * 
     * @see org.apache.lenya.transaction.Operation#getUnitOfWork()
     */
    public UnitOfWork getUnitOfWork() throws ServiceException {
        if (this.unitOfWork == null) {
            setUnitOfWork(new UnitOfWorkImpl());
        }

        return this.unitOfWork;
    }

    /**
     * @param unit The unit of work.
     */
    public void setUnitOfWork(UnitOfWork unit) {
        this.unitOfWork = unit;
        ContainerUtil.enableLogging(this.unitOfWork, getLogger());
        Session session = ContextHelper.getRequest(this.context).getSession(false);
        if (session != null) {
            session.setAttribute(UnitOfWork.class.getName(), unit);
        }
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
        // reset the unit of work
        /*
         * Session session = ContextHelper.getRequest(this.context).getSession(false); if (session !=
         * null) { session.setAttribute(UnitOfWork.class.getName(), null); }
         */
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (this.manager != null) {
            if (this.unitOfWork != null) {
                // FIXME: determine if unitOfWork needs some message
                // that it should clean up itself
            }
        }
    }

    protected Context context;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        this.context = context;
    }

}
