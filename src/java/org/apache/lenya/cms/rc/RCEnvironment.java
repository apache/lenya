/*
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.rc;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Category;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @author Edith Chevrier 
 */
public class RCEnvironment implements Configurable {
    static Category log = Category.getInstance(RCEnvironment.class);
    public static final String CONFIGURATION_FILE = "lenya" + File.separator  +"config" + File.separator +  "rc" + File.separator +"revision-controller.xconf";
    public static final String RCML_DIRECTORY = "rcml-directory";
    public static final String BACKUP_DIRECTORY = "backup-directory";
    private String rcmlDirectory;
    private String backupDirectory;

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

    protected void setBackupDirectory(String backupDir) {
        backupDirectory = backupDir;
    }

}
