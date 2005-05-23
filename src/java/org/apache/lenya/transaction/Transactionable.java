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

/**
 * Object to take part in a transaction.
 *
 * @version $Id:$
 */
public interface Transactionable extends Identifiable, Versionable {

    /**
     * Saves the object.
     * @throws TransactionException if an error occurs.
     */
    void saveTransactionable() throws TransactionException;

    /**
     * Deletes the object.
     * @throws TransactionException if an error occurs.
     */
    void deleteTransactionable() throws TransactionException;
    
    /**
     * Creates the object.
     * @throws TransactionException if an error occurs.
     */
    void createTransactionable() throws TransactionException;
    
}
