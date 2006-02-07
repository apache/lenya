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
package org.apache.lenya.cms.repo;

/**
 * Repository session.
 */
public interface Session {

    /**
     * @param id The publication ID.
     * @return A publication.
     * @throws RepositoryException if the publication does not exist.
     */
    Publication getPublication(String id) throws RepositoryException;
    
    /**
     * Adds a publication.
     * @param id The publication ID.
     * @return The added publication.
     * @throws RepositoryException if the publication already exists.
     */
    Publication addPublication(String id) throws RepositoryException;
    
    /**
     * Checks if a publication exists.
     * @param id The publication ID.
     * @return A boolean value.
     * @throws RepositoryException if an error occurs.
     */
    boolean existsPublication(String id) throws RepositoryException;
    
    /**
     * Saves the session.
     * @throws RepositoryException if an error occurs.
     */
    public void save() throws RepositoryException;
    
    /**
     * @return The repository this session belongs to.
     * @throws RepositoryException if an error occurs.
     */
    Repository getRepository() throws RepositoryException;

    /**
     * Closes the session.
     * @throws RepositoryException if an error occurs.
     */
    void logout() throws RepositoryException;
    
}
