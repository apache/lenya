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

    private Map maps = new HashMap();

    /**
     * @see org.apache.lenya.transaction.IdentityMap#get(java.lang.String, java.lang.String)
     */
    public Transactionable get(String type, String key) {
        Map map = (Map) this.maps.get(type);
        if (map == null) {
            map = new HashMap();
            this.maps.put(type, map);
        }
        Transactionable transactionable = (Transactionable) map.get(key);
        if (transactionable == null) {
            try {
                transactionable = getFactory(type).build(this, key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            map.put(key, transactionable);
        }
        return transactionable;
    }

    private Map factories = new HashMap();

    /**
     * @see org.apache.lenya.transaction.IdentityMap#setFactory(java.lang.String,
     *      org.apache.lenya.transaction.TransactionableFactory)
     */
    public void setFactory(String type, TransactionableFactory factory) {
        this.factories.put(type, factory);
    }

    /**
     * @see org.apache.lenya.transaction.IdentityMap#getFactory(java.lang.String)
     */
    public TransactionableFactory getFactory(String type) {
        return (TransactionableFactory) this.factories.get(type);
    }

}