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

/* $Id: AbstractDocumentCreator.java,v 1.7 2004/03/01 16:18:16 gregor Exp $  */

package org.apache.lenya.lucene.index;

import java.io.File;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class AbstractDocumentCreator implements DocumentCreator {
    /** Creates a new instance of AbstractDocumentCreator */
    public AbstractDocumentCreator() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     * @param htdocsDumpDir DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(File file, File htdocsDumpDir)
        throws Exception {
        // make a new, empty document
        Document doc = new Document();

        // Add the url as a field named "url".  Use an UnIndexed field, so
        // that the url is just stored with the document, but is not searchable.
        String requestURI = file.getPath().replace(File.separatorChar, '/').substring(htdocsDumpDir.getPath()
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
        doc.add(Field.Keyword("modified", DateField.timeToString(file.lastModified())));

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with document, it is indexed, but it is not
        // tokenized prior to indexing.
        doc.add(new Field("uid", IndexIterator.createUID(file, htdocsDumpDir), false, true, false));

        return doc;
    }
}
