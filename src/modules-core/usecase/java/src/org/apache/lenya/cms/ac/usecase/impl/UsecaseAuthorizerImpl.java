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

package org.apache.lenya.cms.ac.usecase.impl;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.cache.BuildException;
import org.apache.lenya.ac.cache.CachingException;
import org.apache.lenya.ac.cache.SourceCache;
import org.apache.lenya.cms.ac.PolicyUtil;
import org.apache.lenya.cms.ac.usecase.UsecaseAuthorizer;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;

/**
 * Authorizer for usecases.
 * <p>
 * Supported parameters via {@link Parameterizable}:
 * </p>
 * <ul>
 * <li>
 *   {@link #PARAMETER_CONFIGURATION} - location of the usecase policies file
 *   (parameterizable for testing purposes)
 * </li>
 * </ul>
 * @version $Id: UsecaseAuthorizer.java 392449 2006-04-07 23:20:38Z michi $
 */
public class UsecaseAuthorizerImpl extends AbstractLogEnabled implements UsecaseAuthorizer,
        Serviceable, Disposable, Parameterizable {

    protected static final String PARAMETER_CONFIGURATION = "configuration";
    protected static final String TYPE = "usecase";
    protected static final String USECASE_PARAMETER = "lenya.usecase";
    private static final String AC_CONFIGURATION_FILE
        = "config/access-control/access-control.xml".replace('/', File.separatorChar);

    private SourceCache cache;
    /** 
      * the configuration URI for this component 
      */
    private String configurationUri;
    private ServiceManager manager;

    /**
     * Maps publication IDs to their configuration URIs.
     * This is a persistent map to avoid unnecessary publication lookups.
     * Whenever an authorization request for a new publication is dealt with,
     * the publication's configuration URI is stored, to be re-used on later
     * occasions (for the lifetime of the component).
     */
    private Map pubId2configUri = new HashMap();


    /**
     * @see org.apache.lenya.cms.ac.usecase.UsecaseAuthorizer#authorizeUsecase(java.lang.String, org.apache.lenya.ac.Role[], org.apache.lenya.cms.publication.Publication)
     */
    public boolean authorizeUsecase(String usecase, Role[] roles, Publication publication)
           throws AccessControlException {
        return authorizeUsecase(usecase, roles, getConfigurationURI(publication));
    }

    private boolean authorizeUsecase(String usecase, Role[] roles, String _configurationUri)
            throws AccessControlException {
        getLogger().debug("Authorizing usecase [" + usecase + "]");
        boolean authorized = false;

        UsecaseRoles usecaseRoles = getUsecaseRoles(_configurationUri);

        if (usecaseRoles == null) {
            throw new AccessControlException("Usecase policies configuration not found at ["
                    + _configurationUri + "]");
        }

        if (usecaseRoles.hasRoles(usecase)) {
            getLogger().debug("Roles for usecase found.");

            List usecaseRoleIds = Arrays.asList(usecaseRoles.getRoles(usecase));

            int i = 0;
            while (!authorized && i < roles.length) {
                authorized = usecaseRoleIds.contains(roles[i].getId());
                getLogger()
                        .debug(
                                "Authorization for role [" + roles[i].getId() + "] is ["
                                        + authorized + "]");
                i++;
            }
        } else {
            getLogger().debug("No roles for usecase [" + usecase + "] found. Denying access.");
        }
        return authorized;
    }

    /**
     * @see org.apache.lenya.cms.ac.usecase.UsecaseAuthorizer#isPermitted(java.lang.String, org.apache.lenya.cms.publication.Publication, org.apache.lenya.ac.Role)
       */
    public boolean isPermitted(String usecase, Publication publication, Role role)
            throws AccessControlException {
        String configUri = getConfigurationURI(publication);
        UsecaseRoles usecaseRoles = getUsecaseRoles(configUri);
        String[] roles = usecaseRoles.getRoles(usecase);
        return Arrays.asList(roles).contains(role.getId());
    }

    /**
     * @see org.apache.lenya.cms.ac.usecase.UsecaseAuthorizer#setPermission(java.lang.String, org.apache.lenya.cms.publication.Publication, org.apache.lenya.ac.Role, boolean)
     */
    public void setPermission(String usecase, Publication publication, Role role, boolean granted)
            throws AccessControlException {
        String configUri = getConfigurationURI(publication);
        if (configUri.startsWith("aggregate-")) {
            configUri = configUri.substring("aggregate-".length());
        }
        UsecaseRoles usecaseRoles = getUsecaseRoles(configUri);
        List roles = Arrays.asList(usecaseRoles.getRoles(usecase));
        String roleId = role.getId();
        if (granted) {
            if (!roles.contains(roleId)) {
                usecaseRoles.addRole(usecase, roleId);
            }
        } else {
            if (roles.contains(roleId)) {
                usecaseRoles.removeRole(usecase, roleId);
            }
        }
        UsecaseRolesBuilder builder = new UsecaseRolesBuilder();
        try {
            builder.save(usecaseRoles, configUri, this.manager);
        } catch (BuildException e) {
            throw new AccessControlException(e);
        }
    }

    /**
     * @see org.apache.lenya.ac.Authorizer#authorize(org.apache.cocoon.environment.Request)
     */
    public boolean authorize(Request request) throws AccessControlException {

        String usecase = request.getParameter(USECASE_PARAMETER);
        boolean authorized = true;

        try {
            if (usecase != null) {

                String _configurationUri;
                // Check if the service has been parameterized with a configuration URI. This
                // can be used for testing purposes etc.
                if (getConfigurationURI() != null) {
                    _configurationUri = getConfigurationURI();
                } else {
                    Publication publication = PublicationUtil.getPublication(this.manager, request);
                    _configurationUri = getConfigurationURI(publication);
                }

                Role[] roles = PolicyUtil.getRoles(request);
                authorized = authorizeUsecase(usecase, roles, _configurationUri);
            } else {
                getLogger().debug("No usecase to authorize. Granting access.");
            }
        } catch (final PublicationException e) {
            throw new AccessControlException(e);
        } catch (final AccessControlException e) {
            throw new AccessControlException(e);
        }

        return authorized;
    }

    /**
     * Returns the configuration source cache.
     * @return A source cache.
     */
    private SourceCache getCache() {
        return this.cache;
    }

    /**
     * Returns the source URI of the usecase role configuration file for a
     * certain publication.
     * 
     * @param publication The publication.
     * @return A string representing a URI.
     */
    protected String getConfigurationURI(Publication publication) {

        String configURI = (String) this.pubId2configUri.get(publication.getId());
        if (configURI == null) {
            try {
                Configuration config = getConfiguration(publication);
                Configuration[] authorizerConfigs = config.getChildren("authorizer");
                for (int i = 0; i < authorizerConfigs.length; i++) {
                    if (authorizerConfigs[i].getAttribute("type").equals("usecase")) {
                        Configuration paraConfig = authorizerConfigs[i].getChild("parameter");
                        configURI = paraConfig.getAttribute("value");
                        this.pubId2configUri.put(publication.getId(), configURI);
                    }
                }
            } catch (Exception e) {
                getLogger().error(e.getMessage(), e);
            }
        }
        return configURI;
    }


    protected UsecaseRoles getUsecaseRoles(String _configurationUri) throws AccessControlException {
        UsecaseRolesBuilder builder = new UsecaseRolesBuilder();
        UsecaseRoles usecaseRoles;
        try {
            usecaseRoles = (UsecaseRoles) getCache().get(_configurationUri, builder);
        } catch (CachingException e) {
            throw new AccessControlException(e);
        }
        return usecaseRoles;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        getLogger().debug("Servicing [" + getClass().getName() + "]");
        this.manager = _manager;
        this.cache = (SourceCache) _manager.lookup(SourceCache.ROLE);
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (getCache() != null) {
            this.manager.release(getCache());
        }
    }

    public void parameterize(Parameters parameters) throws ParameterException {
        if (parameters.isParameter(PARAMETER_CONFIGURATION)) {
            this.configurationUri = parameters.getParameter(PARAMETER_CONFIGURATION);
        }
    }

    private String getConfigurationURI() {
        return this.configurationUri;
    }

    /**
     * Retrieves access control configuration of a specific publication.
     * @param publication The publication.
     * @return Configuration
     * @throws AccessControlException when something went wrong.
     */
    private Configuration getConfiguration(Publication publication) throws AccessControlException {
        File configurationFile = new File(publication.getDirectory(), AC_CONFIGURATION_FILE);

        if (configurationFile.isFile()) {
            try {
                Configuration configuration = new DefaultConfigurationBuilder()
                        .buildFromFile(configurationFile);
                return configuration;
            } catch (Exception e) {
                throw new AccessControlException(e);
            }
        } else {
            throw new AccessControlException("No such file or directory: " + configurationFile);
        }
    }

}