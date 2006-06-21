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

/* $Id$  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.publication.DocumentDoesNotExistException;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.impl.RepositoryUtil;
import org.apache.lenya.util.ServletHelper;

/**
 * Action that checks the sitetree if there is a node with the current document-id and the current
 * language, i.e. if the current document has a version in the current language.
 */
public class LanguageExistsAction extends ServiceableAction {

    /**
     * Check if the current document-id has a document for the currently requested language.
     * 
     * If yes return an empty map, if not return null.
     * 
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     * 
     * @return an empty <code>Map</code> if there is a version of this document for the current
     *         language, null otherwiese
     * @throws Exception if an error occurs
     * 
     * @exception DocumentDoesNotExistException if there is no document with the specified
     *                document-id.
     * @exception Exception if the PageEnvelope could not be created or if the language information
     *                could not be fetched from the document.
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
            Parameters parameters) throws Exception {

        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = RepositoryUtil.getSession(this.manager, request, getLogger());

        String url = ServletHelper.getWebappURI(request);
        Document doc = RepositoryUtil.getDocument(session, url);

        if (doc == null) {
            throw new DocumentDoesNotExistException("Document for URL " + url
                    + " does not exist. Check sitetree, it might need to be reloaded.");
        }
        return null;
    }
}