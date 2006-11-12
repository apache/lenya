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

import java.util.Arrays;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;

/**
 * Publication utility.
 */
public class PublicationUtil {

    /**
     * Creates a new publication. The publication ID is resolved from the request URI. The servlet
     * context path is resolved from the context object.
     * @param manager The service manager.
     * @param objectModel The object model of the Cocoon component.
     * @return a <code>Publication</code>
     * @throws PublicationException if there was a problem creating the publication.
     */
    public static Publication getPublication(ServiceManager manager, Map objectModel)
            throws PublicationException {
        return getPublication(manager, ObjectModelHelper.getRequest(objectModel));
    }

    /**
     * Creates a new publication based on a request.
     * @param manager The service manager.
     * @param request A request.
     * @return A publication.
     * @throws PublicationException if there was a problem creating the publication.
     */
    public static Publication getPublication(ServiceManager manager, Request request)
            throws PublicationException {
        Session session;
        try {
            session = RepositoryUtil.getSession(manager, request);
        } catch (RepositoryException e) {
            throw new PublicationException(e);
        }
        DocumentFactory factory = DocumentUtil.createDocumentFactory(manager, session);
        String webappUrl = ServletHelper.getWebappURI(request);
        return getPublicationFromUrl(manager, factory, webappUrl);
    }

    /**
     * Creates a publication from a webapp URL and a servlet context directory.
     * @param manager The service manager.
     * @param factory The factory.
     * @param webappUrl The URL within the web application (without context prefix)
     * @return A publication
     * @throws PublicationException when something went wrong
     */
    public static Publication getPublicationFromUrl(ServiceManager manager,
            DocumentFactory factory, String webappUrl) throws PublicationException {
        URLInformation info = new URLInformation(webappUrl);
        String pubId = info.getPublicationId();
        return factory.getPublication(pubId);
    }

    /**
     * Returns a list of all available publications.
     * @param manager The service manager.
     * @param factory The document factory.
     * @return An array of publications.
     * @throws PublicationException if an error occurs.
     */
    public static Publication[] getPublications(ServiceManager manager, DocumentFactory factory)
            throws PublicationException {
        PublicationManager pubManager = null;
        try {
            pubManager = (PublicationManager) manager.lookup(PublicationManager.ROLE);
            return pubManager.getPublications(factory);
        } catch (ServiceException e) {
            throw new PublicationException(e);
        } finally {
            if (pubManager != null) {
                manager.release(pubManager);
            }
        }
    }

    /**
     * Checks if a publication id is valid.
     * @param id
     * @return true if the id contains only lowercase letters and/or numbers, and is not an empty
     *         string.
     */
    public static boolean isValidPublicationID(String id) {
        return id.matches("[a-z0-9]+");
    }

    private static final String[] areas = { Publication.AUTHORING_AREA, Publication.DAV_AREA,
            Publication.STAGING_AREA, Publication.LIVE_AREA, Publication.ARCHIVE_AREA,
            Publication.TRASH_AREA };

    /**
     * Returns if a given string is a valid area name.
     * @param area The area string to test.
     * @return A boolean value.
     */
    public static boolean isValidArea(String area) {
        return area != null && Arrays.asList(areas).contains(area);
    }

}
