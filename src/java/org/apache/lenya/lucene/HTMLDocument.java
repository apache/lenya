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

/* $Id: HTMLDocument.java,v 1.20 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lenya.lucene.html.HTMLParser;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;


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

        //HtmlDocument htmlDoc = new HtmlDocument(f);
        HTMLParser parser = new HTMLParser(f);

        // Add the summary as an UnIndexed field, so that it is stored and returned
        // with hit documents for display.
        // Add the title as a separate Text field, so that it can be searched separately.
        /*
                String title = htmlDoc.getTitle();

                if (title != null) {
                    doc.add(Field.Text("title", title));
                } else {
                    doc.add(Field.Text("title", ""));
                }
        */
        doc.add(Field.Text("title", parser.getTitle()));

        //System.out.println("HTMLDocument.getLuceneDocument(): title field added: " + title);
        // Add the tag-stripped contents as a Reader-valued Text field so it will get tokenized and indexed.
        /*
                String body = htmlDoc.getBody();
                String contents = "";

                if ((body != null) && (title != null)) {
                    contents = title + " " + body;
                    doc.add(Field.Text("contents", title + body));
                }

                doc.add(Field.Text("contents", contents));
        */
        doc.add(Field.Text("contents", parser.getReader()));

        return doc;
    }
}
