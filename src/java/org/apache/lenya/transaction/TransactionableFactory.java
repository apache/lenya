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
 * Factory for transactionables.
 *
 * @version $Id:$
 */
public interface TransactionableFactory {

    /**
     * Builds a transactionable.
     * @param map The identity map.
     * @param key The key.
     * @return A transactionable.
     * @throws Exception if an error occurs.
     */
    Transactionable build(IdentityMap map, Object key) throws Exception;
    
    /**
     * @param transactionable The transactionable.
     * @return The key to use in an identity map.
     */
    Object getKey(Transactionable transactionable);
    
}
