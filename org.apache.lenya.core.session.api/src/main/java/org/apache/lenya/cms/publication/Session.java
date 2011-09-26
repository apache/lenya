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
package org.apache.lenya.cms.publication;

//import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.repository.RepositoryException;

public interface Session {
    
    Repository getRepository();

    /**
     * @param id The publication ID.
     * @return A publication.
     * @throws ResourceNotFoundException If the publication does not exist.
     */
    Publication getPublication(String id) throws ResourceNotFoundException;
    
    String[] getPublicationIds();
    
    /**
     * @param id The publication ID.
     * @return A publication.
     * @throws RepositoryException if the publication already exists.
     */
    Publication addPublication(String id) throws RepositoryException;

    boolean existsPublication(String id);
    
    //Florent : remove because create cycliq dependencies.
    //remove it when ok
    //Identity getIdentity();

    void commit() throws RepositoryException;

    void rollback() throws RepositoryException;

    boolean isModifiable();

    UriHandler getUriHandler();

    //Florent : to remove, seems to not be used, and create cyclic dependencies
    //void enqueueEvent(Document document, Object descriptor);

    String getId();

}
