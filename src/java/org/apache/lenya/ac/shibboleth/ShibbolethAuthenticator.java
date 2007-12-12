/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.ac.shibboleth;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.ErrorHandler;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.ac.impl.TransientUser;
import org.apache.lenya.ac.impl.UserAuthenticator;
import org.apache.lenya.ac.saml.AttributeTranslator;
import org.apache.lenya.ac.saml.UserFieldsMapper;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.util.ServletHelper;
import org.apache.shibboleth.AssertionConsumerService;
import org.apache.shibboleth.AttributeRequestService;
import org.apache.shibboleth.impl.AssertionConsumerServiceImpl;
import org.opensaml.SAMLBrowserProfile.BrowserProfileResponse;

/**
 * <p>Shibboleth-based authenticator.<p>
 * <p>Configuration:</p>
 * <ul>
 * <li>
 *   <code>&lt;redirect-to-wayf&gt;true|false&lt;/redirect-to-wayf&gt;</code>
 *   - if the application should redirect to the WAYF server instead of the login screen
 *   if a resource is protected only via group rules
 * </li>
 * </pre>
 */
public class ShibbolethAuthenticator extends UserAuthenticator implements Configurable {

    protected static final String ERROR_MISSING_UID_ATTRIBUTE = "Unable to get unique identifier for subject. "
                            + "Make sure you are listed in the metadata.xml "
                            + "file and your resources your are trying to access "
                            + "are available and your are allowed to see them. (Resourceregistry).";
    
    private boolean redirectToWayf = false;

    public boolean authenticate(AccreditableManager accreditableManager, Request request,
            ErrorHandler handler) throws AccessControlException {

        String reqUsername = request.getParameter("username");
        String reqPassword = request.getParameter("password");

        if (reqUsername != null && reqPassword != null) {
            return super.authenticate(accreditableManager, request, handler);
        } else {
            Identity identity = (Identity) request.getSession(false).getAttribute(
                    Identity.class.getName());
            return authenticateShibbolethResponse(accreditableManager, identity, handler, request);
        }
    }

    protected boolean authenticateShibbolethResponse(AccreditableManager accreditableManager,
            Identity identity, ErrorHandler handler, Request request) throws AccessControlException {
        boolean authenticated = false;

        HttpServletRequest req = getHttpServletRequest();

        AssertionConsumerService consumerService = null;
        AttributeRequestService attrReqService = null;
        try {
            consumerService = (AssertionConsumerService) this.manager
                    .lookup(AssertionConsumerService.ROLE);
            attrReqService = (AttributeRequestService) this.manager
                    .lookup(AttributeRequestService.ROLE);
            
            ShibbolethUtil util = new ShibbolethUtil(this.manager);
            String host = util.getBaseUrl();
            BrowserProfileResponse bpResponse = consumerService.processRequest(req, host);
            Map attributesMap = attrReqService.requestAttributes(bpResponse);
            logAttributesMap(attributesMap);

            // fetch unique identifier from attributes
            String uniqueID = attrReqService.getUniqueID(attributesMap, bpResponse);
            if (uniqueID == null) {
                issueError(handler, ERROR_MISSING_UID_ATTRIBUTE);
            } else {
                User user = accreditableManager.getUserManager().getUser(uniqueID);
                if (user.isPersistent()) {
                    getLogger().error(
                            "Persistent user with ID [" + user.getId()
                                    + "] exists, can't create transient user.");
                    handler.error("Shibboleth authentication error (see logfile for details).");
                } else {
                    passAttributes((TransientUser) user, attributesMap);
                    updateIdentity(identity, user);
                    authenticated = true;
                }
            }

        } catch (Exception e) {
            authenticated = false;
            handler.error("Shibboleth authentication error: " + e.getMessage()
                    + " (see logfile for details)");
            getLogger().error("Exception during Shibboleth authentication: ", e);
        } finally {
            if (consumerService != null) {
                this.manager.release(consumerService);
            }
            if (attrReqService != null) {
                this.manager.release(attrReqService);
            }
        }
        return authenticated;
    }

