/*
 * $Id: FileAccreditableManager.java,v 1.7 2003/10/31 15:16:45 andreas Exp $ <License>
 * 
 * ============================================================================ The Apache Software
 * License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica- tion, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)." Alternately, this acknowledgment may
 * appear in the software itself, if and wherever such third-party acknowledgments normally appear. 4.
 * The names "Apache Lenya" and "Apache Software Foundation" must not be used to endorse or promote
 * products derived from this software without prior written permission. For written permission,
 * please contact apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written permission of the Apache
 * Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU- DING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally created by Michael Wechner <michi@apache.org> .
 * For more information on the Apache Soft- ware Foundation, please see <http://www.apache.org/> .
 * 
 * Lenya includes software developed by the Apache Software Foundation, W3C, DOM4J Project,
 * BitfluxEditor, Xopus, and WebSHPINX. </License>
 */
package org.apache.lenya.cms.ac2.file;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;

import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.GroupManager;
import org.apache.lenya.cms.ac.IPRangeManager;
import org.apache.lenya.cms.ac.RoleManager;
import org.apache.lenya.cms.ac.UserManager;
import org.apache.lenya.cms.ac2.AbstractAccreditableManager;

import java.io.File;
import java.net.URI;

/**
 * File-based accreditable manager.
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
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
	 * @see org.apache.lenya.cms.ac2.AbstractAccreditableManager#initializeGroupManager()
	 */
    protected GroupManager initializeGroupManager() throws AccessControlException {
        return GroupManager.instance(getConfigurationDirectory());
    }

    /**
	 * @see org.apache.lenya.cms.ac2.AbstractAccreditableManager#initializeIPRangeManager()
	 */
    protected IPRangeManager initializeIPRangeManager() throws AccessControlException {
        return IPRangeManager.instance(getConfigurationDirectory());
    }

    /**
	 * @see org.apache.lenya.cms.ac2.AbstractAccreditableManager#initializeRoleManager()
	 */
    protected RoleManager initializeRoleManager() throws AccessControlException {
        return RoleManager.instance(getConfigurationDirectory());
    }

    /**
	 * @see org.apache.lenya.cms.ac2.AbstractAccreditableManager#initializeUserManager()
	 */
    protected UserManager initializeUserManager() throws AccessControlException {
        return UserManager.instance(getConfigurationDirectory());
    }

}
