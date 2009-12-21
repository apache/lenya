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

package org.apache.lenya.cms.cocoon.acting;

import java.io.File;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.rc.RCEnvironment;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryUtil;

/**
 * Revision controller action.
 * 
 * @version $Id: RevisionControllerAction.java 487290 2006-12-14 18:18:35Z
 *          andreas $
 */
public class RevisionControllerAction extends ServiceableAction {

    private String rcmlDirectory = null;
    private String backupDirectory = null;
    private String username = null;
    private Node node = null;

    /**
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector,
     *      org.apache.cocoon.environment.SourceResolver, java.util.Map,
     *      java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
            Parameters parameters) throws Exception {
        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error(".act(): No request object");

            return null;
        }

        PageEnvelope envelope = null;
        Publication publication;

        try {
            publication = PublicationUtil.getPublication(this.manager, request);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
        org.apache.lenya.cms.repository.Session repoSession = RepositoryUtil.getSession(
                this.manager, request);

        DocumentFactory factory = DocumentUtil.createDocumentFactory(this.manager, repoSession);
        Document document = null;

        try {
            publication = PublicationUtil.getPublication(this.manager, objectModel);
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(factory, objectModel,
                    publication);
            document = envelope.getDocument();
        } catch (Exception e) {
            getLogger().error("Resolving page envelope failed: ", e);
            throw e;
        }

        // get Parameters for RC
        String publicationPath = publication.getDirectory().getCanonicalPath();
        RCEnvironment rcEnvironment = RCEnvironment.getInstance(publication.getServletContext()
                .getCanonicalPath(), getLogger());
        this.rcmlDirectory = rcEnvironment.getRCMLDirectory();
        this.rcmlDirectory = publicationPath + File.separator + this.rcmlDirectory;
        this.backupDirectory = rcEnvironment.getBackupDirectory();
        this.backupDirectory = publicationPath + File.separator + this.backupDirectory;

        // Get session
        Session session = request.getSession(false);

        if (session == null) {
            getLogger().error(".act(): No session object");

            return null;
        }

        Identity identity = (Identity) session.getAttribute(Identity.class.getName());
        getLogger().debug(".act(): Identity: " + identity);

        // FIXME: hack because of the uri for the editor bitflux. The filename
        // cannot be get from
        // the page-envelope

        String documentid = document.getPath();
        int bx = documentid.lastIndexOf("-bxe");

        if (bx > 0) {
            String language = document.getLanguage();

            int l = documentid.length();
            int bxLength = "-bxe".length();
            int lang = documentid.lastIndexOf("_", bx);
            int langLength = bx - lang;

            if (bx > 0 && bx + bxLength <= l) {
                documentid = documentid.substring(0, bx) + documentid.substring(bx + bxLength, l);

                if (lang > 0 && langLength + lang < l) {
                    language = documentid.substring(lang + 1, lang + langLength);
                    documentid = documentid.substring(0, lang)
                            + documentid.substring(lang + langLength, l - bxLength);
                }
            }

            Document srcDoc = factory.get(publication, document.getArea(), documentid, language);
            this.node = srcDoc.getRepositoryNode();

        } else {
            this.node = document.getRepositoryNode();
        }

        this.username = null;

        if (identity != null) {
            User user = identity.getUser();
            if (user != null) {
                this.username = user.getId();
            }
        } else {
            getLogger().error(".act(): No identity yet");
        }

        getLogger().debug(".act(): Username: " + this.username);

        return null;
    }

    /**
     * Get the node.
     * @return the node
     */
    protected Node getNode() {
        return this.node;
    }

    /**
     * Get the user name.
     * @return the user name
     */
    protected String getUsername() {
        return this.username;
    }

}