    protected void logAttributesMap(Map attributesMap) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Shib attribute Map: \n\n" + attributesMap.toString() + "\n\n");
        }
    }

    protected void issueError(ErrorHandler handler, String message) {
        getLogger().error(message);
        handler.error(message);
    }

    protected void updateIdentity(Identity identity, User user) {
        if (!identity.contains(user)) {
            User oldUser = identity.getUser();
            if (oldUser != null) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Removing user [" + oldUser + "] from identity.");
                }
                identity.removeIdentifiable(oldUser);
            }
            identity.addIdentifiable(user);
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Adding user [" + user + "] to identity.");
            }
        }
    }

    protected void passAttributes(TransientUser user, Map samlAttributes)
            throws AccessControlException {

        Map translatedAttributes;

        AttributeTranslator translator = null;
        try {
            translator = (AttributeTranslator) this.manager.lookup(AttributeTranslator.ROLE);
            translatedAttributes = translator.translateAttributes(samlAttributes, false);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } finally {
            if (translator != null) {
                this.manager.release(translator);
            }
        }

        for (Iterator keys = translatedAttributes.keySet().iterator(); keys.hasNext();) {
            String key = (String) keys.next();
            String[] values = (String[]) translatedAttributes.get(key);
            user.setAttributeValues(key, values);
        }

        UserFieldsMapper mapper = new UserFieldsMapper(this.manager, samlAttributes);
        String firstName = mapper.getFirstName();
        String lastName = mapper.getLastName();
        if (firstName != null && lastName != null) {
            user.setName(firstName + " " + lastName);
        }
        String eMail = mapper.getEMail();
        if (eMail != null) {
            user.setEmail(eMail);
        }
    }

    protected HttpServletRequest getHttpServletRequest() throws AccessControlException {
        HttpServletRequest req;
        ContextUtility contextUtil = null;
        try {
            contextUtil = (ContextUtility) this.manager.lookup(ContextUtility.ROLE);
            Map objectModel = contextUtil.getObjectModel();
            req = (HttpServletRequest) objectModel.get(HttpEnvironment.HTTP_REQUEST_OBJECT);
        } catch (ServiceException e) {
            throw new AccessControlException(e);
        } finally {
            if (contextUtil != null) {
                this.manager.release(contextUtil);
            }
        }
        return req;
    }

    public String getLoginUri(Request request) {
        if (this.redirectToWayf && isOnlyShibbolethProtected(request)) {
            return request.getRequestURI() + "?lenya.usecase=shibboleth&lenya.step=wayf";
        }
        else {
            return super.getLoginUri(request);
        }
    }

    protected boolean isOnlyShibbolethProtected(Request request) {
        DefaultAccessController accessController = null;
        ServiceSelector selector = null;
        AccessControllerResolver resolver = null;

        try {
            selector = (ServiceSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
            resolver =
                (AccessControllerResolver) selector.select(
                    AccessControllerResolver.DEFAULT_RESOLVER);

            String url = ServletHelper.getWebappURI(request);
            accessController = (DefaultAccessController) resolver.resolveAccessController(url);
            
            AccreditableManager accMgr = accessController.getAccreditableManager();
            Policy policy = accessController.getPolicyManager().getPolicy(accMgr, url);
            
            Role[] roles = accMgr.getRoleManager().getRoles();
            Set accreditables = new HashSet();
            for (int i = 0; i < roles.length; i++) {
                Accreditable[] accrs = policy.getAccreditables(roles[i]);
                accreditables.addAll(Arrays.asList(accrs));
            }
            
            if (accreditables.isEmpty()) {
                return false;
            }
            
            for (Iterator i = accreditables.iterator(); i.hasNext(); ) {
                Accreditable accr = (Accreditable) i.next();
                if (!(accr instanceof Group)) {
                    return false;
                }
                Group group = (Group) accr;
                String rule = group.getRule();
                if (rule == null || rule.trim().length() == 0 || group.getMembers().length > 0) {
                    return false;
                }
            }
            
            return true;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (resolver != null) {
                    if (accessController != null) {
                        resolver.release(accessController);
                    }
                    selector.release(resolver);
                }
                manager.release(selector);
            }
        }
    }

    public void configure(Configuration config) throws ConfigurationException {
        Configuration redirectConfig = config.getChild("redirect-to-wayf", false);
        if (redirectConfig != null) {
            this.redirectToWayf = redirectConfig.getValueAsBoolean();
        }
    }

    public String getTargetUri(Request request) {
        String paramName = AssertionConsumerServiceImpl.REQ_PARAM_TARGET;
        String target = request.getParameter(paramName);
        if (target == null) {
            getLogger().warn("Request parameter " + paramName + " is missing, using current URI as target.");
            return request.getRequestURI();
        } else {
            return target;
        }
    }

}
