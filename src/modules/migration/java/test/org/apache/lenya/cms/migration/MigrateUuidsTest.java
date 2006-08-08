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

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteUtil;

public class MigrateUuidsTest extends AbstractAccessControlTest {

    public void testMigrateUuids() throws Exception {

        login("lenya");

        Session session = RepositoryUtil.getSession(getManager(), getRequest());
        DocumentFactory factory = DocumentUtil.createDocumentIdentityMap(getManager(), session);
        Publication[] pubs = PublicationUtil.getPublications(getManager());
        for (int i = 0; i < pubs.length; i++) {
            this.migratedDocs.clear();
            migratePublication(pubs[i], factory);
        }
        session.commit();
    }

    private void migratePublication(Publication pub, DocumentFactory factory) throws Exception {
        getLogger().info("Migrating publication [" + pub.getId() + "]");

        String[] areas = { Publication.AUTHORING_AREA, Publication.LIVE_AREA,
                Publication.ARCHIVE_AREA, Publication.TRASH_AREA };
        for (int i = 0; i < areas.length; i++) {
            migrateArea(pub, areas[i], factory);
        }
    }

    private void migrateArea(Publication pub, String area, DocumentFactory factory)
            throws Exception {
        getLogger().info("Migrating area [" + pub.getId() + ":" + area + "]");
        Document[] docs = SiteUtil.getDocuments(getManager(), factory, pub, area);
        for (int i = 0; i < docs.length; i++) {
            if (docs[i].getUUID().startsWith("/")) {
                migrateDocument(docs[i]);
            }
        }
    }

    private Map migratedDocs = new HashMap();

    private void migrateDocument(Document doc) throws Exception {

        getLogger().info("Migrating document [" + doc + "]");

        DocumentManager docManager = null;
        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            docManager = (DocumentManager) getManager().lookup(DocumentManager.ROLE);
            selector = (ServiceSelector) getManager().lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(doc.getPublication().getSiteManagerHint());

            String path = SiteUtil.getPath(getManager(), doc);

            Node node = siteManager.getSiteStructure(doc.getFactory(), doc.getPublication(),
                    doc.getArea()).getRepositoryNode();

            if (!node.isLocked()) {
                node.lock();
            }

            Document newDoc;

            String docId = doc.getUUID();
            if (this.migratedDocs.containsKey(docId)) {
                Document migratedDoc = (Document) this.migratedDocs.get(docId);
                newDoc = docManager.addVersion(migratedDoc, doc.getArea(), doc.getLanguage());
            } else {
                newDoc = docManager.add(doc.getFactory(), doc.getResourceType(),
                        doc.getSourceURI(), doc.getPublication(), doc.getArea(), doc.getLanguage(),
                        doc.getExtension(), doc.getLabel());

                String[] uris = doc.getMetaDataNamespaceUris();
                for (int i = 0; i < uris.length; i++) {
                    newDoc.getMetaData(uris[i]).replaceBy(doc.getMetaData(uris[i]));
                }

                migratedDocs.put(docId, newDoc);
                siteManager.set(path, newDoc);
            }

            doc.getRepositoryNode().lock();
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
        }
    }

}
