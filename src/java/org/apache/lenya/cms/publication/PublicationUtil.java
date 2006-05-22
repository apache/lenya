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

import java.util.Arrays;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
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
     * Create a new publication with the given publication-id.
     * @param manager The service manager.
     * @param id the publication id
     * @return a <code>Publication</code>
     * @throws PublicationException if there was a problem creating the publication.
     */
    public static Publication getPublication(ServiceManager manager, String id)
            throws PublicationException {
        PublicationManager pubManager = null;
        try {
            pubManager = (PublicationManager) manager.lookup(PublicationManager.ROLE);
            return pubManager.getPublication(id);
        } catch (ServiceException e) {
            throw new PublicationException(e);
        } finally {
            if (pubManager != null) {
                manager.release(pubManager);
            }
        }
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
        String webappUrl = ServletHelper.getWebappURI(request);
        return getPublicationFromUrl(manager, webappUrl);
    }

    /**
     * Creates a publication from a webapp URL and a servlet context directory.
     * @param manager The service manager.
     * @param webappUrl The URL within the web application (without context prefix)
     * @return A publication
     * @throws PublicationException when something went wrong
     */
    public static Publication getPublicationFromUrl(ServiceManager manager, String webappUrl)
            throws PublicationException {
        URLInformation info = new URLInformation(webappUrl);
        String pubId = info.getPublicationId();
        return getPublication(manager, pubId);
    }

    /**
     * Returns a list of all available publications.
     * @param manager The service manager.
     * @return An array of publications.
     * @throws PublicationException if an error occurs.
     */
    public static Publication[] getPublications(ServiceManager manager) throws PublicationException {
        PublicationManager pubManager = null;
        try {
            pubManager = (PublicationManager) manager.lookup(PublicationManager.ROLE);
            return pubManager.getPublications();
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
