/*
 * $Id: RevisionControllerAction.java,v 1.10 2003/03/04 17:46:34 gregor Exp $
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
package org.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;

import org.lenya.cms.ac.Identity;
import org.lenya.cms.rc.RevisionController;

import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2003.1.5
 */
public class RevisionControllerAction extends AbstractComplementaryConfigurableAction
    implements Configurable {
    String rcmlDirectory = null;
    String backupDirectory = null;
    RevisionController rc = null;
    String username = null;
    String filename = null;

    /**
     * DOCUMENT ME!
     *
     * @param conf DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);

        rcmlDirectory = conf.getChild("rcmlDirectory").getAttribute("href");
        backupDirectory = conf.getChild("backupDirectory").getAttribute("href");

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("CONFIGURATION:\nRCML Directory=" + rcmlDirectory);
            getLogger().debug("CONFIGURATION:\nRCBAK Directory=" + backupDirectory);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
        Parameters parameters) throws Exception {
        // Get Absolute Path of sitemap
        org.apache.cocoon.environment.Source inputSource = resolver.resolve("");
        String sitemapParentPath = inputSource.getSystemId();
        sitemapParentPath = sitemapParentPath.substring(5); // Remove "file:" protocoll
        getLogger().debug(".act(): RESOLVED SOURCE: " + sitemapParentPath);

        // /Get Absolute Path of sitemap
        // Initialize Revision Controller
        rc = new RevisionController(sitemapParentPath + rcmlDirectory,
                sitemapParentPath + backupDirectory);
        getLogger().debug("revision controller" + rc);

        // /Initialize Revision Controller
        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error(".act(): No request object");

            return null;
        }

        // Get session
        Session session = request.getSession(false);

        if (session == null) {
            getLogger().error(".act(): No session object");

            return null;
        }

        Identity identity = (Identity) session.getAttribute("org.lenya.cms.ac.Identity");
        getLogger().debug(".act(): Identity: " + identity);

        filename = sitemapParentPath + parameters.getParameter("filename");
        username = null;

        if (identity != null) {
            username = identity.getUsername();
        } else {
            getLogger().error(".act(): No identity yet");
        }

        getLogger().debug(".act(): Username: " + username);

        return null;
    }
}
