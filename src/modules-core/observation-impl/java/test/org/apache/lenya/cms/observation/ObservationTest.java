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

import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryException;
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
            testListener(doc, allListener);
            
            registry.registerListener(docListener, doc);
            Exception e = null;
            try {
                registry.registerListener(docListener, doc);
            }
            catch (ObservationException e1) {
                e = e1;
            }
            assertNotNull(e);

            testListener(doc, docListener);
            testListener(doc, allListener);
            
        }
        finally {
            if (registry != null) {
                getManager().release(registry);
            }
        }
        
        
    }

    protected void testListener(Document doc, TestListener listener) throws Exception {
        NamespaceHelper xml = new NamespaceHelper("http://apache.org/lenya/test", "", "test");
        doc.getRepositoryNode().lock();
        SourceUtil.writeDOM(xml.getDocument(), doc.getSourceURI(), getManager());
        
        assertFalse(listener.wasNotified());
        
        doc.getRepositoryNode().getSession().commit();
        
        Thread.currentThread().sleep(100);
        
        assertTrue(listener.wasNotified());
        listener.reset();
    }

}
