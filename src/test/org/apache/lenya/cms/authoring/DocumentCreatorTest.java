/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.authoring;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.xml.sax.SAXException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author andreas
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
     * @throws ParserConfigurationException when something went wrong.
     * @throws SAXException when something went wrong.
     * @throws IOException when something went wrong.
     */
    public void testCreator() throws CreatorException, ParserConfigurationException, SAXException, IOException {
        
        Publication publication = PublicationHelper.getPublication();
        DocumentCreator creator = new DocumentCreator();
        File authoringDirectory = new  File(publication.getDirectory(), AUTHORING_DIR);

        creator.create(
                publication,
                authoringDirectory,
                TREE_FILE,
                PARENT_ID,
                CHILD_ID,
                CHILD_NAME,
                CHILD_TYPE,
                DOCUMENT_TYPE);
                
        File documentFile = new File(authoringDirectory, CREATED_FILE);
        assertTrue(documentFile.exists());
        System.out.println("File was created: " + documentFile.getAbsolutePath());
        
        File sitetreeFile = new File(authoringDirectory, TREE_FILE);
        
        SiteTree sitetree = new DefaultSiteTree(sitetreeFile);
        SiteTreeNode node = sitetree.getNode(PARENT_ID + "/" + CHILD_ID);
        assertNotNull(node);
        System.out.println("Sitetree node was created: " + node.getId() + " (label: " + node.getLabel("de") + ")");
        
    }
    
    protected static final String AUTHORING_DIR = "content" + File.separator + "authoring"; 
    protected static final String TREE_FILE = "sitetree.xml";
    protected static final String PARENT_ID = "/tutorial";
    protected static final String CHILD_ID = "test-document"; 
    protected static final String CHILD_NAME = "Test Document";
    protected static final String CHILD_TYPE = "leaf";
    protected static final String DOCUMENT_TYPE = "simple"; 
    protected static final String CREATED_FILE = "tutorial/test-document/index.xml"; 
    
    /** @see junit.framework.TestCase#setUp() */
    protected void setUp() throws Exception {
        if (PublicationHelper.getPublication() == null) {
            String args[] = {
                "D:\\Development\\build\\tomcat-4.1.24\\webapps\\lenya",
                "test"
            };
            PublicationHelper.extractPublicationArguments(args);
        }
    }
}
