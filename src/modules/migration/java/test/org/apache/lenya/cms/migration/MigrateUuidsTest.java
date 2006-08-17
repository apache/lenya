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
package org.apache.lenya.cms.migration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * Migrate from path-based content to UUIDs.
 */
public class MigrateUuidsTest extends AbstractAccessControlTest {

    /**
     * Do the migration.
     * @throws Exception 
     */
    public void testMigrateUuids() throws Exception {

        login("lenya");

        Session session = RepositoryUtil.getSession(getManager(), getRequest());
        DocumentFactory factory = DocumentUtil.createDocumentFactory(getManager(), session);
        Publication[] pubs = factory.getPublications();
        for (int i = 0; i < pubs.length; i++) {
            this.migratedDocs.clear();
            migratePublication(pubs[i]);
        }
        session.commit();
    }

    private void migratePublication(Publication pub) throws Exception {
        getLogger().info("Migrating publication [" + pub.getId() + "]");

        String[] areaNames = pub.getAreaNames();
        for (int i = 0; i < areaNames.length; i++) {
            Area area = pub.getArea(areaNames[i]);
            migrateArea(area);
        }
    }

    private void migrateArea(Area area) throws Exception {
        getLogger().info("Migrating area [" + area + "]");
        Document[] docs = area.getDocuments();
        Map path2langs = new HashMap();

        for (int i = 0; i < docs.length; i++) {
            
            path2langs.put(docs[i].getPath(), docs[i].getLanguages());
            
            if (docs[i].getUUID().startsWith("/")) {
                migrateDocument(docs[i]);
            }
        }
        verifyMigration(area, path2langs);
    }

    protected void verifyMigration(Area area, Map path2langs) throws SiteException, DocumentException {
        SiteStructure site = area.getSite();
        for (Iterator i = path2langs.keySet().iterator(); i.hasNext(); ) {
            String path = (String) i.next();
            String[] langs = (String[]) path2langs.get(path);
            SiteNode node = site.getNode(path);
            Document migratedDoc = node.getLink(node.getLanguages()[0]).getDocument();
            String[] migratedLangs = migratedDoc.getLanguages();
            assertEquals(Arrays.asList(langs), Arrays.asList(migratedLangs));
        }
    }

    private Map migratedDocs = new HashMap();

    private void migrateDocument(Document doc) throws Exception {

        getLogger().info("Migrating document [" + doc + "]");

        DocumentManager docManager = null;
        SiteManager siteManager = null;
        ServiceSelector selector = null;
        SourceResolver resolver = null;
        try {
            docManager = (DocumentManager) getManager().lookup(DocumentManager.ROLE);
            selector = (ServiceSelector) getManager().lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(doc.getPublication().getSiteManagerHint());
            resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);

            String path = doc.getPath();

            Node node = siteManager.getSiteStructure(doc.getFactory(),
                    doc.getPublication(),
                    doc.getArea()).getRepositoryNode();

            if (!node.isLocked()) {
                node.lock();
            }

            Document newDoc;

            String docId = doc.getUUID();
            if (this.migratedDocs.containsKey(docId)) {
                Document migratedDoc = (Document) this.migratedDocs.get(docId);
                newDoc = docManager.addVersion(migratedDoc, doc.getArea(), doc.getLanguage(), false);
                SourceUtil.copy(resolver, doc.getSourceURI(), newDoc.getSourceURI());
            } else {
                newDoc = docManager.add(doc.getFactory(),
                        doc.getResourceType(),
                        doc.getSourceURI(),
                        doc.getPublication(),
                        doc.getArea(),
                        doc.getLanguage(),
                        doc.getExtension());

                migratedDocs.put(docId, newDoc);
                siteManager.set(path, newDoc);
            }

            String[] uris = doc.getMetaDataNamespaceUris();
            for (int i = 0; i < uris.length; i++) {
                newDoc.getMetaData(uris[i]).replaceBy(doc.getMetaData(uris[i]));
            }

            doc.delete();

        } finally {
            if (docManager != null) {
                getManager().release(docManager);
            }
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                getManager().release(selector);
            }
            if (resolver != null) {
                getManager().release(resolver);
            }
        }
    }

}
