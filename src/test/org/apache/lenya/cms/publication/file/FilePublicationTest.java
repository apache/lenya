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

/* $Id$  */

package org.apache.lenya.cms.publication.file;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.apache.lenya.cms.site.tree.TreeSiteManager;

/**
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FilePublicationTest extends TestCase {

    /**
     * Constructor.
     * @param test The test.
     */
    public FilePublicationTest(String test) {
        super(test);
    }

    /**
     * The main program. The parameters are set from the command line arguments.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        PublicationHelper.extractPublicationArguments(args);
        TestRunner.run(getSuite());
    }

    /**
     * Returns the test suite.
     * @return A test suite.
     */
    public static Test getSuite() {
        return new TestSuite(FilePublicationTest.class);
    }

    /**
     * <code>sourceDocumentId</code> The source document id
     */
    public static final String sourceDocumentId = "/tutorial";
    /**
     * <code>destinationDocumentId</code> The destination document id
     */
    public static final String destinationDocumentId = "/doctypes/simple-document";
    /**
     * <code>sourceLanguage</code> The source language
     */
    public static final String sourceLanguage = "en";
    /**
     * <code>destinationLanguage</code> The destination language
     */
    public static final String destinationLanguage = "en";

    /**
     * Tests copying a document.
     * @throws PublicationException when something went wrong.
     * @throws DocumentException
     * @throws SiteException
     */
    public void testCopyDocument() throws PublicationException, DocumentException, SiteException {
        testCopyDocument(Publication.AUTHORING_AREA,
                sourceDocumentId,
                sourceLanguage,
                Publication.AUTHORING_AREA,
                destinationDocumentId,
                destinationLanguage);
        testCopyDocument(Publication.AUTHORING_AREA,
                sourceDocumentId,
                sourceLanguage,
                Publication.LIVE_AREA,
                sourceDocumentId,
                sourceLanguage);
    }

    /**
     * Tests copying a document.
     * @param sourceArea The source area.
     * @param _sourceDocumentId The source document ID.
     * @param _sourceLanguage The source language.
     * @param destinationArea The destination area.
     * @param _destinationDocumentId The destination document ID.
     * @param _destinationLanguage The destination language.
     * @throws PublicationException when something went wrong.
     * @throws DocumentException
     * @throws SiteException
     */
    public void testCopyDocument(String sourceArea, String _sourceDocumentId,
            String _sourceLanguage, String destinationArea, String _destinationDocumentId,
            String _destinationLanguage) throws PublicationException, DocumentException,
            SiteException {

        System.out.println("Copy document");
        System.out.println("    Source area:             [" + sourceArea + "]");
        System.out.println("    Source document ID:      [" + _sourceDocumentId + "]");
        System.out.println("    Source language:         [" + _sourceLanguage + "]");
        System.out.println("    Destination area:        [" + destinationArea + "]");
        System.out.println("    Destination document ID: [" + _destinationDocumentId + "]");
        System.out.println("    Destination language:    [" + _destinationLanguage + "]");

        Publication publication = PublicationHelper.getPublication();
        DocumentIdentityMap map = new DocumentIdentityMap();

        Document sourceDocument = map.getFactory().get(publication,
                sourceArea,
                _sourceDocumentId,
                _sourceLanguage);
        Document destinationDocument = map.getFactory().get(publication,
                destinationArea,
                _destinationDocumentId,
                _destinationLanguage);

        publication.copyDocument(sourceDocument, destinationDocument);

        assertTrue(destinationDocument.exists());

        TreeSiteManager manager = (TreeSiteManager) publication.getSiteManager(map);
        SiteTree destinationTree = manager.getTree(destinationArea);
        SiteTreeNode destinationNode = destinationTree.getNode(_destinationDocumentId);
        assertNotNull(destinationNode);
        Label destinationLabel = destinationNode.getLabel(_destinationLanguage);
        assertNotNull(destinationLabel);

        SiteTreeNode sourceNode = destinationTree.getNode(_sourceDocumentId);
        Label sourceLabel = sourceNode.getLabel(_sourceLanguage);

        assertTrue(destinationLabel.getLabel().equals(sourceLabel.getLabel()));

    }
}