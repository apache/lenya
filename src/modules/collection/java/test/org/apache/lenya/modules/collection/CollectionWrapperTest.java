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
package org.apache.lenya.modules.collection;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.transaction.TransactionException;

/**
 * Collection wrapper test.
 */
public class CollectionWrapperTest extends AbstractAccessControlTest {

    /**
     * @throws PublicationException
     * @throws AccessControlException
     * @throws TransactionException
     * @throws ServiceException
     */
    public void testXLinkCollection() throws PublicationException, AccessControlException,
            TransactionException, ServiceException {

        Session session = login("lenya");
        DocumentFactory map = DocumentUtil.createDocumentFactory(getManager(), session);

        Publication pub = getPublication("test");

        Document collectionDoc = createCollectionDocument(pub);

        CollectionWrapper collection = new CollectionWrapper(collectionDoc, getLogger());

        SiteStructure structure = pub.getArea("authoring").getSite();
        structure.getRepositoryNode().lock();

        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) getManager().lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(pub.getSiteManagerHint());

            siteManager.add("/collection", collection.getDelegate());
        } finally {
            selector.release(siteManager);
            getManager().release(selector);
        }

        Document doc = map.get(pub, Publication.AUTHORING_AREA, "/index", "en");
        collection.add(doc);
        collection.save();

        collection.getDelegate().getRepositoryNode().unlock();
        structure.getRepositoryNode().unlock();

        CollectionWrapper coll2 = new CollectionWrapper(collectionDoc, getLogger());

        assertSame(collection.getDelegate().getRepositoryNode(), coll2.getDelegate()
                .getRepositoryNode());

        assertEquals(coll2.size(), 1);
        assertTrue(coll2.contains(doc));

    }

    protected Document createCollectionDocument(Publication pub) throws ServiceException,
            DocumentBuildException, PublicationException {
        ServiceSelector typeSelector = null;
        ResourceType type = null;
        DocumentManager docMgr = null;
        Document doc;
        try {
            typeSelector = (ServiceSelector) getManager().lookup(ResourceType.ROLE + "Selector");
            type = (ResourceType) typeSelector.select("collection");
            docMgr = (DocumentManager) getManager().lookup(DocumentManager.ROLE);
            ResourceType.Sample sample = type.getSample(type.getSampleNames()[0]);
            doc = docMgr.add(getFactory(), type, sample.getUri(), pub, "authoring", "en", "xml");
            doc.setMimeType(sample.getMimeType());
            
        } finally {
            if (docMgr != null) {
                getManager().release(docMgr);
            }
            if (typeSelector != null) {
                getManager().release(typeSelector);
            }
        }
        return doc;
    }

}
