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

/* $Id: ParseException.java,v 1.5 2004/03/01 16:18:26 gregor Exp $  */

package org.apache.lenya.lucene.parser;

public class ParseException extends Exception {
    /** Creates a new instance of ParseException */
    public ParseException() {
    }

    /**
     * Creates a new ParseException object.
     *
     * @param message the error message
     * @param cause the cause of the exception
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new ParseException object.
     *
     * @param message the error message
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * Creates a new ParseException object.
     *
     * @param cause the cause of the exception
     */
    public ParseException(Throwable cause) {
        super(cause);
    }
}
