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

/**
 * Object to take part in a transaction.
 *
 * @version $Id:$
 */
public interface Transactionable {

    /**
     * Saves the object.
     * @throws TransactionException if an error occurs.
     */
    void save() throws TransactionException;
    
    /**
     * Checks the object in.
     * @throws TransactionException if an error occurs.
     */
    void checkin() throws TransactionException;
    
    /**
     * Checks the object out.
     * @throws TransactionException if an error occurs.
     */
    void checkout() throws TransactionException;
    
    /**
     * @return if the object is checked out.
     * @throws TransactionException if an error occurs.
     */
    boolean isCheckedOut() throws TransactionException;
    
    /**
     * Locks this object.
     * @throws TransactionException if an error occurs.
     */
    void lock() throws TransactionException;
    
    /**
     * Unlocks this object.
     * @throws TransactionException if an error occurs.
     */
    void unlock() throws TransactionException;
    
    /**
     * @return if this object is locked.
     * @throws TransactionException if an error occurs.
     */
    boolean isLocked() throws TransactionException;
    
}
