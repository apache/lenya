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

package org.apache.lenya.cms.ac.usecase;

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
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.cache.BuildException;
import org.apache.lenya.ac.cache.CachingException;
import org.apache.lenya.ac.cache.SourceCache;
import org.apache.lenya.cms.ac.PolicyUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;

/**
 * Authorizer for usecases.
 * @version $Id: UsecaseAuthorizer.java 392449 2006-04-07 23:20:38Z michi $
 */
public class UsecaseAuthorizerImpl extends AbstractLogEnabled implements UsecaseAuthorizer,
        Serviceable, Disposable, Parameterizable {

    protected static final String TYPE = "usecase";
    protected static final String USECASE_PARAMETER = "lenya.usecase";

    private SourceCache cache;
    private String configurationUri;

    /**
     * Returns the configuration source cache.
     * @return A source cache.
     */
    public SourceCache getCache() {
        return this.cache;
    }

    private Map pubId2configUri = new HashMap();

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

    /**
     * @see org.apache.lenya.ac.Authorizer#authorize(org.apache.cocoon.environment.Request)
     */
    public boolean authorize(Request request) throws AccessControlException {

        String usecase = request.getParameter(USECASE_PARAMETER);
        boolean authorized = true;

        SourceResolver resolver = null;

        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            if (usecase != null) {

                String _configurationUri;
                if (getConfigurationURI() != null) {
                    _configurationUri = getConfigurationURI();
                } else {
                    Publication publication = PublicationUtil.getPublication(this.manager, request);
                    _configurationUri = getConfigurationURI(publication);
                }

                Role[] roles = PolicyUtil.getRoles(request);
                authorized = authorizeUsecase(usecase, roles, _configurationUri, request
                        .getRequestURI());
            } else {
                getLogger().debug("No usecase to authorize. Granting access.");
            }
        } catch (final ServiceException e) {
            throw new AccessControlException(e);
        } catch (final PublicationException e) {
            throw new AccessControlException(e);
        } catch (final AccessControlException e) {
            throw new AccessControlException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }

        return authorized;
    }

    /**
     * Authorizes a usecase.
     * 
     * @param usecase The usecase ID.
     * @param roles The roles of the current identity.
     * @param _configurationUri The URI to retrieve the policy configuration
     *        from.
     * @param requestURI The request URI.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    public boolean authorizeUsecase(String usecase, Role[] roles, String _configurationUri,
            String requestURI) throws AccessControlException {
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

    private ServiceManager manager;

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

    protected static final String PARAMETER_CONFIGURATION = "configuration";

    /**
     * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
     */
    public void parameterize(Parameters parameters) throws ParameterException {
        if (parameters.isParameter(PARAMETER_CONFIGURATION)) {
            this.configurationUri = parameters.getParameter(PARAMETER_CONFIGURATION);
        }
    }

    /**
     * Returns the configuration URL.
     * @return The configuration URL.
     */
    public String getConfigurationURI() {
        return this.configurationUri;
    }

    /**
     * Authorizes a usecase.
     * 
     * @param usecase The usecase to authorize.
     * @param roles The roles of the identity.
     * @param publication The publication.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    public boolean authorizeUsecase(String usecase, Role[] roles, Publication publication,
            String requestURI) throws AccessControlException {
        return authorizeUsecase(usecase, roles, getConfigurationURI(publication), requestURI);
    }

    protected boolean authorize(Request request, String webappUrl) throws AccessControlException {
        return authorize(request);
    }

    protected static final String AC_CONFIGURATION_FILE = "config/ac/ac.xconf".replace('/',
            File.separatorChar);

    /**
     * Retrieves access control configuration of a specific publication.
     * @param publication The publication.
     * @return Configuration
     * @throws AccessControlException when something went wrong.
     */
    public Configuration getConfiguration(Publication publication) throws AccessControlException {
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

    public boolean isPermitted(String usecase, Publication publication, Role role)
            throws AccessControlException {
        String configUri = getConfigurationURI(publication);
        UsecaseRoles usecaseRoles = getUsecaseRoles(configUri);
        String[] roles = usecaseRoles.getRoles(usecase);
        return Arrays.asList(roles).contains(role.getId());
    }

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

}
