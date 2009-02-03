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
 * Identity map.
 * 
 * @version $Id$
 */
public interface IdentityMap {

    /**
     * Retrieve an instance from the map. If no instance exists
     * for the given key, the factory is used to build one.
     * @param factory The factory that produces the identifable.
     * @param key The key for the identifiable.
     * @return An identifiable.
     */
    Object get(IdentifiableFactory factory, String key);

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
    
    /**
     * @return All objects in this map.
     */
    Object[] getObjects();

}
