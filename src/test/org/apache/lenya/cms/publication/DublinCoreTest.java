/*
$Id: DublinCoreTest.java,v 1.1 2003/08/28 09:56:22 egli Exp $
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

import org.apache.lenya.cms.PublicationHelper;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * 
 * @author egli
 * 
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
     * The main program.
     * The parameters are set from the command line arguments.
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
    final public void testModifySaveAndReload()
        throws DocumentBuildException, DocumentException {
        Publication publication = PublicationHelper.getPublication();
        DocumentBuilder builder = new DefaultDocumentBuilder();
        Document doc =
            builder.buildDocument(
                publication,
                builder.buildCanonicalUrl(
                    publication,
                    AREA,
                    DOCUMENT_ID,
                    LANGUAGE));
        DublinCore dcCore = doc.getDublinCore();
        String title = dcCore.getTitle();
        String subject = dcCore.getSubject();
        String creator = dcCore.getCreator();
        String dateIssued = dcCore.getDateIssued();

        dcCore.setCreator(CREATOR);
        dcCore.save();

        Document doc2 =
            builder.buildDocument(
                publication,
                builder.buildCanonicalUrl(
                    publication,
                    AREA,
                    DOCUMENT_ID,
                    LANGUAGE));

        DublinCore dcCore2 = doc2.getDublinCore();
        assertEquals(title, dcCore2.getTitle());
        assertEquals(subject, dcCore2.getSubject());
        assertEquals(dateIssued, dcCore2.getDateIssued());
        assertFalse(creator.equals(dcCore2.getCreator()));
        assertEquals(CREATOR, dcCore2.getCreator());
    }

    /** @see junit.framework.TestCase#setUp() */
    protected void setUp() throws Exception {
        if (PublicationHelper.getPublication() == null) {
            String[] args =
                {
                    "/home/egli/build/jakarta-tomcat-4.1.21-LE-jdk14/webapps/lenya",
                    "test" };
            PublicationHelper.extractPublicationArguments(args);
        }
    }
}
