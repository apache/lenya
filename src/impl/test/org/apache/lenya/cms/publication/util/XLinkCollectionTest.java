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
package org.apache.lenya.cms.publication.util;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentifier;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.transaction.TransactionException;

/**
 * XLink collection teest.
 */
public class XLinkCollectionTest extends AbstractAccessControlTest {

    public void testXLinkCollection() throws PublicationException, AccessControlException,
            TransactionException, ServiceException {

        Session session = RepositoryUtil.createSession(getManager(), getIdentity());
        DocumentFactory map = DocumentUtil.createDocumentFactory(getManager(), session);

        Publication pub = getPublication("test");
        DocumentIdentifier identifier = new DocumentIdentifier(pub, Publication.AUTHORING_AREA,
                "12345", "en");
        XlinkCollection collection = new XlinkCollection(getManager(), map, identifier, getLogger());

        SiteStructure structure = pub.getArea(identifier.getArea()).getSite();
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

        collection.getDelegate().getRepositoryNode().lock();
        collection.add(doc);

        collection.save();

        collection.getDelegate().getRepositoryNode().unlock();
        structure.getRepositoryNode().unlock();

        Collection coll2 = new XlinkCollection(getManager(), map, identifier, getLogger());

        assertSame(collection.getDelegate().getRepositoryNode(), coll2.getDelegate()
                .getRepositoryNode());

        assertEquals(collection.getDelegate().getSourceURI(), coll2.getDelegate().getSourceURI());
        assertEquals(coll2.size(), 1);
        assertTrue(coll2.contains(doc));

    }

}
