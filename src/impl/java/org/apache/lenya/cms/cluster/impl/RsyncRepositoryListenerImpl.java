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
package org.apache.lenya.cms.cluster.impl;

import java.io.File;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.cms.cluster.ClusterConfigurationException;
import org.apache.lenya.cms.cluster.ClusterManager;
import org.apache.lenya.cms.cluster.RsyncRepositoryListener;
import org.apache.lenya.cms.observation.ObservationRegistry;
import org.apache.lenya.cms.observation.RepositoryEvent;

/**
 * Rsync repository listener.
 * Triggers rsync when repository was modified for pushing changes to remote
 * hosts.
 * 
 * This is an alternative approach than to having a shared
 * file system (like NFS) for storing content when running in a clustered
 * environment.
 */
public class RsyncRepositoryListenerImpl extends AbstractLogEnabled
implements Serviceable, Startable, RsyncRepositoryListener, Initializable,
        ThreadSafe
{
    private ServiceManager manager;
    private ClusterManager clusterManager;

    private File baseDir;
    private String command;
    private String options;
    private String[] targets;

    @Override
    public void eventFired(RepositoryEvent event) {
        // Synchronize content in a separate thread.
        RsyncExecutionThread rsyncThread = new RsyncExecutionThread(
                event, command, options, targets, baseDir);
        try {
            rsyncThread.service(manager);
        } catch (ServiceException e) {
            if (getLogger().isErrorEnabled())
                getLogger().error("Error initializing RsyncExecutionThread", e);
        }
        rsyncThread.enableLogging(getLogger());
        rsyncThread.start();
    }

    @Override
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
        this.clusterManager = (ClusterManager) manager.lookup(
                ClusterManager.ROLE);
    }

    @Override
    public void initialize() throws Exception {
        command = clusterManager.getRsyncCommand();
        options = clusterManager.getRsyncOptions();
        targets = clusterManager.getRsyncTargets();
        baseDir = new File(clusterManager.getRsyncBaseDir());
        validateConfiguration();
        if (getLogger().isInfoEnabled()) {
            getLogger().info("Using rsync command [" + command +
                    "] with options [" + options + "]");
            getLogger().info("rsync base directory [" +
                    baseDir.getAbsolutePath() + "]");
        }
    }

    /**
     * Validate that command and base directory exists.
     * @throws ClusterConfigurationException If validation fails.
     */
    private void validateConfiguration() throws ClusterConfigurationException {
        // Validate that rsync command exists.
        File f = new File(command);
        if (!f.exists()) {
            throw new ClusterConfigurationException(
                    "rsync command not found [" + command + "]");
        }
        // Validate rsync base directory.
        if (!baseDir.exists()) {
            throw new ClusterConfigurationException(
                    "Rsync base dir not found [" +
                    baseDir.getAbsolutePath() + "]");
        }
    }

    @Override
    public void start() throws Exception {
        if (clusterManager.isRsyncSynchronizationEnabled()) {
            ObservationRegistry registry = null;
            try {
                registry = (ObservationRegistry) manager.lookup(
                        ObservationRegistry.ROLE);
                registry.registerListener(this);
                if (getLogger().isInfoEnabled())
                    getLogger().info("Registered rsync repository listener.");
            } finally {
                if (registry != null) {
                    this.manager.release(registry);
                }
            }
        }
    }

    @Override
    public void stop() throws Exception {
    }

}
