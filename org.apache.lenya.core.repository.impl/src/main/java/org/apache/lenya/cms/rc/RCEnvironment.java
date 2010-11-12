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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper class that holds the revision controller configuration
 */
public class RCEnvironment {

    private static final Log logger = LogFactory.getLog(RCEnvironment.class);

    /**
     * <code>CONFIGURATION_FILE</code> The configuration file
     */
    public static final String CONFIGURATION_FILE = "lenya" + File.separator + "config"
            + File.separator + "rc" + File.separator + "revision-controller.xconf";
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
    public static RCEnvironment getInstance(String contextPath, Log logger) {
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
    public RCEnvironment(String contextPath, Log logger) {
        logger.debug("context path:" + contextPath);

        String configurationFilePath = contextPath + "/" + CONFIGURATION_FILE;
        logger.debug("configuration file path:" + configurationFilePath);
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
     * TODO: Bean wiring
     */
    public void setRcmlDirectory(String rcmlDir) {
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
     * TODO: Bean wiring
     */
    public void setBackupDirectory(String backupDir) {
        this.backupDirectory = backupDir;
    }
}
