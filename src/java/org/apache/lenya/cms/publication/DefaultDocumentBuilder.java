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
        String fullExtension = "".equals(extension) ? "" : "." + extension;
        documentUrl = documentUrl.substring(0, documentUrl.length() - fullExtension.length());

        String language = getLanguage(documentUrl);
        String fullLanguage = "".equals(language) ? "" : "_" + language;
        documentUrl = documentUrl.substring(0, documentUrl.length() - fullLanguage.length());

        String documentId = documentUrl;

        if (!documentId.startsWith("/")) {
            throw new DocumentBuildException(
                "Document ID [" + documentId + "] does not start with '/'!");
        }

        DefaultDocument document = new DefaultDocument(publication, documentId, area);
        document.setLanguage(language);
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
        if (startOfLanguage > -1 && !urlWithoutSuffix.endsWith("_")) {
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
        if (startOfSuffix > -1 && !url.endsWith(".")) {
            suffix = url.substring(startOfSuffix + 1);
        }
        return suffix;
    }

}
