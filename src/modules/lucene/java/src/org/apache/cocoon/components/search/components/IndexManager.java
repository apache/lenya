/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.components.search.components;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.search.Index;
import org.apache.cocoon.components.search.IndexException;
import org.apache.excalibur.source.Source;

/**
 * Index Manager Class allow to register and access to a specific index
 * 
 * @author Maisonneuve Nicolas
 */
public interface IndexManager {

    public static final String ROLE = IndexManager.class.getName();

    /**
     * Return all indexes
     * 
     * @return Array of indexes
     */
    public Index[] getIndex() throws IndexException;

    /**
     * Return the index with the id
     * 
     * @param id
     *            the index ID
     * @return l'index, null if no found
     */
    public Index getIndex(String id) throws IndexException;

    /**
     * add a index in the indexmanager
     * 
     * @param index
     */
    public void addIndex(Index index);

    /**
     * remove a index
     * 
     * @param id
     *            ID de l'index
     */
    public void remove(String id);

    /**
     * Check if the index exist
     * 
     * @param id
     *            ID de l'index
     * @return true if the index exist
     */
    public boolean contains(String id);
    
    /**
     * Adds indexes from the given configuration file to the index manager.
     * @param confSource
     * @throws ConfigurationException
     */
    public void addIndexes(Source confSource) throws ConfigurationException;
}
