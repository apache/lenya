/*
 * Created on Jul 10, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
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
import org.apache.lenya.cms.publication.Publication;

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
    public boolean authorize(Publication publication, Request request)
        throws AccessControlException {
            
        assert publication != null;
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
                                publication,
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

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {

        try {
            Configuration accreditableManagerConfiguration =
                conf.getChild(ACCREDITABLE_MANAGER_ELEMENT);
            String accreditableManagerType =
                accreditableManagerConfiguration.getAttribute(TYPE_ATTRIBUTE);
            getLogger().debug("AccreditableManager type: [" + accreditableManagerType + "]");
            
            accreditableManagerSelector =
                (ServiceSelector) manager.lookup(AccreditableManager.ROLE + "Selector");
            accreditableManager =
                (AccreditableManager) accreditableManagerSelector.select(accreditableManagerType);
            Parameters parameters = Parameters.fromConfiguration(accreditableManagerConfiguration);
            accreditableManager.parameterize(parameters);

            Configuration[] authorizerConfigurations = conf.getChildren(AUTHORIZER_ELEMENT);
            authorizerSelector = (ServiceSelector) manager.lookup(Authorizer.ROLE + "Selector");

            for (int i = 0; i < authorizerConfigurations.length; i++) {
                String type = authorizerConfigurations[i].getAttribute(TYPE_ATTRIBUTE);
                Authorizer authorizer = (Authorizer) authorizerSelector.select(type);
                authorizers.add(authorizer);
                getLogger().debug("Adding authorizer [" + type + "]");
            }

            Configuration policyManagerConfiguration = conf.getChild(POLICY_MANAGER_ELEMENT);
            String policyManagerType = policyManagerConfiguration.getAttribute(TYPE_ATTRIBUTE);
            policyManagerSelector =
                (ServiceSelector) manager.lookup(PolicyManager.ROLE + "Selector");
            policyManager = (PolicyManager) policyManagerSelector.select(policyManagerType);
            getLogger().debug("Policy manager type: [" + policyManagerType + "]");
        } catch (Exception e) {
            throw new ConfigurationException("Configuration failed: ", e);
        }
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
     */
    public AccreditableManager getAccreditableManager() {
        return accreditableManager;
    }

}
