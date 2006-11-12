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
package org.apache.lenya.cms.site.usecases;

import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.site.SiteUtil;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;

/**
 * Link rewriter test.
 */
public class LinkRewriterTest extends AbstractAccessControlTest {

    protected static final String DOCUMENT_ID = "/index";
    protected static final String SOURCE_DOCUMENT_ID = "/concepts";
    protected static final String TARGET_DOCUMENT_ID = "/copied";

    /**
     * Test method.
     * @throws Exception
     */
    public void testLinkRewriter() throws Exception {
/*
        Publication pub = PublicationUtil.getPublication(getManager(), "test");

        Document document = getIdentityMap().get(pub, Publication.AUTHORING_AREA, DOCUMENT_ID, "en");
        org.w3c.dom.Document xml = DocumentHelper.readDocument(getClass().getResourceAsStream("index_en.xml"));
        document.getRepositoryNode().lock();
        SourceUtil.writeDOM(xml, document.getSourceURI(), getManager());

        LinkRewriter rewriter = null;
        DocumentManager docManager = null;
        Document target = null;
        try {
            docManager = (DocumentManager) getManager().lookup(DocumentManager.ROLE);

            Document source = getIdentityMap().get(pub,
                    Publication.AUTHORING_AREA,
                    SOURCE_DOCUMENT_ID,
                    "en");
            source.getRepositoryNode().lock();

            DocumentLocator targetLoc = DocumentLocator.getLocator(pub.getId(),
                    Publication.AUTHORING_AREA,
                    TARGET_DOCUMENT_ID,
                    "en");

            SiteUtil.getSiteStructure(getManager(),
                    source.getFactory(),
                    source.getPublication(),
                    source.getArea()).getRepositoryNode().lock();

            docManager.move(source, targetLoc);
            target = source.getFactory().get(targetLoc);

            rewriter = (LinkRewriter) getManager().lookup(LinkRewriter.ROLE);
            rewriter.rewriteLinks(source, target);

        } finally {
            if (docManager != null) {
                getManager().release(docManager);
            }
            if (rewriter != null) {
                getManager().release(rewriter);
            }
        }

        String[] xPaths = document.getResourceType().getLinkAttributeXPaths();
        assertTrue(xPaths.length > 0);

        org.w3c.dom.Document xmlDoc = SourceUtil.readDOM(document.getSourceURI(), getManager());
        boolean matched = false;

        for (int i = 0; i < xPaths.length; i++) {
            NodeList nodes = XPathAPI.selectNodeList(xmlDoc, xPaths[i]);
            for (int nodeIndex = 0; nodeIndex < nodes.getLength(); nodeIndex++) {
                Attr attribute = (Attr) nodes.item(nodeIndex);
                String targetUrl = attribute.getValue();
                Document targetDoc = getIdentityMap().getFromURL(targetUrl);
                if (targetDoc.equals(target)) {
                    matched = true;
                }
            }
        }
        assertTrue(matched);
        */
    }

}
