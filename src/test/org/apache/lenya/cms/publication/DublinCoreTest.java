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

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.metadata.dublincore.DublinCore;

/**
 * Dublin Core test.
 * 
 * @version $Id$
 */
public class DublinCoreTest extends TestCase {

    private static final String AREA = "authoring";
    private static final String DOCUMENT_ID = "/tutorial";
    private static final String LANGUAGE = "de";
    private static final String CREATOR = "test";

    /**
     * Constructor for DublinCoreTest.
     * @param arg0 a test
     */
    public DublinCoreTest(String arg0) {
        super(arg0);
    }

    /**
     * The main program. The parameters are set from the command line arguments.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        args = PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(DublinCoreTest.class);
    }

    /**
     * Test the fetching, modification and refetching of a dc core object.
     * 
     * @throws DocumentBuildException if an error occurs
     * @throws DocumentException if an error occurs
     */
    final public void testModifySaveAndReload() throws DocumentBuildException, DocumentException {
        Publication publication = PublicationHelper.getPublication();
        DocumentIdentityMap map = new DocumentIdentityMap();
        Document doc = map.get(publication, AREA, DOCUMENT_ID, LANGUAGE);
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
    }

    /** @see junit.framework.TestCase#setUp() */
    protected void setUp() throws Exception {
        if (PublicationHelper.getPublication() == null) {
            String[] args = { "/home/egli/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya",
                    "test" };
            PublicationHelper.extractPublicationArguments(args);
        }
    }
}
