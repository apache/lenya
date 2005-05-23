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

/* $Id$  */

package org.apache.lenya.util;

import java.util.Date;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

/**
 * A map with a maximum capacity. When the map is full, the oldest entry is removed.
 */
public class CacheMap extends HashMap {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(CacheMap.class);
    
    /**
     * Ctor.
     * @param _capacity The maximum number of entries.
     */
    public CacheMap(int _capacity) {
        assert _capacity > -1;
        this.capacity = _capacity;
    }
    
    private int capacity;
    private SortedMap timeToKey = new TreeMap();
    
    /**
     * @see java.util.Map#put(Object, Object)
     */
    public Object put(Object key, Object value) {
        
        if (size() == this.capacity) {
            Object oldestKey = this.timeToKey.get(this.timeToKey.firstKey());
            remove(oldestKey);
            if (log.isDebugEnabled()) {
                log.debug("Clearing cache");
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
        if (log.isDebugEnabled()) {
            if (result != null) {
                log.debug("Using cached object for key [" + key + "]");
            }
            else {
                log.debug("No cached object for key [" + key + "]");
            }
        }
        return result;
    }

}
