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

/* $Id:$  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.text.SimpleDateFormat;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteUtil;

/**
 * Input module to get document information.
 * {doc-info:{publication-id}:{area}:{document-id}:{document-language}:{property}} where {property}
 * may be:
 * <ul>
 * <li>resourceType</li>
 * <li>lastModified</li>
 * <li>mimeType</li>
 * </ul>
 */
public class DocumentInfoModule extends AbstractInputModule implements Serviceable {

    protected ServiceManager manager;

    // Input module parameters:
    protected final static String PARAM_PUBLICATION_ID = "publication-id";
    protected final static String PARAM_AREA = "area";
    protected final static String PARAM_DOCUMENT_ID = "document-id";
    protected final static String PARAM_DOCUMENT_LANGUAGE = "document-language";
    protected final static String PARAM_PROPERTY = "property";
    protected final static int MIN_MANDATORY_PARAMS = 5;

    protected final static String RESOURCE_TYPE = "resourceType";
    protected final static String LAST_MODIFIED = "lastModified";
    protected final static String MIME_TYPE = "mimeType";
    protected final static String VISIBLE_IN_NAVIGATION = "visibleInNav";

    protected final static String[] PARAMS = { PARAM_PUBLICATION_ID, PARAM_AREA, PARAM_DOCUMENT_ID,
            PARAM_DOCUMENT_LANGUAGE, PARAM_PROPERTY, VISIBLE_IN_NAVIGATION };

    protected final static String META_RESOURCE_TYPE = "resourceType";

    /**
     * Parse the parameters and return a document.
     * @param publicationId The publication ID.
     * @param area The area.
     * @param docId The document ID.
     * @param docLang The document language.
     * @param objectModel The object model.
     * @return The document object created.
     * @throws ConfigurationException
     */
    protected Document getDocument(String publicationId, String area, String docId, String docLang,
            Map objectModel) throws ConfigurationException {
        Document document = null;

        Request request = ObjectModelHelper.getRequest(objectModel);

        try {
            Session session = RepositoryUtil.getSession(this.manager, request);
            DocumentIdentityMap docFactory = DocumentUtil.createDocumentIdentityMap(this.manager, session);
            Publication publication = PublicationUtil.getPublication(this.manager, publicationId);
            document = docFactory.get(publication, area, docId, docLang);
        } catch (Exception e) {
            throw new ConfigurationException("Error getting document [" + publicationId + ":"
                    + area + ":" + docId + ":" + docLang + "]: " + e.getMessage(), e);
        }
        return document;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object value = null;

        InputModuleParameters params = new InputModuleParameters(name, PARAMS, MIN_MANDATORY_PARAMS);

        try {
            Document document = getDocument(params.getParameter(PARAM_PUBLICATION_ID),
                    params.getParameter(PARAM_AREA),
                    params.getParameter(PARAM_DOCUMENT_ID),
                    params.getParameter(PARAM_DOCUMENT_LANGUAGE),
                    objectModel);
            String attribute = params.getParameter(PARAM_PROPERTY);

            if (attribute.equals(RESOURCE_TYPE)) {
                value = getResourceType(document);
            } else if (attribute.equals(LAST_MODIFIED)) {
                value = getLastModified(document);
            } else if (attribute.equals(MIME_TYPE)) {
                value = getMimeType(document);
                if (value == null)
                    throw new ConfigurationException("Attribute '" + attribute + "' not defined ["
                            + name + "]");
            } else if (attribute.equals(VISIBLE_IN_NAVIGATION)) {
                value = Boolean.toString(isVisibleInNavigation(document));
            } else {
                throw new ConfigurationException("Attribute '" + attribute + "' not supported ["
                        + name + "]");
            }
        } catch (ParameterException e) {
            throw new ConfigurationException("Error getting input module parameters.", e);
        }

        return value;
    }

    protected boolean isVisibleInNavigation(Document document) throws ConfigurationException {
        try {
            return SiteUtil.isVisibleInNavigation(this.manager, document);
        } catch (SiteException e) {
            throw new ConfigurationException("Obtaining navigation visibility failed ["
                    + document.getId() + "]: " + e.getMessage(), e);
        }

    }

    protected String getResourceType(Document document) throws ConfigurationException {
        String resourceType = null;
        MetaData metaData = null;
        try {
            metaData = document.getMetaDataManager().getLenyaMetaData();
        } catch (DocumentException e) {
            throw new ConfigurationException("Obtaining custom meta data value for ["
                    + document.getSourceURI() + "] failed: " + e.getMessage(), e);
        }

        try {
            resourceType = metaData.getFirstValue(META_RESOURCE_TYPE);
        } catch (DocumentException e) {
            throw new ConfigurationException("Obtaining resource type for [" + document.getId()
                    + "] failed: " + e.getMessage(), e);
        }
        return resourceType;
    }

    /**
     * Returns last modification date in HTTP date format (see RFC 1123).
     * @param document
     * @return Formatted HTTP date string.
     * @throws ConfigurationException
     */
    protected String getLastModified(Document document) throws ConfigurationException {
        SimpleDateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        return httpDateFormat.format(document.getLastModified());
    }

    /**
     * Get mime-type of document.
     * @param document
     * @return Mime-type of document.
     * @throws ConfigurationException
     */
    protected String getMimeType(Document document) throws ConfigurationException {
        String mimeType = null;
        MetaData metaData = null;
        try {
            metaData = document.getMetaDataManager().getDublinCoreMetaData();
        } catch (DocumentException e) {
            throw new ConfigurationException("Obtaining custom meta data value failed ["
                    + document.getSourceURI() + "]: " + e.getMessage(), e);
        }

        try {
            mimeType = metaData.getFirstValue(DublinCore.ELEMENT_FORMAT);
        } catch (DocumentException e) {
            throw new ConfigurationException("Obtaining resource type failed [" + document.getId()
                    + "]: " + e.getMessage(), e);
        }
        return mimeType;
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
}
