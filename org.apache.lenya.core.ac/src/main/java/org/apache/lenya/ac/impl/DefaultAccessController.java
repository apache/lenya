/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *  
 */

package org.apache.lenya.ac.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.lang.Validate;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.AccreditableManagerFactory;
import org.apache.lenya.ac.Authenticator;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.ItemManagerListener;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.PolicyManager;
import org.apache.lenya.ac.Role;
//import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.utils.ServletHelper;
import org.apache.lenya.utils.URLInformation;
import org.springframework.context.ApplicationContext;

/**
 * Default access controller implementation.
 * @version $Id$
 */
public class DefaultAccessController extends AbstractLogEnabled implements AccessController,
        ItemManagerListener, Configurable {

    protected static final String AUTHORIZER_ELEMENT = "authorizer";
    protected static final String TYPE_ATTRIBUTE = "type";
    protected static final String ACCREDITABLE_MANAGER_ELEMENT = "accreditable-manager";
    protected static final String POLICY_MANAGER_ELEMENT = "policy-manager";

    private static final String VALID_IP = "([0-9]{1,3}\\.){3}[0-9]{1,3}";
    private AccreditableManager accreditableManager;
    private PolicyManager policyManager;
    private Map authorizers = new HashMap();
    private List authorizerKeys = new ArrayList();
    private Authenticator authenticator;

    public boolean authenticate(HttpServletRequest request) throws AccessControlException {

        assert request != null;
        boolean authenticated = getAuthenticator().authenticate(getAccreditableManager(), request);

        return authenticated;
    }

    /**
     * @see org.apache.lenya.ac.AccessController#authorize(org.apache.cocoon.environment.Request)
     */
    public boolean authorize(Request request) throws AccessControlException {
        assert request != null;

        boolean authorized = false;

        getLogger().debug("=========================================================");
        getLogger().debug("Beginning authorization.");

        resolveRoles(request);

        if (hasAuthorizers()) {
            Authorizer[] _authorizers = getAuthorizers();
            int i = 0;
            authorized = true;

            while ((i < _authorizers.length) && authorized) {

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("---------------------------------------------------------");
                    getLogger().debug("Invoking authorizer [" + _authorizers[i] + "]");
                }

                authorized = authorized && _authorizers[i].authorize(request);

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(
                            "Authorizer [" + _authorizers[i] + "] returned [" + authorized + "]");
                }

                i++;
            }
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("=========================================================");
            getLogger().debug("Authorization complete, result: [" + authorized + "]");
            getLogger().debug("=========================================================");
        }

        return authorized;
    }

    protected void resolveRoles(Request request) throws AccessControlException {
        Validate.notNull(request, "request");
      //TODO : florent : remove comment when ok 
        //String webappUrl = ServletHelper.getWebappURI(request);
        String webappUrl = new URLInformation().getWebappUrl();
        HttpSession session = request.getSession(true);
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());

        Role[] roles;
        if (identity.belongsTo(this.accreditableManager)) {
            roles = this.policyManager.getGrantedRoles(this.accreditableManager, identity,
                    webappUrl);
        } else {
            roles = new Role[0];
            getLogger().debug(
                    "No roles resolved for identity [" + identity
                            + "] - belongs to wrong accreditable manager.");
        }
        saveRoles(request, roles);
    }

    /**
     * Saves the roles of the current identity to the request.
     * @param request The request.
     * @param roles The roles.
     */
    protected void saveRoles(Request request, Role[] roles) {
        if (getLogger().isDebugEnabled()) {
            StringBuffer rolesBuffer = new StringBuffer();
            for (int i = 0; i < roles.length; i++) {
                rolesBuffer.append(" ").append(roles[i]);
            }
            getLogger().debug("Adding roles [" + rolesBuffer + " ] to request [" + request + "]");
        }
        request.setAttribute(Role.class.getName(), Arrays.asList(roles));
    }

    /**
     * Configures or parameterizes a component, depending on the implementation as Configurable or
     * Parameterizable.
     * @param component The component.
     * @param configuration The configuration to use.
     * @throws ConfigurationException when an error occurs during configuration.
     * @throws ParameterException when an error occurs during parameterization.
     */
    public static void configureOrParameterize(Component component, Configuration configuration)
            throws ConfigurationException, ParameterException {
        if (component instanceof Configurable) {
            ((Configurable) component).configure(configuration);
        }
        if (component instanceof Parameterizable) {
            Parameters parameters = Parameters.fromConfiguration(configuration);
            ((Parameterizable) component).parameterize(parameters);
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     * @deprecated Replace with different configuration mechanism, e.g. commons configuration.
     */
    public void configure(Configuration conf) throws ConfigurationException {

        try {
            setupAccreditableManager(conf);
            setupAuthorizers(conf);
            setupPolicyManager(conf);
            setupAuthenticator();
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Configuration failed: ", e);
        }
    }

    /**
     * Creates the accreditable manager.
     * 
     * @param configuration The access controller configuration.
     * @throws ConfigurationException when the configuration failed.
     * @throws ServiceException when something went wrong.
     * @throws ParameterException when something went wrong.
     */
    protected void setupAccreditableManager(Configuration configuration)
            throws ConfigurationException, ServiceException, ParameterException {
        Configuration config = configuration.getChild(ACCREDITABLE_MANAGER_ELEMENT, false);
        if (config != null) {
            ApplicationContext context = WebAppContextUtils.getCurrentWebApplicationContext();
            AccreditableManagerFactory factory = (AccreditableManagerFactory) context
                    .getBean(AccreditableManagerFactory.ROLE);
            this.accreditableManager = factory.getAccreditableManager(config);
            this.accreditableManager.addItemManagerListener(this);
        }
    }

    /**
     * Creates the authorizers.
     * 
     * @param configuration The access controller configuration.
     * @throws ConfigurationException when the configuration failed.
     * @throws ServiceException when something went wrong.
     * @throws ParameterException when something went wrong.
     */
    protected void setupAuthorizers(Configuration configuration) throws ServiceException,
            ConfigurationException, ParameterException {
        Configuration[] authorizerConfigurations = configuration.getChildren(AUTHORIZER_ELEMENT);
        if (authorizerConfigurations.length > 0) {
            ApplicationContext context = WebAppContextUtils.getCurrentWebApplicationContext();

            for (int i = 0; i < authorizerConfigurations.length; i++) {
                String type = authorizerConfigurations[i].getAttribute(TYPE_ATTRIBUTE);
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Adding authorizer [" + type + "]");
                }

                Authorizer authorizer = (Authorizer) context.getBean(Authorizer.ROLE + "/" + type);
                this.authorizerKeys.add(type);
                this.authorizers.put(type, authorizer);
                configureOrParameterize(authorizer, authorizerConfigurations[i]);
            }
        }
    }

    /**
     * Creates the policy manager.
     * 
     * @param configuration The access controller configuration.
     * @throws ConfigurationException when the configuration failed.
     * @throws ServiceException when something went wrong.
     * @throws ParameterException when something went wrong.
     */
    protected void setupPolicyManager(Configuration configuration) throws ServiceException,
            ConfigurationException, ParameterException {
        Configuration policyManagerConfiguration = configuration.getChild(POLICY_MANAGER_ELEMENT,
                false);
        if (policyManagerConfiguration != null) {
            String policyManagerType = policyManagerConfiguration.getAttribute(TYPE_ATTRIBUTE);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Adding policy manager type: [" + policyManagerType + "]");
            }
            ApplicationContext context = WebAppContextUtils.getCurrentWebApplicationContext();
            this.policyManager = (PolicyManager) context.getBean(PolicyManager.ROLE + "/"
                    + policyManagerType);
            configureOrParameterize(this.policyManager, policyManagerConfiguration);
        }
    }

    /**
     * Sets up the authenticator.
     * @throws ServiceException when something went wrong.
     */
    protected void setupAuthenticator() throws ServiceException {
        this.authenticator = (Authenticator) WebAppContextUtils.getCurrentWebApplicationContext()
                .getBean(Authenticator.ROLE);
    }

    /**
     * Returns the authorizers of this action.
     * @return An array of authorizers.
     */
    public Authorizer[] getAuthorizers() {

        Authorizer[] authorizerArray = new Authorizer[this.authorizers.size()];
        for (int i = 0; i < this.authorizers.size(); i++) {
            String key = (String) this.authorizerKeys.get(i);
            authorizerArray[i] = (Authorizer) this.authorizers.get(key);
        }
        return authorizerArray;
    }

    /**
     * Returns if this action has authorizers.
     * @return A boolean value.
     */
    protected boolean hasAuthorizers() {
        return !this.authorizers.isEmpty();
    }

    /**
     * Returns the accreditable manager.
     * @return An accreditable manager.
     */
    public AccreditableManager getAccreditableManager() {
        return this.accreditableManager;
    }

    /**
     * Returns the policy manager.
     * @return A policy manager.
     */
    public PolicyManager getPolicyManager() {
        return this.policyManager;
    }

    /**
     * Returns the authenticator.
     * @return The authenticator.
     */
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    /**
     * Checks if this identity was initialized by this access controller.
     * 
     * @param identity An identity.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    public boolean ownsIdenity(Identity identity) throws AccessControlException {
        return identity.belongsTo(getAccreditableManager());
    }

    /**
     * @see org.apache.lenya.ac.AccessController#setupIdentity(org.apache.cocoon.environment.Request)
     */
    public void setupIdentity(Request request) throws AccessControlException {
        HttpSession session = request.getSession(true);
        if (!hasValidIdentity(session)) {
            Identity identity = new Identity(getLogger());
            identity.initialize();
            String remoteAddress = request.getRemoteAddr();
            String clientAddress = request.getHeader("x-forwarded-for");

            if (clientAddress != null) {
                Pattern p = Pattern.compile(VALID_IP);
                Matcher m = p.matcher(clientAddress);

                if (m.find()) {
                    remoteAddress = m.group();
                }
            }

            getLogger().info("Remote Address to use: [" + remoteAddress + "]");

            Machine machine = new Machine(remoteAddress);
            IPRange[] ranges = this.accreditableManager.getIPRangeManager().getIPRanges();
            for (int i = 0; i < ranges.length; i++) {
                if (ranges[i].contains(machine)) {
                    machine.addIPRange(ranges[i]);
                }
            }

            identity.addIdentifiable(machine);
            session.setAttribute(Identity.class.getName(), identity);
        }
    }

    /**
     * Checks if the session contains an identity that is not null and belongs to the used access
     * controller.
     * 
     * @param session The current session.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    protected boolean hasValidIdentity(HttpSession session) throws AccessControlException {
        Validate.notNull(session, "session");
        boolean valid = true;
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());
        if (identity == null || !ownsIdenity(identity)) {
            valid = false;
        }
        return valid;
    }

    /**
     * @see org.apache.lenya.ac.ItemManagerListener#itemAdded(org.apache.lenya.ac.Item)
     */
    public void itemAdded(Item item) throws AccessControlException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item was added: [" + item + "]");
            getLogger().debug("Notifying policy manager");
        }
        if (item instanceof Accreditable) {
            getPolicyManager().accreditableAdded(getAccreditableManager(), (Accreditable) item);
        }
    }

    /**
     * @see org.apache.lenya.ac.ItemManagerListener#itemRemoved(org.apache.lenya.ac.Item)
     */
    public void itemRemoved(Item item) throws AccessControlException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item was removed: [" + item + "]");
            getLogger().debug("Notifying policy manager");
        }

        if (!(item instanceof Role)) {
            getPolicyManager().accreditableRemoved(getAccreditableManager(), (Accreditable) item);
        }
    }

}
