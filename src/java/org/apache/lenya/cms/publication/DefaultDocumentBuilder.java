/*
$Id: DefaultDocumentBuilder.java,v 1.6 2003/07/29 14:23:10 andreas Exp $
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


/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DefaultDocumentBuilder implements DocumentBuilder {
    /**
     * Non-public constructor.
     */
    protected DefaultDocumentBuilder() {
    }

    private static DefaultDocumentBuilder instance;

    /**
     * Returns the singleton instance.
     * @return A document builder.
     */
    public static DefaultDocumentBuilder getInstance() {
        if (instance == null) {
            instance = new DefaultDocumentBuilder();
        }

        return instance;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentBuilder#buildDocument(org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public Document buildDocument(Publication publication, String url)
        throws DocumentBuildException {
        String publicationURI = url.substring(("/" + publication.getId()).length());

        String area = publicationURI.split("/")[1];

        String documentUrl = publicationURI.substring(("/" + area).length());

        String extension = getExtension(documentUrl);
        String fullExtension = "".equals(extension) ? "" : ("." + extension);
        documentUrl = documentUrl.substring(0, documentUrl.length() - fullExtension.length());

        String language = getLanguage(documentUrl);
        String fullLanguage = "".equals(language) ? "" : ("_" + language);
        documentUrl = documentUrl.substring(0, documentUrl.length() - fullLanguage.length());

        if ("".equals(language)) {
            language = publication.getDefaultLanguage();
        }


        String documentId = documentUrl;

        if (!documentId.startsWith("/")) {
            throw new DocumentBuildException("Document ID [" + documentId +
                "] does not start with '/'!");
        }

        DefaultDocument document = new DefaultDocument(publication, documentId, area, language);
        document.setExtension(extension);

        return document;
    }

    /**
     * Returns the language of a URL.
     * @param urlWithoutSuffix The URL without the suffix.
     * @return A string.
     */
    protected String getLanguage(String urlWithoutSuffix) {
        int startOfLanguage = urlWithoutSuffix.lastIndexOf('_');
        String suffix = "";

        if ((startOfLanguage > -1) && !urlWithoutSuffix.endsWith("_")) {
            suffix = urlWithoutSuffix.substring(startOfLanguage + 1);
        }

        return suffix;
    }

    /**
     * Returns the extension of a URL.
     * @param url The URL.
     * @return The extension.
     */
    protected String getExtension(String url) {
        int startOfSuffix = url.lastIndexOf('.');
        String suffix = "";

        if ((startOfSuffix > -1) && !url.endsWith(".")) {
            suffix = url.substring(startOfSuffix + 1);
        }

        return suffix;
    }

    /**
     * @see org.apache.lenya.cms.publication.DocumentBuilder#isDocument(org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public boolean isDocument(Publication publication, String url) throws DocumentBuildException {
        boolean isDocument = true;
        
        String publicationURI = url.substring(("/" + publication.getId()).length());
        String area = publicationURI.split("/")[1];
        String documentUrl = publicationURI.substring(("/" + area).length());
        
        if (!documentUrl.startsWith("/")) {
            isDocument = false;
        }

        return isDocument;
    }
}
