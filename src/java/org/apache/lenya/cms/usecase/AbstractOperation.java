/*
 * Created on 20.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.usecase;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
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
     * @see org.apache.lenya.cms.usecase.Operation#getUnitOfWork()
     */
    public UnitOfWork getUnitOfWork() {
        return this.unitOfWork;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception {
        this.unitOfWork = (UnitOfWork) this.manager.lookup(UnitOfWork.ROLE);
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