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

/* $Id$  */

package org.apache.lenya.cms.publication.file;

import org.apache.lenya.ac.impl.AbstractAccessControlTest;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.site.SiteException;

/**
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FilePublicationTest extends AbstractAccessControlTest {

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

        getLogger().info("Copy document");
        getLogger().info("    Source area:             [" + sourceArea + "]");
        getLogger().info("    Source document ID:      [" + _sourceDocumentId + "]");
        getLogger().info("    Source language:         [" + _sourceLanguage + "]");
        getLogger().info("    Destination area:        [" + destinationArea + "]");
        getLogger().info("    Destination document ID: [" + _destinationDocumentId + "]");
        getLogger().info("    Destination language:    [" + _destinationLanguage + "]");

        Publication publication = getPublication("test");
        DocumentFactory map = getFactory();

        Document sourceDocument = map.get(publication,
                sourceArea,
                _sourceDocumentId,
                _sourceLanguage);
        Document destinationDocument = map.get(publication,
                destinationArea,
                _destinationDocumentId,
                _destinationLanguage);
/*
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
*/
    }
}