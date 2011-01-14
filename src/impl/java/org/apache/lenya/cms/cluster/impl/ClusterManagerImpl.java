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

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.cms.cluster.ClusterManager;
import org.apache.lenya.cms.cluster.ClusterConfigurationException;
import org.apache.lenya.cms.cluster.ClusterMode;

/**
 * Cluster manager implementation.
 *
 * For configuration of the cluster see <tt>lenya/config/cluster.xconf</tt>
 */
public class ClusterManagerImpl extends AbstractLogEnabled
implements ClusterManager, Initializable, Serviceable
{

    private final static String SYS_PROP_CONFIG_URI =
        "lenya.cluster.configFile";
    private final static String CONFIG_URI =
        "context:/lenya/config/cluster/cluster.xconf";

    private ServiceManager manager;

    private boolean isClusterEnabled = false;
    private ClusterMode clusterMode = ClusterMode.MASTER;
    private boolean isRsyncSynchronizationEnabled = false;
    private String rsyncCommand;
    private String rsyncOptions;
    private String[] rsyncTargets;
    private String rsyncBaseDir;

    @Override
    public boolean isClusterEnabled() {
        return isClusterEnabled;
    }

    @Override
    public boolean isMaster() {
        return !isClusterEnabled() || ClusterMode.MASTER.equals(clusterMode);
    }

    @Override
    public boolean isSlave() {
        return isClusterEnabled() && ClusterMode.SLAVE.equals(clusterMode);
    }

    @Override
    public boolean isRsyncSynchronizationEnabled() {
        return isClusterEnabled() && isMaster() &&
                isRsyncSynchronizationEnabled;
    }

    /**
     * Get configuration URI.
     * @return Configuration URI.
     */
    private String getConfigurationUri() {
        String configUri = System.getProperty(SYS_PROP_CONFIG_URI);
        if (configUri != null) {
            configUri = "file://" + configUri;
        } else {
            configUri = CONFIG_URI;
        }
        return configUri;
    }

    @Override
    public String getRsyncCommand() {
        return rsyncCommand;
    }

    @Override
    public String getRsyncOptions() {
        return rsyncOptions;
    }

    @Override
    public String[] getRsyncTargets() {
        return rsyncTargets;
    }

    @Override
    public String getRsyncBaseDir() {
        return rsyncBaseDir;
    }

    @Override
    public void initialize() throws Exception {
        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            readConfiguration(resolver);
        } catch (Exception e) {
            if (getLogger().isErrorEnabled())
                getLogger().error("Error reading cluster configuration", e);
        } finally {
            if (resolver != null)
                manager.release(resolver);
        }
        if ( getLogger().isInfoEnabled()) {
            if (isClusterEnabled()) {
                getLogger().info("Running Lenya in cluster mode [" +
                        clusterMode.getText() + "]");
            } else {
                getLogger().info("Lenya cluster mode disabled.");
            }
        }
    }

    /**
     * Read cluster configuration.
     * @param resolver Source resolver.
     * @throws ClusterConfigurationException If reading cluster
     *      configuration failed.
     */
    private void readConfiguration(SourceResolver resolver)
    throws ClusterConfigurationException
    {
        try {
            String configUri = getConfigurationUri();
            Source configSource = resolver.resolveURI(configUri);
            if (!configSource.exists()) {
                throw new ClusterConfigurationException("Cluster " +
                        "configuration file not found [" + configUri + "]");
            }
            if (getLogger().isInfoEnabled()) {
                getLogger().info("Using cluster configuration file [" +
                        configUri + "]");
            }
            DefaultConfigurationBuilder builder =
                new DefaultConfigurationBuilder();
            Configuration config =
                builder.build(configSource.getInputStream());
            // Is cluster enabled.
            Configuration enabledElem = config.getChild("enabled");
            isClusterEnabled = enabledElem.getValueAsBoolean(false);
            // Set cluster mode.
            Configuration modeElem = config.getChild("mode");
            String mode = modeElem.getValue("master");
            if (ClusterMode.MASTER.getText().equals(mode)) {
                clusterMode = ClusterMode.MASTER;
            } else if (ClusterMode.SLAVE.getText().equals(mode)) {
                clusterMode = ClusterMode.SLAVE;
            } else {
                if (getLogger().isWarnEnabled()) {
                    getLogger().warn("Unknown cluster mode [" + mode + "]. " +
                            "Setting cluster mode to master.");
                }
                clusterMode = ClusterMode.MASTER;
            }
            // Read rsync settings.
            Configuration rsyncConfig = config.getChild("rsync");
            Configuration rsyncEnabledElem = rsyncConfig.getChild("enabled");
            isRsyncSynchronizationEnabled = rsyncEnabledElem.getValueAsBoolean(false);
            Configuration rsyncCommandElem = rsyncConfig.getChild("command");
            rsyncCommand = rsyncCommandElem.getValue("/usr/bin/rsync");
            Configuration rsyncOptionsElem = rsyncConfig.getChild("options");
            rsyncOptions = rsyncOptionsElem.getValue("-av");
            Configuration rsyncTargetsElem = rsyncConfig.getChild("targets");
            Configuration[] rsyncTargetElems = rsyncTargetsElem.getChildren("target");
            rsyncTargets = new String[rsyncTargetElems.length];
            for (int i=0; i<rsyncTargets.length; i++) {
                rsyncTargets[i] = rsyncTargetElems[i].getValue();
            }
            Configuration rsyncBaseDirElem = rsyncConfig.getChild("baseDir");
            if (rsyncBaseDirElem == null) {
                rsyncBaseDir = getDefaultRsyncBaseDir();
            } else {
                rsyncBaseDir = rsyncBaseDirElem.getValue(
                        getDefaultRsyncBaseDir());
            }
        } catch (Exception e) {
            throw new ClusterConfigurationException(
                    "Error reading cluster configuration", e);
        }
    }

    /**
     * Get default rsync base directory.
     * This is the servlet context directory.
     * @return Rsync default base directory.
     */
    private String getDefaultRsyncBaseDir() {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context:///");
            return SourceUtil.getFile(source).getCanonicalPath();
        } catch (Exception e) {
            throw new RuntimeException("Error getting servlet context");
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }        
    }

    @Override
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
}
