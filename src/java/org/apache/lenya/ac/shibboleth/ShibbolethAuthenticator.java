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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.ErrorHandler;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Message;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.ac.UserReference;
import org.apache.lenya.ac.attr.AttributeManager;
import org.apache.lenya.ac.attr.AttributeRule;
import org.apache.lenya.ac.attr.AttributeSet;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.ac.impl.TransientUser;
import org.apache.lenya.ac.impl.UserAuthenticator;
import org.apache.lenya.ac.saml.UserFieldsMapper;
import org.apache.lenya.cms.cocoon.acting.DelegatingAuthorizerAction;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.publication.Proxy;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.util.ServletHelper;
import org.apache.shibboleth.AssertionConsumerService;
import org.apache.shibboleth.AttributeRequestService;
import org.apache.shibboleth.impl.AssertionConsumerServiceImpl;
import org.opensaml.SAMLAttribute;
import org.opensaml.SAMLBrowserProfile.BrowserProfileResponse;

/**
 * <p>
 * Shibboleth-based authenticator.
 * <p>
 * <p>
 * Configuration:
 * </p>
 * <ul>
 * <li> <code>&lt;redirect-to-wayf&gt;true|false&lt;/redirect-to-wayf&gt;</code> - if the
 * application should redirect to the WAYF server instead of the login screen if a resource is
 * protected only via group rules </li>
 * 
 * </pre>
 */
