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
 * Meta data registry.
 */
public interface MetaDataRegistry {
    
    /**
     * The Avalon role.
     */
    String ROLE = MetaDataRegistry.class.getName();

    /**
     * @param namespaceUri The namespace URI of the element set.
     * @return the element set.
     * @throws MetaDataException if an error occurs. 
     */
    ElementSet getElementSet(String namespaceUri) throws MetaDataException;
    
    /**
     * Checks if an element set is registered.
     * @param namespaceUri The namespace URI.
     * @return A boolean value.
     * @throws MetaDataException if an error occurs.
     */
    boolean isRegistered(String namespaceUri) throws MetaDataException;
    
    /**
     * Register an element set.
     * @param namespaceUri The namespace URI.
     * @param elementSet The element set.
     * @throws MetaDataException if a set is already registered for this name.
     */
    void register(String namespaceUri, ElementSet elementSet) throws MetaDataException;
    
    /**
     * @return The registered namespace URIs.
     * @throws MetaDataException if an error occurs.
     */
    String[] getNamespaceUris() throws MetaDataException;
    
}
