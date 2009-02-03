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
package org.apache.lenya.cms.repository;

import org.apache.lenya.ac.Identity;

/**
 * Repository manager.
 * @version $Id:$
 */
public interface RepositoryManager {

    /**
     * The role of the service.
     */
    String ROLE = RepositoryManager.class.getName();
    
    /**
     * @param identity The identity the session belongs to.
     * @param modifiable Determines if the repository items in this session should be modifiable.
     * @return A session.
     * @throws RepositoryException if an error occurs.
     */
    Session createSession(Identity identity, boolean modifiable) throws RepositoryException;
    
}
