/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.publication;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;

/**
 * Document utility class.
 */
public class DocumentUtil {

    /**
     * Creates a document identity map.
     * @param manager The service manager.
     * @param session The session.
     * @return if an error occurs.
     */
    public static DocumentFactory createDocumentIdentityMap(ServiceManager manager, Session session) {
        DocumentFactory map;
        DocumentManager docManager = null;
        try {
            docManager = (DocumentManager) manager.lookup(DocumentManager.ROLE);
            map = docManager.createDocumentIdentityMap(session);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } finally {
            if (docManager != null) {
                manager.release(docManager);
            }
        }
        return map;
    }

    /**
     * Returns the currently requested document or <code>null</code> if no document is requested.
     * @param manager The service manager.
     * @param request The request.
     * @return A document.
     * @throws RepositoryException if an error occurs.
     * @throws DocumentBuildException if an error occurs.
     */
    public static Document getCurrentDocument(ServiceManager manager, Request request)
            throws RepositoryException, DocumentBuildException {
        Session session = RepositoryUtil.getSession(manager, request);
        DocumentFactory factory = DocumentUtil.createDocumentIdentityMap(manager, session);
        String url = ServletHelper.getWebappURI(request);
        Document doc = null;
        if (factory.isDocument(url)) {
            doc = factory.getFromURL(url);
        }
        return doc;
    }

}
