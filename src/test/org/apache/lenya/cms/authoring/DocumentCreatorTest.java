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

/* $Id: DocumentCreatorTest.java,v 1.8 2004/03/04 15:41:09 egli Exp $  */

package org.apache.lenya.cms.authoring;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;


/**
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DocumentCreatorTest extends TestCase {
    /**
     * Constructor.
     * @param test The test to invoke.
     */
    public DocumentCreatorTest(String test) {
        super(test);
    }

    /**
     * The main program.
     * The parameters are set from the command line arguments.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        args = PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(getSuite());
    }

    /**
     * Returns the test suite.
     * @return A Test object.
     */
    public static Test getSuite() {
        return new TestSuite(DocumentCreatorTest.class);
    }

    /**
     * Tests whatever you want.
     * @throws CreatorException when something went wrong.
     * @throws SiteTreeException when something went wrong.
     */
    public void testCreator()
        throws CreatorException, SiteTreeException {
        Publication publication = PublicationHelper.getPublication();
        DocumentCreator creator = new DocumentCreator();
        File authoringDirectory = new File(publication.getDirectory(), AUTHORING_DIR);

        creator.create(publication, authoringDirectory, AREA, PARENT_ID, CHILD_ID, CHILD_NAME,
            CHILD_TYPE, DOCUMENT_TYPE, DOCUMENT_LANGUAGE);

        File documentFile = new File(authoringDirectory, CREATED_FILE);
        assertTrue(documentFile.exists());
        System.out.println("File was created: " + documentFile.getAbsolutePath());

        SiteTree sitetree = publication.getSiteTree(AREA);
        SiteTreeNode node = sitetree.getNode(PARENT_ID + "/" + CHILD_ID);
        assertNotNull(node);
        System.out.println("Sitetree node was created: " + node.getId() + " (label: " +
            node.getLabel(DOCUMENT_LANGUAGE) + ")");
    }

    protected static final String AUTHORING_DIR = "content" + File.separator + "authoring";
    protected static final String PARENT_ID = "/tutorial";
    protected static final String CHILD_ID = "test-document";
    protected static final String CHILD_NAME = "Test Document";
    protected static final String CHILD_TYPE = "leaf";
    protected static final String DOCUMENT_TYPE = "simple";
    protected static final String CREATED_FILE = "tutorial/test-document/index_en.xml";
    protected static final String DOCUMENT_LANGUAGE = "en";
    protected static final String AREA = "authoring";

    /** @see junit.framework.TestCase#setUp() */
    protected void setUp() throws Exception {
        if (PublicationHelper.getPublication() == null) {
            String[] args = { "/home/egli/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya", "test" };
            PublicationHelper.extractPublicationArguments(args);
        }
    }
}
