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

/* $Id$  */

package org.apache.lenya.util;

import java.util.Date;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;

/**
 * A map with a maximum capacity. When the map is full, the oldest entry is removed.
 */
public class CacheMap extends HashMap {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Ctor.
     * @param _capacity The maximum number of entries.
     * @param logger The logger.
     */
    public CacheMap(int _capacity, Log logger) {
        this.logger = logger;
        assert _capacity > -1;
        this.capacity = _capacity;
    }

    private int capacity;
    private SortedMap timeToKey = new TreeMap();
    private Log logger;

    /**
     * @see java.util.Map#put(Object, Object)
     */
    public Object put(Object key, Object value) {

        if (size() == this.capacity) {
            Object timeKey = this.timeToKey.firstKey();
            Object oldestKey = this.timeToKey.get(timeKey);
            this.timeToKey.remove(timeKey);
            remove(oldestKey);
            if (logger.isDebugEnabled()) {
                logger.debug("Clearing cache");
            }
        }
        this.timeToKey.put(new Date(), key);
        return super.put(key, value);
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object key) {
        Object result = super.get(key);
        if (logger.isDebugEnabled()) {
            if (result != null) {
                logger.debug("Using cached object for key [" + key + "]");
            } else {
                logger.debug("No cached object for key [" + key + "]");
            }
        }
        return result;
    }

}
