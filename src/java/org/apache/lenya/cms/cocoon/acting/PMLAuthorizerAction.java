/*
 * $Id: PMLAuthorizerAction.java,v 1.17 2003/04/20 22:16:03 michi Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;

import org.apache.xpath.XPathAPI;

import org.w3c.dom.Document;

import org.lenya.cms.ac.Identity;
import org.lenya.cms.ac.Policy;

import java.net.URL;

import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2.1.6
 */
public class PMLAuthorizerAction extends AbstractAuthorizerAction implements ThreadSafe {
    private String domain = null;
    private String port = null;
    private String context = null;
    private String policies = null;

    /**
     * DOCUMENT ME!
     *
     * @param conf DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);

        Configuration domainConf = conf.getChild("domain");
        domain = domainConf.getValue("127.0.0.1");

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(".configure(): domain=" + domain);
        }

        Configuration portConf = conf.getChild("port");
        port = portConf.getValue(null);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(".configure(): port=" + port);
        }

        Configuration contextConf = conf.getChild("context");
        context = contextConf.getValue(null);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(".configure(): context=" + context);
        }

        Configuration policiesConf = conf.getChild("policies");
        policies = policiesConf.getValue(null);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(".configure(): policies=" + policies);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param map DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public boolean authorize(Request request, Map map) throws Exception {
        String remoteAddress = request.getRemoteAddr();

        // Permit ?Identity? and Policy requests for localhost
        String sitemap_uri = request.getRequestURI();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(".authorize(): Request URI: " + sitemap_uri);
            getLogger().debug(".authorize(): Policies path: " + policies);
        }

        // Allow accessing policies from localhost
        if (remoteAddress.equals("127.0.0.1") && (sitemap_uri.indexOf(policies) >= 0)) {
            return true;
        }

        // Get policy
        Document policyDoc = null;

        try {
            policyDoc = getPolicyDoc(request);
        } catch (Exception e) {
            getLogger().error(".authorize(): No policy could be retrieved (" + e + "). Access denied (return false).");
            return false;
        }

        Policy policy = new Policy(policyDoc, getLogger());

        // Read action (read, write, publish, etc.)
        String action = XPathAPI.selectSingleNode(policyDoc, "/ac/request/action/@name").getNodeValue(); //"read";

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(".authorize(): action: " + action);
        }

        // Check permissions
        if (policy.authorizeWorld(action)) {
            return true;
        }

        if (policy.authorizeMachine(action, remoteAddress)) {
            return true;
        }

        Session session = request.getSession(true);

        if (session == null) {
            getLogger().error(".authorize(): No session object");

            return false;
        }


        // Needs to be here after authorizeMachine() check, else every component (XPSAssembler) must be wrapped by a proxy!
        String authenticator_type = (String) session.getAttribute("org.lenya.cms.cocoon.acting.Authenticator.id");
        if (!this.authenticator_type.equals(authenticator_type)) {
            if (authenticator_type == null) {
                getLogger().warn(".authorize(): No authenticator yet");
            } else {
                getLogger().warn(".authorize(): Authenticators do not match: " + authenticator_type + " (Authorizer's authenticator: " + this.authenticator_type + ")");
            }
            getLogger().warn(".authorize(): Permission denied");
            return false;
        }


        Identity identity = (Identity) session.getAttribute("org.lenya.cms.ac.Identity");

        if (identity != null) {
            if (policy.authorizeUser(action, identity.getUsername())) {
                return true;
            }

            String[] groupname = identity.getGroupnames();

            for (int i = 0; i < groupname.length; i++) {
                if (policy.authorizeGroup(action, groupname[i])) {
                    return true;
                }
            }
        }

        getLogger().warn(".authorize(): Permission denied");

        return false;
    }

    /**
     *
     */
    private Document getPolicyDoc(Request request) throws Exception {
        String context = request.getContextPath();
        int port = request.getServerPort();
        String sitemap_uri = request.getSitemapURI();
        String pmlURLString = "http://" + domain;

        if (this.port != null) {
            getLogger().debug(".getPolicyDoc(): Port set by Configuration: " + this.port +
                " (request-port: " + port + ")");
            pmlURLString = pmlURLString + ":" + this.port;
        } else {
            getLogger().debug(".getPolicyDoc(): Port set equals to request port: " + port);
            pmlURLString = pmlURLString + ":" + port;
        }

        if (this.context != null) {
            pmlURLString = pmlURLString + this.context;
        } else {
            pmlURLString = pmlURLString + context;
        }

        pmlURLString = pmlURLString + "/" + policies + sitemap_uri + ".acml";

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(".getPolicyDoc(): " + pmlURLString);
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        return db.parse(new URL(pmlURLString).openStream());
    }
}
