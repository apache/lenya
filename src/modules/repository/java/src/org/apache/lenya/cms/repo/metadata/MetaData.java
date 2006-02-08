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
package org.apache.lenya.cms.repo.metadata;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Meta data.
 */
public interface MetaData {
    
    /**
     * Returns the element set this meta data object belongs to.
     * @return the element set.
     * @throws RepositoryException if an error occurs.
     */
    ElementSet getElementSet() throws RepositoryException;

    /**
     * Returns the value of a non-multiple element.
     * @param name The element name.
     * @return The value of the element.
     * @throws RepositoryException if the element is multiple.
     */
    String getValue(String name) throws RepositoryException;
    
    /**
     * Returns all values of a multiple element.
     * @param name The element name.
     * @return The element values.
     * @throws RepositoryException if the element is not multiple.
     */
    String[] getValues(String name) throws RepositoryException;
    
    /**
     * Sets the value of a non-multiple element.
     * @param name The element name.
     * @param value The value to set.
     * @throws RepositoryException if the element is multiple.
     */
    void setValue(String name, String value) throws RepositoryException;
    
    /**
     * Adds a value to a multiple element.
     * @param name The element name.
     * @param value The value to add.
     * @throws RepositoryException if the element is not multiple.
     */
    void addValue(String name, String value) throws RepositoryException;
    
    /**
     * Removes all values. For non-multiple elements, the value is set to <code>null</code>.
     * @param name The element name.
     * @throws RepositoryException if an error occurs.
     */
    void clear(String name) throws RepositoryException;
    
    /**
     * Removes a value from a multiple element.
     * @param name The element name.
     * @param value The value to remove.
     * @throws RepositoryException if the element is not multiple.
     */
    void removeValue(String name, String value) throws RepositoryException;
    
}
