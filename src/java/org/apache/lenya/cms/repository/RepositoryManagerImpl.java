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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.commons.lang.Validate;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.cluster.ClusterManager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Repository manager implementation.
 * @version $Id:$
 */
public class RepositoryManagerImpl extends AbstractLogEnabled implements RepositoryManager,
        Serviceable {

    protected ServiceManager manager;
    private ClusterManager cluster;
    
    // Cache unmodifiable sessions per identity.
    protected LoadingCache<Identity, Session> sharedSessions = CacheBuilder.newBuilder()
            .weakKeys()
            .weakValues()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build(new CacheLoader<Identity, Session>() {
                @Override
                public Session load(final Identity identity) throws Exception {
                    return new SessionImpl(identity, false, manager, getLogger());
                }
            });

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
        cluster = (ClusterManager) manager.lookup(ClusterManager.ROLE);
    }

    @Override
    public Session createSession(Identity identity, boolean modifiable) throws RepositoryException {
        Validate.notNull(identity, "identity must not be null");
        // Check that instance is not running in cluster slave mode
        // if session is modifiable.
        if (modifiable == true && cluster.isSlave()) {
            throw new RepositoryException("Can't create a modifiable session. " +
            		"Instance is running in clustered mode as slave.");
        }
        if (modifiable) {
            if (getLogger().isDebugEnabled())
                getLogger().debug("Created modifiable session.");
            return new SessionImpl(identity, modifiable, this.manager, getLogger());
        } else {
            // If session is not modifiable then get a shared session.
            return getSharedSession(identity);
        }
    }

    /**
     * Get a shared session for identity.
     * @param identity Identity
     * @return Shared session for identity.
     * @throws ExecutionException 
     */
    protected Session getSharedSession(Identity identity) throws RepositoryException {
        try {
            return sharedSessions.get(identity);
        } catch (ExecutionException e) {
            throw new RepositoryException(e);
        }
    }
}
