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

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.ResourceType;

/**
 * Input module wrapping the page envelope. This module provides publication
 * related information such as document-id, area, publication-id.
 * 
 * @see org.apache.lenya.cms.publication.PageEnvelope
 */
public class PageEnvelopeModule extends AbstractPageEnvelopeModule {

    protected static final String URI_PARAMETER_DOCTYPE = "doctype";

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Object getAttribute(final String attributeName, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        
        final String name = getAttributeName(attributeName);

        if (!Arrays.asList(PageEnvelope.PARAMETER_NAMES).contains(name)) {
            throw new ConfigurationException("The attribute [" + name + "] is not supported!");
        }

        PageEnvelope envelope = getEnvelope(objectModel, attributeName);
        Object value = null;

        try {
            if (name.equals(PageEnvelope.AREA)) {
                value = envelope.getArea();
            } else if (name.equals(PageEnvelope.CONTEXT)) {
                value = envelope.getContext();
            } else if (name.equals(PageEnvelope.PUBLICATION_ID)) {
                value = envelope.getPublication().getId();
            } else if (name.equals(PageEnvelope.PUBLICATION)) {
                value = envelope.getPublication();
            } else if (name.equals(PageEnvelope.PUBLICATION_LANGUAGES_CSV)) {
                value = StringUtils.join(envelope.getPublication().getLanguages(), ',');
            } else if (name.equals(PageEnvelope.DEFAULT_LANGUAGE)) {
                value = envelope.getPublication().getDefaultLanguage();
            } else if (name.equals(PageEnvelope.BREADCRUMB_PREFIX)) {
                value = envelope.getPublication().getBreadcrumbPrefix();
            }

            Document document = envelope.getDocument();
            if (document != null) {
                if (name.equals(PageEnvelope.DOCUMENT)) {
                    value = document;
                } else if (name.equals(PageEnvelope.DOCUMENT_ID)) {
                    value = document.getId();
                } else if (name.equals(PageEnvelope.DOCUMENT_PARENT)) {
                    String id = document.getId();
                    value = id.substring(0,id.lastIndexOf("/"));
                } else if (name.equals(PageEnvelope.DOCUMENT_NAME)) {
                    value = document.getName();
                } else if (name.equals(PageEnvelope.DOCUMENT_LABEL)) {
                    value = document.getLabel();
                } else if (name.equals(PageEnvelope.DOCUMENT_URL)) {
                    value = document.getCanonicalDocumentURL();
                } else if (name.equals(PageEnvelope.DOCUMENT_URL_WITHOUT_LANGUAGE)) {
                    value = document.getCanonicalWebappURL();
                } else if (name.equals(PageEnvelope.DOCUMENT_PATH)) {
                    value = envelope.getDocumentPath();
                } else if (name.equals(PageEnvelope.DOCUMENT_FILE)) {
                    value = document.getFile();
                } else if (name.equals(PageEnvelope.DOCUMENT_EXTENSION)) {
                    value = document.getExtension();
                } else if (name.equals(PageEnvelope.DOCUMENT_SOURCE_EXTENSION)) {
                    value = document.getSourceExtension();
                } else if (name.equals(PageEnvelope.DOCUMENT_UUID)) {
                    value = document.getUUID();
                } else if (name.equals(PageEnvelope.DOCUMENT_LANGUAGE)) {
                    value = document.getLanguage();
                } else if (name.equals(PageEnvelope.DOCUMENT_LANGUAGES)) {
                    value = document.getLanguages();
                } else if (name.equals(PageEnvelope.DOCUMENT_LANGUAGES_CSV)) {
                    value = StringUtils.join(document.getLanguages(), ',');
                } else if (name.equals(PageEnvelope.DOCUMENT_LASTMODIFIED)) {
                    Date date = document.getLastModified();
                    value = new SimpleDateFormat(DATE_FORMAT).format(date);
                } else if (name.equals(PageEnvelope.DOCUMENT_TYPE)) {
                    ResourceType resourceType = document.getResourceType();
                    if (resourceType == null) {
                        value = null;
                    }
                    else {
                        value = resourceType.getName();
                    }
                }
            }
        } catch (final DocumentException e) {
            throw new ConfigurationException("Getting attribute for name [" + name + "] failed: ",
                    e);
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Returning [" + name + "] = [" + value + "]");
        }

        return value;
    }

    /**
     * <code>DATE_FORMAT</code> The date format
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        return Arrays.asList(PageEnvelope.PARAMETER_NAMES).iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel) };

        return objects;
    }

}
