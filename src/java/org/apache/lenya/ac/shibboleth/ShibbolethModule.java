package org.apache.lenya.ac.shibboleth;

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

/**
 * <p>
 * Shibboleth input module.
 * </p>
 * <p>
 * Supported attributes:
 * </p>
 * <ul>
 * <li><em>wayfServer</em> - the URL of the WAYF server</li>
 * <li><em>shire</em> - the value of the shire request parameter for the WAYF
 * server</li>
 * <li><em>target</em> - the value of the target request parameter for the
 * WAYF server</li>
 * <li><em>providerId</em> - the value of the providerId request parameter
 * for the WAYF server</li>
 * </ul>
 */
public class ShibbolethModule extends AbstractInputModule implements Serviceable {

    private ServiceManager manager;

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        Request req = ObjectModelHelper.getRequest(objectModel);

        String serverUrl = req.getScheme() + "://" + req.getServerName() + ":"
                + req.getServerPort();
        String servletUrl = serverUrl + req.getServletPath();

        if (name.equals("wayfServer")) {
            org.apache.shibboleth.ShibbolethModule shibModule = null;
            try {
                shibModule = (org.apache.shibboleth.ShibbolethModule) this.manager
                        .lookup(org.apache.shibboleth.ShibbolethModule.ROLE);
                return shibModule.getWayfServerUrl();
            } catch (ServiceException e) {
                throw new ConfigurationException("Error looking up shibboleth module: ", e);
            } finally {
                if (shibModule != null) {
                    this.manager.release(shibModule);
                }
            }
        } else if (name.equals("shire")) {
            return serverUrl + req.getRequestURI() + "?lenya.usecase=login&lenya.step=login";
        } else if (name.equals("target")) {
            return serverUrl + req.getRequestURI();
        } else if (name.equals("providerId")) {
            return servletUrl + "/shibboleth";
        } else {
            throw new ConfigurationException("Attribute [" + name + "] not supported!");
        }
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
