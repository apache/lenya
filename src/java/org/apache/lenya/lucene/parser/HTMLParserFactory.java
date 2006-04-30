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

package org.apache.lenya.lucene.parser;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Factory to create HTML parsers that are used for indexing HTML.
 */
public class HTMLParserFactory {

    public static final Logger log = Logger.getLogger(HTMLParserFactory.class);

    /**
     * Returns an HTMLParser.
     */
    public static HTMLParser newInstance(File file) {
        HTMLParser parser = null;

        // HTML files
        if (file.getName().endsWith(".html")) {
            parser = new SwingHTMLParser();
        }
        // PDF files
        else if (file.getName().endsWith(".txt")) {
            parser = new PDFParserWrapper();
        } else {
            parser = new SwingHTMLParser();
            log.debug(".newInstance(): WARNING: Suffix did no match (" + file.getName()  + "). SwingHTMLParser as default parser selected!");
        }

        log.debug("returning a " + parser.getClass().getName() + " for " + file.getName());

        return parser;
    }
}
