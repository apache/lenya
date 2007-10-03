package org.apache.lenya.ac.shibboleth;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.cocoon.selection.Selector;
import org.apache.shibboleth.AssertionConsumerService;
import org.apache.shibboleth.AttributeRequestService;
import org.apache.shibboleth.ShibbolethException;
import org.opensaml.SAMLBrowserProfile.BrowserProfileResponse;

public class ShireSelector extends AbstractLogEnabled implements Selector, Serviceable {

    private ServiceManager manager;

    public boolean select(String expression, Map objectModel, Parameters parameters) {

        HttpServletRequest req = (HttpServletRequest) objectModel
                .get(HttpEnvironment.HTTP_REQUEST_OBJECT);

        AssertionConsumerService consumerService = null;
        AttributeRequestService attrReqService = null;
        try {
            consumerService = (AssertionConsumerService) this.manager
                    .lookup(AssertionConsumerService.ROLE);
            attrReqService = (AttributeRequestService) this.manager
                    .lookup(AttributeRequestService.ROLE);

            BrowserProfileResponse bpResponse = null;
            Map attributesMap = null;
            try {
                bpResponse = consumerService.processRequest(req, "");
                attributesMap = attrReqService.requestAttributes(bpResponse);
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(
                            "Shib attribute Map: \n\n" + attributesMap.toString() + "\n\n");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // fetch unique identifier from attributes
            String uniqueID = attrReqService.getUniqueID(attributesMap, bpResponse);
            if (uniqueID == null) {
                throw new ShibbolethException("Unable to get unique identifier for subject. "
                        + "Make sure you are listed in the metadata.xml "
                        + "file and your resources your are trying to access "
                        + "are available and your are allowed to see them. (Resourceregistry). ");
            }

            /*
            UserRequest ureq = new UserRequest(uriPrefix, req, resp);
            Authentication auth = ManagerFactory.getManager().findAuthenticationByAuthusername(
                    uniqueID, PROVIDER_SHIB);
            if (auth == null) { // no matching authentication...
                ShibbolethRegistrationController.putShibAttributes(req, attributesMap);
                ShibbolethRegistrationController.putShibUniqueID(req, uniqueID);
                redirectToShibbolethRegistration(resp);
                return;
            }
            if (!DMZDispatcher
                    .doLogin(auth.getIdentity(), ShibbolethDispatcher.PROVIDER_SHIB, ureq)) {
                DispatcherAction.redirectToDefaultDispatcher(resp); // error,
                // redirect
                // to login
                // screen
                return;
            }

            // successfull login
            ureq.getUserSession().getIdentityEnvironment().setAttributes(
                    ShibbolethModule.getAttributeTranslator().translateSAMLAttributesMap(
                            attributesMap));
            MediaResource mr = ureq.getDispatchResult().getResultingMediaResource();
            if (!(mr instanceof RedirectMediaResource)) {
                DispatcherAction.redirectToDefaultDispatcher(resp); // error,
                // redirect
                // to login
                // screen
                return;
            }

            RedirectMediaResource rmr = (RedirectMediaResource) mr;
            rmr.prepare(resp);
            */

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (consumerService != null) {
                this.manager.release(consumerService);
            }
            if (attrReqService != null) {
                this.manager.release(attrReqService);
            }
        }

        return false;
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
