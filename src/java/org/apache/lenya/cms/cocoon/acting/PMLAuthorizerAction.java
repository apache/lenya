/*
$Id: PMLAuthorizerAction.java,v 1.21 2003/07/23 13:21:30 gregor Exp $
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
package org.apache.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;

import org.apache.lenya.cms.ac.Identity;
import org.apache.lenya.cms.ac.Policy;

import org.apache.xpath.XPathAPI;

import org.w3c.dom.Document;

import java.net.URL;

import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2.1.6
 * @deprecated Replaced by the new access controller.
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
    public boolean authorize(Request request, Map map)
        throws Exception {
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
            getLogger().error(".authorize(): No policy could be retrieved (" + e +
                "). Access denied (return false).");

            return false;
        }

        Policy policy = new Policy(policyDoc, getLogger());

        // Read action (read, write, publish, etc.)
        String action = XPathAPI.selectSingleNode(policyDoc, "/ac/request/action/@name")
                                .getNodeValue(); //"read";

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
        String authenticator_type = (String) session.getAttribute(
                "org.apache.lenya.cms.cocoon.acting.Authenticator.id");

        if (!this.authenticator_type.equals(authenticator_type)) {
            if (authenticator_type == null) {
                getLogger().warn(".authorize(): No authenticator yet");
            } else {
                getLogger().warn(".authorize(): Authenticators do not match: " +
                    authenticator_type + " (Authorizer's authenticator: " +
                    this.authenticator_type + ")");
            }

            getLogger().warn(".authorize(): Permission denied");

            return false;
        }

        Identity identity = (Identity) session.getAttribute("org.apache.lenya.cms.ac.Identity");

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
