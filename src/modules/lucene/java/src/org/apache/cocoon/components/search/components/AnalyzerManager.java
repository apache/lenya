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
import org.apache.lucene.analysis.Analyzer;

/**
 * Analyzer Manager Component
 * 
 * @author Maisonneuve Nicolas
 */

public interface AnalyzerManager {

    public static final String ROLE = AnalyzerManager.class.getName();

    /**
     * Return the analyzer
     * 
     * @param id
     *            analyzer ID
     * @return
     * @see org.apache.lucene.analysis.Analyzer
     */
    public Analyzer getAnalyzer(String id) throws ConfigurationException;

    /**
     * Is this analyzer exist
     * 
     * @param id
     *            String the analyzer id
     * @return boolean
     */
    public boolean exist(String id);

    /**
     * Return all analyzer IDs
     * 
     * @return A array with all id's analyzer
     */
    public String[] getAnalyzersID();

    /**
     * Add a lucene analyser
     * 
     * @param id
     *            the id of the analyzer
     * @param analyzer
     *            the analyzer to add
     */
    public void put(String id, Analyzer analyzer);

    /**
     * Remove a analyzer
     * 
     * @param id
     *            the analyzer ID
     */
    public void remove(String id);
}
