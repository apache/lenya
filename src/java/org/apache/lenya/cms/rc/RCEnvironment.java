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

/* $Id: RCEnvironment.java,v 1.7 2004/03/01 16:18:22 gregor Exp $  */

package org.apache.lenya.cms.rc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Category;

public class RCEnvironment implements Configurable {
    private static Category log = Category.getInstance(RCEnvironment.class);
    public static final String CONFIGURATION_FILE = "lenya" + File.separator + "config" +
        File.separator + "rc" + File.separator + "revision-controller.xconf";
    public static final String RCML_DIRECTORY = "rcml-directory";
    public static final String BACKUP_DIRECTORY = "backup-directory";
    private String rcmlDirectory;
    private String backupDirectory;
    
    private static Map instances = new HashMap();
    
    /**
     * Returns the singleton RC environment for this context path.
     * @param contextPath The context path (the Lenya webapp directory).
     * @return An RC environment.
     */
    public static RCEnvironment getInstance(String contextPath) {
        RCEnvironment instance = (RCEnvironment) instances.get(contextPath); 
        if (instance == null) {
            instance = new RCEnvironment(contextPath);
            instances.put(contextPath, instance);
        }
        return instance;
    }

    /**
     * Creates a new RCEnvironment object.
     *
     * @param contextPath DOCUMENT ME!
     */
    public RCEnvironment(String contextPath) {
        log.debug("context path:" + contextPath);

        String configurationFilePath = contextPath + "/" + CONFIGURATION_FILE;
        log.debug("configuration file path:" + configurationFilePath);

        File configurationFile = new File(configurationFilePath);

        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            Configuration configuration = builder.buildFromFile(configurationFile);
            configure(configuration);
        } catch (Exception e) {
            log.error("Cannot load revision controller configuration! ", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     *
     * @throws org.apache.avalon.framework.configuration.ConfigurationException DOCUMENT ME!
     */
    public void configure(org.apache.avalon.framework.configuration.Configuration configuration)
        throws org.apache.avalon.framework.configuration.ConfigurationException {
        // revision controller
        setRCMLDirectory(configuration.getChild("rcmlDirectory").getAttribute("href"));
        setBackupDirectory(configuration.getChild("backupDirectory").getAttribute("href"));

        log.debug("CONFIGURATION:\nRCML Directory: href=" + getRCMLDirectory());
        log.debug("CONFIGURATION:\nBackup Directory: href=" + getBackupDirectory());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getRCMLDirectory() {
        return rcmlDirectory;
    }

	/**
	 * Set the rcml directory
	 * 
	 * @param rcmlDir the path to the rcml directory
	 */
    protected void setRCMLDirectory(String rcmlDir) {
        rcmlDirectory = rcmlDir;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getBackupDirectory() {
        return backupDirectory;
    }

	/**
	 * Set the backup directory
	 * 
	 * @param backupDir path to the backup directory
	 */
    protected void setBackupDirectory(String backupDir) {
        backupDirectory = backupDir;
    }
}
