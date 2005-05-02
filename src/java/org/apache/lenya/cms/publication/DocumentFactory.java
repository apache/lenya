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

import java.io.File;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.transaction.Identifiable;
import org.apache.lenya.transaction.IdentityMap;
import org.apache.lenya.transaction.IdentifiableFactory;

/**
 * Document factory.
 *
 * @version $Id:$
 */
public class DocumentFactory extends AbstractLogEnabled implements IdentifiableFactory {

    protected ServiceManager manager;

    /**
     * Ctor.
     * @param manager The service manager.
     */
    public DocumentFactory(ServiceManager manager) {
        this.manager = manager;
    }

    /**
     * Builds a document key.
     * @param publication The publication.
     * @param area The area.
     * @param documentId The document ID.
     * @param language The language.
     * @return A key.
     */
    public String getKey(Publication publication, String area, String documentId, String language) {
        return publication.getId() + ":" + area + ":" + documentId + ":" + language;
    }

    /**
     * Builds a document key.
     * @param map The identity map.
     * @param webappUrl The web application URL.
     * @return A key.
     */
    public String getKey(DocumentIdentityMap map, String webappUrl) {
        ServiceSelector selector = null;
        DocumentBuilder builder = null;
        SourceResolver resolver = null;
        Source source = null;
        Document document;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context://");
            File servletContext = SourceUtil.getFile(source);

            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            URLInformation info = new URLInformation(webappUrl);
            String publicationId = info.getPublicationId();
            Publication publication = factory.getPublication(publicationId, servletContext
                    .getAbsolutePath());

            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            builder = (DocumentBuilder) selector.select(publication.getDocumentBuilderHint());
            document = builder.buildDocument(map, publication, webappUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (builder != null) {
                    selector.release(builder);
                }
                this.manager.release(selector);
            }
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
        return getKey(document.getPublication(), document.getArea(), document.getId(), document
                .getLanguage());
    }

    /**
     * @see org.apache.lenya.transaction.IdentifiableFactory#build(org.apache.lenya.transaction.IdentityMap,
     *      java.lang.String)
     */
    public Identifiable build(IdentityMap map, String key) throws Exception {

        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentFactory::build() called with key [" + key + "]");

        String[] snippets = key.split(":");
        String publicationId = snippets[0];
        String area = snippets[1];
        String documentId = snippets[2];
        String language = snippets[3];

        ServiceSelector selector = null;
        DocumentBuilder builder = null;
        SourceResolver resolver = null;
        Source source = null;
        Document document;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context://");
            File servletContext = SourceUtil.getFile(source);

            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            Publication publication = factory.getPublication(publicationId, servletContext
                    .getAbsolutePath());

            selector = (ServiceSelector) this.manager.lookup(DocumentBuilder.ROLE + "Selector");
            builder = (DocumentBuilder) selector.select(publication.getDocumentBuilderHint());
            String webappUrl = builder.buildCanonicalUrl(publication, area, documentId, language);
            document = builder.buildDocument((DocumentIdentityMap) map, publication, webappUrl);
        } finally {
            if (selector != null) {
                if (builder != null) {
                    selector.release(builder);
                }
                this.manager.release(selector);
            }
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
        if (getLogger().isDebugEnabled())
            getLogger().debug("DocumentFactory::build() done.");

        return document;
    }

}
