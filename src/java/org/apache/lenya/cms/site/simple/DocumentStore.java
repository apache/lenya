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
package org.apache.lenya.cms.site.simple;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentifier;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.util.CollectionImpl;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * Site structure object which stores a list of documents.
 * 
 * @version $Id$
 */
public class DocumentStore extends CollectionImpl implements SiteStructure {

    protected static final String DOCUMENT_ID = "/site";

    /**
     * The identifiable type.
     */
    public static final String IDENTIFIABLE_TYPE = "documentstore";

    /**
     * @param manager The service manager.
     * @param map The identity map.
     * @param publication The publication.
     * @param area The area.
     * @param _logger The logger.
     * @throws DocumentException if an error occurs.
     */
    public DocumentStore(ServiceManager manager, DocumentFactory map, Publication publication,
            String area, Logger _logger) throws DocumentException {
        super(manager, map, new DocumentIdentifier(publication,
                area,
                DOCUMENT_ID,
                publication.getDefaultLanguage()), _logger);
    }

    protected static final String ATTRIBUTE_LANGUAGE = "xml:lang";

    /**
     * @see org.apache.lenya.cms.publication.util.CollectionImpl#createDocumentElement(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.xml.NamespaceHelper)
     */
    protected Element createDocumentElement(Document document, NamespaceHelper helper)
            throws DocumentException {
        Element element = super.createDocumentElement(document, helper);
        element.setAttribute(ATTRIBUTE_LANGUAGE, document.getLanguage());
        return element;
    }

    /**
     * @see org.apache.lenya.cms.publication.util.CollectionImpl#loadDocument(org.w3c.dom.Element)
     */
    protected Document loadDocument(Element documentElement) throws DocumentBuildException {
        String documentId = documentElement.getAttribute(ATTRIBUTE_ID);
        String language = documentElement.getAttribute(ATTRIBUTE_LANGUAGE);
        Document document = getDelegate().getIdentityMap().get(getDelegate().getPublication(),
                getDelegate().getArea(),
                documentId,
                language);
        return document;
    }

    /**
     * @return if the document exists.
     * @throws DocumentException if an error occurs.
     */
    public boolean exists() throws DocumentException {
        try {
            return SourceUtil.exists(getDelegate().getSourceURI(), this.manager);
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

    public Node getRepositoryNode() {
        return getDelegate().getRepositoryNode();
    }
}