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

import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteUtil;

/**
 * Document manager test.
 */
public class DocumentManagerTest extends AbstractAccessControlTest {

    /**
     * Do the test.
     * @throws Exception
     */
    public void testDocumentManager() throws Exception {
        DocumentManager docManager = null;
        try {
            docManager = (DocumentManager) getManager().lookup(DocumentManager.ROLE);
            doTestMoveAll(docManager, "/doctypes", "/tutorial/doctypes");
            doTestMoveAll(docManager, "/tutorial/doctypes", "/doctypes");
            doTestCopyAll(docManager, "/doctypes", "/tutorial/doctypes");
        } finally {
            if (docManager != null) {
                getManager().release(docManager);
            }
        }
    }

    protected void doTestCopyAll(DocumentManager docManager, String sourcePath, String targetPath)
            throws SiteException, DocumentException, PublicationException {
        DocumentFactory factory = getFactory();
        Publication pub = factory.getPublication("test");
        Area authoring = pub.getArea("authoring");

        SiteNode sourceNode = authoring.getSite().getNode(sourcePath);
        NodeSet nodes = SiteUtil.getSubSite(getManager(), sourceNode);
        Document[] docs = nodes.getDocuments();
        Map doc2path = new HashMap();

        String sourceBase = sourcePath.substring(0, sourcePath.lastIndexOf("/"));
        String targetBase = targetPath.substring(0, targetPath.lastIndexOf("/"));

        for (int i = 0; i < docs.length; i++) {
            doc2path.put(docs[i], docs[i].getPath().substring(sourceBase.length()));
        }

        docManager.copyAll(authoring, sourcePath, authoring, targetPath);
        for (int i = 0; i < docs.length; i++) {
            assertTrue(docs[i].hasLink());
            String oldPath = (String) doc2path.get(docs[i]);
            String newPath = targetBase + oldPath;
            assertTrue(authoring.getSite().contains(newPath));
            SiteNode newNode = authoring.getSite().getNode(newPath);
            Document newDoc = newNode.getLink(docs[i].getLanguage()).getDocument();
            assertEquals(newDoc.getContentLength(), docs[i].getContentLength());
            assertFalse(newDoc.getUUID().equals(docs[i].getUUID()));
        }
    }

    protected void doTestMoveAll(DocumentManager docManager, String sourcePath, String targetPath)
            throws SiteException, DocumentException, PublicationException {
        DocumentFactory factory = getFactory();
        Publication pub = factory.getPublication("test");
        Area authoring = pub.getArea("authoring");

        SiteNode sourceNode = authoring.getSite().getNode(sourcePath);
        NodeSet nodes = SiteUtil.getSubSite(getManager(), sourceNode);
        Document[] docs = nodes.getDocuments();
        Map doc2path = new HashMap();
        
        String sourceBase = sourcePath.substring(0, sourcePath.lastIndexOf("/"));
        String targetBase = targetPath.substring(0, targetPath.lastIndexOf("/"));

        for (int i = 0; i < docs.length; i++) {
            doc2path.put(docs[i], docs[i].getPath().substring(sourceBase.length()));
        }

        docManager.moveAll(authoring, sourcePath, authoring, targetPath);
        for (int i = 0; i < docs.length; i++) {
            assertTrue(docs[i].hasLink());
            String oldPath = (String) doc2path.get(docs[i]);
            String newPath = docs[i].getPath();
            assertEquals(targetBase + oldPath, newPath);
        }
    }
}
