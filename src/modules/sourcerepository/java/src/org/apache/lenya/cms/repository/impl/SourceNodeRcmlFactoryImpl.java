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
package org.apache.lenya.cms.repository.impl;

import java.util.concurrent.ConcurrentMap;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.commons.lang.Validate;
import org.apache.lenya.cms.cluster.ClusterManager;
import org.apache.lenya.cms.rc.RCML;
import org.apache.lenya.cms.repository.SourceNode;
import org.apache.lenya.cms.repository.SourceNodeRCML;
import org.apache.lenya.cms.repository.SourceNodeRcmlFactory;

import com.google.common.collect.MapMaker;

/**
 * Source node RCML factory implementation.
 */
public class SourceNodeRcmlFactoryImpl
implements SourceNodeRcmlFactory, Serviceable
{
    private ServiceManager manager;
    private ClusterManager cluster;

    private ConcurrentMap<String, SourceNodeRCML> uri2rcml;

    /**
     * C'tor.
     */
    public SourceNodeRcmlFactoryImpl() {
        // Create cache map for RCML objects.
        uri2rcml = new MapMaker().softValues().makeMap();
    }

    @Override
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
        cluster = (ClusterManager) manager.lookup(ClusterManager.ROLE);
    }

    /**
     * @param node The node.
     * @param manager The service manager.
     * @return An RCML object.
     */
    public synchronized RCML getRcml(SourceNode node) {
        Validate.notNull(node, "node must not be null");
        String uri = node.getSourceURI();
        SourceNodeRCML rcml = uri2rcml.get(uri);
        // Reload RCML if modified externally and running in clustered
        // mode as slave.
        if (rcml == null || isReloadRCML(rcml)) {
            rcml = new SourceNodeRCML(node, manager);
            this.uri2rcml.put(uri, rcml);
        }
        return rcml;
    }

    /**
     * Check if RCML needs to be reloaded.
     * Only check for external modifications if running in clustered
     * mode as slave.
     * @param rcml Source node RCML.
     * @return true if RCML needs to be reloaded, otherwise false.
     */
    private boolean isReloadRCML(SourceNodeRCML rcml) {
        Validate.notNull(rcml, "rcml must not be null");
        return cluster.isClusterEnabled() && cluster.isSlave() &&
                rcml.isModifiedExternally();
    }
}
