/*
 * Created on Jul 10, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.lenya.cms.ac2.sitemap;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac2.AccreditableManager;
import org.apache.lenya.cms.ac2.DefaultPolicy;
import org.apache.lenya.cms.ac2.Policy;
import org.apache.lenya.cms.ac2.PolicyManager;
import org.apache.lenya.cms.publication.Publication;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SitemapPolicyManager extends AbstractLogEnabled implements PolicyManager, Serviceable {

    /**
     * @see org.apache.lenya.cms.ac2.PolicyManager#buildURLPolicy(org.apache.lenya.cms.ac2.AccreditableManager, org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public DefaultPolicy buildURLPolicy(AccreditableManager controller, Publication publication, String url) throws AccessControlException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.lenya.cms.ac2.PolicyManager#buildSubtreePolicy(org.apache.lenya.cms.ac2.AccreditableManager, org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public DefaultPolicy buildSubtreePolicy(AccreditableManager controller, Publication publication, String url) throws AccessControlException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.lenya.cms.ac2.PolicyManager#saveURLPolicy(org.apache.lenya.cms.publication.Publication, java.lang.String, org.apache.lenya.cms.ac2.DefaultPolicy)
     */
    public void saveURLPolicy(Publication publication, String url, DefaultPolicy policy) throws AccessControlException {
    }

    /**
     * @see org.apache.lenya.cms.ac2.PolicyManager#saveSubtreePolicy(org.apache.lenya.cms.publication.Publication, java.lang.String, org.apache.lenya.cms.ac2.DefaultPolicy)
     */
    public void saveSubtreePolicy(Publication publication, String url, DefaultPolicy policy) throws AccessControlException {
    }

    /**
     * @see org.apache.lenya.cms.ac2.PolicyManager#getPolicy(org.apache.lenya.cms.ac2.AccreditableManager, org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public Policy getPolicy(AccreditableManager controller, Publication publication, String url) throws AccessControlException {
        
        SourceResolver resolver = null;
        Policy policy = null;
        try {
            resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
            String policyUrl = publication.getId() + "/policies" + url + ".acml";
            getLogger().debug("Policy URL: " + policyUrl);
            Source source = resolver.resolveURI("cocoon://" + policyUrl);
        }
        catch (Exception e) {
            throw new AccessControlException(e);
        }
        finally {
            getManager().release(resolver);
        }
        return policy;
    }
    
    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }



    /**
     * Returns the service manager.
     * @return A service manager.
     */
    public ServiceManager getManager() {
        return manager;
    }

}
