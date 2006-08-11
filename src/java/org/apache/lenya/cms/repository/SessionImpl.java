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
package org.apache.lenya.cms.repository;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.transaction.IdentityMap;
import org.apache.lenya.transaction.Lock;
import org.apache.lenya.transaction.Lockable;
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.Transactionable;
import org.apache.lenya.transaction.UnitOfWork;
import org.apache.lenya.transaction.UnitOfWorkImpl;
import org.apache.lenya.util.Assert;

/**
 * Repository session.
 */
public class SessionImpl extends AbstractLogEnabled implements Session {

    /**
     * Ctor.
     * @param map The identity map.
     * @param identity The identity.
     * @param logger The logger.
     */
    public SessionImpl(IdentityMap map, Identity identity, Logger logger) {
        
        Assert.notNull("identity map", map);
        
        this.unitOfWork = new UnitOfWorkImpl(map, identity, logger);
        this.unitOfWork.setIdentity(identity);
        ContainerUtil.enableLogging(this, logger);
    }

    public Identity getIdentity() {
        return getUnitOfWork().getIdentity();
    }

    private UnitOfWork unitOfWork;

    /**
     * @return The unit of work.
     */
    public UnitOfWork getUnitOfWork() {
        return this.unitOfWork;
    }

    /**
     * Commits the transaction.
     * @throws RepositoryException if an error occurs.
     */
    public void commit() throws RepositoryException {
        try {
            getUnitOfWork().commit();
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Rolls the transaction back.
     * @throws RepositoryException if an error occurs.
     */
    public void rollback() throws RepositoryException {
        try {
            getUnitOfWork().rollback();
        } catch (TransactionException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.repository.Session#getRepositoryItem(org.apache.lenya.cms.repository.RepositoryItemFactory,
     *      java.lang.String)
     */
    public RepositoryItem getRepositoryItem(RepositoryItemFactory factory, String key)
            throws RepositoryException {
        RepositoryItemFactoryWrapper wrapper = new RepositoryItemFactoryWrapper(factory, this);
        return (RepositoryItem) ((UnitOfWorkImpl) getUnitOfWork()).getIdentityMap().get(wrapper,
                key);
    }

    public void registerNew(Transactionable object) throws TransactionException {
        getUnitOfWork().registerNew(object);
    }

    public void registerDirty(Transactionable object) throws TransactionException {
        getUnitOfWork().registerDirty(object);
    }

    public void registerRemoved(Transactionable object) throws TransactionException {
        getUnitOfWork().registerRemoved(object);
    }

    public void setIdentity(Identity identity) {
        getUnitOfWork().setIdentity(identity);
    }

    public boolean isDirty(Transactionable transactionable) {
        return getUnitOfWork().isDirty(transactionable);
    }

    public Lock createLock(Lockable lockable, int version) throws TransactionException {
        return getUnitOfWork().createLock(lockable, version);
    }

    public void removeLock(Lockable lockable) throws TransactionException {
        getUnitOfWork().removeLock(lockable);
    }

}
