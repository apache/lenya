/*
 * $Id: CacheMap.java,v 1.2 2003/07/23 13:21:14 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://cocoon.apache.org/lenya/)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact board@apache.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://cocoon.apache.org/lenya/)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.util;

import java.util.Date;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Category;

/**
 * A map with a maximum capacity. When the map is full, the oldest entry is removed.
 * 
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CacheMap extends HashMap {
    
    private static final Category log = Category.getInstance(CacheMap.class);
    
    /**
     * Ctor.
     * @param capacity The maximum number of entries.
     */
    public CacheMap(int capacity) {
        assert capacity > -1;
        this.capacity = capacity;
    }
    
    private int capacity;
    private SortedMap timeToKey = new TreeMap();
    
    /**
     * @see java.util.Map#put(Object, Object)
     */
    public Object put(Object key, Object value) {
        
        if (size() == capacity) {
            Object oldestKey = timeToKey.get(timeToKey.firstKey());
            remove(oldestKey);
            if (log.isDebugEnabled()) {
                log.debug("Clearing cache");
            }
        }
        timeToKey.put(new Date(), key);
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
