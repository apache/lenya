/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIE
 * 0S OR CONDITIONS OF ANY KIND, either express or implied.
 * 0See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.components.search;

/**
 * Index Exception class
 * 
 * @author Nicolas Maisonneuve
 */
public class IndexException extends Exception {

    private String message;

    public IndexException(String mes) {
        this(mes, null);
    }

    public IndexException(Exception ex) {
        this("", ex);
    }

    /**
     * Constructor
     * 
     * @param mes
     *            message
     * @param ex
     *            initial exception
     */
    public IndexException(String mes, Exception ex) {

        message = mes;
        if (ex != null) {
            initCause(ex);
        }
    }

    public String getMessage() {
        return "message: " + message;
    }

}
