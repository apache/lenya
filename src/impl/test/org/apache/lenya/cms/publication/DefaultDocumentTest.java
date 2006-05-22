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

package org.apache.lenya.cms.publication;

import org.apache.lenya.ac.impl.AccessControlTest;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

/**
 * 
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class DefaultDocumentTest extends AccessControlTest {
    
    protected static final DocumentTestSet[] testSets = {
            new DocumentTestSet("/index.html", "/index", Publication.AUTHORING_AREA, "en", "html"),
            new DocumentTestSet("/index_de.html",
                    "/index",
                    Publication.AUTHORING_AREA,
                    "de",
                    "html") };

    /**
     * Tests a document test set.
     * @param testSet The test set.
     * @throws PublicationException
     */
    protected void doDocumentTest(DocumentTestSet testSet) throws PublicationException {
        Document document = getDocument(testSet);
        getLogger().info("ID:           " + document.getId());
        getLogger().info("Area:         " + document.getArea());
        getLogger().info("Language:     " + document.getLanguage());
        getLogger().info("Document URL: " + document.getCanonicalDocumentURL());
        getLogger().info("Complete URL: " + document.getCanonicalWebappURL());
        getLogger().info("Extension:    " + document.getExtension());

        Publication publication = PublicationUtil.getPublication(getManager(), "test");
        assertEquals(document.getPublication(), publication);
        assertEquals(document.getId(), testSet.getId());
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

    private DocumentFactory identityMap;

    protected DocumentFactory getIdentityMap() {
        if (this.identityMap == null) {
            
            Session session;
            try {
                session = RepositoryUtil.createSession(getManager(), getIdentity());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.identityMap = DocumentUtil.createDocumentIdentityMap(getManager(), session);
        }
        return this.identityMap;
    }

    /**
     * Returns the test document for a given test set.
     * @param testSet A document test set.
     * @return A document.
     * @throws PublicationException 
     */
    protected Document getDocument(DocumentTestSet testSet) throws PublicationException {

        Publication pub = PublicationUtil.getPublication(getManager(), "test");
        DocumentIdentifier id = new DocumentIdentifier(pub,
                testSet.getArea(),
                testSet.getId(),
                testSet.getLanguage());
        DocumentImpl document = new DocumentImpl(getManager(), getIdentityMap(), id, getLogger());
        document.setDocumentURL(testSet.getUrl());
        document.setExtension(testSet.getExtension());

        return document;
    }

    /**
     * Utility class to store test data for a document.
     */
    protected static class DocumentTestSet {
        private String url;
        private String id;
        private String extension;
        private String area;
        private String language;

        /**
         * Ctor.
         * @param _url The url.
         * @param _id The ID.
         * @param _area The area.
         * @param _language The language.
         * @param _extension The extension.
         */
        public DocumentTestSet(String _url, String _id, String _area, String _language,
                String _extension) {
            this.url = _url;
            this.id = _id;
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
         * @return The ID.
         */
        public String getId() {
            return this.id;
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