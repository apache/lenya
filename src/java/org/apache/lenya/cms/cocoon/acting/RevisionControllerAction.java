/*
 * $Id: RevisionControllerAction.java,v 1.17 2003/06/30 08:53:06 andreas Exp $
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
package org.apache.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;

import org.apache.lenya.cms.ac.Identity;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.cms.rc.RCEnvironment;

import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

import java.io.File;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2003.1.5
 */
public class RevisionControllerAction extends AbstractAction {
    String rcmlDirectory = null;
    String backupDirectory = null;
    RevisionController rc = null;
    String username = null;
    String filename = null;

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
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String src,
        Parameters parameters)
        throws Exception {

        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error(".act(): No request object");

            return null;
        }

        PageEnvelope envelope = null;
        Publication publication = null;

        try {
            publication = PublicationFactory.getPublication(objectModel);
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (Exception e) {
            getLogger().error("Resolving page envelope failed: ", e);
        }

        //get Parameters for RC
        String publicationPath = publication.getDirectory().getCanonicalPath();
        String servletContextPath = publication.getServletContext().getCanonicalPath();
        RCEnvironment rcEnvironment = new RCEnvironment(servletContextPath);
        rcmlDirectory = rcEnvironment.getRCMLDirectory();
        rcmlDirectory = publicationPath + rcmlDirectory;
        backupDirectory = rcEnvironment.getBackupDirectory();
        backupDirectory = publicationPath + backupDirectory;

        // Initialize Revision Controller
        rc = new RevisionController(rcmlDirectory, backupDirectory, publicationPath);
        getLogger().debug("revision controller" + rc);
        // /Initialize Revision Controller

        // Get session
        Session session = request.getSession(false);

        if (session == null) {
            getLogger().error(".act(): No session object");

            return null;
        }

        Identity identity = (Identity) session.getAttribute("org.apache.lenya.cms.ac.Identity");
        getLogger().debug(".act(): Identity: " + identity);

        String docId = request.getParameter("documentid");
        String authoringPath =
            new File(
                publication.getDirectory().getCanonicalPath(),
                "content" + File.separator + Publication.AUTHORING_AREA)
                .getCanonicalPath();
        filename = authoringPath + "/" + docId;
        getLogger().debug(".act(): publicationAuthPath + docId : " + authoringPath + " : " + docId);
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
