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
package org.apache.lenya.cms.metadata;

import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

/**
 * Meta data test.
 */
public class MetaDataTest extends AbstractAccessControlTest {

    /**
     * Tests the meta data.
     * @throws Exception
     */
    public void testMetaData() throws Exception {
        Session session = RepositoryUtil.getSession(getManager(), getRequest());
        DocumentFactory factory = DocumentUtil.createDocumentFactory(getManager(), session);

        Publication publication = getPublication("test");
        Document doc = factory.get(publication, Publication.AUTHORING_AREA, "/index", "en");

        String namespaceUri = "foobar";
        Exception e = null;
        try {
            doc.getMetaData(namespaceUri);
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull(e);

        namespaceUri = DublinCore.DC_NAMESPACE;
        MetaData dc = doc.getMetaData(namespaceUri);
        
        doc.getRepositoryNode().lock();
        
        checkSetTitle(dc);
        checkRemoveAllValues(dc);
        
    }

    protected void checkSetTitle(MetaData dc) throws MetaDataException {
        Exception e = null;
        try {
            dc.setValue("foo", "bar");
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull(e);
        dc.setValue("title", "This is the title");
        
        e = null;
        // addValue() should throw an exception because a value is already set
        try {
            dc.addValue("title", "bar");
        } catch (Exception e1) {
            e = e1;
        }
        assertNotNull(e);
        
    }

    protected void checkRemoveAllValues(MetaData dc) throws MetaDataException {
        dc.removeAllValues("title");
        assertTrue(dc.getValues("title").length == 0);
    }
    
}
