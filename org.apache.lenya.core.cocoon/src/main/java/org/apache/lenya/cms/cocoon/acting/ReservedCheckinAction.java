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

/* $Id$  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.utils.ServletHelper;

/**
 * Checkin document
 */
public class ReservedCheckinAction extends RevisionControllerAction {
    
    protected Repository repository;
    
    /**
     * Checkin document
     * @return HashMap with checkin parameters
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector,
     *      org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String,
     *      org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
            Parameters parameters) throws Exception {
        super.act(redirector, resolver, objectModel, src, parameters);

        try {
            Request request = ObjectModelHelper.getRequest(objectModel);
            Identity identity = (Identity) request.getSession().getAttribute(Identity.class.getName());
            Session session = this.repository.startSession(identity, true);
            
            String url = ServletHelper.getWebappURI(request);
            if (session.getUriHandler().isDocument(url)) {
                Document document = session.getUriHandler().getDocument(url);
                if (document.isCheckedOutBySession(session.getId(), session.getIdentity().getUser().getId())) {
                    document.checkin();
                }
            }
            else {
                throw new RuntimeException("The URL [" + url + "] doesn't represent a document.");
            }
            
        } catch (final Exception e) {
            getLogger().error("Could not check in node: ", e);
            Map actionMap = new HashMap();
            actionMap.put("exception", "genericException");
            actionMap.put("filename", getDocument().getSourceURI());
            actionMap.put("message", e.getMessage());
            return actionMap;
        }

        return null;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}