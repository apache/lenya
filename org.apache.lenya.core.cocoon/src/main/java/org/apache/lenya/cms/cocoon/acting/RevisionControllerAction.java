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

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.utils.ServletHelper;

/**
 * Revision controller action.
 * 
 * @version $Id$
 */
public class RevisionControllerAction extends ServiceableAction {

    private String username = null;
    private Repository repository;
    private Document document;

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

        Session repoSession = this.repository.getSession(request);

        PageEnvelope envelope = null;
        String id = new URLInformation(ServletHelper.getWebappURI(request)).getPublicationId();
        Publication publication = repoSession.getPublication(id);

        Document doc = null;

        try {
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel, publication);
            doc = envelope.getDocument();
        } catch (Exception e) {
            getLogger().error("Resolving page envelope failed: ", e);
            throw e;
        }

        // Get session
        HttpSession session = request.getSession(false);

        if (session == null) {
            getLogger().error(".act(): No session object");

            return null;
        }

        Identity identity = (Identity) session.getAttribute(Identity.class.getName());
        getLogger().debug(".act(): Identity: " + identity);

        // FIXME: hack because of the uri for the editor bitflux. The filename
        // cannot be get from
        // the page-envelope

        String path = doc.getPath();
        int bx = path.lastIndexOf("-bxe");

        if (bx > 0) {
            String language = doc.getLanguage();

            int l = path.length();
            int bxLength = "-bxe".length();
            int lang = path.lastIndexOf("_", bx);
            int langLength = bx - lang;

            if (bx > 0 && bx + bxLength <= l) {
                path = path.substring(0, bx) + path.substring(bx + bxLength, l);

                if (lang > 0 && langLength + lang < l) {
                    language = path.substring(lang + 1, lang + langLength);
                    path = path.substring(0, lang)
                            + path.substring(lang + langLength, l - bxLength);
                }
            }

            this.document = doc.area().getSite().getNode(path).getLink(language).getDocument();

        } else {
            this.document = doc;
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
    protected Document getDocument() {
        return this.document;
    }

    /**
     * Get the user name.
     * @return the user name
     */
    protected String getUsername() {
        return this.username;
    }

}