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

/* $Id: PreParser.java,v 1.7 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.lucene.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.log4j.Category;

/**
 * The Java HTML parser cannot handle self-closing text.
 * This class converts all "/>" strings to ">" to avoid this problem.
 */
public class PreParser {
    
    private static Category log = Category.getInstance(PreParser.class);
    
    /** Creates a new instance of PreParser */
    public PreParser() {
        log.debug("creating new object");
    }

    /**
     * Parses HTML from a reader.
     */
    public Reader parse(Reader reader) throws IOException {
        StringBuffer buffer = new StringBuffer();
        boolean pending = false;

        char[] chars = new char[1];

        while (reader.read(chars) != -1) {
            int lastPosition = buffer.length() - 1;

            if ((chars[0] == '>') && (buffer.charAt(lastPosition) == '/')) {
                buffer.deleteCharAt(lastPosition);
            }

            buffer.append(chars[0]);
        }

        return new StringReader(buffer.toString());
    }
}
