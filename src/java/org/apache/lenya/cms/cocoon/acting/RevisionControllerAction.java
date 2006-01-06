/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.rc.RCEnvironment;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.impl.RepositoryUtil;
import org.apache.lenya.util.ServletHelper;

/**
 * Revision controller action.
 * 
 * @version $Id$
 */
public class RevisionControllerAction extends ServiceableAction {

    private String rcmlDirectory = null;
    private String backupDirectory = null;
    private RevisionController rc = null;
    private String username = null;

    /**
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector,
     *      org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String,
     *      org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
            Parameters parameters) throws Exception {
        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error(".act(): No request object");

            return null;
        }

        Publication publication;
        org.apache.lenya.cms.repo.Session repoSession = RepositoryUtil.getSession(this.manager,
                request,
                getLogger());

        String url = ServletHelper.getWebappURI(request);
        String pubId = new URLInformation(url).getPublicationId();
        publication = repoSession.getPublication(pubId);
        Document document = RepositoryUtil.getDocument(repoSession, url);

        // get Parameters for RC
        String contextPath = SourceUtil.getRealPath(this.manager, "");
        String pubPath = SourceUtil.getRealPath(this.manager, "lenya/pubs/" + pubId);
        RCEnvironment rcEnvironment = RCEnvironment.getInstance(contextPath);
        this.rcmlDirectory = rcEnvironment.getRCMLDirectory();
        this.rcmlDirectory = pubPath + File.separator + this.rcmlDirectory;
        this.backupDirectory = rcEnvironment.getBackupDirectory();
        this.backupDirectory = pubPath + File.separator + this.backupDirectory;

        // Initialize Revision Controller
        this.rc = new RevisionController(this.rcmlDirectory, this.backupDirectory, pubPath);
        getLogger().debug("revision controller" + this.rc);

        // /Initialize Revision Controller
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

        int bx = url.lastIndexOf("-bxe");

        if (bx > 0) {
            String language = document.getLanguage();

            int l = url.length();
            int bxLength = "-bxe".length();
            int lang = url.lastIndexOf("_", bx);
            int langLength = bx - lang;

            if (bx > 0 && bx + bxLength <= l) {
                url = url.substring(0, bx) + url.substring(bx + bxLength, l);

                if (lang > 0 && langLength + lang < l) {
                    language = url.substring(lang + 1, lang + langLength);
                    url = url.substring(0, lang)
                            + url.substring(lang + langLength, l - bxLength);
                }
            }

            Document srcDoc = document.getContentNode().getDocument(language);
            // TODO File newFile = srcDoc.getFile();
            // TODO this.filename = newFile.getCanonicalPath();

        } else {
            // TODO this.filename = document.getFile().getCanonicalPath();
        }

        this.filename = this.filename.substring(pubPath.length());

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
    
    private String filename;

    /**
     * Get the filename.
     * @return the filename
     */
    protected String getFilename() {
        return this.filename;
    }

    /**
     * Get the revision controller.
     * @return the revision controller
     */
    protected RevisionController getRc() {
        return this.rc;
    }

    /**
     * Get the user name.
     * @return the user name
     */
    protected String getUsername() {
        return this.username;
    }

}