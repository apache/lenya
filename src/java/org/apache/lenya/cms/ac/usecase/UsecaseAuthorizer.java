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

/* $Id: UsecaseAuthorizer.java,v 1.2 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.cms.ac.usecase;

import java.util.Arrays;
import java.util.List;

import org.apache.avalon.framework.activity.Disposable;
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
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.cache.CachingException;
import org.apache.lenya.ac.cache.SourceCache;
import org.apache.lenya.ac.impl.PolicyAuthorizer;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

public class UsecaseAuthorizer
    extends AbstractLogEnabled
    implements Authorizer, Serviceable, Disposable, Parameterizable {

    public static final String TYPE = "usecase";
    public static final String USECASE_PARAMETER = "lenya.usecase";

    private SourceCache cache;
    private String configurationUri;

    /**
	 * Returns the configuration source cache.
	 * 
	 * @return A source cache.
	 */
    public SourceCache getCache() {
        return cache;
    }

    /**
	 * Returns the source URI of the usecase role configuration file for a certain publication.
	 * 
	 * @param publication The publication.
	 * @return A string representing a URI.
	 */
    protected String getConfigurationURI(Publication publication) {
        return "context:///"
            + Publication.PUBLICATION_PREFIX_URI
            + "/"
            + publication.getId()
            + CONFIGURATION_FILE;
    }

    /**
	 * @see org.apache.lenya.cms.ac2.Authorizer#authorize(org.apache.lenya.cms.ac2.AccreditableManager,
	 *      org.apache.lenya.cms.ac2.PolicyManager, org.apache.lenya.cms.ac2.Identity,
	 *      org.apache.cocoon.environment.Request)
	 */
    public boolean authorize(Request request) throws AccessControlException {

        String usecase = request.getParameter(USECASE_PARAMETER);
        boolean authorized = true;

        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            if (usecase != null) {

                String configurationUri;
                if (getConfigurationURI() != null) {
                    configurationUri = getConfigurationURI();
                } else {
                    Publication publication = PublicationFactory.getPublication(resolver, request);
                    configurationUri = getConfigurationURI(publication);
                }

                Role[] roles = PolicyAuthorizer.getRoles(request);
                authorized = authorizeUsecase(usecase, roles, configurationUri);
            } else {
                getLogger().debug("No usecase to authorize. Granting access.");
            }
        } catch (Exception e) {
            throw new AccessControlException(e);
        } finally {
            if (resolver != null) {
                manager.release(resolver);
            }
        }

        return authorized;
    }

    /**
	 * Authorizes a usecase.
	 * 
	 * @param usecase The usecase ID.
	 * @param roles The roles of the current identity.
	 * @param configurationUri The URI to retrieve the policy configuration from.
	 * @return A boolean value.
	 * @throws AccessControlException when something went wrong.
	 */
    public boolean authorizeUsecase(String usecase, Role[] roles, String configurationUri)
        throws AccessControlException {
        getLogger().debug("Authorizing usecase [" + usecase + "]");
        boolean authorized = true;

        UsecaseRolesBuilder builder = new UsecaseRolesBuilder();
        UsecaseRoles usecaseRoles;
        try {
            usecaseRoles = (UsecaseRoles) getCache().get(configurationUri, builder);
        } catch (CachingException e) {
            throw new AccessControlException(e);
        }
        
        if (usecaseRoles == null) {
            throw new AccessControlException("Usecase policies configuration not found at [" + configurationUri + "]");
        }
        
        if (usecaseRoles.hasRoles(usecase)) {

            getLogger().debug("Roles for usecase found.");

            List usecaseRoleIds = Arrays.asList(usecaseRoles.getRoles(usecase));

            int i = 0;
            authorized = false;
            while (!authorized && i < roles.length) {
                authorized = usecaseRoleIds.contains(roles[i].getId());
                getLogger().debug(
                    "Authorization for role [" + roles[i].getId() + "] is [" + authorized + "]");
                i++;
            }
        } else {
            getLogger().debug("No roles for usecase found. Granting access.");
        }
        return authorized;
    }

    /**
	 * Authorizes a usecase.
	 * 
	 * @param url The webapp URL.
	 * @param usecase The usecase ID to authorize.
	 * @param roles The roles of the current identity.
	 * @return A boolean value.
	 * @throws AccessControlException when something went wrong. public boolean
	 *             authorizeUsecase(String url, String usecase, Request request) throws
	 *             AccessControlException {
	 * 
	 * Role[] roles = PolicyAuthorizer.getRoles(request); Publication publication =
	 * PublicationFactory.getPublication(resolver, request); return authorizeUsecase(url, usecase,
	 * roles, publication); }
	 */

    private ServiceManager manager;

    /**
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
    public void service(ServiceManager manager) throws ServiceException {
        getLogger().debug("Servicing [" + getClass().getName() + "]");
        this.manager = manager;
        this.cache = (SourceCache) manager.lookup(SourceCache.ROLE);
    }

    /**
	 * @see org.apache.avalon.framework.activity.Disposable#dispose()
	 */
    public void dispose() {
        if (getCache() != null) {
            manager.release(getCache());
        }
    }

    public static final String CONFIGURATION_FILE = "/config/ac/usecase-policies.xml";
    public static final String PARAMETER_CONFIGURATION = "configuration";

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
	 * 
	 * @return The configuration URL.
	 */
    public String getConfigurationURI() {
        return configurationUri;
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
    public boolean authorizeUsecase(String usecase, Role[] roles, Publication publication)
        throws AccessControlException {
        return authorizeUsecase(usecase, roles, getConfigurationURI(publication));
    }

}
