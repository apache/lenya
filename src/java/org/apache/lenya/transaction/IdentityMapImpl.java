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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
    public Identifiable get(String type, String key) {
        Map map = (Map) this.maps.get(type);
        if (map == null) {
            map = new HashMap();
            this.maps.put(type, map);
        }
        Identifiable object = (Identifiable) map.get(key);
        if (object == null) {
            try {
                object = getFactory(type).build(this, key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            map.put(key, object);
        }
        return object;
    }

    private Map factories = new HashMap();

    /**
     * @see org.apache.lenya.transaction.IdentityMap#setFactory(java.lang.String,
     *      org.apache.lenya.transaction.IdentifiableFactory)
     */
    public void setFactory(String type, IdentifiableFactory factory) {
        this.factories.put(type, factory);
    }

    /**
     * @see org.apache.lenya.transaction.IdentityMap#getFactory(java.lang.String)
     */
    public IdentifiableFactory getFactory(String type) {
        return (IdentifiableFactory) this.factories.get(type);
    }

    private UnitOfWork unitOfWork;

    /**
     * @see org.apache.lenya.transaction.IdentityMap#getUnitOfWork()
     */
    public UnitOfWork getUnitOfWork() {
        return this.unitOfWork;
    }

    /**
     * @see org.apache.lenya.transaction.IdentityMap#setUnitOfWork(org.apache.lenya.transaction.UnitOfWork)
     */
    public void setUnitOfWork(UnitOfWork unit) {
        this.unitOfWork = unit;
    }

    /**
     * @see org.apache.lenya.transaction.IdentityMap#getObjects()
     */
    public Identifiable[] getObjects() {
        Set objects = new HashSet();
        for (Iterator i = this.maps.values().iterator(); i.hasNext(); ) {
            Map map = (Map) i.next();
            for (Iterator j = map.values().iterator(); j.hasNext(); ) {
                objects.add(j.next());
            }
        }
        return (Identifiable[]) objects.toArray(new Identifiable[objects.size()]);
    }

}