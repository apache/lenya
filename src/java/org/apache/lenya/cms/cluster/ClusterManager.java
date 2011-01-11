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
package org.apache.lenya.cms.cluster;

import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * Cluster manager interface.
 * Classes implementing ClusterManager must be ThreadSafe i.e.
 * are setup as singletons.
 */
public interface ClusterManager extends ThreadSafe
{
    /**
     * Role org.apache.lenya.cms.cluster.ClusterManager
     */
    String ROLE = ClusterManager.class.getName();

    /**
     * Is clustering enabled.
     * @return true if clustering is enabled, otherwise false.
     */
    public boolean isClusterEnabled();

    /**
     * Is Lenya instance in master mode.
     * @return true if instance is master or clustering disabled, otherwise false.
     */
    public boolean isMaster();

    /**
     * Is Lenya instance in slave mode.
     * @return true if clustering is enabled and instance is slave, otherwise false.
     */
    public boolean isSlave();

    boolean isRsyncSynchronizationEnabled();

    String getRsyncCommand();

    String getRsyncOptions();

    String[] getRsyncTargets();

    String getRsyncBaseDir();
}
