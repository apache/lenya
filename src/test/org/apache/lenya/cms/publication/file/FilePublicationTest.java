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
package org.apache.lenya.cms.publication.file;

import org.apache.lenya.cms.PublicationHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author andreas
 *
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
        return new TestSuite(FilePublicationTest.class);
    }

    public static final String sourceDocumentId = "/tutorial";
    public static final String destinationDocumentId = "/doctypes/simple-document";
    public static final String sourceLanguage = "en";
    public static final String destinationLanguage = "en";

    /**
     * Tests copying a document. 
     * @throws PublicationException when something went wrong.
     */
    public void testCopyDocument() throws PublicationException, DocumentException, SiteTreeException {
        testCopyDocument(
            Publication.AUTHORING_AREA,
            sourceDocumentId,
            sourceLanguage,
            Publication.AUTHORING_AREA,
            destinationDocumentId,
            destinationLanguage);
        testCopyDocument(
            Publication.AUTHORING_AREA,
            sourceDocumentId,
            sourceLanguage,
            Publication.LIVE_AREA,
            sourceDocumentId,
            sourceLanguage);
    }

    /**
     * Tests copying a document.
     * @param sourceArea The source area.
     * @param sourceDocumentId The source document ID.
     * @param sourceLanguage The source language.
     * @param destinationArea The destination area.
     * @param destinationDocumentId The destination document ID.
     * @param destinationLanguage The destination language.
     * @throws PublicationException when something went wrong.
     */
    public void testCopyDocument(
        String sourceArea,
        String sourceDocumentId,
        String sourceLanguage,
        String destinationArea,
        String destinationDocumentId,
        String destinationLanguage)
        throws PublicationException, DocumentException, SiteTreeException {
            
        System.out.println("Copy document");
        System.out.println("    Source area:             [" + sourceArea + "]");
        System.out.println("    Source document ID:      [" + sourceDocumentId + "]");
        System.out.println("    Source language:         [" + sourceLanguage + "]");
        System.out.println("    Destination area:        [" + destinationArea + "]");
        System.out.println("    Destination document ID: [" + destinationDocumentId + "]");
        System.out.println("    Destination language:    [" + destinationLanguage + "]");
            
        Publication publication = PublicationHelper.getPublication();
        DocumentBuilder builder = publication.getDocumentBuilder();
        
        String sourceUrl =
            builder.buildCanonicalUrl(publication, sourceArea, sourceDocumentId, sourceLanguage);
        Document sourceDocument = builder.buildDocument(publication, sourceUrl);
        String destinationUrl =
            builder.buildCanonicalUrl(
                publication,
                destinationArea,
                destinationDocumentId,
                destinationLanguage);
        Document destinationDocument = builder.buildDocument(publication, destinationUrl);
        
        publication.copyDocument(sourceDocument, destinationDocument);
        
        assertTrue(destinationDocument.exists());
        
        SiteTree destinationTree = publication.getSiteTree(destinationArea);
        SiteTreeNode destinationNode = destinationTree.getNode(destinationDocumentId);
        assertNotNull(destinationNode);
        Label destinationLabel = destinationNode.getLabel(destinationLanguage);
        assertNotNull(destinationLabel);
        
        SiteTreeNode sourceNode = destinationTree.getNode(sourceDocumentId);
        Label sourceLabel = sourceNode.getLabel(sourceLanguage);
        
        assertTrue(destinationLabel.getLabel().equals(sourceLabel.getLabel()));
        
    }
}
