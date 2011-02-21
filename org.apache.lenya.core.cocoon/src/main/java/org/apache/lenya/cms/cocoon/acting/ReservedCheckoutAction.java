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
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.publication.Session;

/**
 * Action doing reserved checkout
 */
public class ReservedCheckoutAction extends RevisionControllerAction {

    /**
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector, org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
            Parameters parameters) throws Exception {
        super.act(redirector, resolver, objectModel, src, parameters);

        HashMap actionMap = new HashMap();
        Document doc = getDocument();

        //check out
        try {
            
            String username = getUsername();
            
            assert doc != null;
            assert username != null;

            Session session = doc.getSession();
            if (!doc.isCheckedOutBySession(session.getId(), session.getIdentity().getUser().getId())) {
                doc.checkout();
            }
        } catch (RepositoryException e) {
            actionMap.put("exception", "genericException");
            actionMap.put("filename", doc.getSourceURI());
            actionMap.put("message", "" + e.getMessage());
            getLogger().error("The document " + doc.getSourceURI() + " couldn't be checked out: ", e);

            return actionMap;
        }

        return null;
    }
}