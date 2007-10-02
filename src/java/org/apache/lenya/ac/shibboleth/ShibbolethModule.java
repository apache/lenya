package org.apache.lenya.ac.shibboleth;

import java.util.Arrays;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.cocoon.components.modules.input.AbstractPageEnvelopeModule;
import org.apache.lenya.cms.publication.util.OutgoingLinkRewriter;
import org.apache.lenya.util.ServletHelper;

/**
 * <p>
 * Shibboleth input module.
 * </p>
 * <p>
 * Supported attributes:
 * </p>
 * <ul>
 * <li><em>wayfServer</em> - the URL of the WAYF server</li>
 * <li><em>shire</em> - the value of the shire request parameter for the WAYF server</li>
 * <li><em>target</em> - the value of the target request parameter for the WAYF server</li>
 * <li><em>providerId</em> - the value of the providerId request parameter for the WAYF server</li>
 * </ul>
 */
public class ShibbolethModule extends AbstractPageEnvelopeModule implements Serviceable {

    protected static final String ATTR_TARGET = "target";
    protected static final String ATTR_SHIRE = "shire";
    protected static final String ATTR_WAYF_SERVER = "wayfServer";
    protected static final String ATTR_PROVIDER_ID = "providerId";
    private ServiceManager manager;

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        // attributes to get from the org.apache.shibboleth.ShibbolethModule
        String[] shibModuleAttrs = { ATTR_WAYF_SERVER, ATTR_PROVIDER_ID };
        if (Arrays.asList(shibModuleAttrs).contains(name)) {
            org.apache.shibboleth.ShibbolethModule shibModule = null;
            try {
                shibModule = (org.apache.shibboleth.ShibbolethModule) this.manager
                        .lookup(org.apache.shibboleth.ShibbolethModule.ROLE);

                if (name.equals(ATTR_WAYF_SERVER)) {
                    return shibModule.getWayfServerUrl();
                } else if (name.equals(ATTR_PROVIDER_ID)) {
                    return shibModule.getProviderId();
                } else {
                    throw new ConfigurationException("Attribute [" + name + "] not supported!");
                }

            } catch (ServiceException e) {
                throw new ConfigurationException("Error looking up shibboleth module: ", e);
            } finally {
                if (shibModule != null) {
                    this.manager.release(shibModule);
                }
            }
        } else {
            Request req = ObjectModelHelper.getRequest(objectModel);
            String webappUrl = ServletHelper.getWebappURI(req);
            OutgoingLinkRewriter rewriter = new OutgoingLinkRewriter(this.manager, getLogger());
            String outgoingUrl = rewriter.rewrite(webappUrl);

            if (outgoingUrl.startsWith("/")) {
                int port = req.getServerPort();
                String portSuffix = port == 80 ? "" : ":" + port;
                String serverUrl = req.getScheme() + "://" + req.getServerName() + portSuffix;
                outgoingUrl = serverUrl + outgoingUrl;
            }

            if (name.equals(ATTR_SHIRE)) {
                return outgoingUrl + "?lenya.usecase=login&lenya.step=login";
            } else if (name.equals(ATTR_TARGET)) {
                return outgoingUrl;
            } else {
                throw new ConfigurationException("Attribute [" + name + "] not supported!");
            }
        }

    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
