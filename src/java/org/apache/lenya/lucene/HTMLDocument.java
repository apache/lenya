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
package org.apache.lenya.lucene;

import org.apache.lenya.lucene.html.HTMLParser;

import org.apache.lucene.document.*;

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

        //System.out.println("HTMLDocument.getLuceneDocument(): contents field added: " + contents);
        return doc;
    }
}
