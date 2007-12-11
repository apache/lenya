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
package org.apache.lenya.transaction;

/**
 * This is a "Unit of Work" object (see "Unit of Work" pattern by Martin Fowler, 
 * <a href="http://www.martinfowler.com/eaaCatalog/unitOfWork.html">
 *   http://www.martinfowler.com/eaaCatalog/unitOfWork.html
 * </a>: the unit of work "maintains a list of objects affected by a business transaction and coordinates the writing out of changes and the resolution of concurrency problems".
 * 
 * <p>In the current design, this interface allows a use case to generate documents, while ensuring that only one instance of a document is created. This access is provided by the DocumentIdentityMap's DocumentFactory.</p>
 *
 * <p>This interface may be extended in the future to allow for access to further types of business objects.</p>
 * 
 * @version $Id$
 */
public interface UnitOfWork {

    /**
     * Registers an object as new.
     * @param object The object.
     * @throws TransactionException if an error occurs.
     */
    void registerNew(Transactionable object) throws TransactionException;
    
    /**
     * Registers an object as modified.
     * @param object The object.
     * @throws TransactionException if an error occurs.
     */
    void registerDirty(Transactionable object) throws TransactionException;
    
    /**
     * Registers an object as removed.
     * @param object The object.
     * @throws TransactionException if an error occurs.
     */
    void registerRemoved(Transactionable object) throws TransactionException;
    
    /**
     * Commits the transaction.
     * @throws TransactionException if an error occurs.
     */
    void commit() throws TransactionException;
    
    /**
     * Rolls the transaction back.
     * @throws TransactionException if an error occurs.
     */
    void rollback() throws TransactionException;
    
    /**
     * @param transactionable A transactionable.
     * @return If the transactionable is registered as dirty.
     */
    boolean isDirty(Transactionable transactionable);
    
    /**
     * Creates a lock.
     * @param lockable The lockable.
     * @param version The version.
     * @return A lock.
     * @throws TransactionException if a lock is already placed on this transactionable.
     */
    Lock createLock(Lockable lockable, int version) throws TransactionException;
    
    /**
     * Removes a lock.
     * @param lockable The lockable.
     * @throws TransactionException if no lock is placed on this transactionable.
     */
    void removeLock(Lockable lockable) throws TransactionException;

}
