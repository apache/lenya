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

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * Identity map implementation.
 * 
 * @version $Id:$
 */
public class IdentityMapImpl extends AbstractLogEnabled implements IdentityMap {

    private Map key2transactionable = new HashMap();

    /**
     * @see org.apache.lenya.transaction.IdentityMap#get(java.lang.Object)
     */
    public Transactionable get(Object key) {
        Transactionable transactionable = (Transactionable) key2transactionable.get(key);
        if (transactionable == null) {
            try {
                transactionable = this.factory.build(this, key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            key2transactionable.put(key, transactionable);
        }
        return transactionable;
    }

    private TransactionableFactory factory;

    /**
     * @see org.apache.lenya.transaction.IdentityMap#setFactory(org.apache.lenya.transaction.TransactionableFactory)
     */
    public void setFactory(TransactionableFactory factory) {
        this.factory = factory;
    }

    /**
     * @see org.apache.lenya.transaction.IdentityMap#getFactory()
     */
    public TransactionableFactory getFactory() {
        return this.factory;
    }

}