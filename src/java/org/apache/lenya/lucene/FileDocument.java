/*
 * $Id: FileDocument.java,v 1.6 2003/03/06 20:45:42 gregor Exp $
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
package org.lenya.lucene;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;


/**
 * A utility for making Lucene Documents from a File.
 */
public class FileDocument {
    private FileDocument() {
    }

    /**
     * Makes a document for a File.
     * 
     * <p>
     * The document has three fields:
     * 
     * <ul>
     * <li>
     * <code>path</code>--containing the pathname of the file, as a stored, tokenized field;
     * </li>
     * <li>
     * <code>modified</code>--containing the last modified date of the file as a keyword field as
     * encoded by <a href="lucene.document.DateField.html">DateField</a>; and
     * </li>
     * <li>
     * <code>contents</code>--containing the full contents of the file, as a Reader field.
     * </li>
     * </ul>
     * </p>
     *
     * @param f DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws java.io.FileNotFoundException DOCUMENT ME!
     */
    public static Document Document(File f) throws java.io.FileNotFoundException {
        // make a new, empty document
        Document doc = new Document();

        // Add the path of the file as a field named "path".  Use a Text field, so
        // that the index stores the path, and so that the path is searchable
        doc.add(Field.Text("path", f.getPath()));

        // Add the last modified date of the file a field named "modified".  Use a
        // Keyword field, so that it's searchable, but so that no attempt is made
        // to tokenize the field into words.
        doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));

        // Add the contents of the file a field named "contents".  Use a Text
        // field, specifying a Reader, so that the text of the file is tokenized.
        // ?? why doesn't FileReader work here ??
        FileInputStream is = new FileInputStream(f);
        Reader reader = new BufferedReader(new InputStreamReader(is));
        doc.add(Field.Text("contents", reader));

        // return the document
        return doc;
    }
}
