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
package org.apache.lenya.cms.lucene;

import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;

public class IndexUpdaterTest extends AbstractAccessControlTest {

    public void testIndexUpdater() throws Exception {
        Session session = login("lenya");

        Publication pub = getPublication(session, "test");
        SiteStructure site = pub.getArea("authoring").getSite();
        
        SiteNode sourceNode = site.getNode("/tutorial");
        SiteNode destNode = site.getNode("/concepts");
        
        Document sourceDoc = sourceNode.getLink(sourceNode.getLanguages()[0]).getDocument();
        Document destDoc = destNode.getLink(destNode.getLanguages()[0]).getDocument();

        sourceDoc.getRepositoryNode().lock();

        DocumentManager docMgr = null;
        try {
            docMgr = (DocumentManager) getManager().lookup(DocumentManager.ROLE);
            docMgr.copy(sourceDoc, destDoc.getLocator());
        } finally {
            if (docMgr != null) {
                getManager().release(docMgr);
            }
        }
        checkSearchResults(pub, Publication.AUTHORING_AREA);

    }

    protected void checkSearchResults(Publication pub, String area) throws Exception {
//        String searchUri = "cocoon://modules/lucene/search.xml?queryString=tutorial";
//        String searchUri = "cocoon://modules/lucene/search/" + pub.getId() + "/" + area + ".xml";
//        org.w3c.dom.Document searchResult = SourceUtil.readDOM(searchUri, getManager());
        
//        DocumentHelper.writeDocument(searchResult, System.out);
    }
}
