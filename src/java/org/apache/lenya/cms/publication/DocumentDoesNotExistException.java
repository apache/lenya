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

/* $Id: DocumentDoesNotExistException.java,v 1.2 2004/03/01 16:18:16 gregor Exp $  */

package org.apache.lenya.cms.publication;

public class DocumentDoesNotExistException extends DocumentException {

    /**
     * Creates a new DocumentDoesNotExistException
     * 
     */
    public DocumentDoesNotExistException() {
        super();
    }

    /**
     * Creates a new DocumentDoesNotExistException
     * 
     * @param message the exception message
     */
    public DocumentDoesNotExistException(String message) {
        super(message);
    }

    /**
     * Creates a new DocumentDoesNotExistException
     * 
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public DocumentDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new DocumentDoesNotExistException
     * 
     * @param cause the cause of the exception
     */
    public DocumentDoesNotExistException(Throwable cause) {
        super(cause);
    }

}
