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

/* $Id$  */

package org.apache.lenya.lucene.index;

import java.io.File;
import java.io.IOException;

import org.apache.lenya.lucene.parser.HTMLParser;
import org.apache.lenya.lucene.parser.HTMLParserFactory;
import org.apache.lenya.lucene.parser.ParseException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * The default document creator
 */
public class DefaultDocumentCreator extends AbstractDocumentCreator {

    /** 
     * Creates a new instance of DefaultDocumentCreator
     */
    public DefaultDocumentCreator() {
        // do nothing
    }

    /**
     * Returns a document
     * @param file The file
     * @param htdocsDumpDir The dump directory
     * @return The document
     * @throws IOException if an error occurs
     */
    public Document getDocument(File file, File htdocsDumpDir) throws IOException {
        Document document;
        try {
            document = super.getDocument(file, htdocsDumpDir);

            HTMLParser parser = HTMLParserFactory.newInstance(file);
            parser.parse(file);

            document.add(Field.Text("title", parser.getTitle()));
            document.add(Field.Text("keywords", parser.getKeywords()));
            document.add(Field.Text("contents", parser.getReader()));
        } catch (ParseException e) {
            throw new IOException(e.toString());
        } catch (IOException e) {
            throw new IOException(e.toString());
        }

        return document;
    }
}
