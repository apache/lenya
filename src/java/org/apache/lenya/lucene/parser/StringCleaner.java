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

/* $Id: StringCleaner.java,v 1.5 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.lucene.parser;


/**
 * A utility class to convert a string to text that can be included in an XML file.
 */
public class StringCleaner {
    /** Creates a new instance of StringCleaner */
    public StringCleaner() {
    }

    /**
     * Remove all non-word characters from a string.
     */
    public static String clean(String source) {
        String result = "<![CDATA[" + source + "]]>";

        return result;
    }
}
