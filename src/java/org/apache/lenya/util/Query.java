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
package org.apache.lenya.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <p>
 * Utility class to analyze a query string of the form
 * <code>key1=value1&key2=value2&...</code>.
 * Access is not thread-safe.
 * </p>
 * <p>
 * Invalid parts (not a valid key-value-pair) are omitted.
 * </p>
 */
public class Query {

    protected static final String PAIR_DELIMITER = "&";
    protected static final String KEY_VALUE_DELIMITER = "=";
    private String pairDelimiter;
    private String keyValueDelimiter;

    /**
     * Creates a query object with default values for the pair and key-value
     * delimiters.
     * 
     * @param string The query string.
     */
    public Query(String string) {
        this(string, PAIR_DELIMITER, KEY_VALUE_DELIMITER);
    }

    /**
     * Creates a query object.
     * @param string The string.
     * @param pairDelimiter The delimiter between key-value pairs.
     * @param keyValueDelimiter The delimiter between key and value.
     */
    public Query(String string, String pairDelimiter, String keyValueDelimiter) {
        this.key2value = new HashMap();
        this.pairDelimiter = pairDelimiter;
        this.keyValueDelimiter = keyValueDelimiter;
        StringTokenizer tokenizer = new StringTokenizer(string, pairDelimiter);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            StringTokenizer keyValueTokenizer = new StringTokenizer(token, keyValueDelimiter);
            if (keyValueTokenizer.countTokens() == 2) {
                final String key = keyValueTokenizer.nextToken();
                final String value = keyValueTokenizer.nextToken();
                this.key2value.put(key, value);
            }
        }
    }

    private Map key2value;

    /**
     * @param key The value for the key.
     * @return A string or <code>null</code> if no value exists.
     */
    public String getValue(String key) {
        return (String) this.key2value.get(key);
    }

    public String getValue(String key, String defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public String getKeyValueDelimiter() {
        return keyValueDelimiter;
    }

    public String getPairDelimiter() {
        return pairDelimiter;
    }
    
    public void removeValue(String key) {
        this.key2value.remove(key);
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = this.key2value.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            String value = (String) this.key2value.get(key);
            buf.append(key);
            buf.append(this.keyValueDelimiter);
            buf.append(value);
            if (i.hasNext()) {
                buf.append(this.pairDelimiter);
            }
        }
        return buf.toString();
    }

}
