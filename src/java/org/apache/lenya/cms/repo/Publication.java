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
package org.apache.lenya.cms.repo;

import org.apache.lenya.cms.proxy.Proxy;

/**
 * A publication.
 */
public interface Publication {

    /**
     * @return All existing areas.
     * @throws RepositoryException if an error occurs.
     */
    Area[] getAreas() throws RepositoryException;

    /**
     * @param area The area ID.
     * @return The area object.
     * @throws RepositoryException if the area does not exist.
     */
    Area getArea(String area) throws RepositoryException;

    /**
     * @param area The area ID.
     * @return The area object.
     * @throws RepositoryException if the area already exists.
     */
    Area addArea(String area) throws RepositoryException;

    /**
     * Checks if an area exists in this publication.
     * @param area The area.
     * @return A boolean value.
     * @throws RepositoryException of an error occurs.
     */
    boolean existsArea(String area) throws RepositoryException;

    /**
     * @return The publication ID.
     * @throws RepositoryException if an error occurs.
     */
    String getPublicationId() throws RepositoryException;

    /**
     * Get the default language
     * @return the default language
     */
    String getDefaultLanguage();

    /**
     * Get all available languages for this publication
     * @return an <code>Array</code> of languages
     */
    String[] getLanguages();

    /**
     * Returns the hint of the site manager service that is used by this publication.
     * @return A hint to use for service selection.
     */
    String getSiteManagerHint();

    /**
     * Returns the publication template instantiator hint. If the publication does not allow
     * templating, <code>null</code> is returned.
     * @return A hint to use for service selection.
     */
    String getInstantiatorHint();

    /**
     * Returns the proxy which is used for a particular document.
     * @param document The document.
     * @param isSslProtected A boolean value.
     * @return A proxy or <code>null</code> if no proxy is defined for this version.
     * @throws RepositoryException if an error occurs.
     */
    Proxy getProxy(Document document, boolean isSslProtected) throws RepositoryException;

    /**
     * @return The templates of the publication.
     */
    String[] getTemplateIds();

    /**
     * @param documentType The document type.
     * @return The workflow schema to use for this document type or <code>null</code> if no
     *         workflow is registered for this document type.
     */
    String getWorkflowSchema(DocumentType documentType);

    /**
     * @return The resource types that are supported by this publication.
     */
    String[] getResourceTypeNames();
    
    /**
     * @return The session.
     * @throws RepositoryException if an error occurs.
     */
    Session getSession() throws RepositoryException;

}
