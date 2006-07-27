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
package org.apache.lenya.cms.lucene;

import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.xml.DocumentHelper;

public class IndexUpdaterTest extends AbstractAccessControlTest {

    public void testIndexUpdater() throws Exception {
        login("lenya");
        Session session = RepositoryUtil.getSession(getManager(), getRequest());
        DocumentFactory factory = DocumentUtil.createDocumentIdentityMap(getManager(), session);

        Publication publication = PublicationUtil.getPublication(getManager(), "test");
        Document sourceDoc = factory.get(publication, Publication.AUTHORING_AREA, "/tutorial", "en");
        Document destDoc = factory.get(publication, Publication.AUTHORING_AREA, "/concepts", "en");

        destDoc.getRepositoryNode().lock();

        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
            SourceUtil.copy(resolver, sourceDoc.getSourceURI(), destDoc.getSourceURI());
        } finally {
            if (resolver != null) {
                getManager().release(resolver);
            }
        }
        checkSearchResults(publication, Publication.AUTHORING_AREA);

    }

    protected void checkSearchResults(Publication pub, String area) throws Exception {
//        String searchUri = "cocoon://modules/lucene/search.xml?queryString=tutorial";
//        String searchUri = "cocoon://modules/lucene/search/" + pub.getId() + "/" + area + ".xml";
//        org.w3c.dom.Document searchResult = SourceUtil.readDOM(searchUri, getManager());
        
//        DocumentHelper.writeDocument(searchResult, System.out);
    }
}
