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

/* $Id: FileAccreditableManager.java,v 1.4 2004/03/08 16:48:21 gregor Exp $  */

package org.apache.lenya.ac.file;

import java.io.File;
import java.net.URI;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.GroupManager;
import org.apache.lenya.ac.IPRangeManager;
import org.apache.lenya.ac.RoleManager;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.ac.impl.AbstractAccreditableManager;

/**
 * File-based accreditable manager.
 */
public class FileAccreditableManager
    extends AbstractAccreditableManager
    implements Serviceable, Parameterizable {

    /**
	 * Creates a new FileAccreditableManager. If you use this constructor, you have to set the
	 * configuration directory either by calling {@link #setConfigurationDirectory(File)}or by
	 * calling {@link #configure(Configuration)}.
	 */
    public FileAccreditableManager() {
    }

    /**
	 * Creates a new FileAccessController based on a configuration directory.
	 * 
	 * @param configurationDirectory The configuration directory.
	 */
    public FileAccreditableManager(File configurationDirectory) {
        assert configurationDirectory != null;
        assert configurationDirectory.exists();
        assert configurationDirectory.isDirectory();
        this.configurationDirectory = configurationDirectory;
    }

    private File configurationDirectory;

    /**
	 * Returns the configuration directory.
	 * 
	 * @return The configuration directory.
	 * @throws AccessControlException when something went wrong.
	 */
    public File getConfigurationDirectory() throws AccessControlException {

        if (configurationDirectory == null) {

            if (configurationDirectoryPath == null) {
                throw new AccessControlException("Configuration directory not set!");
            }

            Source source = null;
            SourceResolver resolver = null;
            File directory;
            try {

                getLogger().debug(
                    "Configuration directory Path: [" + configurationDirectoryPath + "]");

                resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
                source = resolver.resolveURI(configurationDirectoryPath);

                getLogger().debug("Configuration directory URI: " + source.getURI());
                directory = new File(new URI(source.getURI()));
            } catch (Exception e) {
                throw new AccessControlException(e);
            } finally {
                if (resolver != null) {
                    if (source != null) {
                        resolver.release(source);
                    }
                    getManager().release(resolver);
                }
            }
            setConfigurationDirectory(directory);
        }

        return configurationDirectory;
    }

    protected static final String DIRECTORY = "directory";

    /**
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
    public void parameterize(Parameters parameters) throws ParameterException {
        if (parameters.isParameter(DIRECTORY)) {
            configurationDirectoryPath = parameters.getParameter(DIRECTORY);
            getLogger().debug("Configuration directory: [" + configurationDirectoryPath + "]");
        }
    }

    private String configurationDirectoryPath;

    /**
	 * Sets the configuration directory.
	 * 
	 * @param file The configuration directory.
	 * 
	 * @throws AccessControlException if an error occurs
	 */
    public void setConfigurationDirectory(File file) throws AccessControlException {
        if (file == null || !file.isDirectory()) {
            throw new AccessControlException(
                "Configuration directory [" + file + "] does not exist!");
        }
        configurationDirectory = file;
    }

    private ServiceManager manager;

    /**
	 * Set the global component manager.
	 * 
	 * @param manager The global component manager
	 * @exception ComponentException
	 * @throws ServiceException when something went wrong.
	 */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
	 * Returns the service manager.
	 * 
	 * @return A service manager.
	 */
    protected ServiceManager getManager() {
        return manager;
    }

    /**
	 * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeGroupManager()
	 */
    protected GroupManager initializeGroupManager() throws AccessControlException {
        return FileGroupManager.instance(getConfigurationDirectory());
    }

    /**
	 * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeIPRangeManager()
	 */
    protected IPRangeManager initializeIPRangeManager() throws AccessControlException {
        return FileIPRangeManager.instance(getConfigurationDirectory());
    }

    /**
	 * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeRoleManager()
	 */
    protected RoleManager initializeRoleManager() throws AccessControlException {
        return FileRoleManager.instance(getConfigurationDirectory());
    }

    /**
	 * @see org.apache.lenya.ac.impl.AbstractAccreditableManager#initializeUserManager()
	 */
    protected UserManager initializeUserManager() throws AccessControlException {
        return FileUserManager.instance(getConfigurationDirectory());
    }

}
