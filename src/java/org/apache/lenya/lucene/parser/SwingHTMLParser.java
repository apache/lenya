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

/* $Id: SwingHTMLParser.java,v 1.9 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.lucene.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;

import javax.swing.text.html.parser.ParserDelegator;

public class SwingHTMLParser extends AbstractHTMLParser {
    /** Creates a new instance of SwingHTMLParser */
    public SwingHTMLParser() {
    }

    /**
     * Parses a URI.
     */
    public void parse(URI uri) throws ParseException {
        try {
            ParserDelegator delagator = new ParserDelegator();
            handler = new SwingHTMLHandler();

            Reader reader = new PreParser().parse(getReader(uri));
            delagator.parse(reader, handler, true);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    private SwingHTMLHandler handler;

    protected SwingHTMLHandler getHandler() {
        return handler;
    }

    /**
     * Get title
     *
     * @return DOCUMENT ME!
     */
    public String getTitle() {
        return getHandler().getTitle();
    }

    /**
     * Get keywords
     *
     * @return DOCUMENT ME!
     */
    public String getKeywords() {
        return getHandler().getKeywords();
    }

    private Reader reader;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Reader getReader() {
        return getHandler().getReader();
    }

    protected Reader getReader(URI uri) throws IOException, MalformedURLException {
        if (uri.toString().startsWith("http:")) {
            // uri is url
            URLConnection connection = uri.toURL().openConnection();

            return new InputStreamReader(connection.getInputStream());
        } else {
            // uri is file
            return new FileReader(new File(uri));
        }
    }
}
