/*
 * $Id: HTMLDocument.java,v 1.9 2003/02/17 13:06:57 egli Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.wyona.lucene;

import org.apache.lucene.document.*;

import org.wyona.lucene.html.HTMLParser;
import org.wyona.lucene.html.HtmlDocument;

import java.io.*;


/**
 * A utility for making Lucene Documents for HTML documents.
 */
public class HTMLDocument {
    static char dirSep = System.getProperty("file.separator").charAt(0);

    private HTMLDocument() {
    }

    /**
     * Append path and date into a string in such a way that lexicographic sorting gives the same
     * results as a walk of the file hierarchy.  Thus null (\u0000) is used both to separate
     * directory components and to separate the path from the date.
     *
     * @param f DOCUMENT ME!
     * @param htdocsDumpDir DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String uid(File f, File htdocsDumpDir) {
        String requestURI = f.getPath().substring(htdocsDumpDir.getPath().length());
        String uid = requestURI.replace(dirSep, '\u0000') + "\u0000" +
            DateField.timeToString(f.lastModified());

        return uid;
    }

    /**
     * DOCUMENT ME!
     *
     * @param uid DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String uid2url(String uid) {
        String url = uid.replace('\u0000', '/'); // replace nulls with slashes

        return url.substring(0, url.lastIndexOf('/')); // remove date from end
    }

    /**
     * DOCUMENT ME!
     *
     * @param f DOCUMENT ME!
     * @param htdocsDumpDir DOCUMENT ME!
     *
     * @return org.apache.lucene.document.Document
     *
     * @throws IOException DOCUMENT ME!
     * @throws InterruptedException DOCUMENT ME!
     */
    public static Document Document(File f, File htdocsDumpDir)
        throws IOException, InterruptedException {
        System.out.println("HTMLDocument.Document(File,File): " + f);

        // make a new, empty document
        Document doc = new Document();

        // Add the url as a field named "url".  Use an UnIndexed field, so
        // that the url is just stored with the document, but is not searchable.
        String requestURI = f.getPath().replace(dirSep, '/').substring(htdocsDumpDir.getPath()
                                                                                    .length());

        //System.out.println(requestURI);
        if (requestURI.substring(requestURI.length() - 8).equals(".pdf.txt")) {
            requestURI = requestURI.substring(0, requestURI.length() - 4); // Remove .txt extension from PDF text file

        }

        doc.add(Field.UnIndexed("url", requestURI));

        // Add the mime-type as a field named "mime-type"
        if (requestURI.substring(requestURI.length() - 5).equals(".html")) {
            doc.add(Field.UnIndexed("mime-type", "text/html"));
        } else if (requestURI.substring(requestURI.length() - 4).equals(".txt")) {
            doc.add(Field.UnIndexed("mime-type", "text/plain"));
        } else if (requestURI.substring(requestURI.length() - 4).equals(".pdf")) {
            doc.add(Field.UnIndexed("mime-type", "application/pdf"));
        } else {
            doc.add(Field.UnIndexed("mime-type", "null"));
        }

        // Add the last modified date of the file a field named "modified".  Use a
        // Keyword field, so that it's searchable, but so that no attempt is made
        // to tokenize the field into words.
        doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with document, it is indexed, but it is not
        // tokenized prior to indexing.
        doc.add(new Field("uid", uid(f, htdocsDumpDir), false, true, false));

        HTMLParser parser = new HTMLParser(f);
        HtmlDocument htmlDoc = new HtmlDocument(f);

        // Add the summary as an UnIndexed field, so that it is stored and returned
        // with hit documents for display.

        // Add the title as a separate Text field, so that it can be searched separately.
        String title = htmlDoc.getTitle();

        if (title != null) {
            doc.add(Field.Text("title", title));
        } else {
            doc.add(Field.Text("title", ""));
        }

        System.out.println("HTMLDocument.getLuceneDocument(): title field added: " + title);

        // Add the tag-stripped contents as a Reader-valued Text field so it will
        // get tokenized and indexed.
        String body = htmlDoc.getBody();
        String contents = "";

        if ((body != null) && (title != null)) {
            contents = title + " " + body;
            doc.add(Field.Text("contents", title + body));
        }

        doc.add(Field.Text("contents", contents));

        System.out.println("HTMLDocument.getLuceneDocument(): contents field added: " + contents);

        return doc;
    }
}
