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

/* $Id: PageEnvelopeModule.java,v 1.38 2004/03/16 11:12:16 gregor Exp $  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.commons.lang.StringUtils;
import org.apache.lenya.cms.cocoon.uriparameterizer.URIParameterizer;
import org.apache.lenya.cms.cocoon.uriparameterizer.URIParameterizerException;
import org.apache.lenya.cms.publication.DublinCore;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;

/**
 * Input module wrapping the page envelope. This module provides publication
 * related information such as document-id, area, publication-id.
 * 
 * @see org.apache.lenya.cms.publication.PageEnvelope
 */
public class PageEnvelopeModule extends AbstractPageEnvelopeModule implements Serviceable {

    private ServiceManager manager;

    /**
     * Get the the page envelope for the given objectModel.
     * 
     * @param objectModel the objectModel for which the page enevelope is requested.
     * 
     * @return a <code>PageEnvelope</code>
     * 
     * @throws ConfigurationException if the page envelope could not be instantiated.
     */
    protected PageEnvelope getEnvelope(Map objectModel) throws ConfigurationException {
        PageEnvelope envelope = null;

        if (getLogger().isDebugEnabled()) {
            Request request = ObjectModelHelper.getRequest(objectModel);
            getLogger().debug("Resolving page envelope for URL [" + request.getRequestURI() + "]");
        }

        try {
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (Exception e) {
            throw new ConfigurationException("Resolving page envelope failed: ", e);
        }

        return envelope;
    }

    protected static final String URI_PARAMETER_DOCTYPE = "doctype";

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        PageEnvelope envelope = getEnvelope(objectModel);
        Object value = null;

        try {
            if (name.equals(PageEnvelope.AREA)) {
                value = envelope.getDocument().getArea();
            } else if (name.equals(PageEnvelope.CONTEXT)) {
                value = envelope.getContext();
            } else if (name.equals(PageEnvelope.PUBLICATION_ID)) {
                value = envelope.getPublication().getId();
            } else if (name.equals(PageEnvelope.PUBLICATION)) {
                value = envelope.getPublication();
            } else if (name.equals(PageEnvelope.PUBLICATION_LANGUAGES_CSV)) {
                value =
                    StringUtils.join(
                        envelope.getPublication().getLanguages(),
                        ',');
            } else if (name.equals(PageEnvelope.DOCUMENT)) {
                value = envelope.getDocument();
            } else if (name.equals(PageEnvelope.DOCUMENT_ID)) {
                value = envelope.getDocument().getId();
            } else if (name.equals(PageEnvelope.DOCUMENT_NAME)) {
                value = envelope.getDocument().getName();
            } else if (name.equals(PageEnvelope.DOCUMENT_NODE_ID)) { // FIXME: Why is this here?
                value = envelope.getDocument().getNodeId();
            } else if (name.equals(PageEnvelope.DOCUMENT_LABEL)) { // FIXME: Why is this here?
                value = envelope.getDocument().getLabel();
            } else if (name.equals(PageEnvelope.DOCUMENT_URL)) {
                value = envelope.getDocument().getDocumentURL();
            } else if (name.equals(PageEnvelope.DOCUMENT_URL_WITHOUT_LANGUAGE)) {
                value = envelope.getDocument().getCompleteURLWithoutLanguage();
            } else if (name.equals(PageEnvelope.DOCUMENT_PATH)) {
                value = envelope.getDocumentPath();
            } else if (name.equals(PageEnvelope.DOCUMENT_FILE)) {
                value = envelope.getDocument().getFile();
            } else if (name.equals(PageEnvelope.DOCUMENT_EXTENSION)) {
                value = envelope.getDocument().getExtension();
            } else if (name.equals(PageEnvelope.DEFAULT_LANGUAGE)) {
                value = envelope.getPublication().getDefaultLanguage();
            } else if (name.equals(PageEnvelope.DOCUMENT_LANGUAGE)) {
                value = envelope.getDocument().getLanguage();
            } else if (name.equals(PageEnvelope.DOCUMENT_LANGUAGES)) {
                value = envelope.getDocument().getLanguages();
            } else if (name.equals(PageEnvelope.DOCUMENT_LANGUAGES_CSV)) {
                value =
                    StringUtils.join(
                        envelope.getDocument().getLanguages(),
                        ',');
            } else if (name.equals(PageEnvelope.DOCUMENT_DC_TITLE)) {
                value =
                    envelope.getDocument().getDublinCore().getFirstValue(DublinCore.ELEMENT_TITLE);
            } else if (name.equals(PageEnvelope.DOCUMENT_DC_CREATOR)) {
                value =
                    envelope.getDocument().getDublinCore().getFirstValue(
                        DublinCore.ELEMENT_CREATOR);
            } else if (name.equals(PageEnvelope.DOCUMENT_DC_PUBLISHER)) {
                value =
                    envelope.getDocument().getDublinCore().getFirstValue(
                        DublinCore.ELEMENT_PUBLISHER);
            } else if (name.equals(PageEnvelope.DOCUMENT_DC_SUBJECT)) {
                value =
                    envelope.getDocument().getDublinCore().getFirstValue(
                        DublinCore.ELEMENT_SUBJECT);
            } else if (name.equals(PageEnvelope.DOCUMENT_DC_DESCRIPTION)) {
                value =
                    envelope.getDocument().getDublinCore().getFirstValue(
                        DublinCore.ELEMENT_DESCRIPTION);
            } else if (name.equals(PageEnvelope.DOCUMENT_DC_RIGHTS)) {
                value =
                    envelope.getDocument().getDublinCore().getFirstValue(DublinCore.ELEMENT_RIGHTS);
            } else if (name.equals(PageEnvelope.DOCUMENT_DC_DATE_CREATED)) {
                value =
                    envelope.getDocument().getDublinCore().getFirstValue(DublinCore.TERM_CREATED);
            } else if (name.equals(PageEnvelope.DOCUMENT_LASTMODIFIED)) {
                Date date = envelope.getDocument().getLastModified();
                value = new SimpleDateFormat(DATE_FORMAT).format(date);
            } else if (name.equals(PageEnvelope.BREADCRUMB_PREFIX)) {
                value = envelope.getPublication().getBreadcrumbPrefix();
            } else if (name.equals(PageEnvelope.SSL_PREFIX)) {
                value = envelope.getPublication().getSSLPrefix();
            } else if (name.equals(PageEnvelope.DOCUMENT_TYPE)) {
                value = getDocumentType(objectModel, envelope);
            } else {
                throw new ConfigurationException("The attribute [" + name + "] is not supported!");
            }
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException(
                "Getting attribute for name [" + name + "] failed: ",
                e);
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Returning [" + name + "] = [" + value + "]");
        }

        return value;
    }

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Returns the document type.
     * @param objectModel The object model.
     * @param envelope The page envelope.
     * @return A string.
     * @throws ServiceException when something went wrong.
     * @throws URIParameterizerException when something went wrong.
     */
    protected String getDocumentType(Map objectModel, PageEnvelope envelope)
        throws ServiceException, URIParameterizerException {
        String documentType;
        URIParameterizer parameterizer = null;
        Map map = null;
        try {
            parameterizer = (URIParameterizer) manager.lookup(URIParameterizer.ROLE);

            Parameters parameters = new Parameters();
            parameters.setParameter(
                URI_PARAMETER_DOCTYPE,
                "cocoon://uri-parameter/"
                    + envelope.getPublication().getId()
                    + "/"
                    + URI_PARAMETER_DOCTYPE);

            String source =
                envelope.getDocument().getArea() + envelope.getDocument().getDocumentURL();

            Request request = ObjectModelHelper.getRequest(objectModel);

            map = parameterizer.parameterize(request.getRequestURI(), source, parameters);
            documentType = (String) map.get(URI_PARAMETER_DOCTYPE);
        } finally {
            if (parameterizer != null) {
                manager.release(parameterizer);
            }
        }
        return documentType;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        return Arrays.asList(PageEnvelope.PARAMETER_NAMES).iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel)};

        return objects;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
}
