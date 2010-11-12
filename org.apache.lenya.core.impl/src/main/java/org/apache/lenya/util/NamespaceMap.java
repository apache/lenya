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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * An object of this class provides an easy way to access
 * Strings in a Map that are prefixed like "prefix.foo".
 * The actual map wrapped by this object can contain more
 * key-value-pairs, but you can access only the prefixed keys
 * through the mapper.
 */
public class NamespaceMap {
    /**
     * <code>SEPARATOR</code> The seperator character
     */
    public static final String SEPARATOR = ".";
    private Map map;
    private String prefix;

    /**
     * Creates a new NamespaceMap object.
     * @param _prefix The prefix.
     */
    public NamespaceMap(String _prefix) {
        this(new HashMap(), _prefix);
    }

    /**
     * Creates a new NamespaceMap.
     * @param _map A map containing the prefixed key-value-pairs.
     * @param _prefix The prefix.
     */
    public NamespaceMap(Map _map, String _prefix) {
        this.map = _map;
        this.prefix = _prefix;
    }

    /**
     * Returns the prefix.
     * @return A string.
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Returns the namespace prefix.
     * @return The namespace prefix.
     */
    protected Map getMapObject() {
        return this.map;
    }

    /**
     * Returns a map that contains only the un-prefixed key-value-pairs.
     * @return The map.
     */
    public Map getMap() {
        Map resultMap = new HashMap();

        Set keys = getMapObject().keySet();

        for (Iterator i = keys.iterator(); i.hasNext();) {
            Object key = i.next();

            if (key instanceof String) {
                String keyString = (String) key;

                if (keyString.startsWith(getPrefix() + SEPARATOR)) {
                    resultMap.put(getShortName(getPrefix(), keyString), getMapObject().get(key));
                }
            }
        }

        return resultMap;
    }

    /**
     * Puts a value for prefixed key into the map.
     * @param key The key without prefix.
     * @param value The value.
     */
    public void put(String key, Object value) {
        getMapObject().put(getFullName(getPrefix(), key), value);
    }

    /**
     * Returns the value for a prefixed key.
     * @param key The key without prefix.
     * @return The value.
     */
    public Object get(String key) {
        return getMap().get(key);
    }

    /**
     * Returns the full (prefixed) key for a short (un-prefixed) key.
     * @param prefix The prefix.
     * @param key The un-prefixed key.
     * @return A string (prefix + {@link #SEPARATOR} + key).
     */
    public static String getFullName(String prefix, String key) {
        return prefix + SEPARATOR + key;
    }

    /**
     * Returns the short (un-prefixed) key for a full (prefixed) key.
     * @param prefix The prefix.
     * @param key The full (prefixed) key.
     * @return A string.
     */
    public static String getShortName(String prefix, String key) {
        return key.substring(prefix.length() + SEPARATOR.length());
    }
    
    /**
     * Puts all prefixed key-value-pairs of map into this map.
     * @param _map A map.
     */
    public void putAll(Map _map) {
        for (Iterator i = _map.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            put(key, _map.get(key));
        }
    }
    
    /**
     * Returns a map with prefixed keys.
     * @return A map.
     */
    public Map getPrefixedMap() {
        return new HashMap(getMapObject());
    }
    
}
