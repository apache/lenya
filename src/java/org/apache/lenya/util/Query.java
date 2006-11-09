/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Utility class to analyze a query string of the form
 * <code>key1=value1&key2=value2&...</code>.
 * </p>
 * <p>
 * Invalid parts (not a valid key-value-pair) are omitted.
 * </p>
 */
public class Query {

    protected static final String PAIR_DELIMITER = "&";
    protected static final String KEY_VALUE_DELIMITER = "=";
    private String string;
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
        this.string = string;
        this.pairDelimiter = pairDelimiter;
        this.keyValueDelimiter = keyValueDelimiter;
    }

    private Map key2value;

    protected void analyze() {
        if (this.key2value == null) {
            this.key2value = new HashMap();
            String[] pairs = this.string.split(getPairDelimiter());
            for (int i = 0; i < pairs.length; i++) {
                String[] keyAndValue = pairs[i].split(getKeyValueDelimiter());
                if (keyAndValue.length == 2) {
                    final String key = keyAndValue[0];
                    final String value = keyAndValue[1];
                    this.key2value.put(key, value);
                }
            }
        }
    }

    public String getValue(String key) {
        analyze();
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

}
