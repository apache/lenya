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
     * @return The element set these meta data belong to.
     * @throws RepositoryException if an error occurs.
     */
    ElementSet getElementSet() throws RepositoryException;

    /**
     * @param name The element's name.
     * @return The value.
     * @throws RepositoryException if an error occurs.
     */
    String getValue(String name) throws RepositoryException;
    
    /**
     * @param name The element's name.
     * @return The values.
     * @throws RepositoryException if an error occurs.
     */
    String[] getValues(String name) throws RepositoryException;
    
    /**
     * @param name The element's name.
     * @param value The value.
     * @throws RepositoryException if an error occurs.
     */
    void setValue(String name, String value) throws RepositoryException;
    
    /**
     * @param name The element's name.
     * @param value The value.
     * @throws RepositoryException if an error occurs.
     */
    void addValue(String name, String value) throws RepositoryException;
    
    /**
     * Removes all values.
     * @param name The element's name.
     * @throws RepositoryException if an error occurs.
     */
    void clear(String name) throws RepositoryException;
    
    /**
     * @param name The element's name.
     * @param value The value.
     * @throws RepositoryException if an error occurs.
     */
    void removeValue(String name, String value) throws RepositoryException;
    
}
