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

/* $Id: HTMLParser.java,v 1.7 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.lucene.parser;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;

public interface HTMLParser {
    void parse(File file) throws ParseException;

    void parse(URI uri) throws ParseException;

    /**
     * Returns the title of the HTML document.
     */
    String getTitle() throws IOException;

    /**
     * Returns keywords
     */
    String getKeywords() throws IOException;

    /**
     * Returns a reader that reads the contents of the HTML document.
     */
    Reader getReader() throws IOException;
}
