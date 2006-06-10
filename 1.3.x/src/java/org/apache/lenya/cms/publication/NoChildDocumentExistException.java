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

/* $Id: DocumentDoesNotExistException.java 42598 2004-03-01 16:18:28Z gregor $  */

package org.apache.lenya.cms.publication;

public class NoChildDocumentExistException extends DocumentException {

    /**
     * Creates a new NoChildDocumentExistException
     * 
     */
    public NoChildDocumentExistException() {
        super();
    }

    /**
     * Creates a new NoChildDocumentExistException
     * 
     * @param message the exception message
     */
    public NoChildDocumentExistException(String message) {
        super(message);
    }

    /**
     * Creates a new NoChildDocumentExistException
     * 
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public NoChildDocumentExistException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new NoChildDocumentExistException
     * 
     * @param cause the cause of the exception
     */
    public NoChildDocumentExistException(Throwable cause) {
        super(cause);
    }

}
