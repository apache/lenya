/*
$Id: DefaultAccessController.java,v 1.3 2003/07/14 18:07:01 andreas Exp $
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
package org.apache.lenya.cms.ac2;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.cms.ac.AccessControlException;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DefaultAccessController
    extends AbstractLogEnabled
    implements AccessController, Configurable, Serviceable, Disposable {

    protected static final String AUTHORIZER_ELEMENT = "authorizer";
    protected static final String TYPE_ATTRIBUTE = "type";
    protected static final String ACCREDITABLE_MANAGER_ELEMENT = "accreditable-manager";
    protected static final String POLICY_MANAGER_ELEMENT = "policy-manager";

    private ServiceSelector accreditableManagerSelector;
    private AccreditableManager accreditableManager;

    private ServiceSelector authorizerSelector;
    private List authorizers = new ArrayList();

    private ServiceSelector policyManagerSelector;
    private PolicyManager policyManager;

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#authenticate(org.apache.cocoon.environment.Request)
     */
    public boolean authenticate(Request request) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.apache.lenya.cms.ac2.AccessController#authorize(org.apache.cocoon.environment.Request)
     */
    public boolean authorize(Request request)
        throws AccessControlException {

        assert request != null;

        boolean authorized = false;

        if (request != null) {

            Session session = request.getSession(true);
            Identity identity = (Identity) session.getAttribute(Identity.class.getName());

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Trying to authorize identity: " + identity);
            }

            if (identity != null && hasAuthorizers()) {
                Authorizer[] authorizers = getAuthorizers();
                int i = 0;
                authorized = true;

                while ((i < authorizers.length) && authorized) {

                    authorized =
                        authorized
                            && authorizers[i].authorize(
                                accreditableManager,
                                policyManager,
                                identity,
                                request);

                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug(
                            "Authorizer [" + authorizers[i] + "] returned [" + authorized + "]");
                    }

                    i++;
                }
            }
        }

        return authorized;
    }
    
    private boolean isInitialized = false;
    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {

        try {

            if (!isInitialized) {
                setupAccreditableManager(conf);
                setupAuthorizers(conf);
                setupPolicyManager(conf);
                isInitialized = true;
            }

            Configuration accreditableManagerConfiguration =
                conf.getChild(ACCREDITABLE_MANAGER_ELEMENT);
            Parameters parameters = Parameters.fromConfiguration(accreditableManagerConfiguration);
            accreditableManager.parameterize(parameters);

        } catch (Exception e) {
            throw new ConfigurationException("Configuration failed: ", e);
        }
    }

    /**
     * Creates the accreditable manager. 
     * @param configuration The access controller configuration.
     */
    protected void setupAccreditableManager(Configuration configuration)
        throws ConfigurationException, ServiceException {
        Configuration accreditableManagerConfiguration =
            configuration.getChild(ACCREDITABLE_MANAGER_ELEMENT);
        String accreditableManagerType =
            accreditableManagerConfiguration.getAttribute(TYPE_ATTRIBUTE);
        getLogger().debug("AccreditableManager type: [" + accreditableManagerType + "]");

        accreditableManagerSelector =
            (ServiceSelector) manager.lookup(AccreditableManager.ROLE + "Selector");
        accreditableManager =
            (AccreditableManager) accreditableManagerSelector.select(accreditableManagerType);
    }

    /**
     * Creates the authorizers. 
     * @param configuration The access controller configuration.
     */
    protected void setupAuthorizers(Configuration configuration)
        throws ServiceException, ConfigurationException {
        Configuration[] authorizerConfigurations = configuration.getChildren(AUTHORIZER_ELEMENT);
        authorizerSelector = (ServiceSelector) manager.lookup(Authorizer.ROLE + "Selector");

        for (int i = 0; i < authorizerConfigurations.length; i++) {
            String type = authorizerConfigurations[i].getAttribute(TYPE_ATTRIBUTE);
            Authorizer authorizer = (Authorizer) authorizerSelector.select(type);
            authorizers.add(authorizer);
            getLogger().debug("Adding authorizer [" + type + "]");
        }
    }

    /**
     * Creates the policy manager. 
     * @param configuration The access controller configuration.
     */
    protected void setupPolicyManager(Configuration configuration)
        throws ServiceException, ConfigurationException {
        Configuration policyManagerConfiguration = configuration.getChild(POLICY_MANAGER_ELEMENT);
        String policyManagerType = policyManagerConfiguration.getAttribute(TYPE_ATTRIBUTE);
        policyManagerSelector = (ServiceSelector) manager.lookup(PolicyManager.ROLE + "Selector");
        policyManager = (PolicyManager) policyManagerSelector.select(policyManagerType);
        getLogger().debug("Policy manager type: [" + policyManagerType + "]");
    }

    private ServiceManager manager;

    /**
     * Set the global component manager.
     * @param manager The global component manager
     * @exception ComponentException
     * @throws ServiceException when something went wrong.
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * Returns the service manager.
     * @return A service manager.
     */
    protected ServiceManager getManager() {
        return manager;
    }

    /**
     * Returns the authorizers of this action.
     * @return An array of authorizers.
     */
    protected Authorizer[] getAuthorizers() {
        return (Authorizer[]) authorizers.toArray(new Authorizer[authorizers.size()]);
    }

    /**
     * Returns if this action has authorizers.
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
    }

    /**
     * Returns the accreditable manager.
     * @return An accreditable manager.
     * TODO: change to protected when authenticator actions are refactored
     */
    public AccreditableManager getAccreditableManager() {
        return accreditableManager;
    }

    /**
     * Returns the policy manager.
     * @return A policy manager.
     */
    protected PolicyManager getPolicyManager() {
        return policyManager;
    }

}
