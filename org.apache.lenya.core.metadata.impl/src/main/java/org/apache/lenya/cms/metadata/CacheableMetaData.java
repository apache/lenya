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
package org.apache.lenya.cms.metadata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Cached, read-only meta data.
 */
public class CacheableMetaData implements MetaData {

    /**
     * Maps keys to value arrays.
     */
    private Map key2values = new HashMap();
    private long lastModified;
    private ElementSet elementSet;

    /**
     * @param meta The meta data to build this object from.
     */
    public CacheableMetaData(MetaData meta) {
        this.elementSet = meta.getElementSet();
        String[] keys = meta.getAvailableKeys();
        try {
            this.lastModified = meta.getLastModified();
            for (int i = 0; i < keys.length; i++) {
                String[] values = meta.getValues(keys[i]);
                this.key2values.put(keys[i], values);
            }
        } catch (MetaDataException e) {
            throw new RuntimeException(e);
        }
    }

    public void addValue(String key, String value) throws MetaDataException {
        throw new UnsupportedOperationException();
    }

    public void forcedReplaceBy(MetaData other) throws MetaDataException {
        throw new UnsupportedOperationException();
    }

    public String[] getAvailableKeys() {
        Set keys = key2values.keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    public ElementSet getElementSet() {
        return this.elementSet;
    }

    public String getFirstValue(String key) throws MetaDataException {
        String value = null;
        String[] values = getValues(key);
        if (values.length > 0) {
            value = values[0];
        }
        return value;
    }

    public long getLastModified() throws MetaDataException {
        return this.lastModified;
    }
    
    private String[] possibleKeys;

    public String[] getPossibleKeys() {
        if (this.possibleKeys == null) {
            Element[] elements = getElementSet().getElements();
            this.possibleKeys = new String[elements.length];
            for (int i = 0; i < possibleKeys.length; i++) {
                possibleKeys[i] = elements[i].getName();
            }
        }
        return this.possibleKeys;
    }

    public String[] getValues(String key) throws MetaDataException {
        if (this.key2values.containsKey(key)) {
            return (String[]) this.key2values.get(key);
        }
        else {
            return new String[0];
        }
    }

    public boolean isValidAttribute(String key) {
        return Arrays.asList(getPossibleKeys()).contains(key);
    }

    public void removeAllValues(String key) throws MetaDataException {
        throw new UnsupportedOperationException();
    }

    public void replaceBy(MetaData other) throws MetaDataException {
        throw new UnsupportedOperationException();
    }

    public void setValue(String key, String value) throws MetaDataException {
        throw new UnsupportedOperationException();
    }

}
