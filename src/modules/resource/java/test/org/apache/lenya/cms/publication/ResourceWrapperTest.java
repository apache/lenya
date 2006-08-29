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

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.TraversableSource;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.metadata.MetaDataException;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

/**
 * Resource wrapper test.
 */
public class ResourceWrapperTest extends AbstractAccessControlTest {

    public static final String IMAGE_URL = "context://lenya/resources/images/project-logo.png";

    /**
     * @throws RepositoryException
     * @throws PublicationException
     * @throws ServiceException
     * @throws MetaDataException
     * @throws IOException
     * @throws MalformedURLException
     */
    public void testResourceWrapper() throws RepositoryException, PublicationException,
            ServiceException, MalformedURLException, IOException, MetaDataException {

        String documentId = "/testResource";

        Session session = RepositoryUtil.getSession(getManager(), getRequest());
        DocumentFactory factory = DocumentUtil.createDocumentFactory(getManager(), session);

        Publication pub = getPublication("test");

        Document doc = createResource(factory, pub, documentId, getManager(), getLogger());

        SourceResolver resolver = null;
        TraversableSource source = null;

        try {
            resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
            source = (TraversableSource) resolver.resolveURI(IMAGE_URL);

            assertEquals(doc.getMimeType(), source.getMimeType());
            assertEquals(doc.getContentLength(), source.getContentLength());

        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                getManager().release(resolver);
            }

        }

    }

    /**
     * @param factory
     * @param pub
     * @param path
     * @param manager 
     * @param logger 
     * @return A document.
     * @throws ServiceException
     * @throws DocumentBuildException
     * @throws PublicationException
     * @throws MalformedURLException
     * @throws IOException
     * @throws RepositoryException
     * @throws DocumentException
     * @throws MetaDataException
     */
    public static Document createResource(DocumentFactory factory, Publication pub, String path,
            ServiceManager manager, Logger logger) throws ServiceException, DocumentBuildException,
            PublicationException, MalformedURLException, IOException, RepositoryException,
            DocumentException, MetaDataException {

        String extension = "png";

        Document doc = null;

        ResourceType resourceType = null;
        ServiceSelector selector = null;
        DocumentManager docManager = null;

        try {
            docManager = (DocumentManager) manager.lookup(DocumentManager.ROLE);
            DocumentLocator loc = DocumentLocator.getLocator(pub.getId(),
                    Publication.AUTHORING_AREA, path, pub.getDefaultLanguage());

            pub.getArea(Publication.AUTHORING_AREA).getSite().getRepositoryNode().lock();

            selector = (ServiceSelector) manager.lookup(ResourceType.ROLE + "Selector");
            resourceType = (ResourceType) selector.select("resource");

            String sampleUri = resourceType.getSampleURI(resourceType.getSampleNames()[0]);
            doc = docManager.add(factory, resourceType, sampleUri, pub, Publication.AUTHORING_AREA,
                    path, pub.getDefaultLanguage(), extension, "Test Resource", true);

            ResourceWrapper resource = new ResourceWrapper(doc, manager, logger);
            resource.write(IMAGE_URL);
        } finally {
            if (docManager != null) {
                manager.release(docManager);
            }
            if (selector != null) {
                manager.release(selector);
            }
        }
        return doc;
    }

}
