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

/* $Id: DocumentException.java,v 1.3 2004/03/01 16:18:17 gregor Exp $  */

package org.apache.lenya.cms.publication;

public class DocumentException extends PublicationException {

    /**
     * Creates a new DocumentException
     * 
     */
    public DocumentException() {
        super();
    }

    /**
     * Creates a new DocumentException
     * 
     * @param message the exception message
     */
    public DocumentException(String message) {
        super(message);
    }

    /**
     * Creates a new DocumentException
     * 
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public DocumentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new DocumentException
     * 
     * @param cause the cause of the exception
     */
    public DocumentException(Throwable cause) {
        super(cause);
    }

}
