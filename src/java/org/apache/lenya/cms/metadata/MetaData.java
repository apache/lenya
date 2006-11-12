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

/**
 * Generic meta data interface.
 * 
 * @version $Id$
 */
public interface MetaData {
    
    /**
     * Returns the values for a certain key.
     * @param key The key.
     * @return An array of strings.
     * @throws MetaDataException when something went wrong.
     */
    String[] getValues(String key) throws MetaDataException;

    /**
     * Returns the first value for a certain key.
     * @param key The key.
     * @return A string or <code>null</code> if no value is set for this key.
     * @throws MetaDataException if an error occurs.
     */
    String getFirstValue(String key) throws MetaDataException;
    
    /**
     * Get all available keys.
     * @return The keys available in this MetaData object.
     */
    String[] getAvailableKeys();
    
    /**
     * Sets the value for a certain key. All existing values will be removed.
     * @param key The key.
     * @param value The value to set.
     * @throws MetaDataException when something went wrong.
     */
    void setValue(String key, String value) throws MetaDataException;
    
    /**
     * Addds a value for a certain key. The existing values will not be removed.
     * @param key The key.
     * @param value The value to add.
     * @throws MetaDataException if there's already a value set and the element doesn't support multiple values.
     */
    void addValue(String key, String value) throws MetaDataException;

    /**
     * Replace the contents of the current meta data by the contents of other.
     * @param other The other meta data manager.
     * @throws MetaDataException if an error occurs.
     */
    void replaceBy(MetaData other) throws MetaDataException;
    
    /**
     * Replace the contents of the current meta data by the contents of other.
     * All meta data is replaced, disregarding the rules given by element.getActionOnCopy().
     * @param other The other meta data manager.
     * @throws MetaDataException if an error occurs.
     */
    void forcedReplaceBy(MetaData other) throws MetaDataException;
    
    /**
     * @return All keys that can be used.
     */
    String[] getPossibleKeys();
    
    /**
     * Checks if a key represents a valid metadata attribute.
     * @param key The key.
     * @return A boolean value.
     */
    boolean isValidAttribute(String key);
    
    /**
     * Get last modification date.
     * @return last modification date
     * @throws MetaDataException if an error occurs.
     */
     long getLastModified() throws MetaDataException;
     
     /**
     * @return The element set this meta data object belongs to.
     */
    ElementSet getElementSet();
    
    /**
     * Removes all values for a certain key.
     * @param key The key.
     * @throws MetaDataException if the key is not supported.
     */
    void removeAllValues(String key) throws MetaDataException;
     
}
