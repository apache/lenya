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
 * Identity map.
 * 
 * @version $Id:$
 */
public interface IdentityMap {

    /**
     * @param type The type of the transactionable.
     * @param key The key for the transactionable.
     * @return A transcationable.
     */
    Transactionable get(String type, String key);

    /**
     * Sets the factory.
     * @param type The transactionable type to use the factory for.
     * @param factory The factory to use.
     */
    void setFactory(String type, TransactionableFactory factory);

    /**
     * @param type The type to return the factory for.
     * @return The factory.
     */
    TransactionableFactory getFactory(String type);

    /**
     * Returns the unit of work. This maybe <code>null</code> if the identity map is not involved
     * in a transaction.
     * @return The unit of work.
     */
    UnitOfWork getUnitOfWork();

    /**
     * @param unit The unit of work to use.
     */
    void setUnitOfWork(UnitOfWork unit);

}