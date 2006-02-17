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

import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryTestCase;

/**
 * Dublin Core test.
 * 
 * @version $Id$
 */
public class DublinCoreTest extends RepositoryTestCase {

    private static final String AREA = "authoring";
    private static final String DOCUMENT_ID = "/tutorial";
    private static final String LANGUAGE = "de";
    private static final String CREATOR = "test";

    /**
     * Test the fetching, modification and refetching of a dc core object.
     * @throws PublicationException 
     * @throws RepositoryException 
     */
    final public void testModifySaveAndReload() throws PublicationException, RepositoryException {
        Publication publication = PublicationUtil.getPublication(getManager(), "test");
        
        DocumentIdentityMap map = getIdentityMap();
        Document doc = map.get(publication, AREA, DOCUMENT_ID, LANGUAGE);
        
        doc.getRepositoryNode().lock();
        
        MetaData dcCore = doc.getMetaDataManager().getDublinCoreMetaData();
        String title = dcCore.getFirstValue(DublinCore.ELEMENT_TITLE);
        String subject = dcCore.getFirstValue(DublinCore.ELEMENT_SUBJECT);
        String creator = dcCore.getFirstValue(DublinCore.ELEMENT_CREATOR);
        String dateIssued = dcCore.getFirstValue(DublinCore.TERM_ISSUED);

        dcCore.setValue(DublinCore.ELEMENT_CREATOR, CREATOR);
        dcCore.save();

        Document doc2 = map.get(publication, AREA, DOCUMENT_ID, LANGUAGE);

        MetaData dcCore2 = doc2.getMetaDataManager().getDublinCoreMetaData();
        assertEquals(title, dcCore2.getFirstValue(DublinCore.ELEMENT_TITLE));
        assertEquals(subject, dcCore2.getFirstValue(DublinCore.ELEMENT_SUBJECT));
        assertEquals(dateIssued, dcCore2.getFirstValue(DublinCore.TERM_ISSUED));
        assertFalse(creator.equals(dcCore2.getFirstValue(DublinCore.ELEMENT_CREATOR)));
        assertEquals(CREATOR, dcCore2.getFirstValue(DublinCore.ELEMENT_CREATOR));
        
        doc.getRepositoryNode().unlock();
    }

}
