/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
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
    public static final String CONFIGURATION_FILE = "lenya" + File.separator + "config" +
        File.separator + "rc" + File.separator + "revision-controller.xconf";
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
