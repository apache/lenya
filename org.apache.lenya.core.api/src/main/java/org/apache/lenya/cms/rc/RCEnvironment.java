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

/* $Id$  */

package org.apache.lenya.cms.rc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.xml.sax.SAXException;

/**
 * Helper class that holds the revision controller configuration
 */
public class RCEnvironment extends AbstractLogEnabled implements Configurable {
    
    /**
     * <code>CONFIGURATION_FILE</code> The configuration file
     */
    public static final String CONFIGURATION_FILE = "lenya" + File.separator + "config" +
        File.separator + "rc" + File.separator + "revision-controller.xconf";
    /**
     * <code>RCML_DIRECTORY</code> The RCML directory
     */
    public static final String RCML_DIRECTORY = "rcml-directory";
    /**
     * <code>BACKUP_DIRECTORY</code> The backup directory
     */
    public static final String BACKUP_DIRECTORY = "backup-directory";
    private String rcmlDirectory;
    private String backupDirectory;
    
    private static Map instances = new HashMap();
    
    /**
     * Returns the singleton RC environment for this context path.
     * @param contextPath The context path (the Lenya webapp directory).
     * @param logger The logger.
     * @return An RC environment.
     */
    public static RCEnvironment getInstance(String contextPath, Logger logger) {
        RCEnvironment instance = (RCEnvironment) instances.get(contextPath); 
        if (instance == null) {
            instance = new RCEnvironment(contextPath, logger);
            instances.put(contextPath, instance);
        }
        return instance;
    }

    /**
     * Creates a new RCEnvironment object from the context path
     * @param contextPath The context path
     * @param logger The logger.
     */
    public RCEnvironment(String contextPath, Logger logger) {
        enableLogging(logger);
        getLogger().debug("context path:" + contextPath);

        String configurationFilePath = contextPath + "/" + CONFIGURATION_FILE;
        getLogger().debug("configuration file path:" + configurationFilePath);

        File configurationFile = new File(configurationFilePath);

        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            Configuration configuration = builder.buildFromFile(configurationFile);
            configure(configuration);
        } catch (final ConfigurationException e) {
            getLogger().error("Cannot load revision controller configuration! ", e);
        } catch (final SAXException e) {
            getLogger().error("Cannot load revision controller configuration! ", e);
        } catch (final IOException e) {
            getLogger().error("Cannot load revision controller configuration! ", e);
        }
    }

    /**
     @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)     
     */
    public void configure(org.apache.avalon.framework.configuration.Configuration configuration)
        throws ConfigurationException {
        // revision controller
        setRCMLDirectory(configuration.getChild("rcmlDirectory").getAttribute("href"));
        setBackupDirectory(configuration.getChild("backupDirectory").getAttribute("href"));

        getLogger().debug("CONFIGURATION:\nRCML Directory: href=" + getRCMLDirectory());
        getLogger().debug("CONFIGURATION:\nBackup Directory: href=" + getBackupDirectory());
    }

    /**
     * Get the RCML directory
     * @return The RCML directory
     */
    public String getRCMLDirectory() {
        return this.rcmlDirectory;
    }

	/**
	 * Set the rcml directory
	 * @param rcmlDir the path to the rcml directory
	 */
    protected void setRCMLDirectory(String rcmlDir) {
        this.rcmlDirectory = rcmlDir;
    }

    /**
     * Get the backup directory
     * @return The backup directory
     */
    public String getBackupDirectory() {
        return this.backupDirectory;
    }

	/**
	 * Set the backup directory
	 * @param backupDir path to the backup directory
	 */
    protected void setBackupDirectory(String backupDir) {
        this.backupDirectory = backupDir;
    }
}
