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
package org.apache.lenya.cms.metadata;

import java.util.HashMap;

import org.apache.lenya.cms.publication.DocumentException;

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
     * Get all available keys.
     * @return The keys available in this MetaData object.
     */
    String[] getAvailableKeys();
    
    /**
     * Sets the value for a certain key. All existing values will be removed.
     * @param key The key.
     * @param value The value to set.
     * @throws DocumentException when something went wrong.
     */
    void setValue(String key, String value) throws DocumentException;
    
    /**
     * Addds a value for a certain key. The existing values will not be removed.
     * @param key The key.
     * @param value The value to add.
     * @throws DocumentException when something went wrong.
     */
    void addValue(String key, String value) throws DocumentException;

    /**
     * Replace the contents of the current meta data by the contents of other.
     * @param other The other meta data manager.
     * @throws DocumentException if an error occurs.
     */
    void replaceBy(MetaData other) throws DocumentException;
    
    /**
     * @return All keys that can be used.
     */
    String[] getPossibleKeys();
    
    /**
     * @return All keys and values that exist in the metadata doc.
     */
    HashMap getAvailableKey2Value();

    /**
     * Checks if a key represents a valid metadata attribute.
     * @param key The key.
     * @return A boolean value.
     */
    boolean isValidAttribute(String key);
    
    /**
     * Get last modification date.
     * @return last modification date
     * @throws DocumentException if an error occurs.
     */
     long getLastModified() throws DocumentException;
}
