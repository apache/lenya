/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.publication;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

import org.apache.lenya.cms.PublicationHelper;


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

    protected static final DocumentTestSet[] testSets = {
        new DocumentTestSet("/index.html", "/index", Publication.AUTHORING_AREA, "en", "html"),
        new DocumentTestSet("/index_en.htm", "/index", Publication.AUTHORING_AREA, "en", "htm"),
        new DocumentTestSet("/index_de.html", "/index", Publication.AUTHORING_AREA, "de", "html")
    };

    /**
     * Tests a document test set.
     * @param testSet The test set.
     * @throws DocumentBuildException when something went wrong.
     */
    protected void doDocumentTest(DocumentTestSet testSet)
        throws DocumentBuildException {
        Document document = getDocument(testSet);
        System.out.println("ID:           " + document.getId());
        System.out.println("Area:         " + document.getArea());
        System.out.println("Language:     " + document.getLanguage());
        System.out.println("Document URL: " + document.getDocumentURL());
        System.out.println("Complete URL: " + document.getCompleteURL());
        System.out.println("Extension:    " + document.getExtension());

        Publication publication = PublicationHelper.getPublication();
        assertEquals(document.getPublication(), publication);
        assertEquals(document.getId(), testSet.getId());
        assertEquals(document.getArea(), testSet.getArea());
        assertEquals(document.getLanguage(), testSet.getLanguage());
        assertEquals(document.getDocumentURL(), testSet.getUrl());
        assertEquals(document.getCompleteURL(),
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
    protected Document getDocument(DocumentTestSet testSet)
        throws DocumentBuildException {
        DefaultDocument document = new DefaultDocument(PublicationHelper.getPublication(),
                testSet.getId(), testSet.getArea());
        document.setDocumentURL(testSet.getUrl());
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
        public DocumentTestSet(String url, String id, String area, String language, String extension) {
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
            String[] args = { "D:\\Development\\build\\tomcat-4.1.24\\webapps\\lenya", "test" };
            PublicationHelper.extractPublicationArguments(args);
        }
    }
}
