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
package org.apache.shibboleth.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.shibboleth.AssertionConsumerService;
import org.apache.shibboleth.ShibbolethManager;
import org.apache.shibboleth.ShibbolethModule;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAuthenticationStatement;
import org.opensaml.SAMLBrowserProfile;
import org.opensaml.SAMLBrowserProfileFactory;
import org.opensaml.SAMLException;
import org.opensaml.SAMLStatement;
import org.opensaml.SAMLBrowserProfile.BrowserProfileRequest;
import org.opensaml.SAMLBrowserProfile.BrowserProfileResponse;

import edu.internet2.middleware.shibboleth.wayf.IdPSite;

/**
 * Assertion consumer service.
 */
public class AssertionConsumerServiceImpl extends AbstractLogEnabled implements
        AssertionConsumerService, Serviceable {

    /**
     * Authentication Assertion requests identifier.
     */
    public static final String PATH_SAML_AUTH_ASSERTION = "samlaa";

    private static final String SHIB_ATTR_TARGET = "target";
    private static final String SHIB_ATTR_SHIRE = "shire";
    private static final String SHIB_ATTR_PROVIDERID = "providerId";
    private static final String SHIB_ATTR_TIME = "time";

    private ShibbolethModule shibbolethModule;

    private ServiceManager manager;

    private ShibbolethManager shibManager;

    protected ShibbolethModule getShibbolethModule() {
        if (this.shibbolethModule == null) {
            try {
                this.shibbolethModule = (ShibbolethModule) this.manager
                        .lookup(ShibbolethModule.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.shibbolethModule;
    }

    /**
     * @param req The request.
     * @return A browser profile response.
     * @throws SAMLException
     */
    public BrowserProfileResponse processRequest(HttpServletRequest req) throws SAMLException {
        SAMLBrowserProfile profile = SAMLBrowserProfileFactory.getInstance();

        StringBuffer issuer = new StringBuffer();
        BrowserProfileRequest bpr = getBrowserProfileRequest(req);
        String handlerURL = getCompleteUrl(req);

        ShibbolethModule module = getShibbolethModule();

        BrowserProfileResponse profileResponse = profile.receive(issuer, bpr, handlerURL, module
                .getReplayCache(), module.getArtifactMapper(), 1);
        checkIssueInstant(profileResponse);
        checkRecipient(profileResponse);
        checkIssuer(profileResponse, req.getRemoteAddr());
        return profileResponse;
    }

    /**
     * @param req The request.
     * @return The complete URL, incl. server name and query string.
     */
    protected String getCompleteUrl(HttpServletRequest req) {
        String queryString = req.getQueryString();
        String url = req.getRequestURL().toString()
                + (queryString.length() == 0 ? "" : "?" + queryString);
        return url;
    }

    private BrowserProfileRequest getBrowserProfileRequest(HttpServletRequest req) {
        BrowserProfileRequest bpr = new BrowserProfileRequest();
        bpr.SAMLResponse = req.getParameter("SAMLResponse");
        bpr.TARGET = req.getParameter("TARGET");
        String[] samlArt = null;
        if (bpr.SAMLResponse == null)
            samlArt = req.getParameterValues("SAMLart");
        bpr.SAMLArt = samlArt;
        return bpr;
    }

    /**
     * Check that auth statement is not older than 5 minutes.
     * @param profileResponse
     */
    private void checkIssueInstant(BrowserProfileResponse profileResponse) {
        if (profileResponse.response.getIssueInstant().getTime() - System.currentTimeMillis() > 5 * 60 * 1000) {
            throw new RuntimeException(
                    "Rejecting SAML authentication statement older than 5 minutes.", null);
        }
    }

    /**
     * Check wether this statement is directed at ourselves.
     * @param profileResponse
     */
    private void checkRecipient(BrowserProfileResponse profileResponse) {
        String recipient = profileResponse.response.getRecipient();
        if (recipient != null && !recipient.equals(getShireUrl())) {
            throw new RuntimeException("Rejecting SAML authentication with unknown Recipient: "
                    + profileResponse.response.getRecipient(), null);
        }
    }

    private void checkIssuer(BrowserProfileResponse profileResponse, String remoteIP) {
        if (!getShibbolethModule().checkIssuerIP())
            return;
        SAMLAuthenticationStatement authStatement = getSAMLAuthenticationStatement(profileResponse);
        // check remote address
        if (!authStatement.getSubjectIP().equals(remoteIP))
            throw new RuntimeException("Rejecting SAML authentication claiming IP: "
                    + authStatement.getSubjectIP() + ", coming from: " + remoteIP, null);

        // check issuer
        String issuer = profileResponse.assertion.getIssuer();
        String handleService = getShibbolethManager().lookupIdentityProvider(issuer);
        if (handleService == null)
            throw new RuntimeException("Rejecting SAML authentication from unknown issuer: "
                    + issuer, null);
    }

    protected ShibbolethManager getShibbolethManager() {
        if (this.shibManager == null) {
            try {
                this.shibManager = (ShibbolethManager) this.manager.lookup(ShibbolethManager.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.shibManager;
    }

    /**
     * @param profileResponse The profile response.
     * @return SAML auth statement
     */
    private SAMLAuthenticationStatement getSAMLAuthenticationStatement(
            BrowserProfileResponse profileResponse) {
        if (profileResponse.response == null)
            return null;
        Iterator assertionIterator = profileResponse.response.getAssertions();
        while (assertionIterator.hasNext()) {
            SAMLAssertion assertion = (SAMLAssertion) assertionIterator.next();
            Iterator statementsIterator = assertion.getStatements();
            while (statementsIterator.hasNext()) {
                SAMLStatement statement = (SAMLStatement) statementsIterator.next();
                if (statement instanceof SAMLAuthenticationStatement)
                    return (SAMLAuthenticationStatement) statement;
            }
        }
        return null;
    }

    /**
     * Uses an HTTP Status 307 redirect to forward the user the HS.
     * @param locale The locale.
     * @param idpSite The IdP site.
     * @return A string.
     */
    public String buildRequest(Locale locale, IdPSite idpSite) {
        try {
            StringBuffer buffer = new StringBuffer();
            // get handle service
            String handleService = getShibbolethManager().lookupIdentityProvider(idpSite.getName());
            if (handleService == null)
                throw new RuntimeException("Error forwarding to HS: " + idpSite.getName(), null);
            buffer.append(handleService);
            buffer.append("?");

            // send language if configured
            // this parameter is passed to the login.jsp on shibboleth idP side,
            // thus must be on the same
            // parameter level as the SHIB_ATTR_TARGET parameter.
            if (getShibbolethModule().useLanguageInReq()) {
                String paramNam = getShibbolethModule().getLanguageParamName();
                if (paramNam != null) {
                    buffer.append(paramNam).append("=").append(locale.toString()).append("&");
                }
            }

            buffer.append(SHIB_ATTR_TARGET).append("=olat");

            // shire
            buffer.append("&" + SHIB_ATTR_SHIRE + "=");
            buffer.append(URLEncoder.encode(getShireUrl(), "UTF-8"));

            // providerId (if any)
            String providerId = getShibbolethModule().getProviderId();
            if (providerId != null) {
                buffer.append("&" + SHIB_ATTR_PROVIDERID + "=");
                buffer.append(URLEncoder.encode(providerId, "UTF-8"));
            }

            // time
            buffer.append("&" + SHIB_ATTR_TIME + "=");
            buffer.append(new Long(new Date().getTime() / 1000).toString()); // Unix
            // Time

            // send redirect
            return buffer.toString();

        } catch (IOException ioe) {
            throw new RuntimeException("Error forwarding to HS: " + ioe.toString());
        }
    }

    /**
     * Uses an HTTP Status 307 redirect to forward the user the HS.
     * @param ureq
     * @param getRequestString parameter string for request to IdP
     * @throws OLATRuntimeException
     */
    /*
     * public void forwardToHS(UserRequest ureq, String getRequestString) throws ShibbolethException {
     * if (getLogger().isDebugEnabled()) { getLogger().debug("Forwarding to HS: " +
     * getRequestString); } ureq.getDispatchResult().setResultingMediaResource( new
     * RedirectMediaResource(getRequestString)); }
     */

    /**
     * Return the URL to the SHIRE (that's us)
     * @param shibbolethPath The shibboleth path.
     * @return The complete SHIRE URL
     */
    private String getShireUrl() {
        ContextUtility ctxUtil = null;
        try {
            ctxUtil = (ContextUtility) this.manager.lookup(ContextUtility.ROLE);
            Map objectModel = ctxUtil.getObjectModel();
            HttpServletRequest req = (HttpServletRequest) objectModel
                    .get(HttpEnvironment.HTTP_REQUEST_OBJECT);
            return getCompleteUrl(req);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } finally {
            if (ctxUtil != null) {
                this.manager.release(ctxUtil);
            }
        }
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
}
