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
package org.apache.lenya.cms.site.usecases;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;

import javax.servlet.http.HttpSession;

import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.modules.monitoring.SessionListener;

/**
 * Force a document to be checked in (override a checkout).
 */
public class ForceCheckIn extends DocumentUsecase {

    private static final String IDENTITY = Identity.class.getName();
    private static final String HISTORY = "org.apache.lenya.cms.cocoon.acting.History";

    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();

        Document doc = getSourceDocument();
        if (doc == null) {
            return;
        }
        
        Node node = doc.getRepositoryNode();
        if (!node.isCheckedOut()) {
            String[] params = { DublinCoreHelper.getTitle(doc, true) };
            addErrorMessage("not-checked-out", params);
        }
    }

    protected void prepareView() throws Exception {
        Node node = getSourceDocument().getRepositoryNode();
        if (node.isCheckedOut()) {
            String userId = node.getCheckoutUserId();
            User currentUser = getSession().getIdentity().getUser();
            if (currentUser == null || !userId.equals(currentUser.getId())) {
                String[] params = { userId };
                if (isLoggedIn(userId)) {
                    addInfoMessage("user-logged-in", params);
                }
                else {
                    addInfoMessage("user-not-logged-in", params);
                }
            }
        }
    }

    protected boolean isLoggedIn(String userId) {
        String pubId = getSourceDocument().getPublication().getId();
        SessionListener sessions = new SessionListener();
        WeakHashMap allSessions = sessions.getAllSessions();
        
        boolean loggedIn = false;
        Iterator userit = allSessions.entrySet().iterator();
        while (userit.hasNext()) {
            Map.Entry entry = (Map.Entry) userit.next();
            HttpSession nextsession = (HttpSession) entry.getValue();

            Identity identity = (Identity) nextsession.getAttribute(IDENTITY);

            if(identity == null) {
                continue;
            }

            User user = identity.getUser();
            Vector history = (Vector) nextsession.getAttribute(HISTORY);
            String publicationID = getPublicationIDfromHistory(history);
            if (publicationID.equals(pubId) && user != null && user.getId().equals(userId)) {
                loggedIn = true;
            }
        }
        return loggedIn;
    }

    protected void doExecute() throws Exception {
        super.doExecute();

        Document doc = getSourceDocument();
        Node node = doc.getRepositoryNode();

        node.forceCheckIn();
    }

    /**
     * Extract the publicationID from the history information stored in the
     * session
     * 
     * @param history The history stored in the session.. An entry looks like \/
     *        <publication-id>/authoring/index.html
     * @return A publication ID.
     */
    private String getPublicationIDfromHistory(Vector history) {
        String firstElement = history.firstElement().toString();
        String publicationID = firstElement.substring(1, firstElement.indexOf("/", 1));
        return publicationID;
    }

}