public class ShibbolethAuthenticator extends UserAuthenticator implements Parameterizable,
        Disposable {

    protected static final String PREVIOUSLY_REDIRECTED_USER = ShibbolethAuthenticator.class
            .getName()
            + "previouslyRedirectedUser";

    /**
     * Configuration parameter to determine if the WAYF server should be used for logging in to
     * rule-only protected pages.
     */
    protected static final String PARAM_REDIRECT_TO_WAYF = "redirect-to-wayf";

    /**
     * Configuration parameter to determine the attribute translator for this authenticator.
     */
    protected static final String PARAM_ATTRIBUTE_SET = "attribute-set";

    protected static final String ERROR_MISSING_UID_ATTRIBUTE = "Unable to get unique identifier for subject. "
            + "Make sure you are listed in the metadata.xml "
            + "file and your resources your are trying to access "
            + "are available and your are allowed to see them. (Resourceregistry).";

    private boolean redirectToWayf = false;
    private String attributeSetHint = null;

    private AttributeManager attributeManager;
    private AttributeSet attributeSet;

    /**
     * Authenticates the request. If the request contains the parameters <em>username</em> and
     * <em>password</em>, the authentication is delegated to the super class
     * {@link UserAuthenticator}. Otherwise, the Shibboleth browser profile request is evaluated.
     * @see org.apache.lenya.ac.impl.UserAuthenticator#authenticate(org.apache.lenya.ac.AccreditableManager,
     *      org.apache.cocoon.environment.Request, org.apache.lenya.ac.ErrorHandler)
     */
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
            String host = util.getHostUrl();
            BrowserProfileResponse bpResponse = consumerService.processRequest(req, host);
            Map attributesMap = attrReqService.requestAttributes(bpResponse);
            logAttributesMap(attributesMap);

            // fetch unique identifier from attributes
            String uniqueId = attrReqService.getUniqueID(attributesMap, bpResponse);
            if (uniqueId == null) {
                issueError(handler, ERROR_MISSING_UID_ATTRIBUTE);
            } else {
                UserManager userManager = accreditableManager.getUserManager();
                if (userManager.contains(uniqueId)) {
                    getLogger().error(
                            "Persistent user with ID [" + uniqueId
                                    + "] exists, can't create transient user.");
                    handler.error("Shibboleth authentication error (see logfile for details).");
                } else {
                    TransientUser user = new TransientUser(uniqueId);
                    passAttributes(user, attributesMap);
                    updateIdentity(identity, user, userManager);
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
            getLogger().debug("Shibboleth attribute map: \n\n" + attributesMap.toString() + "\n\n");
        }
    }

    protected void issueError(ErrorHandler handler, String message) {
        getLogger().error(message);
        handler.error(message);
    }

    /**
     * Replaces the identity's user with a new user.
     * @param identity The identity.
     * @param user The new user.
     */
    protected void updateIdentity(Identity identity, TransientUser user, UserManager userMgr) {
        UserReference oldUser = identity.getUserReference();
        if (oldUser != null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Removing user [" + oldUser + "] from identity.");
            }
            identity.removeIdentifiable(oldUser);
        }
        identity.addIdentifiable(new ShibbolethUserReference(user));
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Adding user [" + user + "] to identity.");
        }
    }

    /**
     * Passes the attributes from the <em>samlAttributes</em> parameter to the <em>user</em>
     * object. The {@link AttributeTranslator} service is used to translate the attributes. The name
     * and e-mail attributes are extracted using the {@link UserFieldsMapper}.
     * @param user
     * @param samlAttributes
     * @throws AccessControlException
     */
    protected void passAttributes(TransientUser user, Map samlAttributes)
            throws AccessControlException {

        for (Iterator i = samlAttributes.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            AttributeSet attrs = getAttributeSet();
            String alias = attrs.getAttribute(key).getAlias();
            SAMLAttribute attribute = (SAMLAttribute) samlAttributes.get(key);
            String[] values = getValues(attribute);
            user.setAttributeValues(alias, values);
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

    protected String[] getValues(SAMLAttribute attribute) {
        List valueList = new ArrayList();
        for (Iterator i = attribute.getValues(); i.hasNext();) {
            String value = (String) i.next();
            valueList.add(value);
        }
        return (String[]) valueList.toArray(new String[valueList.size()]);
    }

    /**
     * Extracts the <code>HttpServletRequest</code> object from the current Cocoon context.
     * @return An <code>HttpServletRequest</code> object.
     * @throws AccessControlException
     */
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

    /**
     * <p>
     * This method returns the URI which displays the login screen:
     * </p>
     * <ul>
     * <li>If the configuration option {@link #PARAM_REDIRECT_TO_WAYF} is set to <code>true</code>
     * and the request points to a page which is only protected by rules, we assume that the
     * Shibboleth authentication shall be used and return the URL which redirects to the WAYF
     * server.</li>
     * <li>Otherwise, the Lenya login usecase URL is returned.</li>
     * </ul>
     * @return A string.
     * @see org.apache.lenya.ac.impl.UserAuthenticator#getLoginUri(org.apache.cocoon.environment.Request)
     */
    public String getLoginUri(Request request) {
        String loginUri = null;

        if (this.redirectToWayf && isOnlyRuleProtected(request)) {

            // avoid redirect loop (WAYF->IdP->SP->WAYF) because of failing
            // authorization
            ShibbolethUserReference user = getLoggedInShibboletUser(request);
            if (user != null) {
                String userId = user.getId();
                String previouslyRedirectedUserId = getPreviouslyRedirectedUser(request);
                if (previouslyRedirectedUserId != null && userId.equals(previouslyRedirectedUserId)) {
                    reportAuthorizationError(request);
                    loginUri = super.getLoginUri(request);
                } else {
                    setPreviouslyRedirectedUser(request, userId);
                }
            }
            if (loginUri == null) {
                loginUri = getWayfLoginUrl(request);
            }
        } else {
            loginUri = super.getLoginUri(request);
        }

        return loginUri;
    }

    protected void reportAuthorizationError(Request request) {
        Session session = request.getSession();
        Message[] messages = (Message[]) session.getAttribute(DelegatingAuthorizerAction.ERRORS);
        List messageList = messages == null ? new ArrayList(1) : new ArrayList(Arrays
                .asList(messages));
        messageList.add(new Message("shibboleth-delete-cookies"));
        messages = (Message[]) messageList.toArray(new Message[messageList.size()]);
        request.getSession().setAttribute(DelegatingAuthorizerAction.ERRORS, messages);
    }

    protected void setPreviouslyRedirectedUser(Request request, String userId) {
        request.getSession().setAttribute(PREVIOUSLY_REDIRECTED_USER, userId);
    }

    protected String getPreviouslyRedirectedUser(Request request) {
        return (String) request.getSession().getAttribute(PREVIOUSLY_REDIRECTED_USER);
    }

    /**
     * @param request The current request.
     * @return A Shibboleth user reference or <code>null</code> if no Shiboleth user is logged in.
     */
    protected ShibbolethUserReference getLoggedInShibboletUser(Request request) {
        Session session = request.getSession(false);
        if (session != null) {
            Identity identity = (Identity) session.getAttribute(Identity.class.getName());
            if (identity != null) {
                UserReference user = identity.getUserReference();
                if (user instanceof ShibbolethUserReference) {
                    return (ShibbolethUserReference) user;
                }
            }
        }
        return null;
    }

    /**
     * @param request The current request.
     * @return The URI which issues a redirect to the WAYF server.
     */
    protected String getWayfLoginUrl(Request request) {
        ContextUtility contextUtil = null;
        String proxyUrl;
        try {
            contextUtil = (ContextUtility) this.manager.lookup(ContextUtility.ROLE);
            Context context = ObjectModelHelper.getContext(contextUtil.getObjectModel());
            String servletContextPath = context.getRealPath("");
            String webappUrl = ServletHelper.getWebappURI(request);
            URLInformation info = new URLInformation(webappUrl);
            String pubId = info.getPublicationId();
            Publication pub = PublicationFactory.getPublication(pubId, servletContextPath);

            String area = info.getArea();
            Proxy proxy = pub.getProxy(area, true);
            if (proxy != null) {
                String prefix = "/" + pubId + "/" + area;
                String areaUrl = webappUrl.substring(prefix.length());
                proxyUrl = proxy.getUrl() + areaUrl;
            } else {
                proxyUrl = request.getContextPath() + webappUrl;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (contextUtil != null) {
                this.manager.release(contextUtil);
            }
        }
        return proxyUrl + "?lenya.usecase=shibboleth&lenya.step=wayf";
    }

    /**
     * <p>
     * Checks if a page is protected only with rules:
     * </p>
     * <ul>
     * <li> If the aggregated policy for the current page contains credentials which assign a role
     * to a particular user or IP range, or to a group which contains explicitly assigned members,
     * the method returns <code>false</code>. </li>
     * <li> Otherwise, the method returns <code>true</code>.
     * </ul>
     * @param request The request referring to the page.
     * @return A boolean value.
     */
    protected boolean isOnlyRuleProtected(Request request) {
        DefaultAccessController accessController = null;
        ServiceSelector selector = null;
        AccessControllerResolver resolver = null;

        try {
            selector = (ServiceSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
            resolver = (AccessControllerResolver) selector
                    .select(AccessControllerResolver.DEFAULT_RESOLVER);

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

            for (Iterator i = accreditables.iterator(); i.hasNext();) {
                Accreditable accr = (Accreditable) i.next();
                if (!(accr instanceof Group)) {
                    return false;
                }
                Group group = (Group) accr;
                AttributeRule rule = group.getRule();
                if (rule == null || rule.getRule().trim().length() == 0 || group.getMembers().length > 0) {
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

    /**
     * <p>
     * This method returns the URL of the protected page, as passed from the identity provider as
     * the value of the {@link AssertionConsumerServiceImpl#REQ_PARAM_TARGET} request parameter. If
     * the request parameter is missing, the current URL, i.e. the assertion consumer URL, is
     * returned.
     * </p>
     * @see org.apache.lenya.ac.impl.UserAuthenticator#getTargetUri(org.apache.cocoon.environment.Request)
     */
    public String getTargetUri(Request request) {
        String paramName = AssertionConsumerServiceImpl.REQ_PARAM_TARGET;
        String target = request.getParameter(paramName);
        if (target == null) {
            getLogger().warn(
                    "Request parameter " + paramName + " is missing, using current URI as target.");
            return request.getRequestURI();
        } else {
            return target;
        }
    }

    public void parameterize(Parameters params) throws ParameterException {
        this.redirectToWayf = params.getParameterAsBoolean(PARAM_REDIRECT_TO_WAYF,
                this.redirectToWayf);
        this.attributeSetHint = params.getParameter(PARAM_ATTRIBUTE_SET, this.attributeSetHint);
    }

    public AttributeSet getAttributeSet() {
        if (this.attributeSet == null) {
            try {
                this.attributeManager = (AttributeManager) this.manager.lookup(AttributeManager.ROLE);
                this.attributeSet = this.attributeManager.getAttributeSet(this.attributeSetHint);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this.attributeSet;
    }

    public void dispose() {
        if (this.attributeManager != null) {
            this.manager.release(this.attributeManager);
        }
    }

}
