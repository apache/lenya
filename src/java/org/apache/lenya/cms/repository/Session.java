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
import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.UnitOfWork;
import org.apache.lenya.transaction.UnitOfWorkImpl;

/**
 * Repository session.
 */
public class Session extends AbstractLogEnabled {
    
    /**
     * Ctor.
     * @param identity The identity.
     * @param logger The logger.
     */
    public Session(Identity identity, Logger logger) {
        this.unitOfWork = new UnitOfWorkImpl(logger);
        this.unitOfWork.setIdentity(identity);
        ContainerUtil.enableLogging(this, logger);
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
    
}
