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
package org.apache.lenya.cms.site;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.simple.DocumentStore;

public class SimpleSiteManagerTest extends AbstractAccessControlTest {

    protected static final String PATH = "/foo/bar";

    protected static final String PARENT_PATH = "/foo";

    public void testSimpleSiteManager() throws Exception {

        Session session = login("lenya");

        DocumentFactory factory = DocumentUtil.createDocumentFactory(getManager(), session);
        Publication[] pubs = factory.getPublications();

        for (int i = 0; i < pubs.length; i++) {
            checkPublication(session, factory, pubs[i]);
        }
    }

    protected void checkPublication(Session session, DocumentFactory factory, Publication pub)
            throws ServiceException, SiteException, DocumentBuildException, PublicationException,
            RepositoryException {
        DocumentManager docManager = null;
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        ServiceSelector resourceTypeSelector = null;

        try {
            selector = (ServiceSelector) getManager().lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(pub.getSiteManagerHint());
            SiteStructure structure = siteManager.getSiteStructure(factory, pub,
                    Publication.AUTHORING_AREA);

            docManager = (DocumentManager) getManager().lookup(DocumentManager.ROLE);

            resourceTypeSelector = (ServiceSelector) getManager().lookup(
                    ResourceType.ROLE + "Selector");
            ResourceType type = (ResourceType) resourceTypeSelector.select("entry");
            String contentSourceUri = "context://sitemap.xmap";

            Document doc = docManager.add(factory, type, contentSourceUri, pub,
                    Publication.AUTHORING_AREA, "en", "xml");

            structure.add(PATH, doc);
            assertTrue(structure.contains(PATH));
            Document linkDoc = structure.getNode(PATH).getLink("en").getDocument();
            assertSame(linkDoc, doc);

            if (!(structure instanceof DocumentStore)) {
                Link link = doc.getLink();
                checkSetLabel(link);
            }

            SiteNode[] nodes = structure.getNodes();
            assertTrue(nodes.length > 0);

            for (int i = 0; i < nodes.length; i++) {

                assertTrue(structure.contains(nodes[i].getPath()));

                SiteNode node = structure.getNode(nodes[i].getPath());
                assertNotNull(node);
                assertEquals(nodes[i], node);

                checkLinks(siteManager, node);
            }
            
            doc.getLink().delete();
            assertFalse(structure.containsByUuid(doc.getUUID(), doc.getLanguage()));
            assertFalse(structure.contains(PATH));
            assertFalse(structure.contains(PARENT_PATH));
            
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                getManager().release(selector);
            }
            if (docManager != null) {
                getManager().release(docManager);
            }
            if (resourceTypeSelector != null) {
                getManager().release(resourceTypeSelector);
            }
        }
        // session.commit();
    }

    protected void checkSetLabel(Link link) {
        String newLabel = "New Label";
        String oldLabel = link.getLabel();
        assertFalse(oldLabel.equals(newLabel));
        link.setLabel(newLabel);
        assertTrue(link.getLabel().equals(newLabel));
        link.setLabel(oldLabel);
    }

    protected void checkLinks(SiteManager siteManager, SiteNode node) throws SiteException {
        String[] languages = node.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            Link link = node.getLink(languages[i]);
            assertEquals(link.getLanguage(), languages[i]);
            assertNotNull(link.getLabel());
            
            if (node.getUuid() != null) {
                Document doc = link.getDocument();
                assertNotNull(doc);
    
                String docUuid = doc.getUUID();
                String nodeUuid = node.getUuid();
    
                assertNotNull(doc.getUUID());
                assertEquals(docUuid, nodeUuid);
                assertEquals(doc.getLanguage(), link.getLanguage());
    
                // it may not be allowed to insert the doc twice
                try {
                    siteManager.add("/sidebar", doc);
                    assertTrue("No exception thrown", false);
                } catch (Exception expected) {
                }
            }
        }
    }

}
