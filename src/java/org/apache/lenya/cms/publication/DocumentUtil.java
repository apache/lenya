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
public final class DocumentUtil {

    private static DocumentFactoryBuilder builder;

    /**
     * Creates a document factory.
     * @param manager The service manager.
     * @param session The session.
     * @return a document factory.
     */
    public static final DocumentFactory createDocumentFactory(ServiceManager manager,
            Session session) {
        DocumentFactoryBuilder builder = getBuilder(manager);
        return builder.createDocumentFactory(session);
    }

    protected static DocumentFactoryBuilder getBuilder(ServiceManager manager) {
        if (DocumentUtil.builder == null) {
            try {
                DocumentUtil.builder = (DocumentFactoryBuilder) manager.lookup(DocumentFactoryBuilder.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return DocumentUtil.builder;
    }

    /**
     * Returns a document factory for the session which is attached to the
     * request. If no session exists, it is created.
     * @param manager The service manager.
     * @param request The request.
     * @return A document factory.
     */
    public static DocumentFactory getDocumentFactory(ServiceManager manager, Request request) {
        Session session;
        try {
            session = RepositoryUtil.getSession(manager, request);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        return createDocumentFactory(manager, session);
    }

    /**
     * Returns the currently requested document or <code>null</code> if no
     * document is requested.
     * @param manager The service manager.
     * @param request The request.
     * @return A document.
     * @throws RepositoryException if an error occurs.
     * @throws DocumentBuildException if an error occurs.
     */
    public static Document getCurrentDocument(ServiceManager manager, Request request)
            throws RepositoryException, DocumentBuildException {
        Session session = RepositoryUtil.getSession(manager, request);
        DocumentFactory factory = DocumentUtil.createDocumentFactory(manager, session);
        String url = ServletHelper.getWebappURI(request);
        Document doc = null;
        if (factory.isDocument(url)) {
            doc = factory.getFromURL(url);
        }
        return doc;
    }

}
