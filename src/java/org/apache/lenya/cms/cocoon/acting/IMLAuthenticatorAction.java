/*
 * $Id: IMLAuthenticatorAction.java,v 1.6 2003/02/17 11:38:29 egli Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;

import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.wyona.cms.ac.Identity;
import org.wyona.cms.ac.Password;

import java.io.File;

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

                Identity identity = new Identity(username, idoc);

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("IDENTITY: " + identity);
                }

                session.setAttribute("org.wyona.cms.ac.Identity", identity);
                session.setAttribute("org.wyona.cms.cocoon.acting.IMLAuthenticator.type", type);

                return true;
            }
        }

        return false;
    }

    private Document getIdentityDoc(String username, int port, String context)
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
