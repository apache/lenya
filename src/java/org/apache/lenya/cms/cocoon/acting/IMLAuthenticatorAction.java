/*
$Id
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
import org.apache.lenya.cms.ac.Password;

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
public class IMLAuthenticatorAction extends AbstractUsernamePasswordAuthenticatorAction
    implements ThreadSafe {
    private String domain = null;
    private String port = null;
    private String context = null;
    private String passwd = null;
    private String type = null;

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
            getLogger().debug("CONFIGURATION: domain=" + domain);
        }

        Configuration portConf = conf.getChild("port");
        port = portConf.getValue(null);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("CONFIGURATION: port=" + port);
        }

        Configuration contextConf = conf.getChild("context");
        context = contextConf.getValue(null);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("CONFIGURATION: context=" + context);
        }

        Configuration passwdConf = conf.getChild("passwd");
        passwd = passwdConf.getValue(null);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("CONFIGURATION: passwd=" + passwd);
        }

        Configuration typeConf = conf.getChild("type");
        type = typeConf.getValue(null);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("CONFIGURATION: type=" + type);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param username DOCUMENT ME!
     * @param password DOCUMENT ME!
     * @param request DOCUMENT ME!
     * @param map DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public boolean authenticate(String username, String password, Request request, Map map)
        throws Exception {
        if ((username != null) && (password != null)) {
            String passwordString = null;
            Document idoc = null;

            try {
                String context = request.getContextPath();
                int port = request.getServerPort();
                idoc = getIdentityDoc(username, port, context);
                passwordString = Identity.getPassword(idoc);
            } catch (Exception e) {
                getLogger().error(".authenticate(): " + e);

                return false;
            }

            if (Password.encrypt(password).equals(passwordString)) {
                Session session = request.getSession(true);

                if (session == null) {
                    return false;
                }

                Identity identity = new Identity(idoc);

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("IDENTITY: " + identity);
                }

                session.setAttribute("org.apache.lenya.cms.ac.Identity", identity);

                return true;
            }
        }

        return false;
    }

    /**
     *
     */
    protected Document getIdentityDoc(String username, int port, String context)
        throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        String imlURLString = "http://" + domain;

        if (this.port != null) {
            imlURLString = imlURLString + ":" + this.port;
        } else {
            imlURLString = imlURLString + ":" + port;
        }

        if (this.context != null) {
            imlURLString = imlURLString + this.context;
        } else {
            imlURLString = imlURLString + context;
        }

        imlURLString = imlURLString + "/" + passwd + username + ".iml";
        getLogger().debug(".getIdentity(): " + imlURLString);

        return db.parse(new URL(imlURLString).openStream());
    }
}
