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

/* $Id: DefaultAccessController.java,v 1.3 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.ac.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Authenticator;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.ItemManagerListener;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.PolicyManager;

public class DefaultAccessController
    extends AbstractLogEnabled
    implements AccessController, Configurable, Serviceable, Disposable, ItemManagerListener {

    protected static final String AUTHORIZER_ELEMENT = "authorizer";
    protected static final String TYPE_ATTRIBUTE = "type";
    protected static final String ACCREDITABLE_MANAGER_ELEMENT = "accreditable-manager";
    protected static final String POLICY_MANAGER_ELEMENT = "policy-manager";

    private ServiceSelector accreditableManagerSelector;
    private AccreditableManager accreditableManager;

    private ServiceSelector authorizerSelector;
    private Map authorizers = new HashMap();
    private List authorizerKeys = new ArrayList();

    private ServiceSelector policyManagerSelector;
    private PolicyManager policyManager;

    private Authenticator authenticator;

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#authenticate(org.apache.cocoon.environment.Request)
     */
    public boolean authenticate(Request request) throws AccessControlException {

        assert request != null;
        boolean authenticated = getAuthenticator().authenticate(getAccreditableManager(), request);

        return authenticated;
    }

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#authorize(org.apache.cocoon.environment.Request)
     */
    public boolean authorize(Request request) throws AccessControlException {

        assert request != null;

        boolean authorized = false;

        getLogger().debug("=========================================================");
        getLogger().debug("Beginning authorization.");

        if (hasAuthorizers()) {
            Authorizer[] authorizers = getAuthorizers();
            int i = 0;
            authorized = true;

            while ((i < authorizers.length) && authorized) {

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("---------------------------------------------------------");
                    getLogger().debug("Invoking authorizer [" + authorizers[i] + "]");
                }

                if (authorizers[i] instanceof PolicyAuthorizer) {
                    PolicyAuthorizer authorizer = (PolicyAuthorizer) authorizers[i];
                    authorizer.setAccreditableManager(accreditableManager);
                    authorizer.setPolicyManager(policyManager);
                }

                authorized = authorized && authorizers[i].authorize(request);

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(
                        "Authorizer [" + authorizers[i] + "] returned [" + authorized + "]");
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

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
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
     * Configures or parameterizes a component, depending on the implementation
     * as Configurable or Parameterizable.
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
     * Creates the accreditable manager.
     * 
     * @param configuration The access controller configuration.
     * @throws ConfigurationException when the configuration failed.
     * @throws ServiceException when something went wrong.
     * @throws ParameterException when something went wrong.
     */
    protected void setupAccreditableManager(Configuration configuration)
        throws ConfigurationException, ServiceException, ParameterException {

        Configuration accreditableManagerConfiguration =
            configuration.getChild(ACCREDITABLE_MANAGER_ELEMENT, false);
        if (accreditableManagerConfiguration != null) {
            String accreditableManagerType =
                accreditableManagerConfiguration.getAttribute(TYPE_ATTRIBUTE);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("AccreditableManager type: [" + accreditableManagerType + "]");
            }

            accreditableManagerSelector =
                (ServiceSelector) manager.lookup(AccreditableManager.ROLE + "Selector");
            accreditableManager =
                (AccreditableManager) accreditableManagerSelector.select(accreditableManagerType);
            accreditableManager.addItemManagerListener(this);
            configureOrParameterize(accreditableManager, accreditableManagerConfiguration);
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
    protected void setupAuthorizers(Configuration configuration)
        throws ServiceException, ConfigurationException, ParameterException {
        Configuration[] authorizerConfigurations = configuration.getChildren(AUTHORIZER_ELEMENT);
        if (authorizerConfigurations.length > 0) {
            authorizerSelector = (ServiceSelector) manager.lookup(Authorizer.ROLE + "Selector");

            for (int i = 0; i < authorizerConfigurations.length; i++) {
                String type = authorizerConfigurations[i].getAttribute(TYPE_ATTRIBUTE);
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Adding authorizer [" + type + "]");
                }

                Authorizer authorizer = (Authorizer) authorizerSelector.select(type);
                authorizerKeys.add(type);
                authorizers.put(type, authorizer);
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
    protected void setupPolicyManager(Configuration configuration)
        throws ServiceException, ConfigurationException, ParameterException {
        Configuration policyManagerConfiguration =
            configuration.getChild(POLICY_MANAGER_ELEMENT, false);
        if (policyManagerConfiguration != null) {
            String policyManagerType = policyManagerConfiguration.getAttribute(TYPE_ATTRIBUTE);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Adding policy manager type: [" + policyManagerType + "]");
            }
            policyManagerSelector =
                (ServiceSelector) manager.lookup(PolicyManager.ROLE + "Selector");
            policyManager = (PolicyManager) policyManagerSelector.select(policyManagerType);
            configureOrParameterize(policyManager, policyManagerConfiguration);
        }
    }

    /**
     * Sets up the authenticator.
     * 
     * @throws ServiceException when something went wrong.
     */
    protected void setupAuthenticator() throws ServiceException {
        authenticator = (Authenticator) manager.lookup(Authenticator.ROLE);
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
     * Returns the authorizers of this action.
     * 
     * @return An array of authorizers.
     */
    public Authorizer[] getAuthorizers() {

        Authorizer[] authorizerArray = new Authorizer[authorizers.size()];
        for (int i = 0; i < authorizers.size(); i++) {
            String key = (String) authorizerKeys.get(i);
            authorizerArray[i] = (Authorizer) authorizers.get(key);
        }

        return authorizerArray;
    }

    /**
     * Returns if this action has authorizers.
     * 
     * @return A boolean value.
     */
    protected boolean hasAuthorizers() {
        return !authorizers.isEmpty();
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {

        if (accreditableManagerSelector != null) {
            if (accreditableManager != null) {
                accreditableManager.removeItemManagerListener(this);
                accreditableManagerSelector.release(accreditableManager);
            }
            getManager().release(accreditableManagerSelector);
        }

        if (policyManagerSelector != null) {
            if (policyManager != null) {
                policyManagerSelector.release(policyManager);
            }
            getManager().release(policyManagerSelector);
        }

        if (authorizerSelector != null) {
            Authorizer[] authorizers = getAuthorizers();
            for (int i = 0; i < authorizers.length; i++) {
                authorizerSelector.release(authorizers[i]);
            }
            getManager().release(authorizerSelector);
        }

        if (authenticator != null) {
            getManager().release(authenticator);
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Disposing [" + this +"]");
        }
    }

    /**
     * Returns the accreditable manager.
     * 
     * @return An accreditable manager.
     */
    public AccreditableManager getAccreditableManager() {
        return accreditableManager;
    }

    /**
     * Returns the policy manager.
     * 
     * @return A policy manager.
     */
    public PolicyManager getPolicyManager() {
        return policyManager;
    }

    /**
     * Returns the authenticator.
     * 
     * @return The authenticator.
     */
    public Authenticator getAuthenticator() {
        return authenticator;
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
     * @see org.apache.lenya.cms.ac2.AccessController#setupIdentity(org.apache.cocoon.environment.Request)
     */
    public void setupIdentity(Request request) throws AccessControlException {
        Session session = request.getSession(true);
        if (!hasValidIdentity(session)) {
            Identity identity = new Identity();
            String remoteAddress = request.getRemoteAddr();

            Machine machine = new Machine(remoteAddress);
            IPRange[] ranges = accreditableManager.getIPRangeManager().getIPRanges();
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
    protected boolean hasValidIdentity(Session session) throws AccessControlException {
        boolean valid = true;
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());
        if (identity == null || !ownsIdenity(identity)) {
            valid = false;
        }
        return valid;
    }

    /**
     * @see org.apache.lenya.cms.ac.ItemManagerListener#itemAdded(org.apache.lenya.cms.ac.Item)
     */
    public void itemAdded(Item item) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item was added: [" + item + "]");
        }
    }

    /**
     * @see org.apache.lenya.cms.ac.ItemManagerListener#itemRemoved(org.apache.lenya.cms.ac.Item)
     */
    public void itemRemoved(Item item) throws AccessControlException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Item was removed: [" + item + "]");
            getLogger().debug("Notifying policy manager");
        }
        getPolicyManager().accreditableRemoved(getAccreditableManager(), (Accreditable) item);
    }

}
