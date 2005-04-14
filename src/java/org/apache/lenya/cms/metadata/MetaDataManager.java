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
package org.apache.lenya.cms.metadata;

import org.apache.lenya.cms.publication.DocumentException;

/**
 * Generic meta data interface.
 * 
 * @version $Id:$
 */
public interface MetaDataManager {
    
    /**
     * Save the meta data.
     * 
     * @throws DocumentException if the meta data could not be made persistent.
     */
    void save() throws DocumentException;

    /**
     * Returns the values for a certain key.
     * @param key The key.
     * @return An array of strings.
     * @throws DocumentException when something went wrong.
     */
    String[] getValues(String key) throws DocumentException;

    /**
     * Returns the first value for a certain key.
     * @param key The key.
     * @return A string or <code>null</code> if no value is set for this key.
     * @throws DocumentException if an error occurs.
     */
    String getFirstValue(String key) throws DocumentException;

    /**
     * Sets the value for a certain key. All existing values will be removed.
     * @param key The key.
     * @param value The value to set.
     * @throws DocumentException when something went wrong.
     */
    void setValue(String key, String value) throws DocumentException;

    /**
     * Adds a value for a certain key.
     * @param key The key.
     * @param value The value to add.
     * @throws DocumentException when something went wrong.
     */
    void addValue(String key, String value) throws DocumentException;

    /**
     * Add all values for a certain key.
     * 
     * @param key The key
     * @param values The value to add
     * @throws DocumentException if something went wrong
     */
    void addValues(String key, String[] values) throws DocumentException;

    /**
     * Removes a specific value for a certain key.
     * @param key The key.
     * @param value The value to remove.
     * @throws DocumentException when something went wrong.
     */
    void removeValue(String key, String value) throws DocumentException;

    /**
     * Removes all values for a certain key.
     * @param key The key.
     * @throws DocumentException when something went wrong.
     */
    void removeAllValues(String key) throws DocumentException;

    /**
     * Replace the contents of the current meta data by the contents of other.
     * @param other The other meta data manager.
     * @throws DocumentException if an error occurs.
     */
    void replaceBy(MetaDataManager other) throws DocumentException;
    
    /**
     * @return All keys that can be used.
     */
    String[] getPossibleKeys();

}
