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

/* $Id:$  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

/**
 * Input module to get document information.
 * {doc-info:{publication-id}:{area}:{uuid}:{document-language}:{property}} where {property} may be:
 * <ul>
 * <li><strong><code>contentLength</code></strong> - the content length (number of bytes).</li>
 * <li><strong><code>expires</code></strong> - the expiration date in ISO 8601 format.</li>
 * <li><strong><code>lastModified</code></strong> - the last modification date in ISO 8601
 * format.</li>
 * <li><strong><code>mimeType</code></strong> - the MIME type</li>
 * <li><strong><code>nodeName</code></strong> - the name of the node in the site structure</li>
 * <li><strong><code>path</code></strong> - the path in the site structure (starting with a
 * slash) or an empty string if the document is not referenced in the site structure.</li>
 * <li><strong><code>resourceType</code></strong> - the name of the resource type</li>
 * <li><strong><code>sourceExtension</code></strong> - the source extension</li>
 * <li><strong><code>visibleInNav</code></strong> - <code>true</code> if the document's node
 * is visible in the navigation, <code>false</code> otherwise.</li>
 * <li><strong><code>webappUrl</code></strong> - the web application URL of the document or
 * an empty string if the document is not referenced in the site structure.</li>
 * </ul>
 */
public class DocumentInfoModule extends AbstractInputModule implements Serviceable {

    protected ServiceManager manager;

    // Input module parameters:
    protected final static String PARAM_PUBLICATION_ID = "publication-id";
    protected final static String PARAM_AREA = "area";
    protected final static String PARAM_UUID = "uuid";
    protected final static String PARAM_DOCUMENT_LANGUAGE = "document-language";
    protected final static String PARAM_PROPERTY = "property";
    protected final static String PARAM_REVISION = "revision";
    protected final static int MIN_MANDATORY_PARAMS = 5;

    protected final static String UUID = "uuid";
    protected final static String LANGUAGE = "language";
    protected final static String PATH = "path";
    protected final static String NODE_NAME = "nodeName";
    protected final static String WEBAPP_URL = "webappUrl";
    protected final static String DOCUMENT_URL = "documentUrl";
    protected final static String RESOURCE_TYPE = "resourceType";
    protected final static String LAST_MODIFIED = "lastModified";
    protected final static String MIME_TYPE = "mimeType";
    protected final static String CONTENT_LENGTH = "contentLength";
    protected final static String SOURCE_EXTENSION = "sourceExtension";
    protected final static String EXPIRES = "expires";
    protected final static String VISIBLE_IN_NAVIGATION = "visibleInNav";

    protected final static String[] PARAMS = { PARAM_PUBLICATION_ID, PARAM_AREA, PARAM_UUID,
            PARAM_DOCUMENT_LANGUAGE, PARAM_PROPERTY, PARAM_REVISION };

    protected final static String META_RESOURCE_TYPE = "resourceType";
    protected final static String META_EXPIRES = "expires";

    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    
    /**
     * Parse the parameters and return a document.
     * @param publicationId The publication ID.
     * @param area The area.
     * @param uuid The document UUID.
     * @param language The document language.
     * @param revision The revision.
     * @param objectModel The object model.
     * @return The document object created.
     * @throws ConfigurationException
     */
    protected Document getDocument(String publicationId, String area, String uuid, String language,
            int revision, Map objectModel) throws ConfigurationException {
        Document document = null;

        Request request = ObjectModelHelper.getRequest(objectModel);

        try {
            Session session = RepositoryUtil.getSession(this.manager, request);
            DocumentFactory docFactory = DocumentUtil.createDocumentFactory(this.manager, session);
            Publication pub = docFactory.getPublication(publicationId);
            document = docFactory.get(pub, area, uuid, language, revision);
        } catch (Exception e) {
            throw new ConfigurationException("Error getting document [" + publicationId + ":"
                    + area + ":" + uuid + ":" + language + "]: " + e.getMessage(), e);
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
            int rev = -1;
            if (params.isParameter(PARAM_REVISION)) {
                String revision = params.getParameter(PARAM_REVISION);
                if (!revision.equals("")) {
                    rev = Integer.valueOf(revision).intValue();
                }
            }

            Document document = getDocument(params.getParameter(PARAM_PUBLICATION_ID), params
                    .getParameter(PARAM_AREA), params.getParameter(PARAM_UUID), params
                    .getParameter(PARAM_DOCUMENT_LANGUAGE), rev, objectModel);
            
            String attribute = params.getParameter(PARAM_PROPERTY);

            if (attribute.equals(RESOURCE_TYPE)) {
                value = document.getResourceType().getName();
            } else if (attribute.equals(LAST_MODIFIED)) {
                value = this.dateFormat.format(new Date(document.getLastModified()));
            } else if (attribute.equals(MIME_TYPE)) {
                value = document.getMimeType();
            } else if (attribute.equals(CONTENT_LENGTH)) {
                value = Long.toString(document.getContentLength());
            } else if (attribute.equals(SOURCE_EXTENSION)) {
                value = document.getSourceExtension();
            } else if (attribute.equals(LANGUAGE)) {
                value = document.getLanguage();
            } else if (attribute.equals(PATH)) {
                value = document.getPath();
            } else if (attribute.equals(NODE_NAME)) {
                value = document.getName();
            } else if (attribute.equals(UUID)) {
                value = document.getUUID();
            } else if (attribute.equals(WEBAPP_URL)) {
                value = document.getCanonicalWebappURL();
            } else if (attribute.equals(DOCUMENT_URL)) {
                value = document.getCanonicalDocumentURL();
            } else if (attribute.equals(EXPIRES)) {
                try {
                    Date expires = document.getExpires();
                    value = this.dateFormat.format(expires);
                } catch (DocumentException e) {
                    throw new ConfigurationException("Error getting expires date from document.", e);
                }
            } else if (attribute.equals(VISIBLE_IN_NAVIGATION)) {
                value = Boolean.toString(isVisibleInNavigation(document));
            } else {
                throw new ConfigurationException("Attribute '" + attribute + "' not supported ["
                        + name + "]");
            }
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Error getting input module parameters.", e);
        }

        return value;
    }

    protected boolean isVisibleInNavigation(Document document) throws ConfigurationException {
        try {
            return document.getLink().getNode().isVisible();
        } catch (DocumentException e) {
            throw new ConfigurationException("Obtaining navigation visibility failed [" + document
                    + "]: " + e.getMessage(), e);
        }

    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
}
