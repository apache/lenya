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

/* $Id: DefaultDocumentCreator.java,v 1.9 2004/03/01 16:18:16 gregor Exp $  */

package org.apache.lenya.lucene.index;

import java.io.File;

import org.apache.lenya.lucene.parser.HTMLParser;
import org.apache.lenya.lucene.parser.HTMLParserFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class DefaultDocumentCreator extends AbstractDocumentCreator {

    /** 
     * Creates a new instance of DefaultDocumentCreator
     */
    public DefaultDocumentCreator() {
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
    public Document getDocument(File file, File htdocsDumpDir) throws Exception {
        Document document = super.getDocument(file, htdocsDumpDir);

        HTMLParser parser = HTMLParserFactory.newInstance(file);
        parser.parse(file);

        document.add(Field.Text("title", parser.getTitle()));
        document.add(Field.Text("keywords", parser.getKeywords()));
        document.add(Field.Text("contents", parser.getReader()));

        return document;
    }
}
