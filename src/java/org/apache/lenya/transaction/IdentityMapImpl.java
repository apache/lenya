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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * Identity map implementation.
 * 
 * @version $Id$
 */
public final class IdentityMapImpl extends AbstractLogEnabled implements IdentityMap {

    private Map maps = Collections.synchronizedMap(new HashMap());
    
    /**
     * Ctor.
     * @param logger The logger.
     */
    public IdentityMapImpl(Logger logger) {
        ContainerUtil.enableLogging(this, logger);
    }

    public Object get(IdentifiableFactory factory, String key) {
        String type = factory.getType();
        Map map = (Map) this.maps.get(type);
        if (map == null) {
            map = Collections.synchronizedMap(new HashMap());
            this.maps.put(type, map);
        }
        Object object = map.get(key);

        if (getLogger().isDebugEnabled())
            getLogger().debug("IdentityMapImpl::get() looked up type [" + type + "], key [" + key
                    + "] in map, is it there ? " + (object != null));

        if (object == null) {
            try {
                object = factory.build(this, key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            map.put(key, object);
        }
        return object;
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
    public Object[] getObjects() {
        Set objects = new HashSet();
        for (Iterator i = this.maps.values().iterator(); i.hasNext();) {
            Map map = (Map) i.next();
            for (Iterator j = map.values().iterator(); j.hasNext();) {
                objects.add(j.next());
            }
        }
        return (Object[]) objects.toArray(new Object[objects.size()]);
    }

}
