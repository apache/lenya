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
package org.apache.lenya.cms.repo.impl;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.Publication;
import org.apache.lenya.cms.repo.Repository;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Session;
import org.apache.lenya.cms.repo.SiteNode;
import org.apache.lenya.cms.repo.avalon.RepositoryFactory;
import org.apache.lenya.util.ServletHelper;

/**
 * Repository utility class.
 */
public class RepositoryUtil {

    /**
     * @param manager The service manager.
     * @param request The request object.
     * @param logger The logger to use.
     * @return A session.
     */
    public static Session getSession(ServiceManager manager, Request request, Logger logger) {
        Session session = (Session) request.getAttribute(Session.class.getName());
        if (session == null) {

            RepositoryFactory factory = null;
            try {
                factory = (RepositoryFactory) manager.lookup(RepositoryFactory.ROLE);
                Repository repository = factory.getRepository();
                session = repository.createSession();
                request.setAttribute(Session.class.getName(), session);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (factory != null) {
                    manager.release(factory);
                }
            }

        }
        return session;
    }

    /**
     * @param session The session.
     * @param webappUrl The web application URL.
     * @return The document represented by the URL.
     * @throws RepositoryException if an error occurs.
     */
    public static Document getDocument(Session session, String webappUrl)
            throws RepositoryException {

        URLInformation info = new URLInformation(webappUrl);
        Publication pub = session.getPublication(info.getPublicationId());
        Area area = pub.getArea(info.getArea());

        String path = "";
        final String docUrl = info.getDocumentUrl();
        int firstDotIndex = docUrl.indexOf(".");
        if (firstDotIndex > 0) {
            path = docUrl.substring(0, firstDotIndex);
        } else {
            path = docUrl;
        }
        SiteNode siteNode = area.getSite().getNode(path);

        Document document = null;
        if (siteNode != null) {
            document = siteNode.getDocument();
        }
        return document;
    }

    /**
     * @param manager The service manager.
     * @param request The request.
     * @param logger The logger.
     * @return A publication.
     * @throws RepositoryException if an error occurs.
     */
    public static Publication getPublication(ServiceManager manager, Request request, Logger logger)
            throws RepositoryException {
        Session session = getSession(manager, request, logger);
        String url = ServletHelper.getWebappURI(request);
        String pubId = new URLInformation(url).getPublicationId();
        return session.getPublication(pubId);
    }

    /**
     * @param manager The service manager.
     * @param request The request.
     * @param logger The logger.
     * @return A document.
     * @throws RepositoryException if an error occurs.
     */
    public static Document getDocument(ServiceManager manager, Request request, Logger logger)
            throws RepositoryException {
        Session session = getSession(manager, request, logger);
        String url = ServletHelper.getWebappURI(request);
        return getDocument(session, url);
    }

}
