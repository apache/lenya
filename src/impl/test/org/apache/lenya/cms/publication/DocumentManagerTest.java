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
package org.apache.lenya.cms.publication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.util.StringUtil;

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

            String areaName1 = "authoring";
            String areaName2 = "live";
            String path1 = "/tutorial";
            String path2 = "/doctypes";

            doTestCopyToArea(docManager, areaName1, areaName2, path1, path2);

        } finally {
            if (docManager != null) {
                getManager().release(docManager);
            }
        }
    }

    protected void doTestCopyToArea(DocumentManager docManager, String sourceAreaName,
            String destAreaName, String path1, String path2) throws PublicationException,
            SiteException {
        
        DocumentFactory factory = getFactory();
        Publication pub = factory.getPublication("test");
        SiteStructure sourceArea = pub.getArea(sourceAreaName).getSite();
        SiteStructure destArea = pub.getArea(destAreaName).getSite();

        if (destArea.contains(path1)) {
            destArea.getNode(path1).delete();
        }
        if (destArea.contains(path2)) {
            destArea.getNode(path2).delete();
        }
        
        assertFalse(destArea.contains(path1));
        assertFalse(destArea.contains(path2));
        
        // copy second node first to test correct ordering
        doTestCopyToArea(docManager, path2, sourceAreaName, destAreaName);
        doTestCopyToArea(docManager, path1, sourceAreaName, destAreaName);

        List sourceNodes = Arrays.asList(sourceArea.getNodes());

        SiteNode authoringNode1 = sourceArea.getNode(path1);
        assertTrue(sourceNodes.contains(authoringNode1));
        int sourcePos1 = sourceNodes.indexOf(authoringNode1);

        SiteNode sourceNode2 = sourceArea.getNode(path2);
        int sourcePos2 = sourceNodes.indexOf(sourceNode2);

        assertTrue(sourcePos1 < sourcePos2);

        assertTrue(destArea.contains(path1));
        List liveNodes = Arrays.asList(destArea.getNodes());
        SiteNode liveNode1 = destArea.getNode(path1);
        assertTrue(liveNodes.contains(liveNode1));
        int livePos1 = liveNodes.indexOf(liveNode1);

        SiteNode liveNode2 = destArea.getNode(path2);
        int livePos2 = liveNodes.indexOf(liveNode2);

        assertTrue(livePos1 < livePos2);
    }

    protected void doTestCopyToArea(DocumentManager docManager, String path, String areaName1,
            String areaName2) throws PublicationException {
        DocumentFactory factory = getFactory();
        Publication pub = factory.getPublication("test");
        Area area1 = pub.getArea(areaName1);
        Document doc = area1.getSite().getNode(path).getLink("en").getDocument();
        docManager.copyToArea(doc, areaName2);
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
        
        String[] sourceNames = getChildNames(sourceNode);
        SiteNode targetNode = authoring.getSite().getNode(targetPath);
        String[] targetNames = getChildNames(targetNode);
        assertEquals(StringUtil.join(sourceNames, ","), StringUtil.join(targetNames, ","));
    }

	protected String[] getChildNames(SiteNode node) {
		SiteNode[] sourceChildren = node.getChildren();
        String[] names = new String[sourceChildren.length];
        for (int i = 0; i < names.length; i++) {
        	names[i] = sourceChildren[i].getName();
        }
		return names;
	}

    protected void doTestMoveAll(DocumentManager docManager, String sourcePath, String targetPath)
            throws SiteException, DocumentException, PublicationException {
        DocumentFactory factory = getFactory();
        Publication pub = factory.getPublication("test");
        Area authoring = pub.getArea("authoring");

        SiteNode sourceNode = authoring.getSite().getNode(sourcePath);
        String[] sourceNames = getChildNames(sourceNode);

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
        
        SiteNode targetNode = authoring.getSite().getNode(targetPath);
        String[] targetNames = getChildNames(targetNode);
        assertEquals(StringUtil.join(sourceNames, ","), StringUtil.join(targetNames, ","));
        
    }
}
