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
 * A publication.
 */
public interface Publication {

    /**
     * @param area The area ID.
     * @return The area object.
     * @throws RepositoryException if the area does not exist.
     */
    Area getArea(String area) throws RepositoryException;
    
    /**
     * @param area The area ID.
     * @return The area object.
     * @throws RepositoryException if the area already exists.
     */
    Area addArea(String area) throws RepositoryException;
    
    /**
     * Checks if an area exists in this publication.
     * @param area The area.
     * @return A boolean value.
     * @throws RepositoryException of an error occurs.
     */
    boolean existsArea(String area) throws RepositoryException;
    
    /**
     * @return The publication ID.
     * @throws RepositoryException if an error occurs.
     */
    String getPublicationId() throws RepositoryException;
    
}
