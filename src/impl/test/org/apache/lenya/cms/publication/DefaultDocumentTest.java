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

package org.apache.lenya.cms.publication;

import org.apache.lenya.ac.impl.AbstractAccessControlTest;

/**
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class DefaultDocumentTest extends AbstractAccessControlTest {

    protected static final DocumentTestSet[] testSets = {
            new DocumentTestSet("/index.html", "/index", Publication.AUTHORING_AREA, "en", "html"),
            new DocumentTestSet("/index_de.html", "/index", Publication.AUTHORING_AREA, "de",
                    "html") };

    /**
     * Tests a document test set.
     * @param testSet The test set.
     * @throws PublicationException
     */
    protected void doDocumentTest(DocumentTestSet testSet) throws PublicationException {
        Document document = getDocument(testSet);
        getLogger().info("UUID:         " + document.getUUID());
        getLogger().info("Area:         " + document.getArea());
        getLogger().info("Language:     " + document.getLanguage());
        getLogger().info("Document URL: " + document.getCanonicalDocumentURL());
        getLogger().info("Complete URL: " + document.getCanonicalWebappURL());
        getLogger().info("Extension:    " + document.getExtension());

        Publication publication = getPublication("test");
        assertEquals(document.getPublication(), publication);
        assertEquals(document.getPath(), testSet.getPath());
        assertEquals(document.getArea(), testSet.getArea());
        assertEquals(document.getLanguage(), testSet.getLanguage());
        assertEquals(document.getCanonicalDocumentURL(), testSet.getUrl());
        assertEquals(document.getCanonicalWebappURL(), "/" + publication.getId() + "/"
                + document.getArea() + testSet.getUrl());
        assertEquals(document.getExtension(), testSet.getExtension());

        getLogger().info("-----------------------------------------------");
    }

    /**
     * Tests the default document.
     * @throws PublicationException
     */
    public void testDefaultDocument() throws PublicationException {
        for (int i = 0; i < testSets.length; i++) {
            doDocumentTest(testSets[i]);
        }
    }

    /**
     * Returns the test document for a given test set.
     * @param testSet A document test set.
     * @return A document.
     * @throws PublicationException
     */
    protected Document getDocument(DocumentTestSet testSet) throws PublicationException {

        Publication pub = getPublication("test");
        String uuid = pub.getArea(testSet.getArea()).getSite().getNode(testSet.getPath()).getUuid();
        DocumentIdentifier id = new DocumentIdentifier(pub.getId(), testSet.getArea(), uuid, testSet
                .getLanguage());
        DocumentImpl document = new DocumentImpl(getManager(), getFactory(), id, -1, getLogger());
        document.setExtension(testSet.getExtension());

        return document;
    }

    /**
     * Utility class to store test data for a document.
     */
    protected static class DocumentTestSet {
        private String url;
        private String path;
        private String extension;
        private String area;
        private String language;

        /**
         * Ctor.
         * @param _url The url.
         * @param _path The path.
         * @param _area The area.
         * @param _language The language.
         * @param _extension The extension.
         */
        public DocumentTestSet(String _url, String _path, String _area, String _language,
                String _extension) {
            this.url = _url;
            this.path = _path;
            this.area = _area;
            this.language = _language;
            this.extension = _extension;
        }

        /**
         * @return The area.
         */
        public String getArea() {
            return this.area;
        }

        /**
         * @return The extension.
         */
        public String getExtension() {
            return this.extension;
        }

        /**
         * @return The path.
         */
        public String getPath() {
            return this.path;
        }

        /**
         * @return The language.
         */
        public String getLanguage() {
            return this.language;
        }

        /**
         * @return The URL.
         */
        public String getUrl() {
            return this.url;
        }
    }

}