
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

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Publication;

/**
 * A DocumentIdentityMap avoids the multiple instanciation of a document object.
 * 
 * @version $Id$
 */
public class DocumentIdentityMap {

    private Map key2document = new HashMap();
    private DocumentFactory factory;

    private Map key2siteStructure = new HashMap();

    /**
     * Ctor.
     * @param manager The service manager.
     */
    public DocumentIdentityMap(ServiceManager manager) {
        this.manager = manager;
    }

    /**
     * Returns the document factory.
     * @return A document factory.
     */
    public DocumentFactory getFactory() {
        if (this.factory == null) {
            this.factory = new DocumentFactory(this.manager, this);
        }
        return this.factory;
    }

    /**
     * Returns a site structure object.
     * @param publication The publication.
     * @param area The area.
     * @return The site structure object.
     */
    public Object getSiteStructure(Publication publication, String area) {
        String key = getSiteStructureKey(publication, area);
        return this.key2siteStructure.get(key);
    }

    /**
     * Adds a site structure object.
     * @param publication The publication.
     * @param area The area.
     * @param siteStructure The site structure to add.
     */
    public void putSiteStructure(Publication publication, String area, Object siteStructure) {
        String key = getSiteStructureKey(publication, area);
        this.key2siteStructure.put(key, siteStructure);
    }

    protected String getSiteStructureKey(Publication publication, String area) {
        return publication.getId() + ":" + area;
    }

    /**
     * Returns a document.
     * @param publication The publication.
     * @param area The area.
     * @param documentId The document ID.
     * @param language The language.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    protected Document get(Publication publication, String area, String documentId, String language)
            throws DocumentBuildException {
        String key = getKey(area, documentId, language);
        Document document = (Document) this.key2document.get(key);
        if (document == null) {
            
            ServiceSelector selector = null;
            DocumentBuilder builder = null;
            try {
                selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
                builder = (DocumentBuilder) selector.select(publication.getDocumentBuilderHint());
                
                String url = builder.buildCanonicalUrl(publication, area, documentId, language);
                document = builder.buildDocument(this, publication, url);
                this.key2document.put(key, document);
            } catch (ServiceException e) {
                throw new DocumentBuildException(e);
            }
            finally {
                if (selector != null) {
                    if (builder != null) {
                        selector.release(builder);
                    }
                    this.manager.release(selector);
                }
            }
        }
        return document;
    }

    /**
     * Returns the document identified by a certain web application URL.
     * @param publication The publication to use.
     * @param webappUrl The web application URL.
     * @return A document.
     * @throws DocumentBuildException if an error occurs.
     */
    protected Document getFromURL(Publication publication, String webappUrl)
            throws DocumentBuildException {
        ServiceSelector selector = null;
        DocumentBuilder builder = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            builder = (DocumentBuilder) selector.select(publication.getDocumentBuilderHint());
            
            if (!builder.isDocument(publication, webappUrl)) {
                throw new DocumentBuildException("The webapp URL [" + webappUrl
                        + "] does not identify a valid document");
            }

            Document document = builder.buildDocument(this, publication, webappUrl);
            String key = getKey(document.getArea(), document.getId(), document.getLanguage());

            Document resultDocument;
            if (this.key2document.containsKey(key)) {
                resultDocument = (Document) this.key2document.get(key);
            } else {
                resultDocument = document;
                this.key2document.put(key, resultDocument);
            }
            return resultDocument;
            
        } catch (ServiceException e) {
            throw new DocumentBuildException(e);
        }
        finally {
            if (selector != null) {
                if (builder != null) {
                    selector.release(builder);
                }
                this.manager.release(selector);
            }
        }
    }

    /**
     * Calculates a map key.
     * @param area The area.
     * @param documentId The document ID.
     * @param language The language.
     * @return A string.
     */
    protected String getKey(String area, String documentId, String language) {
        return area + ":" + documentId + ":" + language;
    }

    protected ServiceManager manager;

}