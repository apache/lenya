/*
 * $Id: UsecaseAuthorizer.java,v 1.8 2003/10/31 15:16:46 andreas Exp $ <License>
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

package org.apache.lenya.cms.ac2.usecase;

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
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac2.Authorizer;
import org.apache.lenya.cms.ac2.PolicyAuthorizer;
import org.apache.lenya.cms.ac2.cache.CachingException;
import org.apache.lenya.cms.ac2.cache.SourceCache;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

/**
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
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
