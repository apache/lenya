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
package org.apache.lenya.cms.publication;

import org.apache.lenya.cms.PublicationHelper;

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
public class DefaultDocumentTest extends TestCase {

    /**
     * Constructor.
     * @param test The test.
     */
    public DefaultDocumentTest(String test) {
        super(test);
    }

    /**
     * The main program.
     * The parameters are set from the command line arguments.
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
        return new TestSuite(DefaultDocumentTest.class);
    }

    protected static final DocumentTestSet[] testSets =
        {
            new DocumentTestSet("/index.html", "/index", Publication.AUTHORING_AREA, "", "html"),
            new DocumentTestSet("/index_en.htm", "/index", Publication.AUTHORING_AREA, "en", "htm")};

    /**
     * Tests a document test set.
     * @param testSet The test set.
     * @throws DocumentBuildException when something went wrong.
     */
    protected void doDocumentTest(DocumentTestSet testSet) throws DocumentBuildException {
        Document document = getDocument(testSet);
        System.out.println("ID:           " + document.getId());
        System.out.println("Area:         " + document.getArea());
        System.out.println("Language:     " + document.getLanguage());
        System.out.println("Document URL: " + document.getDocumentUrl());
        System.out.println("Complete URL: " + document.getCompleteUrl());
        System.out.println("Extension:    " + document.getExtension());

        Publication publication = PublicationHelper.getPublication();
        assertEquals(document.getPublication(), publication);
        assertEquals(document.getId(), testSet.getId());
        assertEquals(document.getArea(), testSet.getArea());
        assertEquals(document.getLanguage(), testSet.getLanguage());
        assertEquals(
            document.getDocumentUrl(), testSet.getUrl());
        assertEquals(
            document.getCompleteUrl(),
            "/" + publication.getId() + "/" + document.getArea() + testSet.getUrl());
        assertEquals(document.getExtension(), testSet.getExtension());

        System.out.println("-----------------------------------------------");
    }

    /**
     * Tests the default document. 
     * @throws DocumentBuildException when something went wrong.
     */
    public void testDefaultDocument() throws DocumentBuildException {

        for (int i = 0; i < testSets.length; i++) {
            doDocumentTest(testSets[i]);
        }
    }

    /**
     * Returns the test document for a given test set.
     * @param testSet A document test set.
     * @return A document.
     * @throws DocumentBuildException when something went wrong.
     */
    protected Document getDocument(DocumentTestSet testSet)throws DocumentBuildException {
        DefaultDocument document =
            new DefaultDocument(
                PublicationHelper.getPublication(),
                testSet.getId(),
                testSet.getArea());
        document.setLanguage(testSet.getLanguage());
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
         * @param url The url.
         * @param id The ID.
         * @param area The area.
         * @param language The language.
         * @param extension The extension.
         */
        public DocumentTestSet(
            String url,
            String id,
            String area,
            String language,
            String extension) {
            this.url = url;
            this.id = id;
            this.area = area;
            this.language = language;
            this.extension = extension;
        }

        /**
         * @return The area.
         */
        public String getArea() {
            return area;
        }

        /**
         * @return The extension.
         */
        public String getExtension() {
            return extension;
        }

        /**
         * @return The ID.
         */
        public String getId() {
            return id;
        }

        /**
         * @return The language.
         */
        public String getLanguage() {
            return language;
        }

        /**
         * @return The URL.
         */
        public String getUrl() {
            return url;
        }

    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        if (PublicationHelper.getPublication() == null) {
            String args[] = { "D:\\Development\\build\\tomcat-4.1.24\\webapps\\lenya", "test" };
            PublicationHelper.extractPublicationArguments(args);
        }
    }

}
