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
package org.apache.lenya.cms.publication;

import java.util.Map;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.cocoon.uriparameterizer.URIParameterizer;

/**
 * Service to resolve the document type for a document.
 *
 * <p>Note that in the current architecture, this requires access to the
 * request, that is, the document type is determined upon each request.
 * This service is currently not poolable.</p>
 * 
 * @version $Id: DocumentTypeResolverImpl.java 152682 2005-02-08 18:13:39Z
 *          gregor $
 */
public class DocumentTypeResolverImpl extends AbstractLogEnabled implements Serviceable,
        Contextualizable, DocumentTypeResolver {

    protected static final String URI_PARAMETER_DOCTYPE = "doctype";

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

    private Context context;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context _context) throws ContextException {
        this.context = _context;
    }

    /**
     * Determine the document type for a document.
     *
     * @see org.apache.lenya.cms.publication.DocumentTypeResolver#resolve(org.apache.lenya.cms.publication.Document)
     */
    public DocumentType resolve(Document document) {
        
        DocumentType documentType;
        URIParameterizer parameterizer = null;
        Map map = null;
        try {
            parameterizer = (URIParameterizer) this.manager.lookup(URIParameterizer.ROLE);

            Parameters parameters = new Parameters();
            parameters.setParameter(URI_PARAMETER_DOCTYPE, "cocoon://uri-parameter/"
                    + document.getPublication().getId() + "/" + URI_PARAMETER_DOCTYPE);

            String source = document.getArea() + document.getCanonicalDocumentURL();

            Request request = ContextHelper.getRequest(this.context);
            String context = request.getContextPath();
            String webappUrl = document.getCanonicalWebappURL();
            String url = context + webappUrl;
            if (getLogger().isDebugEnabled())
                getLogger().debug("DocumentTypeResolverImpl.resolve() url = [" + url + "]");

            map = parameterizer.parameterize(filterURI(url), filterURI(source), parameters);
            String name = (String) map.get(URI_PARAMETER_DOCTYPE);

            if (getLogger().isDebugEnabled())
                getLogger().debug("DocumentTypeResolverImpl.resolve() name = [" + name + "], now calling DocumentTypeBuilder");

            documentType = DocumentTypeBuilder.buildDocumentType(name, document.getPublication());

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (parameterizer != null) {
                this.manager.release(parameterizer);
            }
        }

        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentTypeResolverImpl.resolve() returning documentType = [" + documentType + "]");

        return documentType;
    }

    /**
     * uri will be filtered by certain rules i.e. session information encoded
     * within the uri will be removed.
     * @param uri The uri to be filtered
     * @return uri filtered by certain rules i.e
     */
    // FIXME Maybe make this more configureable
    private String filterURI(final String uri) {
        final int index = uri.indexOf(";jsessionid");

        if (index >= 0) {
            return uri.substring(0, index);
        }
        return uri;
    }

}
