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
package org.apache.lenya.cms.observation;

import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.xml.NamespaceHelper;

public class ObservationTest extends AbstractAccessControlTest {
    
    public void testObservation() throws Exception {
        login("lenya");
        Session session = RepositoryUtil.getSession(getManager(), getRequest());
        DocumentFactory factory = DocumentUtil.createDocumentIdentityMap(getManager(), session);

        Publication publication = PublicationUtil.getPublication(getManager(), "test");
        Document doc = factory.get(publication, Publication.AUTHORING_AREA, "/index", "en");
        
        TestListener docListener = new TestListener();
        TestListener allListener = new TestListener();
        
        ObservationRegistry registry = null;
        try {
            registry = (ObservationRegistry) getManager().lookup(ObservationRegistry.ROLE);

            // check if it works if only the allListener is registered
            registry.registerListener(allListener);
            testChanged(doc, allListener);
            
            registry.registerListener(docListener, doc);
            Exception e = null;
            try {
                registry.registerListener(docListener, doc);
            }
            catch (ObservationException e1) {
                e = e1;
            }
            assertNotNull(e);

            testChanged(doc, docListener);
            testChanged(doc, allListener);
            
        }
        finally {
            if (registry != null) {
                getManager().release(registry);
            }
        }
        
        
    }

    protected void testChanged(Document doc, TestListener listener) throws Exception {
        listener.reset();
        NamespaceHelper xml = new NamespaceHelper("http://apache.org/lenya/test", "", "test");
        SourceUtil.writeDOM(xml.getDocument(), doc.getSourceURI(), getManager());

        String mimeType = doc.getMimeType();
        doc.setMimeType("");
        doc.setMimeType(mimeType);
        
        assertFalse(listener.wasChanged());
        doc.getRepositoryNode().getSession().commit();
        Thread.currentThread().sleep(100);
        assertTrue(listener.wasChanged());
    }

    protected void testRemoved(Document doc, TestListener listener) throws Exception {
        listener.reset();

        DocumentManager docManager = null;
        try {
            docManager = (DocumentManager) getManager().lookup(DocumentManager.ROLE);
            Document target = doc.getFactory().get(doc.getPublication(), doc.getArea(), "/testTarget", doc.getLanguage());
            docManager.move(doc, target.getLocator());

            assertFalse(listener.wasRemoved());
            doc.getRepositoryNode().getSession().commit();
            Thread.currentThread().sleep(100);
            assertTrue(listener.wasRemoved());
        
            docManager.move(target, doc.getLocator());
            assertFalse(listener.wasChanged());
            doc.getRepositoryNode().getSession().commit();
            Thread.currentThread().sleep(100);
            assertTrue(listener.wasChanged());
        }
        finally {
            if (docManager != null) {
                getManager().release(docManager);
            }
        }
        
    }

}
