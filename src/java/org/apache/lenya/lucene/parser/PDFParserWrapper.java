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

/* $Id: PDFParserWrapper.java,v 1.7 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.lucene.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;

public class PDFParserWrapper extends AbstractHTMLParser {
    /** Creates a new instance of PDFParserWrapper */
    public PDFParserWrapper() {
    }

    /** Returns a reader that reads the contents of the HTML document.
     *
     */
    public Reader getReader() throws IOException {
        return getParser().getReader();
    }

    /** Returns the title of the HTML document.
     *
     */
    public String getTitle() throws IOException {
        try {
            return getParser().getTitle();
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }
    }

    /** Returns the keywords of the HTML document.
     *
     */
    public String getKeywords() throws IOException {
        try {
            return getParser().getKeywords();
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }
    }

    org.apache.lenya.lucene.html.HTMLParser parser;

    protected org.apache.lenya.lucene.html.HTMLParser getParser() {
        return parser;
    }

    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     *
     * @throws ParseException DOCUMENT ME!
     */
    public void parse(File file) throws ParseException {
        try {
            parser = new org.apache.lenya.lucene.html.HTMLParser(file);
        } catch (FileNotFoundException e) {
            throw new ParseException(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param uri DOCUMENT ME!
     *
     * @throws ParseException DOCUMENT ME!
     */
    public void parse(URI uri) throws ParseException {
        try {
            URLConnection connection = uri.toURL().openConnection();
            Reader reader = new InputStreamReader(connection.getInputStream());
            parser = new org.apache.lenya.lucene.html.HTMLParser(reader);
        } catch (MalformedURLException e) {
            throw new ParseException(e);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }
}
