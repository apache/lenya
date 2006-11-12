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
package org.apache.lenya.cms.repo.metadata;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * <p>
 * This interface defines the methods to register and access meta data element sets.
 * The registry concept ensures that meta data specifications can be stored together
 * with documents.
 * </p>
 * <p>
 * Once a set of elements is registered, it is stored in the repository.
 * </p>
 */
public interface MetaDataRegistry {

    /**
     * @return All available element sets.
     * @throws RepositoryException if an error occurs.
     */
    String[] getElementSetNames() throws RepositoryException;

    /**
     * @param name The name of the element set.
     * @return the element set.
     * @throws RepositoryException if an error occurs. 
     */
    ElementSet getElementSet(String name) throws RepositoryException;

    /**
     * Registers an array of elements.
     * @param name The name.
     * @param elements The elements.
     * @throws RepositoryException if the name is already registered.
     */
    void register(String name, Element[] elements) throws RepositoryException;
    
    /**
     * Checks if an element set is registered.
     * @param name The name.
     * @return A boolean value.
     * @throws RepositoryException if an error occurs.
     */
    boolean isRegistered(String name) throws RepositoryException;
}
